package com.mfano.mems.models;

import java.time.LocalDate;

import com.mfano.mems.auth.models.BaseObject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

    @Column(name = "grade_id", insertable=false, updatable=false)
    private Long grade; // Grade
    @Column(name = "grade_id", insertable=false, updatable=false)
    private Long stream; // RED, BLUE
    private String parentName;
    private String parentPhone;
    private LocalDate admissionDate;
    private String status = "ACTIVE"; // SUSPENDED/DECEASED/TRANSFERED/GRADUATED/EXPELLED

    @Column(name = "branch_id", insertable=false, updatable=false)
    private Long branch;

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
}
