package com.example.attendance.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "students")
public class Student {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String name;
    @Column(nullable = false, unique = true) private String studentNumber;
    @Column(nullable = false) private String className;

    protected Student() { }
    public Student(String name, String studentNumber, String className) {
        this.name = name; this.studentNumber = studentNumber; this.className = className;
    }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getStudentNumber() { return studentNumber; }
    public String getClassName() { return className; }
}
