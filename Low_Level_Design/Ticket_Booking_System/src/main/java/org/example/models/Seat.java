package org.example.models;

import org.example.enums.SeatCategory;

public class Seat {
    private final int id;
    private final int row;
    private final int seatCost;
    private final SeatCategory seatCategory;

    public Seat(int id, int row, SeatCategory seatCategory, int seatCost) {
        this.id = id;
        this.row = row;
        this.seatCategory = seatCategory;
        this.seatCost = seatCost;
    }

    public int getId() {
        return id;
    }

    public int getRow() {
        return row;
    }

    public SeatCategory getSeatCategory() {
        return seatCategory;
    }


}
