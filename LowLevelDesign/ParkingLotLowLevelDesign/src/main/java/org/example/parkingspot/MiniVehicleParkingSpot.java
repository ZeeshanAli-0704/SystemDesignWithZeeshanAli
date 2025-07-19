package org.example.parkingspot;

import org.example.domain.model.enums.VehicleType;

public class MiniVehicleParkingSpot extends AbstractParkingSpot {

    public MiniVehicleParkingSpot(int floor, int dist) {
        super(floor, dist);
    }

    @Override
    public void occupy(VehicleType vehicleType) {
        if (vehicleType == VehicleType.MINI) {
            isOccupied = true;
        }
    }

}
