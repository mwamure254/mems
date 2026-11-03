package com.mfano.mems.auth.models;

import com.mfano.mems.auth.models.CommonObject;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Setter
@Getter
public class Role extends CommonObject {

}
