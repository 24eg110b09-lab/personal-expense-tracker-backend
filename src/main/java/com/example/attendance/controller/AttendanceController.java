package com.example.attendance.controller;

import com.example.attendance.model.AttendanceRecord;
import com.example.attendance.model.Student;
import com.example.attendance.repository.AttendanceRepository;
import com.example.attendance.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AttendanceController {
    private final StudentRepository students;
    private final AttendanceRepository attendance;
    public AttendanceController(StudentRepository students, AttendanceRepository attendance) {
        this.students = students; this.attendance = attendance;
    }

    @GetMapping("/students")
    public List<Student> getStudents() { return students.findAll(); }

    @PostMapping("/students")
    @ResponseStatus(HttpStatus.CREATED)
    public Student addStudent(@RequestBody StudentRequest request) {
        String name = required(request.name(), "Name");
        String number = required(request.studentNumber(), "Student number");
        String className = required(request.className(), "Class");
        if (students.existsByStudentNumber(number)) throw new ResponseStatusException(HttpStatus.CONFLICT, "Student number already exists");
        return students.save(new Student(name, number, className));
    }

    @GetMapping("/attendance")
    public List<AttendanceRecord> getAttendance(@RequestParam(defaultValue = "") String date) {
        LocalDate selectedDate = date.isBlank() ? LocalDate.now() : parseDate(date);
        return attendance.findByDateOrderByStudent_Name(selectedDate);
    }

    @PostMapping("/attendance")
    public AttendanceRecord markAttendance(@RequestBody AttendanceRequest request) {
        if (request.studentId() == null || request.date() == null || request.status() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student, date, and status are required");
        String status = request.status().trim().toUpperCase();
        if (!List.of("PRESENT", "ABSENT", "LATE").contains(status))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status must be PRESENT, ABSENT, or LATE");
        Student student = students.findById(request.studentId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        AttendanceRecord record = attendance.findByStudentIdAndDate(student.getId(), request.date())
                .orElseGet(() -> new AttendanceRecord(student, request.date(), status));
        record.setStatus(status);
        return attendance.save(record);
    }

    @GetMapping("/students/{id}/summary")
    public Map<String, Object> studentSummary(@PathVariable Long id) {
        Student student = students.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        List<AttendanceRecord> records = attendance.findByStudentIdOrderByDateDesc(id);
        long present = records.stream().filter(r -> r.getStatus().equals("PRESENT")).count();
        long absent = records.stream().filter(r -> r.getStatus().equals("ABSENT")).count();
        long late = records.stream().filter(r -> r.getStatus().equals("LATE")).count();
        double rate = records.isEmpty() ? 0 : (present + late) * 100.0 / records.size();
        return Map.of("student", student, "records", records, "present", present, "absent", absent, "late", late, "attendanceRate", Math.round(rate * 10.0) / 10.0);
    }

    private static String required(String value, String field) {
        if (value == null || value.isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " is required");
        return value.trim();
    }
    private static LocalDate parseDate(String date) {
        try { return LocalDate.parse(date); }
        catch (Exception e) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date must use YYYY-MM-DD format"); }
    }
    public record StudentRequest(String name, String studentNumber, String className) { }
    public record AttendanceRequest(Long studentId, LocalDate date, String status) { }
}
