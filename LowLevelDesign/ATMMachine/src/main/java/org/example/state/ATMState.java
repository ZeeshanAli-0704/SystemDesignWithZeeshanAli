package org.example.state;

import org.example.ATMMachineContext;

public interface ATMState {
    String getStateName();
    // Method to handle state transitions/
     ATMState next(ATMMachineContext context);
}
