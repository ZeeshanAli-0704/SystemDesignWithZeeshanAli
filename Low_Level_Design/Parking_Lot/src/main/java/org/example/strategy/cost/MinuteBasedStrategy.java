package org.example.strategy.cost;

import org.example.domain.model.Ticket;
import org.example.domain.model.enums.VehicleType;

import java.time.Duration;
import java.time.LocalDateTime;

public class MinuteBasedStrategy implements CostComputation {
    @Override
    public int calculateCostForTicket(VehicleType vehicleType, Ticket ticket, LocalDateTime exitTime) {
        long minutes = Duration.between(ticket.getTime(), exitTime).toMinutes();
        return switch (vehicleType) {
            case MINI ->  (int) minutes * 2;
            case COMPACT -> (int)  minutes * 3;
            case LARGE -> (int)  minutes * 5;
        };
    }
}
