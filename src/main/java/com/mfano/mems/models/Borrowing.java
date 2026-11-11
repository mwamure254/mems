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
    private Long bookId;
    private Long studentId;
    private LocalDate borrowedDate;
    private LocalDate dueDate;
    private String status = "OUT";// IN/RETURNED

    @Column(name = "branch_id", insertable=false, updatable=false)
    private Long branch;
}
