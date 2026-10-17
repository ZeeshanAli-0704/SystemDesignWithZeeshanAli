package org.example.parkingspot;

import org.example.domain.model.enums.VehicleType;

public interface ParkingSpot {
    String getSpotId();
    boolean isEmpty();
    boolean tryOccupy(VehicleType vehicleType);
    void vacateParkingSpot();
    int getFloorNumber();
    int getDistanceFromEntrance();
}
