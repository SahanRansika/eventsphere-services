package com.eventsphere.booking.dto;
import java.math.BigDecimal; import java.time.*;
public record BookingResponse(String id,String bookingReference,Long eventId,String eventTitle,String eventVenue,LocalDate eventDate,LocalTime eventTime,Long userId,String userFullName,Integer ticketCount,BigDecimal totalPrice,String status,LocalDateTime bookingDate){}
