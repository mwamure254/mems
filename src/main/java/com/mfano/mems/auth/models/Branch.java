package com.mfano.mems.auth.models;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "branches")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Branch extends CommonObject {
    private String location;
    private String code;
    private String manager;
    private String phone;  
    private String email;
    private String address;
    
    //SIGNED AGREEMENT FORM URL
    private String document;

    @Column(precision = 9, scale = 6)
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private BigDecimal latitude;

    @Column(precision = 10, scale = 6)
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private BigDecimal longitude;
}
