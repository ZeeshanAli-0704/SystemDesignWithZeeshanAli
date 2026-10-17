package org.example.domain.model;

import org.example.domain.model.enums.VehicleType;

public class Vehicle {
    private final VehicleType vehicleType;
    private final String vehicleNumber;
    private final String ownerName;         // Optional
    private final String insurancePolicy;   // Optional

    private Vehicle(Builder builder) {
        this.vehicleType = builder.vehicleType;
        this.vehicleNumber = builder.vehicleNumber;
        this.ownerName = builder.ownerName;
        this.insurancePolicy = builder.insurancePolicy;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getInsurancePolicy() {
        return insurancePolicy;
    }

    public static class Builder {
        private VehicleType vehicleType;
        private String vehicleNumber;
        private String ownerName;
        private String insurancePolicy;

        public Builder vehicleType(VehicleType vehicleType) {
            this.vehicleType = vehicleType;
            return this;
        }

        public Builder vehicleNumber(String vehicleNumber) {
            this.vehicleNumber = vehicleNumber;
            return this;
        }

        public Builder ownerName(String ownerName) {
            this.ownerName = ownerName;
            return this;
        }

        public Builder insurancePolicy(String insurancePolicy) {
            this.insurancePolicy = insurancePolicy;
            return this;
        }

        public Vehicle build() {
            return new Vehicle(this);
        }
    }
}
