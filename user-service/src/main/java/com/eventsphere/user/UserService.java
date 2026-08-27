package com.eventsphere.user;

import com.eventsphere.user.dto.AuthResponse;
import com.eventsphere.user.dto.LoginRequest;
import com.eventsphere.user.dto.RegisterRequest;
import com.eventsphere.user.dto.UpdateUserRequest;
import com.eventsphere.user.dto.UserResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

 private final UserRepository repo;
 private final BCryptPasswordEncoder enc;
 private final JwtUtil jwt;

 public UserService(UserRepository repo, BCryptPasswordEncoder enc, JwtUtil jwt) {
  this.repo = repo;
  this.enc = enc;
  this.jwt = jwt;
 }

 @Transactional
 public AuthResponse register(RegisterRequest request) {
  String email = normalizeEmail(request.getEmail());
  if (repo.existsByEmail(email)) {
   throw new IllegalArgumentException("Email already registered");
  }

  User user = new User();
  user.setFullName(request.getFullName().trim());
  user.setEmail(email);
  user.setPhone(request.getPhone());
  user.setPassword(enc.encode(request.getPassword()));
  user.setRole(User.Role.USER);

  repo.save(user);
  return out(user);
 }

 public AuthResponse login(LoginRequest request) {
  String email = normalizeEmail(request.getEmail());
  User user = repo.findByEmail(email)
          .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

  if (!enc.matches(request.getPassword(), user.getPassword())) {
   throw new IllegalArgumentException("Invalid email or password");
  }

  return out(user);
 }

 public List<UserResponse> getUsers() {
  return repo.findAll().stream().map(UserResponse::from).toList();
 }

 public UserResponse getUser(Long id, String requestingRole) {
  requireAdmin(requestingRole);
  return UserResponse.from(findUser(id));
 }

 @Transactional
 public UserResponse updateUser(Long id, UpdateUserRequest request, String requestingRole) {
  requireAdmin(requestingRole);

  User user = findUser(id);
  String email = normalizeEmail(request.getEmail());

  repo.findByEmail(email).ifPresent(existing -> {
   if (!existing.getId().equals(id)) {
    throw new IllegalArgumentException("Email is already used by another user");
   }
  });

  user.setFullName(request.getFullName().trim());
  user.setEmail(email);
  user.setPhone(request.getPhone());

  if (request.getRole() != null && !request.getRole().isBlank()) {
   try {
    user.setRole(User.Role.valueOf(request.getRole().trim().toUpperCase()));
   } catch (IllegalArgumentException ex) {
    throw new IllegalArgumentException("Role must be USER or ADMIN");
   }
  }

  return UserResponse.from(repo.save(user));
 }

 @Transactional
 public void deleteUser(Long id, String requestingRole, Long requestingUserId) {
  requireAdmin(requestingRole);

  if (id.equals(requestingUserId)) {
   throw new IllegalArgumentException("You cannot delete your own admin account");
  }

  User user = findUser(id);
  repo.delete(user);
 }

 private User findUser(Long id) {
  return repo.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
 }

 private void requireAdmin(String role) {
  if (!"ADMIN".equalsIgnoreCase(role)) {
   throw new IllegalArgumentException("Admin access required");
  }
 }

 private String normalizeEmail(String email) {
  return email == null ? null : email.trim().toLowerCase();
 }

 private AuthResponse out(User user) {
  return new AuthResponse(
          jwt.token(user),
          user.getId(),
          user.getFullName(),
          user.getEmail(),
          user.getRole().name()
  );
 }

 public void seed() {
  if (!repo.existsByEmail("admin@eventsphere.com")) {
   User user = new User();
   user.setFullName("EventSphere Admin");
   user.setEmail("admin@eventsphere.com");
   user.setPassword(enc.encode("Admin@123"));
   user.setRole(User.Role.ADMIN);
   repo.save(user);
  }
 }
}
