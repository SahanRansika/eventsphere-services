package com.eventsphere.user;
import com.eventsphere.user.dto.*; import com.eventsphere.common.ApiResponse; import jakarta.validation.Valid; import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/auth") public class AuthController{
 private final UserService s; public AuthController(UserService s){this.s=s;}
 @PostMapping("/register") ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest r){return ResponseEntity.status(201).body(ApiResponse.success("Registration successful",s.register(r)));}
 @PostMapping("/login") ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest r){return ApiResponse.success("Login successful",s.login(r));}
}