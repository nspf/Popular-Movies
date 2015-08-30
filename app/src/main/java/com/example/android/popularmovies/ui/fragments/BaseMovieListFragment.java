package com.example.android.popularmovies.ui.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.android.popularmovies.FavoriteMovieEvent;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.model.Movie;
import com.example.android.popularmovies.data.provider.movie.MovieColumns;
import com.example.android.popularmovies.ui.adapters.MovieListAdapter;
import com.example.android.popularmovies.ui.widgets.EmptyRecyclerView;
import com.example.android.popularmovies.ui.widgets.EmptyStateView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.BindInt;
import butterknife.BindString;
import butterknife.ButterKnife;


public class BaseMovieListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    protected ArrayList<Movie> mMovieList;
    protected MovieListAdapter mMovieListAdapter;
    protected GridLayoutManager mGridLayoutManager;
    protected int mMovieListPage = 1;


    protected final String MOVIE_LIST = "movie list";
    protected final String PAGE = "page";

    protected static final int MOVIES_LOADER = 0;


    @Bind(R.id.movie_list_recyclerview) EmptyRecyclerView mRecyclerView;
    @Bind(R.id.movie_list_progressbar) ProgressBar mProgressBar;
    @Bind(R.id.movie_list_empty_view) EmptyStateView mEmptyView;
    @BindInt(R.integer.movie_list_columns) int mMovieListColumns;
    @BindString(R.string.error_network) String mNetworkError;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mRootView = inflater.inflate(R.layout.fragment_movie_list, container, false);
        ButterKnife.bind(this, mRootView);
        return mRootView;
    }


    public void initRecyclerView() {
        mRecyclerView.setEmptyView(mProgressBar);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mMovieListAdapter);

    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        if (savedInstanceState == null) {
            mMovieList = new ArrayList<>();
            mMovieListPage = 1;
        } else {
            mMovieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST);
            mMovieListPage = savedInstanceState.getInt(PAGE);
        }

        mMovieListAdapter = new MovieListAdapter(getActivity(), mMovieList);
        mGridLayoutManager = new GridLayoutManager(getActivity(), mMovieListColumns);

        initRecyclerView();

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

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    @SuppressWarnings("unused")
    public void onEvent(FavoriteMovieEvent event) {
        mMovieListAdapter.setItemFavorite(mMovieListAdapter.getSelectedItem(), event.getValue().isFavorite());
    }

}
