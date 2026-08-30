package com.eventsphere.event;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Configuration
public class EventSeedConfig {

    @Bean
    CommandLineRunner seedEvents(EventRepository repository) {
        return args -> {
            if (repository.count() > 0) return;

            repository.save(event(
                    "FutureTech 2026",
                    "A practical technology conference covering cloud, AI and modern software engineering.",
                    "BMICH, Colombo",
                    LocalDate.of(2026, 9, 12), LocalTime.of(9, 30),
                    "45.00", 180, "CONFERENCE"));

            repository.save(event(
                    "Colombo Live Sessions",
                    "An evening of live music, local artists and an energetic EventSphere audience.",
                    "Nelum Pokuna, Colombo",
                    LocalDate.of(2026, 9, 19), LocalTime.of(18, 30),
                    "30.00", 250, "CONCERT"));

            repository.save(event(
                    "Comedy Night",
                    "A stand-up comedy showcase featuring fresh local talent and special guests.",
                    "Lionel Wendt Theatre, Colombo",
                    LocalDate.of(2026, 9, 26), LocalTime.of(19, 00),
                    "20.00", 120, "COMEDY"));
        };
    }

    private Event event(String title, String description, String venue,
                        LocalDate date, LocalTime time, String price,
                        int seats, String category) {
        Event event = new Event();
        event.setTitle(title);
        event.setDescription(description);
        event.setVenue(venue);
        event.setEventDate(date);
        event.setEventTime(time);
        event.setPrice(new BigDecimal(price));
        event.setTotalSeats(seats);
        event.setAvailableSeats(seats);
        event.setCategory(category);
        return event;
    }
}
