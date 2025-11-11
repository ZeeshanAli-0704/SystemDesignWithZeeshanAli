package org.example.model.user;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DeliveryAgent extends User{


    public DeliveryAgent(String userName, String email, String city, GenderType gender) {
        super(UserType.DELIVERY_AGENT, userName, email, city, gender);
    };

    public String getAgentId() {
        return getUserId();
    }
}
