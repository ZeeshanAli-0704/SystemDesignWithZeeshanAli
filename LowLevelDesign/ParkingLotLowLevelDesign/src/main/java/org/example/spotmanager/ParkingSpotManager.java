package org.example.spotmanager;

import org.example.floor.Floor;
import org.example.strategy.parking.ParkingStrategy;
import org.example.parkingspot.compatibility.DefaultSpotCompatibilityChecker;
import org.example.parkingspot.compatibility.SpotCompatibilityChecker;
import org.example.domain.model.enums.VehicleType;
import org.example.parkingspot.ParkingSpot;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ParkingSpotManager {
    private final Map<Integer, Floor> floorMap = new ConcurrentHashMap<Integer, Floor>();
    private ParkingStrategy parkingStrategy;
    private SpotCompatibilityChecker compatibilityChecker = new DefaultSpotCompatibilityChecker();


    public void setParkingStrategy(ParkingStrategy parkingStrategy) {
        this.parkingStrategy = parkingStrategy;
    };


    public void addFloor(Floor floor){
        if(floorMap.containsKey(floor.getFloorNumber())){
            throw new IllegalArgumentException("Floor " + floor.getFloorNumber() + " already exists.");
        }
        floorMap.put(floor.getFloorNumber(), floor);
    };

    public Floor getFloor(int floorNumber) {
        return floorMap.get(floorNumber);
    }

    public ParkingSpot findParkingSpot(VehicleType vehicleType) {
        if (parkingStrategy == null) {
            throw new IllegalStateException("Parking strategy not set");
        }
        for (Floor floor : floorMap.values()) {
            ParkingSpot spot = parkingStrategy.findParkingSpot(floor.getParkingSpotList(), vehicleType, compatibilityChecker);
            if (spot != null) return spot;
        }
        return null;
    }

    public void addSpotToFloor(int floorNum, ParkingSpot spot) {
        Floor floor = floorMap.get(floorNum);
        if(floor != null){
        
            floor.addSpot(spot);
            System.out.println("Spot added: " + spot.getSpotId());
        }else{
            throw new IllegalArgumentException("No Such Floor Available "+ floorNum);
        }

    }

    public Collection<Floor> getAllFloors() {
        return floorMap.values();
    }
}
