package org.example.state;

public class ATMStateFactory {
    private static ATMStateFactory instance = null;

    public static ATMStateFactory getInstance(){
        if(instance == null){
            instance= new ATMStateFactory();
        }
        return instance;
    };

    public ATMState createIdleState() {
        return new IdleState();
    }
    public ATMState createHasCardState(){
        return new HasCardState();
    }

    public ATMState createSelectOperationState(){
        return new SelectOperationState();
    }

    public ATMState createTransactionState(){
        return new TransactionState();
    }



}
