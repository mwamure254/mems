package com.mfano.mems.models;

import java.time.LocalDate;

import com.mfano.mems.auth.models.BaseObject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "transfers")
@Setter
@Getter
public class Transfer extends BaseObject{
    @Column(name = "student_id", insertable=false, updatable=false)
    private Long student; // null for transfer in
    private String studentName;
    private String schoolName; 
    private LocalDate transferDate;
    private String reason;
    private String type; // IN or OUT

    @Column(name = "branch_id", insertable=false, updatable=false)
    private Long branch;
}
