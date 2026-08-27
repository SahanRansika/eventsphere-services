package com.eventsphere.booking;

import com.eventsphere.common.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "event-service")
public interface EventClient {

 @GetMapping("/api/events/{id}")
 ApiResponse<EventView> get(@PathVariable("id") Long id);

 @PostMapping("/api/events/{id}/reserve")
 EventView reserve(
         @PathVariable("id") Long id,
         @RequestParam("count") int count
 );

 @PostMapping("/api/events/{id}/release")
 EventView release(
         @PathVariable("id") Long id,
         @RequestParam("count") int count
 );

 record EventView(
         Long id,
         String title,
         String description,
         String venue,
         java.time.LocalDate eventDate,
         java.time.LocalTime eventTime,
         java.math.BigDecimal price,
         Integer totalSeats,
         Integer availableSeats,
         String category
 ) {}
}