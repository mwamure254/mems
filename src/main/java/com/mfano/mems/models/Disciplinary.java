package com.mfano.mems.models;

import com.mfano.mems.auth.models.CommonObject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "disciplinaries")
@Setter
@Getter
public class Disciplinary extends CommonObject{
    @Column(name = "student_id", insertable=false, updatable=false)
    private Long student;
    private String penalty;

    @Column(name = "branch_id", insertable=false, updatable=false)
    private Long branch;
}
