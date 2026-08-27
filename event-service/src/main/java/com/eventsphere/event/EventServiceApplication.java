package com.eventsphere.event;
import org.springframework.boot.*; import org.springframework.boot.autoconfigure.SpringBootApplication; import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
@EnableDiscoveryClient @SpringBootApplication public class EventServiceApplication{public static void main(String[]a){SpringApplication.run(EventServiceApplication.class,a);}}
