package org.example;

import org.example.models.Booking;
import org.example.controller.*;
import org.example.enums.SeatCategory;
import org.example.payment.strategy.DebitCardStrategy;
import org.example.seats.ISeatLockProvider;
import org.example.seats.SeatLockProvider;
import org.example.service.*;
import org.example.user.User;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            // Initialize services
            MovieService movieService = new MovieService();
            TheatreService theatreService = new TheatreService();
            ShowService showService = new ShowService();
            // Create a seat lock provider with a 10-minute timeout (600 seconds)
            ISeatLockProvider seatLockProvider = new SeatLockProvider(600);
            // Initialize booking service with the seat lock provider
            BookingService bookingService = new BookingService(seatLockProvider);
            // Initialize seat availability service
            SeatAvailabilityService seatAvailabilityService = new SeatAvailabilityService(bookingService, seatLockProvider);

            // Initialize payment service with a simple payment strategy
            PaymentService paymentService = new PaymentService(new DebitCardStrategy(), bookingService);

            // Initialize controllers
            MovieController movieController = new MovieController(movieService);
            TheatreController theatreController = new TheatreController(theatreService);
            ShowController showController = new ShowController(seatAvailabilityService, showService, theatreService, movieService);
            BookingController bookingController = new BookingController(showService, bookingService, theatreService);
            PaymentController paymentController = new PaymentController(paymentService);

            // Step 1: Create a theatre
            System.out.println("Creating a new theatre...");
            int theatreId = theatreController.createTheatre("PVR Cinemas", "PVR");
            System.out.println("Theatre created with ID: " + theatreId);
            // Step 2: Create a screen in the theatre
            System.out.println("Creating a new screen...");
            int screenId = theatreController.createScreenInTheatre("nrml", "Screen 1", theatreId);
            System.out.println("Screen created with ID: " + screenId);

            // Step 3: Create seats in the screen
            System.out.println("Creating seats...");
            // Create 5 rows with 10 seats each
            for (int row = 1; row <= 5; row++) {
                SeatCategory category;
                if (row == 1) {
                    category = SeatCategory.PLATINUM; // First row is premium
                } else if (row <= 3) {
                    category = SeatCategory.GOLD;     // Next two rows are gold
                } else {
                    category = SeatCategory.SILVER;   // Rest are silver
                };
                int baseCost = 0;
                for (int seatNum = 1; seatNum <= 10; seatNum++) {
                    int seatId = theatreController.createSeatInScreen(row, category, screenId, baseCost);
                    System.out.println("Created seat at row " + row + " with ID: " + seatId + " and category: " + category);
                }
            }

            // Step 4: Create a movie
            System.out.println("nCreating a new movie...");
            int movieId = movieController.createMovie("Inception", 150, "Sci-Fi");
            System.out.println("Movie created with ID: " + movieId);

            // Step 5: Create a show
            System.out.println("nCreating a new show...");
            Date showTime = new Date(); // Current time
            int showId = showController.createShow(movieId, screenId, showTime, 150, "Show Info");
            System.out.println("Show created with ID: " + showId);

            // Step 6: Get available seats for the show
            System.out.println("nChecking available seats...");
            List<Integer> availableSeats = showController.getAvailableSeats(showId);
            System.out.println("Available seats: " + availableSeats);

            // Step 7: Create a user
            System.out.println("nCreating a user...");
            User user = new User("John Doe", "john.doe@example.com");
            System.out.println("User created: " + user.getUserName() + " with email: " + user.getUserEmail());

            // Step 8: Book tickets sequentially
            System.out.println("nSequential booking of seats 1, 2, 3...");
            String bookingId = bookingController.createBooking(user, showId, Arrays.asList(1, 2, 3));
            System.out.println("Booking created with ID: " + bookingId);

            // Step 9: Process payment for the booking
            System.out.println("nProcessing payment...");
            paymentController.processPayment(bookingId, user);
            System.out.println("Payment processed successfully!");

            // Step 10: Verify booking status
            Booking booking = bookingService.getBooking(bookingId);
            System.out.println("nBooking status: " + booking.getBookingStatus());
            System.out.println("Is booking confirmed? " + booking.isConfirmed());

            // Step 11: Check available seats again after booking
            System.out.println("nChecking available seats after booking...");
            availableSeats = showController.getAvailableSeats(showId);
            System.out.println("Available seats: " + availableSeats);

            // ------------------------------
            // CONCURRENT BOOKING SIMULATION
            // ------------------------------
            System.out.println("nSimulating concurrent booking attempts...");
            Thread t1 = new Thread(() -> {
                try {
                    // User 1 (John Doe) trying to book seats 5, 6, 7
                    String bookingIdT1 = bookingController.createBooking(user, showId, Arrays.asList(5, 6, 7));
                    System.out.println("User1 booking (seats 5,6,7) succeeded with Booking ID: " + bookingIdT1);
                } catch (`Exception e) {
                    System.err.println("User1 booking (seats 5,6,7) failed: " + e.getMessage());
                }
            });
            Thread t2 = new Thread(() -> {
                try {
                    // User 2 trying to book seats 7, 8, 9 (seat 7 overlaps with User1’s attempt)
                    User user2 = new User("Jane Doe", "jane.doe@example.com");
                    String bookingIdT2 = bookingController.createBooking(user2, showId, Arrays.asList(7, 8, 9));
                    System.out.println("User2 booking (seats 7,8,9) succeeded with Booking ID: " + bookingIdT2);
                } catch (Exception e) {
                    System.err.println("User2 booking (seats 7,8,9) failed: " + e.getMessage());
                }
            });
            t1.start();
            t2.start();
            t1.join();
            t2.join();
            // Final available seats after concurrent attempts
            System.out.println("nFinal available seats after concurrent booking attempts: " + showController.getAvailableSeats(showId));
        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
