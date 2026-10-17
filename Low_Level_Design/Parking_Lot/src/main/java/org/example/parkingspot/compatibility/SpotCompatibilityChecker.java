package org.example.parkingspot.compatibility;

import org.example.parkingspot.ParkingSpot;
import org.example.domain.model.enums.VehicleType;

public interface SpotCompatibilityChecker {
    boolean isCompatible(ParkingSpot spot, VehicleType vehicleType);
}
