package com.eventsphere.booking;

import com.eventsphere.booking.dto.BookingRequest;
import com.eventsphere.booking.dto.BookingResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class BookingService {

    private final BookingRepository repo;
    private final EventClient events;
    private final EmailService emailService;

    public BookingService(
            BookingRepository repo,
            EventClient events,
            EmailService emailService) {
        this.repo = repo;
        this.events = events;
        this.emailService = emailService;
    }

    public BookingResponse create(
            Long userId,
            String fullName,
            String userEmail,
            BookingRequest request) {

        EventClient.EventView event = events.reserve(
                request.getEventId(),
                request.getTicketCount()
        );

        if (event == null) {
            throw new IllegalArgumentException(
                    "Event could not be reserved"
            );
        }

        Booking booking = new Booking();

        booking.setBookingReference(
                "ES-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase()
        );

        booking.setUserId(userId);
        booking.setUserFullName(fullName);
        booking.setEventId(event.id());
        booking.setEventTitle(event.title());
        booking.setEventVenue(event.venue());
        booking.setEventDate(event.eventDate());
        booking.setEventTime(event.eventTime());
        booking.setTicketCount(request.getTicketCount());
        booking.setTotalPrice(
                event.price().multiply(
                        BigDecimal.valueOf(
                                request.getTicketCount()
                        )
                )
        );
        booking.setStatus(Booking.Status.CONFIRMED);

        Booking saved;
        try {
            saved = repo.save(booking);
        } catch (RuntimeException ex) {
            try {
                events.release(request.getEventId(), request.getTicketCount());
            } catch (RuntimeException compensationError) {
                // Keep the original database error while logging the compensation failure.
                org.slf4j.LoggerFactory.getLogger(BookingService.class)
                        .error("Could not restore seats after booking save failure", compensationError);
            }
            throw ex;
        }

        BookingResponse response = out(saved);

        // Send the confirmation after MongoDB save succeeds.
        emailService.sendBookingConfirmation(
                userEmail,
                response
        );

        return response;
    }

    public BookingResponse get(String id, Long userId) {
        Booking booking = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Booking not found"));

        if (!booking.getUserId().equals(userId)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN,
                    "You are not authorized to view this booking");
        }

        return out(booking);
    }

    public List<BookingResponse> mine(Long userId) {
        return repo
                .findByUserIdOrderByBookingDateDesc(userId)
                .stream()
                .map(this::out)
                .toList();
    }

    public BookingResponse cancel(String id, Long userId) {
        Booking booking = repo.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Booking not found"
                        )
                );

        if (!booking.getUserId().equals(userId)) {
            throw new IllegalArgumentException(
                    "You are not authorized to cancel this booking"
            );
        }

        if (booking.getStatus() == Booking.Status.CANCELLED) {
            throw new IllegalArgumentException(
                    "This booking is already cancelled"
            );
        }

        events.release(
                booking.getEventId(),
                booking.getTicketCount()
        );

        booking.setStatus(Booking.Status.CANCELLED);

        return out(repo.save(booking));
    }

    private BookingResponse out(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getBookingReference(),
                booking.getEventId(),
                booking.getEventTitle(),
                booking.getEventVenue(),
                booking.getEventDate(),
                booking.getEventTime(),
                booking.getUserId(),
                booking.getUserFullName(),
                booking.getTicketCount(),
                booking.getTotalPrice(),
                booking.getStatus().name(),
                booking.getBookingDate()
        );
    }
}
