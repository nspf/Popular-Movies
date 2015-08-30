package com.example.android.popularmovies;

import com.example.android.popularmovies.data.model.Movie;

/**
 * Created by nico on 8/27/15.
 */


public class MovieSelectedEvent {

    public Movie movie;

    public MovieSelectedEvent(Movie movie) {
        this.movie = movie;
    }

    public Movie getValue() {
        return this.movie;
    }

}
