package org.example.parkingspot;

import org.example.domain.model.enums.VehicleType;

public interface ParkingSpot {
    String getSpotId();
    boolean isEmpty();
    void occupy(VehicleType vehicleType);
    void vacateParkingSpot();
    int getFloorNumber();
    int getDistanceFromEntrance();
}
