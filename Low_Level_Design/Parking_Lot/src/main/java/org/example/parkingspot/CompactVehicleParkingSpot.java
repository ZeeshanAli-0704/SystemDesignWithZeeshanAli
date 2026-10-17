package org.example.parkingspot;

import org.example.domain.model.enums.VehicleType;

public class CompactVehicleParkingSpot extends AbstractParkingSpot {

    public CompactVehicleParkingSpot(int floor, int dist) {
        super(floor, dist);
    }

    protected boolean isCompatible(VehicleType vehicleType) {
        return vehicleType == VehicleType.MINI;
    };
}
