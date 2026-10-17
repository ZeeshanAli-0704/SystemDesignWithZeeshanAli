package org.example.strategy.cost;
import org.example.domain.model.Ticket;
import java.time.Duration;
import java.time.LocalDateTime;

public class MinuteBasedSelector implements StrategySelector {
    @Override
    public boolean supports(Ticket ticket, LocalDateTime exitTime) {
        long minutes = Duration.between(ticket.getTime(), exitTime).toMinutes();
        return minutes <= 60;
    }

    @Override
    public CostComputation getStrategy() {
        return new MinuteBasedStrategy();
    }
}
