package com.example.android.popularmovies;

import android.app.Application;

import ollie.Ollie;

public class PopMoviesApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Ollie.with(this)
                .setName("pop_movies.db")
                .setVersion(1)
                .setLogLevel(Ollie.LogLevel.FULL)
                .setCacheSize(Ollie.DEFAULT_CACHE_SIZE)
                .init();
    }

}
