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

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.event.MovieSelectedEvent;
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
    private final String FAVORITES = "favorites";
    private final String MOVIE_LIST_FRAGMENT_TAG = "movieListFragment";

    private Fragment mFragment;
    private Boolean mTwoPane;

    private final int mMenuMostPopular = R.id.menu_most_popular;
    private final int mMenuHighestRated = R.id.menu_highest_rated;
    private final int mMenuMostRated = R.id.menu_most_rated;
    private final int mMenuFavorites = R.id.menu_favorites;
    private final int mMovieDetailContainer = R.id.movie_detail_fragment_container;
    private final int mMovieListContainer = R.id.movie_list_fragment_container;

    @Bind(R.id.movie_list_toolbar) Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        ButterKnife.bind(this);

        mTwoPane = findViewById(mMovieDetailContainer) != null;

        if(mToolbar != null) {
            setSupportActionBar(mToolbar);
        }

        if (savedInstanceState != null) {
            mFragment = getSupportFragmentManager().findFragmentByTag(MOVIE_LIST_FRAGMENT_TAG);
        }
        else {
            loadNewMovieListFragment();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_movie_list, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        super.onPrepareOptionsMenu(menu);
        switch(getMovieListOrder()) {

            case MOST_POPULAR:
                setMovieSortMode(menu.findItem(mMenuMostPopular));
                break;

            case HIGHEST_RATED:
                setMovieSortMode(menu.findItem(mMenuHighestRated));
                break;

            case MOST_RATED:
                setMovieSortMode(menu.findItem(mMenuMostRated));
                break;

            case FAVORITES:
                setMovieSortMode(menu.findItem(mMenuFavorites));
                break;
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case mMenuMostPopular:
                setMovieListOrder(MOST_POPULAR);
                break;

            case mMenuHighestRated:
                setMovieListOrder(HIGHEST_RATED);
                break;

            case mMenuMostRated:
                setMovieListOrder(MOST_RATED);
                break;

            case mMenuFavorites:
                setMovieListOrder(FAVORITES);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        setMovieSortMode(item);
        return  true;
    }


    public void setMovieSortMode(MenuItem item) {
        item.setChecked(!item.isChecked());
        mToolbar.setSubtitle(item.getTitle());
    }


    // Movie list order is stored in Preferences.
    public void setMovieListOrder(String movieListOrder) {
        getPreferences(Context.MODE_PRIVATE)
                .edit()
                .putString(SORT_BY, movieListOrder)
                .commit();

        loadNewMovieListFragment();
    }


    public String getMovieListOrder() {
        return getPreferences(Context.MODE_PRIVATE)
                .getString(SORT_BY, MOST_POPULAR);
    }


    public void loadNewMovieListFragment() {

        switch(getMovieListOrder()) {
            case FAVORITES:
                mFragment = new FavoriteMoviesFragment();
                loadFragment(mFragment, mMovieListContainer, MOVIE_LIST_FRAGMENT_TAG);
                break;

            default:
                sortedMovieListFragment(getMovieListOrder());
                break;

        }
    }

    public void sortedMovieListFragment(String sortMode) {

        Bundle arguments = new Bundle();
        arguments.putString(MovieListFragment.SORT_BY, sortMode);

        mFragment = new MovieListFragment();
        mFragment.setArguments(arguments);
        loadFragment(mFragment, mMovieListContainer, MOVIE_LIST_FRAGMENT_TAG);

    }


    @SuppressWarnings("unused")
    public void onEvent(MovieSelectedEvent event) {

        if(mTwoPane) {

            Bundle arguments = new Bundle();
            arguments.putParcelable(MovieDetailFragment.MOVIE, event.getMovie());

            MovieDetailFragment fragment = new MovieDetailFragment();

            fragment.setArguments(arguments);
            loadFragment(fragment, mMovieDetailContainer, MovieDetailFragment.MOVIE);

        }

        else {
            Intent intent = new Intent(this, MovieDetailActivity.class);
                    intent.putExtra(MovieDetailFragment.MOVIE, event.getMovie());
                    startActivity(intent);
        }
    }


    public void loadFragment(Fragment fragment, int container, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(container, fragment, tag)
                .commit();
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

}