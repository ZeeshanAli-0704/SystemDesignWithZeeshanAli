package org.example;
import org.example.config.ParkingLotConfiguration;
import org.example.entrance.Entrance;
import org.example.exit.ExitGate;
import org.example.floor.Floor;
import org.example.parkingspot.CompactVehicleParkingSpot;
import org.example.parkingspot.LargeVehicleParkingSpot;
import org.example.parkingspot.MiniVehicleParkingSpot;
import org.example.strategy.payment.CreditCardPayment;
import org.example.strategy.payment.PaymentStrategy;
import org.example.strategy.payment.SetPaymentMode;
import org.example.strategy.payment.UpiPayment;
import org.example.spotmanager.ParkingSpotManager;
import org.example.domain.model.Ticket;
import org.example.domain.model.Vehicle;
import org.example.domain.model.enums.VehicleType;
import org.example.service.EntryExitRegistry;
import java.time.LocalDateTime;
import java.util.Scanner;

public class ParkingLotDemo1 {
    public static void main(String[] args) {
        // Config
        ParkingLotConfiguration config = new ParkingLotConfiguration();
        ParkingSpotManager manager = new ParkingSpotManager();
        manager.addFloor(new Floor(1));
        manager.addFloor(new Floor(2));
        // 3 spots on floor 1, 
        manager.addSpotToFloor(1, new MiniVehicleParkingSpot(1, 10));
        manager.addSpotToFloor(1, new MiniVehicleParkingSpot(1, 5));
        manager.addSpotToFloor(1, new LargeVehicleParkingSpot(1, 2));
        // 1 spot on floor 2
        manager.addSpotToFloor(2, new CompactVehicleParkingSpot(2, 7));

        //  Strategy set once
        manager.setParkingStrategy(config.parkingStrategy());

        // Create Entrances and Register
        Entrance entrance1 = new Entrance("E1", "North Gate", manager);
        Entrance entrance2 = new Entrance("E2", "South Gate", manager);

        EntryExitRegistry registry = new EntryExitRegistry();
        registry.registerEntrance(entrance1);
        registry.registerEntrance(entrance2);


        Vehicle vehicle = new Vehicle.Builder()
                .vehicleType(VehicleType.MINI)
                .vehicleNumber("DL09CJ9999")
                .ownerName("Zeeshan Ali")
                .insurancePolicy("LIC-9988-ZX12")
                .build();


        Entrance selectedEntrance = registry.getEntranceByIndex(1); // South Gate
        Ticket ticket = selectedEntrance.bookSpotAndGiveTicket(vehicle);

        if (ticket != null) {
            System.out.println("Ticket issued for floor: " + ticket.getParkingSpot().getFloorNumber());
            ExitGate exit1 = new ExitGate("X1", "North Exit");
            ExitGate exit2 = new ExitGate("X2", "South Exit");

            registry.registerExit(exit1);
            registry.registerExit(exit2);

            LocalDateTime exitTime = ticket.getTime().plusHours(1);
            // LocalDateTime.now();

            ExitGate selectedExit = registry.getExitByIndex(0);
            int price =  selectedExit.processExitAndReturnCost(ticket, exitTime);
            System.out.println("Pay Rs." + price);

            // Select payment method **dynamically per session**
            Scanner sc = new Scanner(System.in);
            System.out.println("Choose Payment Mode: 1. Card  2. UPI  3. Wallet");
            int choice = sc.nextInt();

            PaymentStrategy strategy;
            switch (choice) {
                case 1 -> strategy = new CreditCardPayment("2134-2345-2445-4124");
                case 2 -> strategy = new UpiPayment("zeeshan@ybl");
                default -> throw new IllegalArgumentException("Invalid payment mode");
            }
            
            SetPaymentMode mode  = new SetPaymentMode(strategy);
            mode.payAmount(price);
            selectedExit.vacateParking(ticket);

        } else {
            System.out.println("No spot available");
        }
    }
}
