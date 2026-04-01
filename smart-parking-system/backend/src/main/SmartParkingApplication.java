package com.parking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SmartParkingApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartParkingApplication.class, args);
        System.out.println("\n==========================================");
        System.out.println("  Smart Parking System is running!");
        System.out.println("  API: http://localhost:8080/api");
        System.out.println("==========================================\n");
    }
}
