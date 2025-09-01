package org.example.models;

import org.example.enums.BookingStatus;
import org.example.user.User;

import java.util.List;

public class Booking {
    private final String id;
    private BookingStatus bookingStatus;
    private final Show show;
    private Screen screen;
    private final List<Seat> seatsBooked;
    private final User user;

    public Booking(final String id, final Show show, final User user, final List<Seat> seatsBooked) {
        this.id = id;
        this.show = show;
        this.seatsBooked = seatsBooked;
        this.user = user;
        this.bookingStatus = BookingStatus.CREATED; // Initial booking status is set to Created.
    }

    public boolean isConfirmed(){
        return this.bookingStatus == BookingStatus.CONFIRMED;
    };

    public void confirmBooking() throws Exception {
        if (this.bookingStatus != BookingStatus.CREATED) {
            throw new Exception("Cannot confirm a booking that is not in the Created state.");
        }
        this.bookingStatus = BookingStatus.CONFIRMED; // Update the booking status to Confirmed.
    };

    public void expireBooking() throws Exception {
        if (this.bookingStatus != BookingStatus.CREATED) {
            throw new Exception("Cannot expire a booking that is not in the Created state.");
        }
        this.bookingStatus = BookingStatus.EXPIRED; // Update the booking status to expire.
    }

    public String getId() {
        return id;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public Show getShow() {
        return show;
    }

    public List<Seat> getSeatsBooked() {
        return seatsBooked;
    }

    public User getUser() {
        return user;
    }
}