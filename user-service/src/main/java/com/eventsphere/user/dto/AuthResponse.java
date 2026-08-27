package com.eventsphere.user.dto;
public record AuthResponse(String token,Long userId,String fullName,String email,String role){}
