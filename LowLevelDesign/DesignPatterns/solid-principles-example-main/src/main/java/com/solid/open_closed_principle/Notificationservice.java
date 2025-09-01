package com.solid.open_closed_principle;

public interface Notificationservice {

    public void sendOTP(String medium);

    public void sendTransactionReport(String medium);

}
