package com.eventsphere.booking.dto;
import jakarta.validation.constraints.*; public class BookingRequest{@NotNull Long eventId; @NotNull @Min(1) Integer ticketCount; public Long getEventId(){return eventId;} public void setEventId(Long v){eventId=v;} public Integer getTicketCount(){return ticketCount;} public void setTicketCount(Integer v){ticketCount=v;}}
