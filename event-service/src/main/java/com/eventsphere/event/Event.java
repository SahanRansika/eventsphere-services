package com.eventsphere.event;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

@Entity
@Table(name = "events")
public class Event {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @Column(nullable = false)
 private String title;

 @Column(length = 2000)
 private String description;

 @Column(nullable = false)
 private String venue;

 @Column(nullable = false)
 private LocalDate eventDate;

 @Column(nullable = false)
 private LocalTime eventTime;

 @Column(nullable = false, precision = 10, scale = 2)
 private BigDecimal price;

 @Column(nullable = false)
 private Integer totalSeats;

 @Column(nullable = false)
 private Integer availableSeats;

 @Column(nullable = false)
 private String category;

 public Long getId() {
  return id;
 }

 public void setId(Long id) {
  this.id = id;
 }

 public String getTitle() {
  return title;
 }

 public void setTitle(String title) {
  this.title = title;
 }

 public String getDescription() {
  return description;
 }

 public void setDescription(String description) {
  this.description = description;
 }

 public String getVenue() {
  return venue;
 }

 public void setVenue(String venue) {
  this.venue = venue;
 }

 public LocalDate getEventDate() {
  return eventDate;
 }

 public void setEventDate(LocalDate eventDate) {
  this.eventDate = eventDate;
 }

 public LocalTime getEventTime() {
  return eventTime;
 }

 public void setEventTime(LocalTime eventTime) {
  this.eventTime = eventTime;
 }

 public BigDecimal getPrice() {
  return price;
 }

 public void setPrice(BigDecimal price) {
  this.price = price;
 }

 public Integer getTotalSeats() {
  return totalSeats;
 }

 public void setTotalSeats(Integer totalSeats) {
  this.totalSeats = totalSeats;
 }

 public Integer getAvailableSeats() {
  return availableSeats;
 }

 public void setAvailableSeats(Integer availableSeats) {
  this.availableSeats = availableSeats;
 }

 public String getCategory() {
  return category;
 }

 public void setCategory(String category) {
  this.category = category;
 }

 // =========================================================
 // EVENT DATE + TIME
 // =========================================================

 @Transient
 public LocalDateTime getEventDateTime() {

  if (eventDate == null || eventTime == null) {
   return null;
  }

  return LocalDateTime.of(
          eventDate,
          eventTime
  );
 }

 // =========================================================
 // AUTO EXPIRATION
 // =========================================================

 @Transient
 public boolean isExpired() {

  LocalDateTime eventDateTime =
          getEventDateTime();

  if (eventDateTime == null) {
   return false;
  }

  LocalDateTime now =
          LocalDateTime.now(
                  ZoneId.of("Asia/Colombo")
          );

  return !now.isBefore(eventDateTime);
 }

 // =========================================================
 // STATUS
 // =========================================================

 @Transient
 public String getStatus() {

  if (isExpired()) {
   return "EXPIRED";
  }

  if (availableSeats != null
          && availableSeats <= 0) {

   return "SOLD_OUT";
  }

  return "AVAILABLE";
 }
}