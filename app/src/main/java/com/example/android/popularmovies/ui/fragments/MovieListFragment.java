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

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
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
import android.widget.Toast;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.api.MoviesService;
import com.example.android.popularmovies.data.model.Movie;
import com.example.android.popularmovies.data.provider.MoviesProvider;
import com.example.android.popularmovies.ui.adapters.MovieListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MovieListFragment extends Fragment {

    private ArrayList<Movie> mMovieList;
    private MovieListAdapter mMovieListAdapter;
    MovieListLoader mMovieListLoader;

    private String mMovieListOrder;
    private int mMovieListPage = 1;
    private int mMovieListColumns = 2;

    private GridLayoutManager mGridLayoutManager;
    MoviesProvider mMoviesProvider;

    private boolean mLoading = false;
    private boolean mFirstTime = true;

    private final String MOVIE_LIST = "movie list";
    private final String PAGE = "page";
    public static final String SORT_BY = "sort_by";
    private final int MOVIE_LIST_LOADER = 0;

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
        getLoaderManager().initLoader(MOVIE_LIST_LOADER, null, mMovieListLoader);
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

        //LOADER
        mMovieListLoader = new MovieListLoader();
        getActivity().getSupportLoaderManager().initLoader(0, null, mMovieListLoader);
        //

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

        return mRootView;
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
        MoviesService
                .getMoviesApiClient()
                .getMovieList(order, page
                        , new Callback<List<Movie>>() {
                    @Override
                    public void success(List<Movie> movies, Response response) {
                        mMovieListAdapter.add(movies);
                        mLoading = false;
                        mMovieListPage++;

                        Uri uri = Uri.parse("content://" + getString(R.string.authority) + "/movies");
                        Vector<ContentValues> cVVector = new Vector<ContentValues>(movies.size());


                        if ( movies.size() > 0 ) {
                            for (Movie movie : movies){

                                ContentValues values = new ContentValues();

                                values.put("adult", movie.getAdult());
                                values.put("backdrop_path", movie.getFullBackdropPath());
                                values.put("original_language", movie.getOriginalLanguage());
                                values.put("original_title", movie.getOriginalTitle());
                                values.put("overview", movie.getOverview());
                                values.put("release_date", movie.getReleaseDate());
                                values.put("poster_path", movie.getFullPosterPath());
                                values.put("popularity", movie.getPopularity());
                                values.put("title", movie.getTitle());
                                values.put("video", movie.getVideo());
                                values.put("vote_average", movie.getVoteAverage());
                                values.put("vote_count", movie.getVoteCount());

                                cVVector.add(values);

                            }

                            int inserted = 0;

                            if ( cVVector.size() > 0 ) {
                                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                                cVVector.toArray(cvArray);
                                inserted = getActivity().getContentResolver().bulkInsert(uri, cvArray);
                            }

                            Log.d("INSERT", "fethMovieList Complete. " + inserted + " Inserted");

                            /*for (Movie movie : movies) {
                                getActivity().getContentResolver().bulkInsert(uri, movie);

                            }*/
                        }









                        //Add to database

                        /*for (Movie movie : movies.getResults()) {
                            movie.save();



                        }*/


                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        Toast.makeText(getActivity(), retrofitError.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("retrofiterror",retrofitError.getMessage());

                    }
                });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIE_LIST, mMovieList);
        outState.putInt(PAGE, mMovieListPage);
    }


    /// LOADER

    public class MovieListLoader implements LoaderManager.LoaderCallbacks<Cursor> {

        /*@Override
        public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
            return new ModelLoader<Movie>(getActivity(), Movie.class, true);

        }*/

        public Loader<Cursor> onCreateLoader(int arg0, Bundle cursor) {
            Uri uri = Uri.parse("content://" + getString(R.string.authority) + "/movies");
            return new CursorLoader(getActivity(), uri, null, null, null, null);
        }


        @Override
        public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
            //((mMovieListAdapter)mySpinner.getAdapter()).swapCursor(cursor);
            mMovieListAdapter.swapCursor(cursor);

        }

        @Override
        public void onLoaderReset(Loader<Cursor> arg0) {
            //((SimpleCursorAdapter)mySpinner.getAdapter()).swapCursor(null);
        }

        /*@Override
        public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {

            Log.i("DEBUG", "Fetched " + data.size());
            //mMovieListAdapter.clear();
            mMovieListAdapter.add(data);
            //mMovieListAdapter.notifyDataSetChanged();
        }

        @Override
        public void onLoaderReset(Loader<List<Movie>> arg0) {
            mMovieListAdapter.clear();
            mMovieListAdapter.notifyDataSetChanged();
        }*/

    }

}