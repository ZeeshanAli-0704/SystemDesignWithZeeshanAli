package org.example.strategy.cost;


import org.example.domain.model.Ticket;

import java.time.LocalDateTime;

public interface StrategySelector {

    boolean supports(Ticket ticket, LocalDateTime localDateTime);
    CostComputation getStrategy();
}
