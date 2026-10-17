package org.example.strategy.cost;

import org.example.domain.model.Ticket;
import org.example.domain.model.enums.VehicleType;

import java.time.LocalDateTime;

public interface CostComputation {
    int calculateCostForTicket(VehicleType vehicleType, Ticket ticket, LocalDateTime exitTime);
}
