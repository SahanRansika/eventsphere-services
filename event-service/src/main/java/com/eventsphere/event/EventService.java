package com.eventsphere.event;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class EventService {

    private static final ZoneId ZONE = ZoneId.of("Asia/Colombo");

    private final EventRepository repo;

    public EventService(EventRepository repo) {
        this.repo = repo;
    }

    public List<Event> all(String search, String category) {
        String normalizedSearch = normalize(search);
        String normalizedCategory = normalize(category);
        LocalDateTime now = LocalDateTime.now(ZONE);

        return repo.findAll().stream()
                .filter(event -> event.getEventDate() != null && event.getEventTime() != null)
                .filter(event -> event.getEventDateTime().isAfter(now))
                .filter(event -> normalizedSearch.isBlank()
                        || contains(event.getTitle(), normalizedSearch)
                        || contains(event.getDescription(), normalizedSearch)
                        || contains(event.getVenue(), normalizedSearch))
                .filter(event -> normalizedCategory.isBlank()
                        || contains(event.getCategory(), normalizedCategory))
                .sorted(eventComparator())
                .toList();
    }

    public List<Event> allForAdmin(String search, String category) {
        String normalizedSearch = normalize(search);
        String normalizedCategory = normalize(category);

        return repo.findAll().stream()
                .filter(event -> normalizedSearch.isBlank()
                        || contains(event.getTitle(), normalizedSearch)
                        || contains(event.getDescription(), normalizedSearch)
                        || contains(event.getVenue(), normalizedSearch))
                .filter(event -> normalizedCategory.isBlank()
                        || contains(event.getCategory(), normalizedCategory))
                .sorted(eventComparator())
                .toList();
    }

    public Event one(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Event not found with id: " + id));
    }

    @Transactional
    public synchronized Event reserve(Long id, int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Seat count must be greater than zero");
        }

        Event event = one(id);
        if (event.isExpired()) {
            throw new IllegalArgumentException("This event has already started or expired");
        }
        if (event.getAvailableSeats() < count) {
            throw new IllegalArgumentException(
                    "Only " + event.getAvailableSeats() + " seat(s) left for this event");
        }

        event.setAvailableSeats(event.getAvailableSeats() - count);
        return repo.save(event);
    }

    @Transactional
    public synchronized Event release(Long id, int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Seat count must be greater than zero");
        }

        Event event = one(id);
        event.setAvailableSeats(Math.min(
                event.getTotalSeats(),
                event.getAvailableSeats() + count));
        return repo.save(event);
    }

    @Transactional
    public Event create(Event request) {
        validateEvent(request);
        request.setTitle(request.getTitle().trim());
        request.setVenue(request.getVenue().trim());
        request.setCategory(request.getCategory().trim());
        request.setAvailableSeats(request.getTotalSeats());
        return repo.save(request);
    }

    @Transactional
    public Event update(Long id, Event request) {
        Event existing = one(id);
        validateEvent(request);

        int oldTotalSeats = existing.getTotalSeats();
        int oldAvailableSeats = existing.getAvailableSeats();
        int soldSeats = Math.max(0, oldTotalSeats - oldAvailableSeats);

        if (request.getTotalSeats() < soldSeats) {
            throw new IllegalArgumentException(
                    "Total seats cannot be less than the number of tickets already sold (" + soldSeats + ")");
        }

        existing.setTitle(request.getTitle().trim());
        existing.setDescription(request.getDescription());
        existing.setVenue(request.getVenue().trim());
        existing.setEventDate(request.getEventDate());
        existing.setEventTime(request.getEventTime());
        existing.setPrice(request.getPrice());
        existing.setTotalSeats(request.getTotalSeats());
        existing.setCategory(request.getCategory().trim());
        existing.setAvailableSeats(existing.getTotalSeats() - soldSeats);

        return repo.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        one(id);
        repo.deleteById(id);
    }

    private void validateEvent(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Event payload is required");
        }
        if (event.getTitle() == null || event.getTitle().isBlank()) {
            throw new IllegalArgumentException("Event title is required");
        }
        if (event.getVenue() == null || event.getVenue().isBlank()) {
            throw new IllegalArgumentException("Event venue is required");
        }
        if (event.getEventDate() == null || event.getEventTime() == null) {
            throw new IllegalArgumentException("Event date and time are required");
        }
        if (event.getPrice() == null || event.getPrice().signum() < 0) {
            throw new IllegalArgumentException("Event price must be zero or greater");
        }
        if (event.getTotalSeats() == null || event.getTotalSeats() <= 0) {
            throw new IllegalArgumentException("Total seats must be greater than zero");
        }
        if (event.getCategory() == null || event.getCategory().isBlank()) {
            throw new IllegalArgumentException("Event category is required");
        }
    }

    private boolean contains(String value, String query) {
        return value != null && value.toLowerCase().contains(query);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    private Comparator<Event> eventComparator() {
        return Comparator
                .comparing(Event::getEventDate, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(Event::getEventTime, Comparator.nullsLast(Comparator.naturalOrder()));
    }
}
