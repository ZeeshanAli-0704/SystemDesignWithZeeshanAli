package org.example.seats;

import org.example.models.Seat;
import org.example.models.Show;
import org.example.user.SeatLock;
import org.example.user.User;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SeatLockProvider implements ISeatLockProvider{

    private final Integer lockTimeout;
    // Stores the locks per show and seat
    private final Map<Show, Map<Integer, SeatLock>> locks;

    public SeatLockProvider(Integer lockTimeout) {
        this.lockTimeout = lockTimeout;
        this.locks = new ConcurrentHashMap<>();;
    }

    @Override
    public void lockSeats(Show show, List<Seat> seat, User user) throws Exception {
        Map<Integer, SeatLock> seatLocks = locks.computeIfAbsent(show, x -> new ConcurrentHashMap<>());

        synchronized (seatLocks) {
            for (Seat s : seat) {
                if (seatLocks.containsKey(s.getId())) {
                    SeatLock seatLock = seatLocks.get(s.getId());
                    if (!seatLock.isLockExpired()) {
                        throw new Exception("Seat" + s.getId() + "already locked by some user");
                    }
                }
            };

            // now add seats in above seat lock
            Date now = new Date();
            for (Seat s : seat) {
                SeatLock sLock = new SeatLock(s, show, user, lockTimeout, now);
                seatLocks.put(s.getId(), sLock);
            }
        }
    }


    @Override
    public void unlockSeats(Show show, List<Seat> seats, User user) {
        Map<Integer, SeatLock> seatLocks = locks.get(show);
        if (seatLocks == null)
            return;

        synchronized (seatLocks) {
            for (Seat seat : seats) {
                SeatLock lock = seatLocks.get(seat.getId());
                if (lock != null && lock.getLockedBy().equals(user)) {
                    seatLocks.remove(seat.getId());
                }
            }
        }
    }

    @Override
    public boolean validateLock(Show show, Seat seat, User user) {
        Map<Integer, SeatLock> seatLocks = locks.get(show);
        if (seatLocks == null)
            return false;
        synchronized (seatLocks) {
            SeatLock lock = seatLocks.get(seat.getId());
            return lock != null && !lock.isLockExpired()
                    && lock.getLockedBy().equals(user);
        }
    }

    @Override
    public List<Seat> getLockedSeats(Show show) {
        Map<Integer, SeatLock> seatLocks = locks.get(show);
        if (seatLocks == null) {
            return Collections.emptyList();
        }
        synchronized (seatLocks) {
            return seatLocks.entrySet()
                    .stream()
                    .filter(entry -> !entry.getValue().isLockExpired())
                    .map(entry -> entry.getValue().getSeat())
                    .collect(Collectors.toList());
        }
    }
}
