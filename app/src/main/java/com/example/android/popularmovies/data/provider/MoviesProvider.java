package com.example.android.popularmovies.data.provider;

import ollie.OllieProvider;

public class MoviesProvider extends OllieProvider {

    protected String getAuthority() {
        return "com.example.android.popularmovies";
    }

    protected int getDatabaseVersion()  {
        return 1;
    }

    protected String getDatabaseName()  {
        return "pop_movies.db";
    }

}
