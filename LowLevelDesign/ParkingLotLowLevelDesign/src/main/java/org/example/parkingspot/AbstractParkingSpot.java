package org.example.parkingspot;

import java.util.UUID;

public abstract class AbstractParkingSpot implements ParkingSpot {

    protected final String spotId;
    protected boolean isOccupied;
    protected int floorNumber;
    protected int price;
    protected int distanceFromEntrance;

    public AbstractParkingSpot(int floorNumber, int distanceFromEntrance) {
        this.spotId = UUID.randomUUID().toString(); //  auto-generate
        this.floorNumber = floorNumber;
        this.distanceFromEntrance = distanceFromEntrance;
        this.isOccupied = false;
    }

    @Override
    public boolean isEmpty() {
        return !isOccupied;
    }

    @Override
    public void vacateParkingSpot() {
        isOccupied = false;
    }

    @Override
    public int getFloorNumber() {
        return floorNumber;
    }

    @Override
    public int getDistanceFromEntrance() {
        return distanceFromEntrance;
    }

    @Override
    public String getSpotId() {
        return spotId;
    };

    @Override
    public String toString() {
        return "SpotID: " + spotId + ", Floor: " + floorNumber + ", Distance: " + distanceFromEntrance;
    }

}
