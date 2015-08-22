package com.example.android.popularmovies.data.provider;

import com.activeandroid.content.ContentProvider;

public class MoviesProvider extends ContentProvider {

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
