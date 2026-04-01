package com.parking.dto;

import com.parking.model.Booking;
import com.parking.model.ParkingSlot;
import com.parking.model.User;
import java.time.LocalDateTime;

public class DTOs {

    public static class RegisterRequest {
        public String name;
        public String email;
        public String password;
        public String phone;
        public String vehicleNo;
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getPassword() { return password; }
        public String getPhone() { return phone; }
        public String getVehicleNo() { return vehicleNo; }
    }

    public static class LoginRequest {
        public String email;
        public String password;
        public String getEmail() { return email; }
        public String getPassword() { return password; }
    }

    public static class AuthResponse {
        public String token;
        public String type = "Bearer";
        public Long id;
        public String name;
        public String email;
        public String role;
        public String vehicleNo;
        public AuthResponse(String token, User user) {
            this.token = token;
            this.id = user.getId();
            this.name = user.getName();
            this.email = user.getEmail();
            this.role = user.getRole().name();
            this.vehicleNo = user.getVehicleNo();
        }
    }

    public static class SlotResponse {
        public Long id;
        public String slotNumber;
        public Integer floor;
        public String section;
        public String type;
        public String status;
        public Double pricePerHour;
        public SlotResponse(ParkingSlot slot) {
            this.id = slot.getId();
            this.slotNumber = slot.getSlotNumber();
            this.floor = slot.getFloor();
            this.section = slot.getSection();
            this.type = slot.getType().name();
            this.status = slot.getStatus().name();
            this.pricePerHour = slot.getPricePerHour();
        }
    }

    public static class BookingRequest {
        public Long slotId;
        public String vehicleNo;
        public String notes;
        public Long getSlotId() { return slotId; }
        public String getVehicleNo() { return vehicleNo; }
        public String getNotes() { return notes; }
    }

    public static class BookingResponse {
        public Long id;
        public String slotNumber;
        public String section;
        public Integer floor;
        public String vehicleNo;
        public LocalDateTime startTime;
        public LocalDateTime endTime;
        public Double totalAmount;
        public String status;
        public String userName;
        public Double pricePerHour;
        public BookingResponse(Booking b) {
            this.id = b.getId();
            this.slotNumber = b.getParkingSlot().getSlotNumber();
            this.section = b.getParkingSlot().getSection();
            this.floor = b.getParkingSlot().getFloor();
            this.vehicleNo = b.getVehicleNo();
            this.startTime = b.getStartTime();
            this.endTime = b.getEndTime();
            this.totalAmount = b.getTotalAmount();
            this.status = b.getStatus().name();
            this.userName = b.getUser().getName();
            this.pricePerHour = b.getParkingSlot().getPricePerHour();
        }
    }

    public static class DashboardStats {
        public long totalSlots;
        public long availableSlots;
        public long occupiedSlots;
        public long totalBookingsToday;
        public double totalRevenue;
        public long totalUsers;
    }
}
