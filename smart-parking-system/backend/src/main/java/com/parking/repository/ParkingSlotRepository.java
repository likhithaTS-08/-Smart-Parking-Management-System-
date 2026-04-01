package com.parking.repository;

import com.parking.model.ParkingSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {
    List<ParkingSlot> findByStatus(ParkingSlot.SlotStatus status);
    List<ParkingSlot> findByStatusAndType(ParkingSlot.SlotStatus status, ParkingSlot.SlotType type);
    Optional<ParkingSlot> findBySlotNumber(String slotNumber);
    long countByStatus(ParkingSlot.SlotStatus status);
}
