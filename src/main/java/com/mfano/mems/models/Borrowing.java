package com.mfano.mems.models;

import java.time.LocalDate;

import com.mfano.mems.auth.models.BaseObject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "borrowings")
@Setter
@Getter
public class Borrowing extends BaseObject {
    @Column(name = "book_id")
    private Long book;
    @Column(name = "student_id")
    private Long student;
    @Column(name = "staff_id")
    private Long staff;
    //this.createdAt; // borrowedDate;
    private LocalDate dueDate;

    @Column(name = "branch_id")
    private Long branch;
}
