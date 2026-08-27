package com.eventsphere.user.dto;
import jakarta.validation.constraints.*; public class RegisterRequest{
 @NotBlank private String fullName; @Email @NotBlank private String email; private String phone; @NotBlank(message="Password is required") @Size(min=6, message="Password must be at least 6 characters") private String password;
 public String getFullName(){return fullName;} public void setFullName(String v){fullName=v;} public String getEmail(){return email;} public void setEmail(String v){email=v;}
 public String getPhone(){return phone;} public void setPhone(String v){phone=v;} public String getPassword(){return password;} public void setPassword(String v){password=v;}
}