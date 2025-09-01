package org.example.service;

import org.example.models.Movie;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MovieService {
    private final Map<Integer, Movie> movieMap;
    private final AtomicInteger movieCounter;

    public MovieService() {
        this.movieMap =  new HashMap<>();
        this.movieCounter = new AtomicInteger(0);
    }

    public Movie getMovie(final int movieId) throws Exception {
        if (!movieMap.containsKey(movieId)) {
            throw new Exception("Movie with ID " + movieId + " not found.");
        };
        return movieMap.get(movieId);
    };

    public Movie createMovie(final String movieName, final int durationInMinutes, final String movieCategory) {
        int movieId = movieCounter.incrementAndGet(); // Increment the counter and get the new value.
        Movie movie = new Movie(movieId, movieName, durationInMinutes, movieCategory);
        movieMap.put(movieId, movie);
        return movie;
    }
}
