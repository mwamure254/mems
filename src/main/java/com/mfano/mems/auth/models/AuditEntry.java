package com.mfano.mems.auth.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "audits")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuditEntry extends BaseObject {
    private String action;
    private String details;
    private String status;

    @Column(name = "branch_id", insertable=false, updatable=false)
    private Long branch;
}
