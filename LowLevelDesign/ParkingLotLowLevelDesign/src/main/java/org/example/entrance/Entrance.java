package org.example.entrance;

import org.example.strategy.cost.CostComputation;
import org.example.parkingspot.ParkingSpot;
import org.example.spotmanager.ParkingSpotManager;
import org.example.domain.model.Ticket;
import org.example.domain.model.Vehicle;

import java.time.LocalDateTime;

public class Entrance {
    private final String id;
    private final String name;
    private ParkingSpotManager manager;

    public Entrance(String id, String name, ParkingSpotManager manager) {
        this.id = id;
        this.name = name;
        this.manager = manager;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Ticket bookSpotAndGiveTicket(Vehicle vehicle) {
        ParkingSpot spot = manager.findParkingSpot(vehicle.getVehicleType());
        if (spot != null) {
            spot.occupy(vehicle.getVehicleType());
            return new Ticket.Builder()
                    .floorNo(spot.getFloorNumber())
                    .time(LocalDateTime.now())
                    .vehicleType(vehicle.getVehicleType())
                    .parkingSpot(spot)
                    .build();
        }
        return null;
    }
}
