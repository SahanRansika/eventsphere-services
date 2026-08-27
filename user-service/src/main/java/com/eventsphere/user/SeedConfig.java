package com.eventsphere.user;
import org.springframework.boot.CommandLineRunner; import org.springframework.context.annotation.Bean; import org.springframework.context.annotation.Configuration;
@Configuration public class SeedConfig{@Bean CommandLineRunner seed(UserService s){return a->s.seed();}}
