package com.mfano.mems.models;

import com.mfano.mems.auth.models.CommonObject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "books")
@Setter
@Getter
public class Book extends CommonObject{
    private String author;
    private String category; // YOUNG READERS, TEXTBOOKS
    private String serial;
    private Integer copies;

    @Column(name = "branch_id", insertable=false, updatable=false)
    private Long branch;
}
