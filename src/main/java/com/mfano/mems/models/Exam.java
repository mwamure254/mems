package com.mfano.mems.models;

import com.mfano.mems.auth.models.CommonObject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "exams")
@Setter
@Getter
public class Exam extends CommonObject{
    //this.name; // END TERM EXAM
    private Integer term; // 1, 2, 3
    private Integer year; // 2027

    @Column(name = "branch_id", insertable=false, updatable=false)
    private Long branch;
}
