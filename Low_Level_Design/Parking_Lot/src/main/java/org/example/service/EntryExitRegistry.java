package org.example.service;

import org.example.entrance.Entrance;
import org.example.exit.ExitGate;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EntryExitRegistry {
    private final List<Entrance> entrances = new CopyOnWriteArrayList<>();
    private final List<ExitGate> exits = new CopyOnWriteArrayList<>();

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
