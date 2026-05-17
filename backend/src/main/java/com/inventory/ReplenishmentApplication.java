package com.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ReplenishmentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReplenishmentApplication.class, args);
    }
}
