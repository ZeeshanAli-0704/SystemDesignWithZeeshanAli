package org.example.domain.model;

import org.example.domain.model.enums.VehicleType;
import org.example.parkingspot.ParkingSpot;

import java.time.LocalDateTime;

public class Ticket {
    private final int floorNo;
    private final LocalDateTime time;
    private final VehicleType vehicleType;
    private final ParkingSpot parkingSpot;

    private Ticket(Builder builder) {
        this.floorNo = builder.floorNo;
        this.time = builder.time;
        this.vehicleType = builder.vehicleType;
        this.parkingSpot = builder.parkingSpot;
    }

    public int getFloorNo() {
        return floorNo;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public ParkingSpot getParkingSpot() {
        return parkingSpot;
    }


    public static class Builder {
        private int floorNo;
        private LocalDateTime time;
        private VehicleType vehicleType;
        private ParkingSpot parkingSpot;

        public Builder floorNo(int floorNo) {
            this.floorNo = floorNo;
            return this;
        }

        public Builder time(LocalDateTime time) {
            this.time = time;
            return this;
        }

        public Builder vehicleType(VehicleType vehicleType) {
            this.vehicleType = vehicleType;
            return this;
        }

        public Builder parkingSpot(ParkingSpot parkingSpot) {
            this.parkingSpot = parkingSpot;
            return this;
        }


        public Ticket build() {
            return new Ticket(this);
        }
    }
}
