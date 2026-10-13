package com.example.attendance.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "attendance", uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "attendance_date"}))
public class AttendanceRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "student_id") private Student student;
    @Column(name = "attendance_date", nullable = false) private LocalDate date;
    @Column(nullable = false) private String status;

    protected AttendanceRecord() { }
    public AttendanceRecord(Student student, LocalDate date, String status) {
        this.student = student; this.date = date; this.status = status;
    }
    public Long getId() { return id; }
    public Student getStudent() { return student; }
    public LocalDate getDate() { return date; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
