package org.example.parkingspot;

import org.example.domain.model.enums.VehicleType;

public class LargeVehicleParkingSpot extends AbstractParkingSpot {
    public LargeVehicleParkingSpot(int floor, int dist) {
        super(floor, dist);
    }

    protected boolean isCompatible(VehicleType vehicleType) {
        return vehicleType == VehicleType.MINI;
    };
}
