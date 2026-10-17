package org.example.strategy.cost;

import org.example.domain.model.Ticket;

import java.time.LocalDateTime;
import java.util.List;

public class CostComputationStrategyResolver {

    private final List<StrategySelector> selectors;

    public CostComputationStrategyResolver() {
        this.selectors = List.of(
                new MinuteBasedSelector(),
                new HourBasedSelector()
        );
    }

    public CostComputation resolveStrategy(Ticket ticket, LocalDateTime exitTime) {
        return selectors.stream()
                .filter(selector -> selector.supports(ticket, exitTime))
                .map(StrategySelector::getStrategy)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No strategy found"));
    }
}
