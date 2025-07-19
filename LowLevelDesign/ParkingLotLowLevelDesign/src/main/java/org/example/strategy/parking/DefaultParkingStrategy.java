package org.example.strategy.parking;

import org.example.parkingspot.ParkingSpot;
import org.example.parkingspot.compatibility.SpotCompatibilityChecker;
import org.example.domain.model.enums.VehicleType;

import java.util.List;

public class DefaultParkingStrategy implements ParkingStrategy {
    @Override
    public ParkingSpot findParkingSpot(List<ParkingSpot> parkingSpotList, VehicleType vehicleType, SpotCompatibilityChecker spotCompatibilityChecker) {
        for (ParkingSpot spot : parkingSpotList) {
            if (spot.isEmpty() && spotCompatibilityChecker.isCompatible(spot, vehicleType)) return spot;
        }
        return null;
    }
}
