package com.parking.service;

import com.parking.model.ParkingSlot;
import com.parking.repository.ParkingSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ParkingSlotService {
    @Autowired
    private ParkingSlotRepository slotRepository;

    public List<ParkingSlot> getAllSlots() { return slotRepository.findAll(); }

    public List<ParkingSlot> getAvailableSlots() {
        return slotRepository.findByStatus(ParkingSlot.SlotStatus.AVAILABLE);
    }

    public List<ParkingSlot> getAvailableSlotsByType(ParkingSlot.SlotType type) {
        return slotRepository.findByStatusAndType(ParkingSlot.SlotStatus.AVAILABLE, type);
    }

    public Optional<ParkingSlot> getSlotById(Long id) { return slotRepository.findById(id); }

    public ParkingSlot createSlot(ParkingSlot slot) { return slotRepository.save(slot); }

    public ParkingSlot updateSlotStatus(Long id, ParkingSlot.SlotStatus status) {
        ParkingSlot slot = slotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Slot not found: " + id));
        slot.setStatus(status);
        return slotRepository.save(slot);
    }

    public void deleteSlot(Long id) { slotRepository.deleteById(id); }

    public long countAvailable() { return slotRepository.countByStatus(ParkingSlot.SlotStatus.AVAILABLE); }
    public long countOccupied() { return slotRepository.countByStatus(ParkingSlot.SlotStatus.OCCUPIED); }
}
