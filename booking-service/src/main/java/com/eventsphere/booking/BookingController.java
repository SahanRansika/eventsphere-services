package com.eventsphere.booking;

import com.eventsphere.booking.dto.BookingRequest;
import com.eventsphere.booking.dto.BookingResponse;
import com.eventsphere.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> create(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader(value = "X-User-Name", defaultValue = "Customer") String userName,
            @RequestHeader("X-User-Email") String userEmail,
            @Valid @RequestBody BookingRequest request) {

        BookingResponse response = service.create(
                userId,
                userName,
                userEmail,
                request
        );

        return ResponseEntity
                .status(201)
                .body(ApiResponse.success(
                        "Booking confirmed successfully",
                        response
                ));
    }

    @GetMapping("/my-bookings")
    public ApiResponse<List<BookingResponse>> mine(
            @RequestHeader("X-User-Id") Long userId) {

        return ApiResponse.success(
                "Bookings fetched successfully",
                service.mine(userId)
        );
    }

    @PutMapping("/{id}/cancel")
    public ApiResponse<BookingResponse> cancel(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable String id) {

        return ApiResponse.success(
                "Booking cancelled successfully",
                service.cancel(id, userId)
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<BookingResponse> get(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable String id) {

        return ApiResponse.success(
                "Booking fetched successfully",
                service.get(id, userId)
        );
    }
}
