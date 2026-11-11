package com.mfano.mems.models;

import java.math.BigDecimal;

import com.mfano.mems.auth.models.BaseObject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "fees")
@Setter
@Getter
public class Fees extends BaseObject{
    @Column(name = "grade_id", insertable=false, updatable=false)
    private Long grade; // Grade 1, Grade 2
    private Integer term; // 1,2,3
    private Integer year;
    private BigDecimal amount;

    @Column(name = "branch_id", insertable=false, updatable=false)
    private Long branch;
}
