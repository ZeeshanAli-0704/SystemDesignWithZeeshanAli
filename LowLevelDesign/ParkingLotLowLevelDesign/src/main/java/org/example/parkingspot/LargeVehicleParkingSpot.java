package org.example.parkingspot;

import org.example.domain.model.enums.VehicleType;

public class LargeVehicleParkingSpot extends AbstractParkingSpot {
    public LargeVehicleParkingSpot(int floor, int dist) {
        super(floor, dist);
    }

    @Override
    public void occupy(VehicleType vehicleType) {
        if (vehicleType == VehicleType.LARGE) {
            isOccupied = true;
        }
    }
}
