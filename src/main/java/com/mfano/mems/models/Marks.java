package com.mfano.mems.models;

import org.yaml.snakeyaml.error.Mark;

import com.mfano.mems.auth.models.BaseObject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "marks")
@Setter
@Getter
public class Marks extends BaseObject {
    @Column(name = "student_id")
    private Long student;
    @Column(name = "exam_id")
    private Long exam;
    @Column(name = "subject_id")
    private Long subject;
    private Integer score;
    private String level; // EE, ME, AE, BE
    private Integer points;
    private String remark;

    @Column(name = "branch_id")
    private Long branch;

}
