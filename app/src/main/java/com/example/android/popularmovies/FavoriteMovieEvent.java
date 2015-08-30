package com.example.android.popularmovies;

import com.example.android.popularmovies.data.model.Movie;

public class FavoriteMovieEvent {

    public Movie movie;

    public FavoriteMovieEvent(Movie movie) {
        this.movie = movie;
    }

    public Movie getValue() {
        return this.movie;
    }

}