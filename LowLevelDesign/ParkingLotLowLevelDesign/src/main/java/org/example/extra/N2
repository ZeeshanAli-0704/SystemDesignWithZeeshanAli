// ---------------- ENUM ----------------
public enum VehicleType {
    TWO_WHEELER, THREE_WHEELER, FOUR_WHEELER
}

// ---------------- VEHICLE ----------------
public class Vehicle {
    private VehicleType vehicleType;
    private String color;
    private String vehicleNumber;

    public Vehicle(VehicleType vehicleType, String color, String vehicleNumber) {
        this.vehicleType = vehicleType;
        this.color = color;
        this.vehicleNumber = vehicleNumber;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }
}

// ---------------- FLOOR ----------------
import java.util.*;

public class Floor {
    private int floorNumber;
    private List<ParkingSpot> parkingSpots;

    public Floor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.parkingSpots = new ArrayList<>();
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public List<ParkingSpot> getParkingSpots() {
        return parkingSpots;
    }

    public void addSpot(ParkingSpot spot) {
        parkingSpots.add(spot);
    }
}

// ---------------- PARKING SPOT INTERFACE ----------------
public interface ParkingSpot {
    boolean isEmpty();
    void occupy(VehicleType vehicleType);
    void vacateParkingSpot();
    int getFloorNumber();
    int getDistanceFromEntrance();
}

// ---------------- PARKING SPOT IMPLEMENTATIONS ----------------
public abstract class AbstractParkingSpot implements ParkingSpot {
    protected boolean isOccupied;
    protected int floorNumber;
    protected int price;
    protected int distanceFromEntrance;

    public boolean isEmpty() {
        return !isOccupied;
    }

    public void vacateParkingSpot() {
        isOccupied = false;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public int getDistanceFromEntrance() {
        return distanceFromEntrance;
    }
}

public class TwoWheelerParkingSpot extends AbstractParkingSpot {
    public TwoWheelerParkingSpot(int floor, int dist) {
        this.floorNumber = floor;
        this.distanceFromEntrance = dist;
    }

    public void occupy(VehicleType vehicleType) {
        if (vehicleType == VehicleType.TWO_WHEELER) {
            isOccupied = true;
        }
    }
}

public class ThreeWheelerParkingSpot extends AbstractParkingSpot {
    public ThreeWheelerParkingSpot(int floor, int dist) {
        this.floorNumber = floor;
        this.distanceFromEntrance = dist;
    }

    public void occupy(VehicleType vehicleType) {
        if (vehicleType == VehicleType.THREE_WHEELER) {
            isOccupied = true;
        }
    }
}

public class FourWheelerParkingSpot extends AbstractParkingSpot {
    public FourWheelerParkingSpot(int floor, int dist) {
        this.floorNumber = floor;
        this.distanceFromEntrance = dist;
    }

    public void occupy(VehicleType vehicleType) {
        if (vehicleType == VehicleType.FOUR_WHEELER) {
            isOccupied = true;
        }
    }
}

// ---------------- PARKING STRATEGY ----------------
public interface ParkingStrategy {
    ParkingSpot findParkingSpot(List<ParkingSpot> spots);
}

public class DefaultParkingStrategy implements ParkingStrategy {
    public ParkingSpot findParkingSpot(List<ParkingSpot> spots) {
        for (ParkingSpot spot : spots) {
            if (spot.isEmpty()) return spot;
        }
        return null;
    }
}

public class NearToEntranceParkingStrategy implements ParkingStrategy {
    public ParkingSpot findParkingSpot(List<ParkingSpot> spots) {
        return spots.stream()
            .filter(ParkingSpot::isEmpty)
            .min(Comparator.comparingInt(ParkingSpot::getDistanceFromEntrance))
            .orElse(null);
    }
}

// ---------------- COST COMPUTATION STRATEGY ----------------
public interface CostComputation {
    int calculateCostForTicket(VehicleType vehicleType);
}

public class DefaultStrategy implements CostComputation {
    public int calculateCostForTicket(VehicleType vehicleType) {
        return switch (vehicleType) {
            case TWO_WHEELER -> 20;
            case THREE_WHEELER -> 30;
            case FOUR_WHEELER -> 50;
        };
    }
}

public class MinuteBasedStrategy implements CostComputation {
    public int calculateCostForTicket(VehicleType vehicleType) {
        return switch (vehicleType) {
            case TWO_WHEELER -> 1;
            case THREE_WHEELER -> 2;
            case FOUR_WHEELER -> 3;
        };
    }
}

public class HourBasedStrategy implements CostComputation {
    public int calculateCostForTicket(VehicleType vehicleType) {
        return switch (vehicleType) {
            case TWO_WHEELER -> 10;
            case THREE_WHEELER -> 15;
            case FOUR_WHEELER -> 25;
        };
    }
}

// ---------------- TICKET ----------------
public class Ticket {
    private int floorNo;
    private int price;
    private long time;
    private VehicleType vehicleType;
    private ParkingSpot parkingSpot;

    public Ticket(int floorNo, int price, long time, VehicleType vehicleType, ParkingSpot parkingSpot) {
        this.floorNo = floorNo;
        this.price = price;
        this.time = time;
        this.vehicleType = vehicleType;
        this.parkingSpot = parkingSpot;
    }

    public int getPrice() {
        return price;
    }

    public ParkingSpot getParkingSpot() {
        return parkingSpot;
    }
}

// ---------------- PARKING SPOT MANAGER ----------------
public class ParkingSpotManager {
    private List<Floor> floors;

    public ParkingSpotManager(List<Floor> floors) {
        this.floors = floors;
    }

    public ParkingSpot findParkingSpot(ParkingStrategy strategy) {
        for (Floor floor : floors) {
            ParkingSpot spot = strategy.findParkingSpot(floor.getParkingSpots());
            if (spot != null) return spot;
        }
        return null;
    }

    public void addSpotToFloor(int floorNum, ParkingSpot spot) {
        for (Floor f : floors) {
            if (f.getFloorNumber() == floorNum) {
                f.addSpot(spot);
                break;
            }
        }
    }
}

// ---------------- ENTRANCE ----------------
public class Entrance {
    private ParkingSpotManager manager;

    public Entrance(ParkingSpotManager manager) {
        this.manager = manager;
    }

    public Ticket bookSpotAndGiveTicket(Vehicle vehicle, ParkingStrategy ps, CostComputation costStrategy) {
        ParkingSpot spot = manager.findParkingSpot(ps);
        if (spot != null) {
            spot.occupy(vehicle.getVehicleType());
            int cost = costStrategy.calculateCostForTicket(vehicle.getVehicleType());
            return new Ticket(spot.getFloorNumber(), cost, System.currentTimeMillis(), vehicle.getVehicleType(), spot);
        }
        return null;
    }
}

// ---------------- EXIT GATE ----------------
public class ExitGate {
    private Ticket ticket;

    public ExitGate(Ticket ticket) {
        this.ticket = ticket;
    }

    public int payPrice() {
        return ticket.getPrice();
    }

    public void vacateParking() {
        ticket.getParkingSpot().vacateParkingSpot();
    }
}

// ---------------- DEMO ----------------
public class ParkingLotDemo {
    public static void main(String[] args) {
        Floor floor1 = new Floor(1);
        Floor floor2 = new Floor(2);

        floor1.addSpot(new TwoWheelerParkingSpot(1, 10));
        floor1.addSpot(new TwoWheelerParkingSpot(1, 5));
        floor2.addSpot(new FourWheelerParkingSpot(2, 7));
        floor2.addSpot(new FourWheelerParkingSpot(2, 2));

        List<Floor> allFloors = Arrays.asList(floor1, floor2);
        ParkingSpotManager manager = new ParkingSpotManager(allFloors);

        Entrance entrance = new Entrance(manager);
        Vehicle vehicle = new Vehicle(VehicleType.FOUR_WHEELER, "Black", "DL09CJ9999");

        Ticket ticket = entrance.bookSpotAndGiveTicket(vehicle, new NearToEntranceParkingStrategy(), new HourBasedStrategy());

        if (ticket != null) {
            System.out.println("Ticket issued for floor: " + ticket.getParkingSpot().getFloorNumber());
            ExitGate exitGate = new ExitGate(ticket);
            System.out.println("Pay price: Rs." + exitGate.payPrice());
            exitGate.vacateParking();
        } else {
            System.out.println("No spot available");
        }
    }
}