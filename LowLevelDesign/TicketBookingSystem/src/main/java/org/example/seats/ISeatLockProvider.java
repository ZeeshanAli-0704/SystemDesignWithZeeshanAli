package org.example.seats;

import org.example.models.Seat;
import org.example.models.Show;
import org.example.user.User;

import java.util.List;

public interface ISeatLockProvider {
    default void lockSeats(Show show, List<Seat> seat, User user) throws Exception {

    }

    void unlockSeats(Show show, List<Seat> seat, User user);
    boolean validateLock(Show show, Seat seat, User user);
    List<Seat> getLockedSeats(Show show);
}
