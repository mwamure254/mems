package com.mfano.mems.auth.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "subjects")
@Setter
@Getter
public class Subject extends CommonObject {
    // this.name; // ENGLISH
    private String code; // 0002
    /*
     * @Column(name = "grade_id")
     * private Long grade; // Grade 1, null = All Grades
     * 
     * @Column(name = "teacher_id")
     * private Long teacher; // Teacher assigned
     * 
     * @Column(name = "branch_id")
     * private Long branch;
     */
}
