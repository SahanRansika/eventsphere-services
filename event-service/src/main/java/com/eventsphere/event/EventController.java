package com.eventsphere.event;

import com.eventsphere.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<Event>> all(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category) {
        return ApiResponse.success("Events fetched successfully", service.all(search, category));
    }

    @GetMapping("/admin/all")
    public ApiResponse<List<Event>> allForAdmin(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        requireAdmin(role);
        return ApiResponse.success("All events fetched successfully", service.allForAdmin(search, category));
    }

    @GetMapping("/{id}")
    public ApiResponse<Event> one(@PathVariable Long id) {
        return ApiResponse.success("Event fetched successfully", service.one(id));
    }

    @PostMapping
    public ApiResponse<Event> create(
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @RequestBody Event event) {
        requireAdmin(role);
        return ApiResponse.success("Event created successfully", service.create(event));
    }

    @PutMapping("/{id}")
    public ApiResponse<Event> update(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @RequestBody Event event) {
        requireAdmin(role);
        return ApiResponse.success("Event updated successfully", service.update(id, event));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        requireAdmin(role);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reserve")
    public Event reserve(
            @PathVariable Long id,
            @RequestParam int count) {
        return service.reserve(id, count);
    }

    @PostMapping("/{id}/release")
    public Event release(
            @PathVariable Long id,
            @RequestParam int count) {
        return service.release(id, count);
    }

    private void requireAdmin(String role) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN,
                    "Admin access required");
        }
    }
}
