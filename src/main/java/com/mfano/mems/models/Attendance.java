package com.mfano.mems.models;

import java.time.LocalDate;
import java.time.LocalTime;

import com.mfano.mems.auth.models.BaseObject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "attendance")
@Setter
@Getter
public class Attendance extends BaseObject{
    private Long staffId;
    private LocalDate date;
    private LocalTime timeIn;
    private LocalTime timeOut;

    @Column(name = "branch_id", insertable=false, updatable=false)
    private Long branch;
}
