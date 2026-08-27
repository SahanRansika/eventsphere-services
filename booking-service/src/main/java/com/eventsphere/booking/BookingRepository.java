package com.eventsphere.booking;
import org.springframework.data.mongodb.repository.MongoRepository; import java.util.*;
public interface BookingRepository extends MongoRepository<Booking,String>{List<Booking> findByUserIdOrderByBookingDateDesc(Long userId);}
