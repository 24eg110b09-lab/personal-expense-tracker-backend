package com.example.attendance.repository;

import com.example.attendance.model.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<AttendanceRecord, Long> {
    List<AttendanceRecord> findByDateOrderByStudent_Name(LocalDate date);
    List<AttendanceRecord> findByStudentIdOrderByDateDesc(Long studentId);
    Optional<AttendanceRecord> findByStudentIdAndDate(Long studentId, LocalDate date);
}
