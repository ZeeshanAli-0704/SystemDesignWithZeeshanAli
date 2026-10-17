package org.example.controller;

import org.example.models.Movie;
import org.example.models.Screen;
import org.example.models.Seat;
import org.example.service.MovieService;
import org.example.service.SeatAvailabilityService;
import org.example.service.ShowService;
import org.example.service.TheatreService;
import org.example.models.Show;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


public class ShowController {
    // Dependencies injected for handling show operations, theatre data, movie data, and seat availability.
    private final SeatAvailabilityService seatAvailabilityService;
    private final ShowService showService;
    private final TheatreService theatreService;
    private final MovieService movieService;
    // Constructor to inject all required services
    public ShowController(SeatAvailabilityService seatAvailabilityService, ShowService showService,
                          TheatreService theatreService, MovieService movieService) {
        this.seatAvailabilityService = seatAvailabilityService;
        this.showService = showService;
        this.theatreService = theatreService;
        this.movieService = movieService;
    }
    public int createShow(final int movieId, final int screenId, final Date startTime,
                          final Integer durationInSeconds, final String showInfo) throws Exception{
        final Screen screen = theatreService.getScreen(screenId);
        final Movie movie = movieService.getMovie(movieId);
        return showService.createShow(movie, screen, startTime, durationInSeconds, showInfo).getId();
    }
    public List<Integer> getAvailableSeats(final int showId) throws Exception{
        final Show show = showService.getShow(showId);
        final List<Seat> availableSeats = seatAvailabilityService.getAvailableSeats(show);
        return availableSeats.stream().map(Seat::getId).collect(Collectors.toList());
    }
}
