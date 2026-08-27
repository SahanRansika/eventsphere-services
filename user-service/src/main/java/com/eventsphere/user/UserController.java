package com.eventsphere.user;

import com.eventsphere.common.ApiResponse;
import com.eventsphere.user.dto.UpdateUserRequest;
import com.eventsphere.user.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<UserResponse>> getUsers(
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        requireAdmin(role);
        return ApiResponse.success("Users fetched successfully", service.getUsers());
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUser(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        return ApiResponse.success("User fetched successfully", service.getUser(id, role));
    }

    @PutMapping("/{id}")
    public ApiResponse<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        return ApiResponse.success("User updated successfully", service.updateUser(id, request, role));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @RequestHeader(value = "X-User-Id", required = false) Long requestingUserId) {
        service.deleteUser(id, role, requestingUserId);
        return ResponseEntity.noContent().build();
    }

    private void requireAdmin(String role) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN,
                    "Admin access required");
        }
    }
}
