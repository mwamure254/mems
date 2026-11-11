package com.mfano.mems.auth.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "grades")
@Setter
@Getter
public class Grade extends CommonObject{
    
}
