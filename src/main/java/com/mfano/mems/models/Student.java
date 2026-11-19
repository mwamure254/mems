package com.mfano.mems.models;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.mfano.mems.auth.models.BaseObject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "students")
@Setter
@Getter
public class Student extends BaseObject {
    private String admissionNumber;
    private String firstName;
    private String lastName;
    private String otherName;
    private String gender;
    private LocalDate dob; // Date of birth
    private String upload;

    @Column(name = "grade_id")
    private Long grade; // Grade
    private String stream; // RED, BLUE
    private String parentName;
    private String parentPhone;
    //this.createdAt; // admissionDate;
    private String status = "ACTIVE"; // SUSPENDED/DECEASED/TRANSFERED/GRADUATED/EXPELLED

    @Column(name = "branch_id")
    private Long branch;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "student_exams", joinColumns = @JoinColumn(name = "student_id"), inverseJoinColumns = @JoinColumn(name = "exam_id"))
    private Set<Exam> exams = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "student_fees", joinColumns = @JoinColumn(name = "student_id"), inverseJoinColumns = @JoinColumn(name = "fees_id"))
    private Set<Fees> fees = new HashSet<>();

    public void setFirstName(String firstName) {
        if (firstName == null || firstName.isBlank()) {
            this.firstName = firstName;
            return;
        }

        firstName = firstName.trim().toLowerCase();
        this.firstName = Character.toUpperCase(firstName.charAt(0)) + firstName.substring(1);
    }

    public void setLastName(String lastName) {
        if (lastName == null || lastName.isBlank()) {
            this.lastName = lastName;
            return;
        }

        lastName = lastName.trim().toLowerCase();
        this.lastName = Character.toUpperCase(lastName.charAt(0)) + lastName.substring(1);
    }

    public void setOtherName(String otherName) {
        if (otherName == null || otherName.isBlank()) {
            this.otherName = otherName;
            return;
        }

        otherName = otherName.trim().toLowerCase();
        this.otherName = Character.toUpperCase(otherName.charAt(0)) + otherName.substring(1);
    }
}
