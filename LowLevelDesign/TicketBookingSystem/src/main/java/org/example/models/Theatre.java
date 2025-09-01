package org.example.models;

import java.util.ArrayList;
import java.util.List;

public class Theatre {
    private final int id;
    private final String theatreName;
    private final String theatreInfo;
    private final List<Screen> screenList;

    public Theatre(int id, String theatreName, String theatreInfo) {
        this.id = id;
        this.theatreName = theatreName;
        this.theatreInfo = theatreInfo;
        this.screenList = new ArrayList<Screen>();
    }

    public int getTheatreId() {
        return id;
    }
    public String getTheatreName() {
        return theatreName;
    }

    public List<Screen> getScreenList() {
        return screenList;
    }

    public void addScreen(Screen screen) {
        screenList.add(screen);
    }


}
