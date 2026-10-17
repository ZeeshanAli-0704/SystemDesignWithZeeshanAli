package org.example.model.restaurant;

import org.example.model.food.FoodItem;
import org.example.model.user.User;

import java.util.*;

public class Restaurant {
    private final String id;
    private final String name;
    private final String location;
    private final User owner;
    private final int pincode;
    private final Set<Integer> serviceablePincodes;
    private Map<String, FoodItem> menu;


    public Restaurant(String name, String location, User owner, int pincode) {
        this.id = "REST-" + UUID.randomUUID().toString();
        this.name = name;
        this.location = location;
        this.owner = owner;
        this.pincode = pincode;
        this.serviceablePincodes = new HashSet<>();
        this.menu = new LinkedHashMap<>(); // preserves insertion order
    };

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public User getOwner() {
        return owner;
    };

    public int getPincode() {
        return pincode;
    };

    public void addServiceablePincode(int pincode){
        serviceablePincodes.add(pincode);
    }

    public void removeServiceablePincode(int pincode){
        serviceablePincodes.remove(pincode);
    }
    // Menu-related methods
    public void addFoodItem(FoodItem food) {
        menu.put(food.getFoodId(), food);
    }

    public void removeFoodItem(String foodId) {
        menu.remove(foodId);
    }

    public List<FoodItem> getMenu() {
        return new ArrayList<>(menu.values());
    }

    public Set<Integer> getServiceablePincodes() {
        return serviceablePincodes;
    }
}
