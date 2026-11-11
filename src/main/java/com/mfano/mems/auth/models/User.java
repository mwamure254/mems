package com.mfano.mems.auth.models;

import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseObject {
    private String fin;
    private String lan;
    private String image;
    private String designation;
    private String password;
    private String gender;

    @Column(unique = true)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "branch_id", nullable = true)
    private Branch branch;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public void setFin(String fin){
        if (fin == null || fin.isBlank()) {
            this.fin = fin;
            return;
        }

        fin = fin.trim().toLowerCase();
        this.fin = Character.toUpperCase(fin.charAt(0)) + fin.substring(1);
    }
     public void setLan(String lan){
        if (lan == null || lan.isBlank()) {
            this.lan = lan;
            return;
        }

        lan = lan.trim().toLowerCase();
        this.lan = Character.toUpperCase(fin.charAt(0)) + lan.substring(1);
    }

}
