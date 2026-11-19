package com.mfano.mems.dtos;

import java.util.List;

import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class ReportCardDTO {
    private String studentName;
    private String admNo;
    private String grade;
    private List<MarkDto> marks;
    private Integer total;
    private Double average;
    private Integer maxTotal;
    private String teacherComment;
}