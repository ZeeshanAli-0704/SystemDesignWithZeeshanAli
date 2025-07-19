package org.example.strategy.cost;

import org.example.domain.model.Ticket;
import org.example.domain.model.enums.VehicleType;

import java.time.Duration;
import java.time.LocalDateTime;

public class HourBasedStrategy implements CostComputation {
    @Override
    public int calculateCostForTicket(VehicleType vehicleType, Ticket ticket, LocalDateTime exitTime) {
        long hours = Duration.between(ticket.getTime(), exitTime).toHours();
        return switch (vehicleType) {
            case MINI -> (int) hours * 50;
            case COMPACT -> (int) hours * 100;
            case LARGE ->  (int) hours * 150;
        };
    }
}
