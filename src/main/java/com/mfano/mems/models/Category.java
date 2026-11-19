package com.mfano.mems.models;

import com.mfano.mems.auth.models.CommonObject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "book_categories")
@Setter
@Getter
public class Category extends CommonObject{
    @Column(name = "branch_id")
    private Long branch;
}
