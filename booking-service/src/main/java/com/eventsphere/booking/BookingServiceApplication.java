package com.eventsphere.booking;
import org.springframework.boot.*; import org.springframework.boot.autoconfigure.SpringBootApplication; import org.springframework.cloud.client.discovery.EnableDiscoveryClient; import org.springframework.cloud.openfeign.EnableFeignClients;
@EnableDiscoveryClient @EnableFeignClients @SpringBootApplication public class BookingServiceApplication{public static void main(String[]a){SpringApplication.run(BookingServiceApplication.class,a);}}
