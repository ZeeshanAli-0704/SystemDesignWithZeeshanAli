package org.example.parkingspot;

import org.example.domain.model.enums.VehicleType;

public class ParkingSpotFactory {
    public static ParkingSpot createSpot(VehicleType type, int floor, int distance) {
        return switch (type) {
            case MINI -> new MiniVehicleParkingSpot(floor, distance);
            case COMPACT -> new CompactVehicleParkingSpot(floor, distance);
            case LARGE -> new LargeVehicleParkingSpot(floor, distance);
        };
    }
}
