package com.mfano.mems.models;

import com.mfano.mems.auth.models.CommonObject;
import com.mfano.mems.auth.models.Term;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "exams")
@Setter
@Getter
public class Exam extends CommonObject{
    //this.name; // END TERM EXAM
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "term_id", nullable = true)
    private Term term;

    @Column(name = "branch_id")
    private Long branch;
}
