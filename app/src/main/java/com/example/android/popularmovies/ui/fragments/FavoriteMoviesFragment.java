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

package com.example.android.popularmovies.ui.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.model.Movie;
import com.example.android.popularmovies.data.provider.movie.MovieColumns;
import com.example.android.popularmovies.ui.adapters.MovieListAdapter;

import org.chalup.microorm.MicroOrm;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FavoriteMoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private ArrayList<Movie> mMovieList;
    private MovieListAdapter mMovieListAdapter;
    private String mMovieListOrder;
    private int mMovieListPage = 1;
    private int mMovieListColumns = 2;

    private GridLayoutManager mGridLayoutManager;

    private boolean mLoading = false;
    private boolean mFirstTime = true;

    MicroOrm mOrm = new MicroOrm();

    private final String MOVIE_LIST = "movie list";
    private final String PAGE = "page";
    public static final String SORT_BY = "sort_by";

    private static final int FAVORITE_MOVIES_LOADER = 0;

    @Bind(R.id.movie_list_recyclerview) RecyclerView mRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mMovieListOrder = getArguments().getString(SORT_BY);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FAVORITE_MOVIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mRootView = inflater.inflate(R.layout.fragment_movie_list, container, false);
        ButterKnife.bind(this, mRootView);

        if (savedInstanceState == null) {
            mMovieList = new ArrayList<>();
            mMovieListPage = 1;
        } else {
            mMovieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST);
            mMovieListPage = savedInstanceState.getInt(PAGE);
        }

        mMovieListAdapter = new MovieListAdapter(getActivity(), mMovieList);

        mGridLayoutManager = new GridLayoutManager(getActivity(), mMovieListColumns);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mMovieListAdapter);
        /*mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                int totalItemCount = mGridLayoutManager.getItemCount();
                int lastVisibleItem = mGridLayoutManager.findLastVisibleItemPosition();

                if (totalItemCount > 1) {

                    if ((lastVisibleItem >= totalItemCount - 1) && (!mLoading)) {
                        //fetchMovieList(mMovieListOrder, mMovieListPage);
                    }
                }
            }

        });*/

        return mRootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mFirstTime){
            //fetchMovieList(mMovieListOrder,mMovieListPage);
            mFirstTime = false;
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIE_LIST, mMovieList);
        outState.putInt(PAGE, mMovieListPage);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args){

        return new CursorLoader(getActivity(),
                MovieColumns.CONTENT_URI,
                MovieColumns.ALL_COLUMNS,
                null,
                null,
                null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor != null) {
            List<Movie> favoriteMovies = mOrm.listFromCursor(cursor, Movie.class);
            mMovieList = new ArrayList<>(favoriteMovies);
            mMovieListAdapter.add(mMovieList);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }





}
