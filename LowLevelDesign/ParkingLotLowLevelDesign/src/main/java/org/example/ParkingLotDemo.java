package org.example;

import org.example.config.ParkingLotConfiguration;
import org.example.domain.model.Ticket;
import org.example.domain.model.Vehicle;
import org.example.domain.model.enums.VehicleType;
import org.example.entrance.Entrance;
import org.example.exit.ExitGate;
import org.example.floor.Floor;
import org.example.parkingspot.CompactVehicleParkingSpot;
import org.example.parkingspot.LargeVehicleParkingSpot;
import org.example.parkingspot.MiniVehicleParkingSpot;
import org.example.parkingspot.ParkingSpotFactory;
import org.example.service.EntryExitRegistry;
import org.example.spotmanager.ParkingSpotManager;
import org.example.strategy.payment.SetPaymentMode;
import org.example.strategy.payment.UpiPayment;
import org.example.strategy.payment.PaymentStrategy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ParkingLotDemo {
    public static void main(String[] args) {
        // Setup configuration and parking manager
        ParkingLotConfiguration config = new ParkingLotConfiguration();

        ParkingSpotManager manager = new ParkingSpotManager();

        // set parking strategy
        manager.setParkingStrategy(config.parkingStrategy());

        // Setup registry and gates

        EntryExitRegistry registry = new EntryExitRegistry();
        Entrance eNorth = new Entrance("E1", "North Gate", manager);
        Entrance eSouth = new Entrance("E2", "South Gate", manager);
        registry.registerEntrance(eNorth);
        registry.registerEntrance(eSouth);

        ExitGate xNorth = new ExitGate("X1", "North Exit");
        ExitGate xSouth = new ExitGate("X2", "South Exit");
        registry.registerExit(xNorth);
        registry.registerExit(xSouth);

        System.out.println("\n*** Parking Lot Demo with Multiple Vehicles ***");


        /**
        // NORMAL FLOW
        manager.addFloor(new Floor(1));
        manager.addFloor(new Floor(2));

        // 3 spots on floor 1, 1 spot on floor 2
        manager.addSpotToFloor(1, ParkingSpotFactory.createSpot(VehicleType.MINI, 1, 10));
        manager.addSpotToFloor(1, ParkingSpotFactory.createSpot(VehicleType.MINI, 1, 5));
        manager.addSpotToFloor(1, ParkingSpotFactory.createSpot(VehicleType.LARGE, 1, 2));
        manager.addSpotToFloor(2, ParkingSpotFactory.createSpot(VehicleType.COMPACT, 1, 7));




        // Prepare vehicles
        List<Vehicle> vehicles = List.of(
                new Vehicle.Builder().vehicleType(VehicleType.MINI).vehicleNumber("DL09CJ1001").ownerName("User1").insurancePolicy("P1").build(),
                new Vehicle.Builder().vehicleType(VehicleType.MINI).vehicleNumber("DL09CJ1002").ownerName("User2").insurancePolicy("P2").build(),
                new Vehicle.Builder().vehicleType(VehicleType.LARGE).vehicleNumber("DL09CJ1003").ownerName("User3").insurancePolicy("P3").build(),
                new Vehicle.Builder().vehicleType(VehicleType.COMPACT).vehicleNumber("DL09CJ1004").ownerName("User4").insurancePolicy("P4").build(),
                new Vehicle.Builder().vehicleType(VehicleType.MINI).vehicleNumber("DL09CJ1005").ownerName("User5").insurancePolicy("P5").build() // Will likely trigger parking full
        );

        // Keep record of issued tickets with vehicle index for later exits
        List<Ticket> issuedTickets = new ArrayList<>();

        // Vehicles entering
        for (int i = 0; i < vehicles.size(); i++) {
            Vehicle vehicle = vehicles.get(i);
            // Alternate entrances
            Entrance chosenEntrance = registry.getEntranceByIndex(i % registry.getEntrances().size());
            System.out.println("\n[" + vehicle.getVehicleNumber() + "] trying to =enter via " + chosenEntrance.getName());
            Ticket ticket = chosenEntrance.bookSpotAndGiveTicket(vehicle);
            if (ticket != null) {
                System.out.println("---> Ticket issued: floor=" + ticket.getParkingSpot().getFloorNumber() + ", spot=" + ticket.getParkingSpot());
                issuedTickets.add(ticket);
            } else {
                System.out.println("---> Parking FULL: No spot available for " + vehicle.getVehicleNumber());
            }
        }

        // Let a few vehicles exit (simulate exit after 1 hour)
        int[] toExit = {0, 2}; // indexes of tickets to exit
        for (int idx : toExit) {
            if (idx < issuedTickets.size()) {
                Ticket ticket = issuedTickets.get(idx);
                // Alternate exits
                ExitGate chosenExit = registry.getExitByIndex(idx % registry.getExits().size());
                LocalDateTime exitTime = ticket.getTime().plusHours(1);

                int price = chosenExit.processExitAndReturnCost(ticket, exitTime);
                System.out.println("---> Parking Fee: Rs." + price + " (paid via UPI: " + "zeeshan@ybl@ybl )");
                PaymentStrategy paymentStrategy = new UpiPayment("zeeshan@ybl@ybl");
                SetPaymentMode setPaymentMode = new SetPaymentMode(paymentStrategy);
                setPaymentMode.payAmount(price);

                chosenExit.vacateParking(ticket);
            }
        }

        // Now try to make the "full" vehicle try again after a spot was freed
        Vehicle waitingVehicle = vehicles.get(4); // The one that previously saw full
        Entrance tryAgainEntrance = registry.getEntranceByIndex(1); // South gate
        System.out.println("\n[Retry] " + waitingVehicle.getVehicleNumber() + " trying to enter via " + tryAgainEntrance.getName());
        Ticket retryTicket = tryAgainEntrance.bookSpotAndGiveTicket(waitingVehicle);
        if (retryTicket != null) {
            System.out.println("---> Ticket issued after retry: floor=" + retryTicket.getParkingSpot().getFloorNumber() + ", spot=" + retryTicket.getParkingSpot());
        } else {
            System.out.println("---> Still Parking FULL: No spot available for " + waitingVehicle.getVehicleNumber());
        }

        System.out.println("\n*** End of Demo ***");

         */


        // ------------- Threaded Test Case START ---------------
        System.out.println("\n*** Threading Test: 2 MINI vehicles, 1 spot! ***");

        // 1 MINI spot only on a new floor for clarity
        manager.addFloor(new Floor(3));
        manager.addSpotToFloor(3, ParkingSpotFactory.createSpot(VehicleType.MINI, 3, 15));

        // Create the vehicles
        Vehicle threadVehicle1 = new Vehicle.Builder()
                .vehicleType(VehicleType.MINI).vehicleNumber("DL00AA0001")
                .ownerName("ThreadUser1").insurancePolicy("TP1").build();
        Vehicle threadVehicle2 = new Vehicle.Builder()
                .vehicleType(VehicleType.MINI).vehicleNumber("DL00AA0002")
                .ownerName("ThreadUser2").insurancePolicy("TP2").build();

        // Two entrances to simulate entry from different gates
        Entrance threadEntrance1 = registry.getEntranceByIndex(0); // North Gate
        Entrance threadEntrance2 = registry.getEntranceByIndex(1); // South Gate

        // Result holders
        final Ticket[] tickets = new Ticket[2];

        // Create threads
        Thread t1 = new Thread(() -> {
            tickets[0] = threadEntrance1.bookSpotAndGiveTicket(threadVehicle1);
            if (tickets[0] != null) {
                System.out.println("[Thread 1] Ticket issued: " + tickets[0].getVehicleType() + " at spot " + tickets[0].getParkingSpot());
            } else {
                System.out.println("[Thread 1] Parking FULL!");
            }
        });
        Thread t2 = new Thread(() -> {
            tickets[1] = threadEntrance2.bookSpotAndGiveTicket(threadVehicle2);
            if (tickets[1] != null) {
                System.out.println("[Thread 2] Ticket issued: " + tickets[1].getVehicleType() + " at spot " + tickets[1].getParkingSpot());
            } else {
                System.out.println("[Thread 2] Parking FULL!");
            }
        });

        // Start both threads at nearly the same time
        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("*** End of threading demo. Only one ticket above should succeed for the only MINI spot. ***");

        // ------------- Threaded Test Case END ---------------
    }
}