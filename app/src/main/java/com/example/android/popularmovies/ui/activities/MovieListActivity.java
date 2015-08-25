/*
 * Copyright 2015 Nicolas Pintos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.popularmovies.ui.activities;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.model.Movie;
import com.example.android.popularmovies.ui.fragments.FavoriteMoviesFragment;
import com.example.android.popularmovies.ui.fragments.MovieDetailFragment;
import com.example.android.popularmovies.ui.fragments.MovieListFragment;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MovieListActivity extends AppCompatActivity implements MovieDetailFragment.mCallback {

    private final String SORT_BY = "sort_by";
    private final String MOST_POPULAR = "popularity.desc";
    private final String HIGHEST_RATED = "vote_average.desc";
    private final String FAVORITES = "favorites";
    private final String MOVIE_LIST_FRAGMENT_TAG = "movieListFragment";

    private MovieListFragment mMovieListFragment;

    @Bind(R.id.movie_list_toolbar) Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        ButterKnife.bind(this);


        if(mToolbar != null) {
            setSupportActionBar(mToolbar);
        }

        if (savedInstanceState != null) {
            mMovieListFragment = (MovieListFragment)
                    getSupportFragmentManager().findFragmentByTag(MOVIE_LIST_FRAGMENT_TAG);
        }

        else {
            loadNewMovieListFragment();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        switch(getMovieListOrder()){

            case MOST_POPULAR:
                menu.findItem(R.id.menu_most_popular).setChecked(true);
                break;
            case HIGHEST_RATED:
                menu.findItem(R.id.menu_highest_rated).setChecked(true);
                break;
            case FAVORITES:
                menu.findItem(R.id.menu_favorites).setChecked(true);
                break;

        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.menu_most_popular:
                item.setChecked(!item.isChecked());
                setMovieListOrder(MOST_POPULAR);
                return true;

            case R.id.menu_highest_rated:
                item.setChecked(!item.isChecked());
                setMovieListOrder(HIGHEST_RATED);
                return  true;

            case R.id.menu_favorites:
                item.setChecked(!item.isChecked());
                setMovieListOrder(FAVORITES);
                return  true;

        }

        return super.onOptionsItemSelected(item);
    }


    // Movie list order is stored in Preferences.
    public void setMovieListOrder(String movieListOrder) {
        getPreferences(Context.MODE_PRIVATE)
                .edit()
                .putString(SORT_BY, movieListOrder)
                .commit();

        loadNewMovieListFragment();

    }

    // By default, movies sort mode is by most popular
    public String getMovieListOrder() {
        return getPreferences(Context.MODE_PRIVATE)
                .getString(SORT_BY, MOST_POPULAR);
    }



    // Create the movie list fragment and add it to the activity
    public void loadNewMovieListFragment() {

        if(getMovieListOrder().equals(FAVORITES)) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.movie_list_fragment_container,new FavoriteMoviesFragment(), MOVIE_LIST_FRAGMENT_TAG)
                    .commit();

        }

        else {

            Bundle arguments = new Bundle();
            arguments.putString(MovieListFragment.SORT_BY,getMovieListOrder());

            mMovieListFragment = new MovieListFragment();
            mMovieListFragment.setArguments(arguments);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.movie_list_fragment_container, mMovieListFragment, MOVIE_LIST_FRAGMENT_TAG)
                    .commit();

        }


    }

    @Override
    public void onFavoritedMovie(Movie movie) {
        Log.d("wiii", "se envio el callback");
    }

}

