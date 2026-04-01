package com.parking.service;

import com.parking.model.Booking;
import com.parking.model.ParkingSlot;
import com.parking.model.User;
import com.parking.repository.BookingRepository;
import com.parking.repository.ParkingSlotRepository;
import com.parking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {
    @Autowired private BookingRepository bookingRepository;
    @Autowired private ParkingSlotRepository slotRepository;
    @Autowired private UserRepository userRepository;

    @Transactional
    public Booking createBooking(Long userId, Long slotId, String vehicleNo, String notes) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        ParkingSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found"));
        if (slot.getStatus() != ParkingSlot.SlotStatus.AVAILABLE)
            throw new RuntimeException("Slot is not available!");
        slot.setStatus(ParkingSlot.SlotStatus.OCCUPIED);
        slotRepository.save(slot);
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setParkingSlot(slot);
        booking.setVehicleNo(vehicleNo);
        booking.setNotes(notes);
        booking.setStatus(Booking.BookingStatus.ACTIVE);
        booking.setStartTime(LocalDateTime.now());
        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking checkout(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        if (booking.getStatus() != Booking.BookingStatus.ACTIVE)
            throw new RuntimeException("Booking is not active");
        LocalDateTime endTime = LocalDateTime.now();
        booking.setEndTime(endTime);
        long minutes = Duration.between(booking.getStartTime(), endTime).toMinutes();
        double hours = Math.max(1.0, Math.ceil(minutes / 60.0));
        booking.setTotalAmount(Math.round(hours * booking.getParkingSlot().getPricePerHour() * 100.0) / 100.0);
        booking.setStatus(Booking.BookingStatus.COMPLETED);
        ParkingSlot slot = booking.getParkingSlot();
        slot.setStatus(ParkingSlot.SlotStatus.AVAILABLE);
        slotRepository.save(slot);
        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        ParkingSlot slot = booking.getParkingSlot();
        slot.setStatus(ParkingSlot.SlotStatus.AVAILABLE);
        slotRepository.save(slot);
        return bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() { return bookingRepository.findAll(); }
    public List<Booking> getUserBookings(Long uid) { return bookingRepository.findByUserIdOrderByCreatedAtDesc(uid); }
    public List<Booking> getActiveBookings() { return bookingRepository.findByStatus(Booking.BookingStatus.ACTIVE); }
    public Double getTotalRevenue() { return bookingRepository.getTotalRevenue(); }
    public long getTodayBookingCount() { return bookingRepository.countTodayBookings(); }
}
