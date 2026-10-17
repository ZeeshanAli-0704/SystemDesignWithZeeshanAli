package org.example.controller;

import org.example.enums.SeatCategory;
import org.example.models.Screen;
import org.example.service.TheatreService;
import org.example.models.Theatre;

public class TheatreController {
    private final TheatreService theatreService;
    // Constructor to inject TheatreService
    public TheatreController(final TheatreService theatreService) {
        this.theatreService = theatreService;
    }
    public int createTheatre(final String theatreName, final String theaterInfo) {
        return theatreService.createTheatre(theatreName, theaterInfo).getTheatreId();
    }
    public int createScreenInTheatre( final String type, final String screenName, final int theatreId) throws Exception {
        final Theatre theatre = theatreService.getTheatre(theatreId);
        return theatreService.createScreenInTheatre(type, screenName, theatre).getScreenId();
    }

    public int createSeatInScreen(final Integer rowNo, final SeatCategory seatCategory, final int screenId, final int seatCost) throws Exception {
        final Screen screen = theatreService.getScreen(screenId);
        return theatreService.createSeatInScreen(rowNo, seatCategory, screen, seatCost).getId();
    }
}
