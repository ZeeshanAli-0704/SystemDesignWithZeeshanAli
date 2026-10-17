package org.example.strategy.parking;

import org.example.parkingspot.compatibility.SpotCompatibilityChecker;
import org.example.domain.model.enums.VehicleType;
import org.example.parkingspot.ParkingSpot;

import java.util.List;

public interface ParkingStrategy {
    ParkingSpot findParkingSpot(List<ParkingSpot> parkingSpotList, VehicleType type, SpotCompatibilityChecker spotCompatibilityChecker);
}
