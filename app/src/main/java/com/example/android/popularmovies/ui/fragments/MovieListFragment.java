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
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.android.popularmovies.data.api.MoviesService;
import com.example.android.popularmovies.data.model.Movie;
import com.example.android.popularmovies.data.provider.movie.MovieColumns;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MovieListFragment extends BaseMovieListFragment{

    private ArrayList<Integer> mFavoriteMovieListIds;
    private String mMovieListOrder;
    private boolean mLoading = false;
    private boolean mFirstTime = true;

    public static final String SORT_BY = "sort_by";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMovieListOrder = getArguments().getString(SORT_BY);
        }

        EventBus.getDefault().register(this);
    }


    @Override
    public void initRecyclerView() {
        super.initRecyclerView();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                int totalItemCount = mGridLayoutManager.getItemCount();
                int lastVisibleItem = mGridLayoutManager.findLastVisibleItemPosition();

                if (totalItemCount > 1) {

                    if ((lastVisibleItem >= totalItemCount - 1) && (!mLoading)) {
                        fetchMovieList(mMovieListOrder, mMovieListPage);
                    }
                }
            }

        });

    }


    @Override
    public void onStart() {
        super.onStart();
        if (mFirstTime){
            fetchMovieList(mMovieListOrder,mMovieListPage);
            mFirstTime = false;
        }

    }

    private void fetchMovieList(String order, int page) {
        mLoading = true;
        MoviesService.
                getMoviesApiClient().
                getMovieList(order, page
                        , new Callback<List<Movie>>() {
                    @Override
                    public void success(List<Movie> movies, Response response) {

                        for (Movie movie : movies) {
                            if (mFavoriteMovieListIds.contains(movie.getMovieId())) {
                                movie.setFavorite(true);
                            }
                        }

                        mMovieListAdapter.add(movies);
                        mLoading = false;
                        mMovieListPage++;

                    }

                    @Override public void failure(RetrofitError error) {
                        if (error.getKind() == RetrofitError.Kind.NETWORK) {
                            if (error.getCause() instanceof SocketTimeoutException) {
                                Toast.makeText(getActivity(), error.toString() + "socketTimeout", Toast.LENGTH_SHORT).show();
                            } else {
                                //mRecyclerView.setEmptyView(mEmptyView);
                                mEmptyView.setVisibility(View.VISIBLE);
                                //mEmptyView.setMessageText(mNetworkError);
                                mProgressBar.setVisibility(View.GONE);
                            }
                        } else {
                            Toast.makeText(getActivity(), error.toString() + "error desconocido", Toast.LENGTH_SHORT).show();                        }

                    }
                });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIE_LIST, mMovieList);
        outState.putInt(PAGE, mMovieListPage);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        super.onLoadFinished(loader,cursor);

        if (cursor != null) {
            mFavoriteMovieListIds = new ArrayList<>();
            for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
                mFavoriteMovieListIds.add(cursor.getInt(cursor.getColumnIndex(MovieColumns.MOVIE_ID)));
            }

        }
    }

}