package org.example.model.food;

import java.util.UUID;

public class FoodItem {
    private final String foodId;
    private String name;
    private double price;
    private FoodType type;
    private String description;

    public FoodItem(String name, double price, FoodType type, String description){
        this.foodId = UUID.randomUUID().toString();
        this.name = name;
        this.price = price;
        this.type = type;
        this.description = description;
    };

    public String getFoodId() {
        return foodId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public FoodType getType() {
        return type;
    }

    public void setType(FoodType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
