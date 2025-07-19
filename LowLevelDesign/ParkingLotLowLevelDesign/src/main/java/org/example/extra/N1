// ---------------- ENUM ----------------
public enum VehicleType {
    TWO_WHEELER, THREE_WHEELER, FOUR_WHEELER
}

// ---------------- VEHICLE ----------------
public class Vehicle {
    private VehicleType vehicleType;
    private String color;
    private String vehicleNumber;

    // constructor, getters
    public Vehicle(VehicleType vehicleType, String color, String vehicleNumber) {
        this.vehicleType = vehicleType;
        this.color = color;
        this.vehicleNumber = vehicleNumber;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }
}

// ---------------- PARKING SPOT INTERFACE ----------------
public interface ParkingSpot {
    boolean isEmpty();
    void occupy(VehicleType vehicleType);
    void vacateParkingSpot();
}

// ---------------- PARKING SPOT IMPLEMENTATIONS ----------------
public abstract class AbstractParkingSpot implements ParkingSpot {
    protected boolean isOccupied;
    protected int floorNumber;
    protected int price;

    public boolean isEmpty() {
        return !isOccupied;
    }

    public void vacateParkingSpot() {
        isOccupied = false;
    }
}

public class TwoWheelerParkingSpot extends AbstractParkingSpot {
    public void occupy(VehicleType vehicleType) {
        if (vehicleType == VehicleType.TWO_WHEELER) {
            isOccupied = true;
        }
    }
}

public class ThreeWheelerParkingSpot extends AbstractParkingSpot {
    public void occupy(VehicleType vehicleType) {
        if (vehicleType == VehicleType.THREE_WHEELER) {
            isOccupied = true;
        }
    }
}

public class FourWheelerParkingSpot extends AbstractParkingSpot {
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
    private List<ParkingSpot> parkings;

    public ParkingSpotManager(List<ParkingSpot> ps) {
        this.parkings = ps;
    }

    public ParkingSpot findParkingSpot(ParkingStrategy strategy) {
        return strategy.findParkingSpot(parkings);
    }

    public void addParkingSpot(ParkingSpot ps) {
        parkings.add(ps);
    }

    public void removeParkingSpot(ParkingSpot ps) {
        parkings.remove(ps);
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
            return new Ticket(0, cost, System.currentTimeMillis(), vehicle.getVehicleType(), spot);
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
