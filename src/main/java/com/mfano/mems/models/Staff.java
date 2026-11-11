package com.mfano.mems.models;

import java.time.LocalDate;

import com.mfano.mems.auth.models.BaseObject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "staff")
@Setter
@Getter
public class Staff extends BaseObject{
    private String firstName; 
    private String lastName; 
    private String otherName; 
    private String staffNumber; // TSC Number, ID Number
    private String subjects; // MATHS, ENGLISH
    private String jobType; // TEACHNG STAFF, SUPPORT STAFF
    private String jobTitle; // GATEMAN, COOK, TEACHER, BURSAR
    private String gender; // MALE, FEMALE
    private String phone;
    private String email; 
    private String address; 
    private String location; 
    private String responsibility; //GAMES TEACHER/ CLUBS' TEACHER
    private LocalDate joinedDate;

    @Column(name = "branch_id", insertable=false, updatable=false)
    private Long branch;
}
