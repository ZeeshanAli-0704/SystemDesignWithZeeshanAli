package org.example.models;

public class Movie {
    private final int id;
    private final String movieName;
    private final String movieCategory;
    private final int movieDurationInMinutes;

    public Movie(int id, String movieName, int movieDurationInMinutes, String movieCategory) {
        this.id = id;
        this.movieName = movieName;
        this.movieCategory = movieCategory;
        this.movieDurationInMinutes = movieDurationInMinutes;
    }

    public int getMovieDuration() {
        return movieDurationInMinutes;
    }

    public String getMovieName() {
        return movieName;
    }

    public int getMovieId() {
        return id;
    }

}
