package com.eventsphere.event;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

 // Requested IDE-resolvable methods: all sorted by date then time.
 List<Event> findByTitleContainingIgnoreCaseOrderByEventDateAscEventTimeAsc(String title);

 List<Event> findByCategoryIgnoreCaseOrderByEventDateAscEventTimeAsc(String category);

 List<Event> findAllByOrderByEventDateAscEventTimeAsc();

 // Future-event variants used by the public event catalogue.
 List<Event> findByEventDateGreaterThanEqualOrderByEventDateAscEventTimeAsc(LocalDate eventDate);

 List<Event> findByTitleContainingIgnoreCaseAndEventDateGreaterThanEqualOrderByEventDateAscEventTimeAsc(
         String title, LocalDate eventDate);

 List<Event> findByCategoryIgnoreCaseAndEventDateGreaterThanEqualOrderByEventDateAscEventTimeAsc(
         String category, LocalDate eventDate);

 // Used when today's events must also be filtered by their actual event time.
 List<Event> findByEventDateGreaterThanOrderByEventDateAscEventTimeAsc(LocalDate eventDate);

 List<Event> findByEventDateAndEventTimeGreaterThanEqualOrderByEventDateAscEventTimeAsc(
         LocalDate eventDate, LocalTime eventTime);

 List<Event> findByTitleContainingIgnoreCaseAndEventDateGreaterThanOrderByEventDateAscEventTimeAsc(
         String title, LocalDate eventDate);

 List<Event> findByTitleContainingIgnoreCaseAndEventDateAndEventTimeGreaterThanEqualOrderByEventDateAscEventTimeAsc(
         String title, LocalDate eventDate, LocalTime eventTime);

 List<Event> findByCategoryIgnoreCaseAndEventDateGreaterThanOrderByEventDateAscEventTimeAsc(
         String category, LocalDate eventDate);

 List<Event> findByCategoryIgnoreCaseAndEventDateAndEventTimeGreaterThanEqualOrderByEventDateAscEventTimeAsc(
         String category, LocalDate eventDate, LocalTime eventTime);
}
