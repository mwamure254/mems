package com.mfano.mems.auth.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "terms")
@Setter
@Getter
public class Term extends BaseObject{
    private Integer term; // 1,2,3
    private Integer year;
}
