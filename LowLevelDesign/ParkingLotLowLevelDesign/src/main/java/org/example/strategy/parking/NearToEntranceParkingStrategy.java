package org.example.strategy.parking;

import org.example.parkingspot.ParkingSpot;
import org.example.parkingspot.compatibility.SpotCompatibilityChecker;
import org.example.domain.model.enums.VehicleType;

import java.util.Comparator;
import java.util.List;

public class NearToEntranceParkingStrategy implements ParkingStrategy {

    @Override
    public ParkingSpot findParkingSpot(List<ParkingSpot> spots, VehicleType vehicleType, SpotCompatibilityChecker spotCompatibilityChecker) {
        if (spots == null || spots.isEmpty()) return null;

        return spots.stream()
                .filter(spot ->spot.isEmpty() && spotCompatibilityChecker.isCompatible(spot, vehicleType))
               .min(Comparator.comparingInt(ParkingSpot::getDistanceFromEntrance))
                .orElse(null);
    }


};
