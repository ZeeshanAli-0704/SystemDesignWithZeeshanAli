package org.example.state;

import org.example.ATMMachineContext;

public class TransactionState implements ATMState {
    public TransactionState() {

    }

    @Override
    public String getStateName() {
        return "Transaction State";
    }

    @Override
    public ATMState next(ATMMachineContext context) {
        if (context.getCurrentCard() == null) {
            return context.getStateFactory().createIdleState();
        };

        // After transaction completion, go back to select operation
        return context.getStateFactory().createSelectOperationState();
    }
}
