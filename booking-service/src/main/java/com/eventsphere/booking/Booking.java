package com.eventsphere.booking;
import org.springframework.data.annotation.Id; import org.springframework.data.mongodb.core.mapping.Document; import java.math.BigDecimal; import java.time.*;
@Document("bookings") public class Booking{
 @Id private String id; private String bookingReference; private Long userId; private String userFullName; private Long eventId; private String eventTitle; private String eventVenue; private LocalDate eventDate; private LocalTime eventTime; private Integer ticketCount; private BigDecimal totalPrice; private Status status; private LocalDateTime bookingDate=LocalDateTime.now();
 public enum Status{CONFIRMED,CANCELLED}
 public String getId(){return id;} public String getBookingReference(){return bookingReference;} public void setBookingReference(String v){bookingReference=v;} public Long getUserId(){return userId;} public void setUserId(Long v){userId=v;} public String getUserFullName(){return userFullName;} public void setUserFullName(String v){userFullName=v;}
 public Long getEventId(){return eventId;} public void setEventId(Long v){eventId=v;} public String getEventTitle(){return eventTitle;} public void setEventTitle(String v){eventTitle=v;} public String getEventVenue(){return eventVenue;} public void setEventVenue(String v){eventVenue=v;}
 public LocalDate getEventDate(){return eventDate;} public void setEventDate(LocalDate v){eventDate=v;} public LocalTime getEventTime(){return eventTime;} public void setEventTime(LocalTime v){eventTime=v;} public Integer getTicketCount(){return ticketCount;} public void setTicketCount(Integer v){ticketCount=v;}
 public BigDecimal getTotalPrice(){return totalPrice;} public void setTotalPrice(BigDecimal v){totalPrice=v;} public Status getStatus(){return status;} public void setStatus(Status v){status=v;} public LocalDateTime getBookingDate(){return bookingDate;}
}
