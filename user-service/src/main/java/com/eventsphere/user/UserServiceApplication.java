package com.eventsphere.user;
import org.springframework.boot.*; import org.springframework.boot.autoconfigure.SpringBootApplication; import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
@EnableDiscoveryClient @SpringBootApplication public class UserServiceApplication{public static void main(String[]a){SpringApplication.run(UserServiceApplication.class,a);}}
