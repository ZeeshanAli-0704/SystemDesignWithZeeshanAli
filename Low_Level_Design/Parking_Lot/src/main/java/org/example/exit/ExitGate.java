package org.example.exit;

import org.example.strategy.cost.CostComputation;
import org.example.strategy.cost.CostComputationStrategyResolver;
import org.example.domain.model.Ticket;
import org.example.domain.model.enums.VehicleType;

import java.time.LocalDateTime;

public class ExitGate {
    private String id;
    private String name;

    public ExitGate(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
         return id; 
        }
    public String getName() { 
        return name; 
    }

    public int processExitAndReturnCost(Ticket ticket, LocalDateTime exitTime){
        VehicleType vehicleType = ticket.getVehicleType();

        CostComputationStrategyResolver resolver = new CostComputationStrategyResolver();
        CostComputation strategy = resolver.resolveStrategy(ticket, exitTime);
        return strategy.calculateCostForTicket(vehicleType, ticket, exitTime);
    }

    public void vacateParking(Ticket ticket) {
        ticket.getParkingSpot().vacateParkingSpot();
    }
}

