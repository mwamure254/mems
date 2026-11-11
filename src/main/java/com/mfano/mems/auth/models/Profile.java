package com.mfano.mems.auth.models;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Profile extends BaseObject {
    // personal
    private String about;
    private String fin;
    private String lan;
    private String other;
    private String SN;
    private String phone;
    private String email;
    private String image;
    private String gender;
    // address
    private String county;
    private String address;
    private String designation;
    // Social Media
    private String twitter;
    private String facebook;
    private String linkedin;
    private String instagram;

    @JoinColumn(name = "user_id", unique = true)
    private Long user;

    public void setFin(String fin) {
        if (fin == null || fin.isBlank()) {
            this.fin = fin;
            return;
        }

        fin = fin.trim().toLowerCase();
        this.fin = Character.toUpperCase(fin.charAt(0)) + fin.substring(1);
    }

    public void setLan(String lan) {
        if (lan == null || lan.isBlank()) {
            this.lan = lan;
            return;
        }

        lan = lan.trim().toLowerCase();
        this.lan = Character.toUpperCase(lan.charAt(0)) + lan.substring(1);
    }

    public void setOther(String other) {
        if (other == null || other.isBlank()) {
            this.other = other;
            return;
        }

        other = other.trim().toLowerCase();
        this.other = Character.toUpperCase(other.charAt(0)) + other.substring(1);
    }

     //set address to sentence case
    public void setAddress(String address) {
        if (address == null || address.isBlank()) {
            this.address = address;
            return;
        }

        String value = address.trim().toLowerCase();
        StringBuilder result = new StringBuilder(value.length());
        boolean capitalizeNext = true;

        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);

            if (capitalizeNext && Character.isLetter(c)) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(c);
            }

            if (c == '.' || c == '!' || c == '?') {
                capitalizeNext = true;
            }
        }

        this.address = result.toString();
    } 
    
    //set about to sentence case
    public void setAbout(String about) {
        if (about == null || about.isBlank()) {
            this.about = about;
            return;
        }

        String value = about.trim().toLowerCase();
        StringBuilder result = new StringBuilder(value.length());
        boolean capitalizeNext = true;

        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);

            if (capitalizeNext && Character.isLetter(c)) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(c);
            }

            if (c == '.' || c == '!' || c == '?') {
                capitalizeNext = true;
            }
        }

        this.about = result.toString();
    }
}