package org.example.parkingspot;

import org.example.domain.model.enums.VehicleType;

public class CompactVehicleParkingSpot extends AbstractParkingSpot {

    public CompactVehicleParkingSpot(int floor, int dist) {
        super(floor, dist);
    }

    @Override
    public void occupy(VehicleType vehicleType) {
        if (vehicleType == VehicleType.COMPACT) {
            isOccupied = true;
        }
    }
}
