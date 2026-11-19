package com.mfano.mems.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MarkDto {
    private Long student;
    private Long exam;
    private Long subject;
    private Integer score;
    private String level; // EE, ME, AE, BE
    private Integer points;
    private String remark;
    private Long branch;
}

    
    