package com.mfano.mems.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.mfano.mems.auth.models.CommonObject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "payments")
@Setter
@Getter
public class Payment extends CommonObject{
    @Column(name = "student_id", insertable=false, updatable=false)
    private Long student;
    private BigDecimal amountPaid;
    private Integer forTerm; // 1,2,3
    private Integer forYear;
    private String paymentMethod; // M-Pesa, Bank, Cash
    private String transactionReference; // M-Pesa code
    private LocalDate datePaid;

    @Column(name = "branch_id", insertable=false, updatable=false)
    private Long branch;
}
