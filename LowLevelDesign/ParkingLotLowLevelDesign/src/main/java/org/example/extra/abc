package org.example.config;
import org.example.strategy.parking.NearToEntranceParkingStrategy;
import org.example.strategy.parking.ParkingStrategy;
import org.example.strategy.payment.CreditCardPayment;
import org.example.strategy.payment.PaymentStrategy;

public class ParkingLotConfiguration {

    public ParkingStrategy parkingStrategy() {
        return new NearToEntranceParkingStrategy();
    }

    public PaymentStrategy defaultPaymentStrategy() {
        return new CreditCardPayment("2134-2345-2445-4124");
    }
}



package org.example.domain.model.enums;

public enum VehicleType {
    MINI, COMPACT, LARGE
}


package org.example.domain.model;

import org.example.domain.model.enums.VehicleType;
import org.example.parkingspot.ParkingSpot;

import java.time.LocalDateTime;

public class Ticket {
    private final int floorNo;
    private final LocalDateTime time;
    private final VehicleType vehicleType;
    private final ParkingSpot parkingSpot;

    private Ticket(Builder builder) {
        this.floorNo = builder.floorNo;
        this.time = builder.time;
        this.vehicleType = builder.vehicleType;
        this.parkingSpot = builder.parkingSpot;
    }

    public int getFloorNo() {
        return floorNo;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public ParkingSpot getParkingSpot() {
        return parkingSpot;
    }


    public static class Builder {
        private int floorNo;
        private LocalDateTime time;
        private VehicleType vehicleType;
        private ParkingSpot parkingSpot;

        public Builder floorNo(int floorNo) {
            this.floorNo = floorNo;
            return this;
        }

        public Builder time(LocalDateTime time) {
            this.time = time;
            return this;
        }

        public Builder vehicleType(VehicleType vehicleType) {
            this.vehicleType = vehicleType;
            return this;
        }

        public Builder parkingSpot(ParkingSpot parkingSpot) {
            this.parkingSpot = parkingSpot;
            return this;
        }


        public Ticket build() {
            return new Ticket(this);
        }
    }
}


package org.example.domain.model;

import org.example.domain.model.enums.VehicleType;

public class Vehicle {
    private final VehicleType vehicleType;
    private final String vehicleNumber;
    private final String ownerName;         // Optional
    private final String insurancePolicy;   // Optional

    private Vehicle(Builder builder) {
        this.vehicleType = builder.vehicleType;
        this.vehicleNumber = builder.vehicleNumber;
        this.ownerName = builder.ownerName;
        this.insurancePolicy = builder.insurancePolicy;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getInsurancePolicy() {
        return insurancePolicy;
    }

    public static class Builder {
        private VehicleType vehicleType;
        private String vehicleNumber;
        private String ownerName;
        private String insurancePolicy;

        public Builder vehicleType(VehicleType vehicleType) {
            this.vehicleType = vehicleType;
            return this;
        }

        public Builder vehicleNumber(String vehicleNumber) {
            this.vehicleNumber = vehicleNumber;
            return this;
        }

        public Builder ownerName(String ownerName) {
            this.ownerName = ownerName;
            return this;
        }

        public Builder insurancePolicy(String insurancePolicy) {
            this.insurancePolicy = insurancePolicy;
            return this;
        }

        public Vehicle build() {
            return new Vehicle(this);
        }
    }
}


package org.example.entrance;

import org.example.strategy.cost.CostComputation;
import org.example.parkingspot.ParkingSpot;
import org.example.spotmanager.ParkingSpotManager;
import org.example.domain.model.Ticket;
import org.example.domain.model.Vehicle;

import java.time.LocalDateTime;

public class Entrance {
    private final String id;
    private final String name;
    private ParkingSpotManager manager;

    public Entrance(String id, String name, ParkingSpotManager manager) {
        this.id = id;
        this.name = name;
        this.manager = manager;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Ticket bookSpotAndGiveTicket(Vehicle vehicle) {
        ParkingSpot spot = manager.findParkingSpot(vehicle.getVehicleType());
        if (spot != null) {
            spot.occupy(vehicle.getVehicleType());
            return new Ticket.Builder()
                    .floorNo(spot.getFloorNumber())
                    .time(LocalDateTime.now())
                    .vehicleType(vehicle.getVehicleType())
                    .parkingSpot(spot)
                    .build();
        }
        return null;
    }
}



package org.example.exit;

import org.example.strategy.cost.CostComputation;
import org.example.strategy.cost.CostComputationStrategyResolver;
import org.example.domain.model.Ticket;
import org.example.domain.model.enums.VehicleType;

import java.time.LocalDateTime;

public class ExitGate {
    private String id;
    private String name;

    public ExitGate(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
         return id;
        }
    public String getName() {
        return name;
    }

    public int processExitAndReturnCost(Ticket ticket, LocalDateTime exitTime){
        VehicleType vehicleType = ticket.getVehicleType();

        CostComputationStrategyResolver resolver = new CostComputationStrategyResolver();
        CostComputation strategy = resolver.resolveStrategy(ticket, exitTime);
        return strategy.calculateCostForTicket(vehicleType, ticket, exitTime);
    }

    public void vacateParking(Ticket ticket) {
        ticket.getParkingSpot().vacateParkingSpot();
    }
}



package org.example.floor;

import org.example.parkingspot.ParkingSpot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Floor {
    private int floorNumber;
    private List<ParkingSpot> parkingSpotList;

    public Floor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.parkingSpotList =  new ArrayList<ParkingSpot>();
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public void addSpot(ParkingSpot spot){
        this.parkingSpotList.add(spot);
    }

    public ParkingSpot removeSpot(String spotId) {
        Iterator<ParkingSpot> iterator = this.parkingSpotList.iterator();
        while (iterator.hasNext()) {
            ParkingSpot spot = iterator.next();
            if (spot.getSpotId().equals(spotId)) {
                iterator.remove();
                return spot;
            }
        }
        return null;
    }

    public List<ParkingSpot> getParkingSpotList() {
        return parkingSpotList;
    }
}


package org.example.parkingspot.compatibility;
import org.example.parkingspot.*;
import org.example.domain.model.enums.VehicleType;

public class DefaultSpotCompatibilityChecker implements SpotCompatibilityChecker {
    @Override
    public boolean isCompatible(ParkingSpot spot, VehicleType vehicleType) {
        switch (vehicleType) {
            case MINI:
                return spot instanceof MiniVehicleParkingSpot;
            case COMPACT:
                return spot instanceof CompactVehicleParkingSpot;
            case LARGE:
                return spot instanceof LargeVehicleParkingSpot;
            default:
                return false;
        }
    }
}

package org.example.parkingspot.compatibility;

import org.example.parkingspot.ParkingSpot;
import org.example.domain.model.enums.VehicleType;

public interface SpotCompatibilityChecker {
    boolean isCompatible(ParkingSpot spot, VehicleType vehicleType);
}




package org.example.parkingspot;

import java.util.UUID;

public abstract class AbstractParkingSpot implements ParkingSpot {

    protected final String spotId;
    protected boolean isOccupied;
    protected int floorNumber;
    protected int distanceFromEntrance;

    public AbstractParkingSpot(int floorNumber, int distanceFromEntrance) {
        this.spotId = UUID.randomUUID().toString(); //  auto-generate
        this.floorNumber = floorNumber;
        this.distanceFromEntrance = distanceFromEntrance;
        this.isOccupied = false;
    }

    @Override
    public boolean isEmpty() {
        return !isOccupied;
    }

    @Override
    public void vacateParkingSpot() {
        isOccupied = false;
    }

    @Override
    public int getFloorNumber() {
        return floorNumber;
    }

    @Override
    public int getDistanceFromEntrance() {
        return distanceFromEntrance;
    }

    @Override
    public String getSpotId() {
        return spotId;
    };

    @Override
    public String toString() {
        return "SpotID: " + spotId + ", Floor: " + floorNumber + ", Distance: " + distanceFromEntrance;
    }

}


package org.example.parkingspot;

import org.example.domain.model.enums.VehicleType;

public class CompactVehicleParkingSpot extends AbstractParkingSpot {

    public CompactVehicleParkingSpot(int floor, int dist) {
        super(floor, dist);
    }

    @Override
    public void occupy(VehicleType vehicleType) {
        if (vehicleType == VehicleType.COMPACT) {
            isOccupied = true;
        }
    }
}



package org.example.parkingspot;

import org.example.domain.model.enums.VehicleType;

public class LargeVehicleParkingSpot extends AbstractParkingSpot {
    public LargeVehicleParkingSpot(int floor, int dist) {
        super(floor, dist);
    }

    @Override
    public void occupy(VehicleType vehicleType) {
        if (vehicleType == VehicleType.LARGE) {
            isOccupied = true;
        }
    }
}


package org.example.parkingspot;

import org.example.domain.model.enums.VehicleType;

public class MiniVehicleParkingSpot extends AbstractParkingSpot {

    public MiniVehicleParkingSpot(int floor, int dist) {
        super(floor, dist);
    }

    @Override
    public void occupy(VehicleType vehicleType) {
        if (vehicleType == VehicleType.MINI) {
            isOccupied = true;
        }
    }

}


package org.example.parkingspot;

import org.example.domain.model.enums.VehicleType;

public interface ParkingSpot {
    String getSpotId();
    boolean isEmpty();
    void occupy(VehicleType vehicleType);
    void vacateParkingSpot();
    int getFloorNumber();
    int getDistanceFromEntrance();
}



package org.example.service;

import org.example.entrance.Entrance;
import org.example.exit.ExitGate;

import java.util.ArrayList;
import java.util.List;

public class EntryExitRegistry {
    private final List<Entrance> entrances = new ArrayList<>();
    private final List<ExitGate> exits = new ArrayList<>();

    public void registerEntrance(Entrance entrance) {
        entrances.add(entrance);
    }

    public void registerExit(ExitGate exitGate) {
        exits.add(exitGate);
    }

    public List<Entrance> getEntrances() {
        return entrances;
    }

    public List<ExitGate> getExits() {
        return exits;
    }

    public Entrance getEntranceByIndex(int index) {
        return (index >= 0 && index < entrances.size()) ? entrances.get(index) : null;
    }

    public ExitGate getExitByIndex(int index) {
        return (index >= 0 && index < exits.size()) ? exits.get(index) : null;
    }
}



package org.example.spotmanager;

import org.example.floor.Floor;
import org.example.strategy.parking.ParkingStrategy;
import org.example.parkingspot.compatibility.DefaultSpotCompatibilityChecker;
import org.example.parkingspot.compatibility.SpotCompatibilityChecker;
import org.example.domain.model.enums.VehicleType;
import org.example.parkingspot.ParkingSpot;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ParkingSpotManager {
    private final Map<Integer, Floor> floorMap = new HashMap<Integer, Floor>();
    private ParkingStrategy parkingStrategy;
    private SpotCompatibilityChecker compatibilityChecker = new DefaultSpotCompatibilityChecker();


    public void setParkingStrategy(ParkingStrategy parkingStrategy) {
        this.parkingStrategy = parkingStrategy;
    };


    public void addFloor(Floor floor){
        if(floorMap.containsKey(floor.getFloorNumber())){
            throw new IllegalArgumentException("Floor " + floor.getFloorNumber() + " already exists.");
        }
        floorMap.put(floor.getFloorNumber(), floor);
    };

    public Floor getFloor(int floorNumber) {
        return floorMap.get(floorNumber);
    }

    public ParkingSpot findParkingSpot(VehicleType vehicleType) {
        if (parkingStrategy == null) {
            throw new IllegalStateException("Parking strategy not set");
        }
        for (Floor floor : floorMap.values()) {
            ParkingSpot spot = parkingStrategy.findParkingSpot(floor.getParkingSpotList(), vehicleType, compatibilityChecker);
            if (spot != null) return spot;
        }
        return null;
    }

    public void addSpotToFloor(int floorNum, ParkingSpot spot) {
        Floor floor = floorMap.get(floorNum);
        if(floor != null){

            floor.addSpot(spot);
            System.out.println("Spot added: " + spot.getSpotId());
        }else{
            throw new IllegalArgumentException("No Such Floor Available "+ floorNum);
        }

    }

    public Collection<Floor> getAllFloors() {
        return floorMap.values();
    }
}



package org.example.strategy.cost;

import org.example.domain.model.Ticket;
import org.example.domain.model.enums.VehicleType;

import java.time.LocalDateTime;

public interface CostComputation {
    int calculateCostForTicket(VehicleType vehicleType, Ticket ticket, LocalDateTime exitTime);
}


package org.example.strategy.cost;

import org.example.domain.model.Ticket;

import java.time.LocalDateTime;
import java.util.List;

public class CostComputationStrategyResolver {

    private final List<StrategySelector> selectors;

    public CostComputationStrategyResolver() {
        this.selectors = List.of(
                new MinuteBasedSelector(),
                new HourBasedSelector()
        );
    }

    public CostComputation resolveStrategy(Ticket ticket, LocalDateTime exitTime) {
        return selectors.stream()
                .filter(selector -> selector.supports(ticket, exitTime))
                .map(StrategySelector::getStrategy)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No strategy found"));
    }
}



package org.example.strategy.cost;

import org.example.domain.model.Ticket;
import java.time.Duration;
import java.time.LocalDateTime;

public class HourBasedSelector implements StrategySelector {
    @Override
    public boolean supports(Ticket ticket, LocalDateTime exitTime) {
        long minutes = Duration.between(ticket.getTime(), exitTime).toMinutes();
        return minutes > 60 && minutes <= (24 * 60);
    }

    @Override
    public CostComputation getStrategy() {
        return new HourBasedStrategy();
    }
}


package org.example.strategy.cost;

import org.example.domain.model.Ticket;
import org.example.domain.model.enums.VehicleType;

import java.time.Duration;
import java.time.LocalDateTime;

public class HourBasedStrategy implements CostComputation {
    @Override
    public int calculateCostForTicket(VehicleType vehicleType, Ticket ticket, LocalDateTime exitTime) {
        long hours = Duration.between(ticket.getTime(), exitTime).toHours();
        return switch (vehicleType) {
            case MINI -> (int) hours * 50;
            case COMPACT -> (int) hours * 100;
            case LARGE ->  (int) hours * 150;
        };
    }
}



package org.example.strategy.cost;
import org.example.domain.model.Ticket;
import java.time.Duration;
import java.time.LocalDateTime;

public class MinuteBasedSelector implements StrategySelector {
    @Override
    public boolean supports(Ticket ticket, LocalDateTime exitTime) {
        long minutes = Duration.between(ticket.getTime(), exitTime).toMinutes();
        return minutes <= 60;
    }

    @Override
    public CostComputation getStrategy() {
        return new MinuteBasedStrategy();
    }
}


package org.example.strategy.cost;
import org.example.domain.model.Ticket;
import java.time.Duration;
import java.time.LocalDateTime;

public class MinuteBasedSelector implements StrategySelector {
    @Override
    public boolean supports(Ticket ticket, LocalDateTime exitTime) {
        long minutes = Duration.between(ticket.getTime(), exitTime).toMinutes();
        return minutes <= 60;
    }

    @Override
    public CostComputation getStrategy() {
        return new MinuteBasedStrategy();
    }
}


package org.example.strategy.cost;

import org.example.domain.model.Ticket;
import org.example.domain.model.enums.VehicleType;

import java.time.Duration;
import java.time.LocalDateTime;

public class MinuteBasedStrategy implements CostComputation {
    @Override
    public int calculateCostForTicket(VehicleType vehicleType, Ticket ticket, LocalDateTime exitTime) {
        long minutes = Duration.between(ticket.getTime(), exitTime).toMinutes();
        return switch (vehicleType) {
            case MINI ->  (int) minutes * 2;
            case COMPACT -> (int)  minutes * 3;
            case LARGE -> (int)  minutes * 5;
        };
    }
}


package org.example.strategy.cost;


import org.example.domain.model.Ticket;

import java.time.LocalDateTime;

public interface StrategySelector {

    boolean supports(Ticket ticket, LocalDateTime localDateTime);
    CostComputation getStrategy();
}



package org.example.strategy.parking;

import org.example.parkingspot.ParkingSpot;
import org.example.parkingspot.compatibility.SpotCompatibilityChecker;
import org.example.domain.model.enums.VehicleType;

import java.util.List;

public class DefaultParkingStrategy implements ParkingStrategy {
    @Override
    public ParkingSpot findParkingSpot(List<ParkingSpot> parkingSpotList, VehicleType vehicleType, SpotCompatibilityChecker spotCompatibilityChecker) {
        for (ParkingSpot spot : parkingSpotList) {
            if (spot.isEmpty() && spotCompatibilityChecker.isCompatible(spot, vehicleType)) return spot;
        }
        return null;
    }
}


package org.example.strategy.parking;

import org.example.parkingspot.ParkingSpot;
import org.example.parkingspot.compatibility.SpotCompatibilityChecker;
import org.example.domain.model.enums.VehicleType;

import java.util.Comparator;
import java.util.List;

public class NearToEntranceParkingStrategy implements ParkingStrategy {

    @Override
    public ParkingSpot findParkingSpot(List<ParkingSpot> spots, VehicleType vehicleType, SpotCompatibilityChecker spotCompatibilityChecker) {
        if (spots == null || spots.isEmpty()) return null;

        return spots.stream()
                .filter(spot ->spot.isEmpty() && spotCompatibilityChecker.isCompatible(spot, vehicleType))
               .min(Comparator.comparingInt(ParkingSpot::getDistanceFromEntrance))
                .orElse(null);
    }


};


package org.example.strategy.parking;

import org.example.parkingspot.compatibility.SpotCompatibilityChecker;
import org.example.domain.model.enums.VehicleType;
import org.example.parkingspot.ParkingSpot;

import java.util.List;

public interface ParkingStrategy {
    ParkingSpot findParkingSpot(List<ParkingSpot> parkingSpotList, VehicleType type, SpotCompatibilityChecker spotCompatibilityChecker);
}



package org.example.strategy.payment;

public class CreditCardPayment  implements PaymentStrategy{
    private String cardNumber;

    public CreditCardPayment(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Override
    public boolean payAmount(double amount) {
        System.out.println("Paid ₹" + amount + " using Credit Card: " + cardNumber);
        return true;
    }
}


package org.example.strategy.payment;

public interface PaymentStrategy {

    boolean payAmount(double amount);
}


package org.example.strategy.payment;

public class SetPaymentMode {

    private PaymentStrategy selectedPaymentMode;

    public SetPaymentMode(PaymentStrategy strategy){
        selectedPaymentMode = strategy;
    }

    public String payAmount(double amount){
        if(selectedPaymentMode != null){
           boolean status =  selectedPaymentMode.payAmount(amount);
           if(status){
               return "Payment Completed";
           }else{
               return "Payment Failed";
           }
        }
        return null;
    }
}


package org.example.strategy.payment;

public class UpiPayment implements PaymentStrategy{
    private String upiID;

    public UpiPayment(String upiID) {
        this.upiID = upiID;
    }

    @Override
    public boolean payAmount(double amount) {
        System.out.println("Paid ₹" + amount + " using UPI: " + upiID);
        return true;
    }
}


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
        manager.addFloor(new Floor(1));
        manager.addFloor(new Floor(2));

        // 3 spots on floor 1, 1 spot on floor 2
        manager.addSpotToFloor(1, new MiniVehicleParkingSpot(1, 10));
        manager.addSpotToFloor(1, new MiniVehicleParkingSpot(1, 5));
        manager.addSpotToFloor(1, new LargeVehicleParkingSpot(1, 2));
        manager.addSpotToFloor(2, new CompactVehicleParkingSpot(2, 7));

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
            System.out.println("\n[" + vehicle.getVehicleNumber() + "] trying to enter via " + chosenEntrance.getName());
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
    }