package com.parking.controller;

import com.parking.dto.DTOs.*;
import com.parking.model.Booking;
import com.parking.model.ParkingSlot;
import com.parking.model.User;
import com.parking.repository.ParkingSlotRepository;
import com.parking.repository.UserRepository;
import com.parking.service.BookingService;
import com.parking.service.ParkingSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ParkingSlotController {

    @Autowired private ParkingSlotService slotService;
    @Autowired private ParkingSlotRepository slotRepository;

    @GetMapping("/slots")
    public ResponseEntity<List<SlotResponse>> getAllSlots() {
        return ResponseEntity.ok(slotService.getAllSlots().stream()
                .map(SlotResponse::new).collect(Collectors.toList()));
    }

    @GetMapping("/slots/available")
    public ResponseEntity<List<SlotResponse>> getAvailableSlots(
            @RequestParam(required = false) String type) {
        List<ParkingSlot> slots = type != null
                ? slotService.getAvailableSlotsByType(ParkingSlot.SlotType.valueOf(type.toUpperCase()))
                : slotService.getAvailableSlots();
        return ResponseEntity.ok(slots.stream().map(SlotResponse::new).collect(Collectors.toList()));
    }

    @PostMapping("/admin/slots")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SlotResponse> createSlot(@RequestBody ParkingSlot slot) {
        return ResponseEntity.ok(new SlotResponse(slotService.createSlot(slot)));
    }

    @PutMapping("/admin/slots/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SlotResponse> updateStatus(
            @PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(new SlotResponse(
                slotService.updateSlotStatus(id,
                        ParkingSlot.SlotStatus.valueOf(status.toUpperCase()))));
    }

    @DeleteMapping("/admin/slots/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteSlot(@PathVariable Long id) {
        slotService.deleteSlot(id);
        return ResponseEntity.ok("Slot deleted");
    }
}

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
class BookingController {

    @Autowired private BookingService bookingService;
    @Autowired private UserRepository userRepository;
    @Autowired private ParkingSlotRepository slotRepository;

    @PostMapping("/bookings")
    public ResponseEntity<?> createBooking(
            @RequestBody BookingRequest request, Authentication auth) {
        try {
            User user = userRepository.findByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return ResponseEntity.ok(new BookingResponse(
                    bookingService.createBooking(user.getId(),
                            request.getSlotId(), request.getVehicleNo(), request.getNotes())));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/bookings/{id}/checkout")
    public ResponseEntity<?> checkout(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(new BookingResponse(bookingService.checkout(id)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/bookings/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(new BookingResponse(bookingService.cancelBooking(id)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/bookings/my")
    public ResponseEntity<List<BookingResponse>> myBookings(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(bookingService.getUserBookings(user.getId())
                .stream().map(BookingResponse::new).collect(Collectors.toList()));
    }

    @GetMapping("/admin/bookings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookingResponse>> allBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings()
                .stream().map(BookingResponse::new).collect(Collectors.toList()));
    }

    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardStats> dashboard() {
        DashboardStats stats = new DashboardStats();
        stats.totalSlots = slotRepository.count();
        stats.availableSlots = slotRepository.countByStatus(ParkingSlot.SlotStatus.AVAILABLE);
        stats.occupiedSlots = slotRepository.countByStatus(ParkingSlot.SlotStatus.OCCUPIED);
        stats.totalBookingsToday = bookingService.getTodayBookingCount();
        stats.totalRevenue = bookingService.getTotalRevenue();
        stats.totalUsers = userRepository.count();
        return ResponseEntity.ok(stats);
    }
}
