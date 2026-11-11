package com.mfano.mems.dtos;

import org.springframework.web.multipart.MultipartFile;

import jakarta.mail.Multipart;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter 
@Getter 
@NoArgsConstructor 
@AllArgsConstructor 
public class UserDto {
  @Email 
  @NotBlank 
  private String email;
  private String password;
  private String fin;
  private String lan;
  private String gender;
  private String image;
  @NotNull 
  private Long branch;
  @NotNull 
  private Long role;

}
