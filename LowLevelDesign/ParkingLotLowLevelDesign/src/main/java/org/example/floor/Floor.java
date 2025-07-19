package org.example.floor;

import org.example.parkingspot.ParkingSpot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Floor {
    private int floorNumber;
    private List<ParkingSpot> parkingSpotList;

    public Floor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.parkingSpotList =  new ArrayList<ParkingSpot>();
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public void addSpot(ParkingSpot spot){
        this.parkingSpotList.add(spot);
    }

    public ParkingSpot removeSpot(String spotId) {
        Iterator<ParkingSpot> iterator = this.parkingSpotList.iterator();
        while (iterator.hasNext()) {
            ParkingSpot spot = iterator.next();
            if (spot.getSpotId().equals(spotId)) {
                iterator.remove();
                return spot;
            }
        }
        return null;
    }

    public List<ParkingSpot> getParkingSpotList() {
        return parkingSpotList;
    }
}
