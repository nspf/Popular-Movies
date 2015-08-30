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
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.popularmovies.MovieSelectedEvent;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.ui.fragments.BaseMovieListFragment;
import com.example.android.popularmovies.ui.fragments.FavoriteMoviesFragment;
import com.example.android.popularmovies.ui.fragments.MovieDetailFragment;
import com.example.android.popularmovies.ui.fragments.MovieListFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;


public class MovieListActivity extends AppCompatActivity {

    private final String SORT_BY = "sort_by";
    private final String MOST_POPULAR = "popularity.desc";
    private final String HIGHEST_RATED = "vote_average.desc";
    private final String MOST_RATED = "vote_count.desc";
    private final String MOVIE_LIST_FRAGMENT_TAG = "movieListFragment";

    private BaseMovieListFragment mMovieListFragment;
    private Boolean mTwoPane;

    private final int mMenuMostPopular = R.id.menu_most_popular;
    private final int mMenuHighestRated = R.id.menu_highest_rated;
    private final int mMenuMostRated = R.id.menu_most_rated;

    private final int mMenuFavorites = R.id.menu_favorites;

    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    @Bind(R.id.movie_list_toolbar) Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);


        if (findViewById(R.id.movie_detail_fragment_container) != null) {
            mTwoPane = true;
        }
        else {
            mTwoPane = false;
        }


        if(mToolbar != null) {
            setSupportActionBar(mToolbar);
        }

        if (savedInstanceState != null) {
            mMovieListFragment = (BaseMovieListFragment)
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
        MenuItem item = menu.findItem(getMovieListOrder());
        item.setChecked(true);
        mToolbar.setSubtitle(item.getTitle());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case mMenuMostPopular:
            case mMenuHighestRated:
            case mMenuMostRated:
            case mMenuFavorites:
                item.setChecked(!item.isChecked());
                setMovieListOrder(item.getItemId());
                mToolbar.setSubtitle(item.getTitle());
                return  true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // Movie list order is stored in Preferences.
    public void setMovieListOrder(int movieListOrder) {
        getPreferences(Context.MODE_PRIVATE)
                .edit()
                .putInt(SORT_BY, movieListOrder)
                .commit();

        loadNewMovieListFragment();
    }


    public Integer getMovieListOrder() {
        return getPreferences(Context.MODE_PRIVATE)
                .getInt(SORT_BY, mMenuMostPopular);
    }


    public void loadNewMovieListFragment() {

        switch(getMovieListOrder()) {
            case mMenuFavorites:
                loadFragment(new FavoriteMoviesFragment());
                break;
            case mMenuMostPopular:
                sortedMovieListFragment(MOST_POPULAR);
                break;
            case mMenuHighestRated:
                sortedMovieListFragment(HIGHEST_RATED);
                break;
            case mMenuMostRated:
                sortedMovieListFragment(MOST_RATED);
                break;

        }
        /*if (getMovieListOrder() == mMenuFavorites) {
            loadFragment(new FavoriteMoviesFragment());
        } else {
            sortedMovieListFragment(getMovieListOrder());

        }*/

    }

    public void sortedMovieListFragment(String sortMode) {

        Bundle arguments = new Bundle();
        arguments.putString(MovieListFragment.SORT_BY, sortMode);

        mMovieListFragment = new MovieListFragment();
        mMovieListFragment.setArguments(arguments);
        loadFragment(mMovieListFragment);

    }

    public void loadFragment(Fragment fragment) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.movie_list_fragment_container, fragment, MOVIE_LIST_FRAGMENT_TAG)
                .commit();
    }


    public void onEvent(MovieSelectedEvent event) {

        if(mTwoPane) {

            Bundle arguments = new Bundle();
            arguments.putParcelable(MovieDetailFragment.MOVIE, event.getValue());

            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_fragment_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();

        }

        else {
            Intent intent = new Intent(this, MovieDetailActivity.class);
                    intent.putExtra(MovieDetailFragment.MOVIE, event.getValue());
                    startActivity(intent);
        }
    }

}

