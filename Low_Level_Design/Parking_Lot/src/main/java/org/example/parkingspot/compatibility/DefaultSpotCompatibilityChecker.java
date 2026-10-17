package org.example.parkingspot.compatibility;
import org.example.parkingspot.*;
import org.example.domain.model.enums.VehicleType;

public class DefaultSpotCompatibilityChecker implements SpotCompatibilityChecker {
    @Override
    public boolean isCompatible(ParkingSpot spot, VehicleType vehicleType) {
        switch (vehicleType) {
            case MINI:
                return spot instanceof MiniVehicleParkingSpot;
            case COMPACT:
                return spot instanceof CompactVehicleParkingSpot;
            case LARGE:
                return spot instanceof LargeVehicleParkingSpot;
            default:
                return false;
        }
    }
}

