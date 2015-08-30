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

import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.FavoriteMovieEvent;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.api.MoviesService;
import com.example.android.popularmovies.data.model.Movie;
import com.example.android.popularmovies.data.model.MovieData;
import com.example.android.popularmovies.data.model.Youtube;
import com.example.android.popularmovies.data.provider.movie.MovieColumns;
import com.example.android.popularmovies.data.provider.movie.MovieContentValues;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.chalup.microorm.MicroOrm;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MovieDetailFragment extends Fragment /*implements LoaderManager.LoaderCallbacks<Cursor> */{

    private static final EventBus bus = EventBus.getDefault();
    public static final String MOVIE = "movie";
    private static final int DETAIL_LOADER = 1;

    private Movie mMovieDetail;
    private View mView;
    private Uri mUri;
    private int mPosition;
    MicroOrm mOrm;

    @Bind(R.id.movie_detail_image_backdrop)     ImageView mMovieBackdrop;
    @Bind(R.id.movie_detail_image_poster)       ImageView mMoviePoster;
    @Bind(R.id.movie_detail_text_title)         TextView mMovieTitle;
    @Bind(R.id.movie_detail_text_date)          TextView mMovieDate;
    @Bind(R.id.movie_detail_text_rating)        TextView mMovieRating;
    @Bind(R.id.movie_detail_text_synopsis)      TextView mMovieSynopsis;
    @Nullable @Bind(R.id.movie_detail_toolbar)            Toolbar mToolbar;
    @Bind(R.id.movie_detail_collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbar;
    @Bind(R.id.movie_detail_trailer_container)  LinearLayout mMovieTrailerContainer;
    @Bind(R.id.movie_detail_favorite_button) FloatingActionButton mFavoritebutton;


    @BindDrawable(R.drawable.no_poster)         Drawable mMovieErrorImg;
    @BindColor(R.color.transparent) int mColorTransparent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            //Bundle bundle = this.getArguments();
            //mUri = bundle.getParcelable(MOVIE);
            mMovieDetail = getArguments().getParcelable(MOVIE);
            Log.d("detail",mMovieDetail.toString());
            ///mPosition = bundle.getInt("id");
            EventBus.getDefault().register(this);

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //////getLoaderManager().initLoader(DETAIL_LOADER, null, this);

        if (mToolbar != null) {
            ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
            ActionBar mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (mActionBar != null){
                mActionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        //((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);



        mCollapsingToolbar.setTitle(mMovieDetail.getTitle());


        mCollapsingToolbar.setExpandedTitleColor(mColorTransparent);

        Picasso.with(getActivity())
                .load(mMovieDetail.getFullBackdropPath())
                .error(mMovieErrorImg)
                .into(mMovieBackdrop, new Callback() {

                    @Override
                    public void onSuccess() {

                        Bitmap bitmap = ((BitmapDrawable) mMovieBackdrop.getDrawable()).getBitmap();
                        new Palette.Builder(bitmap).generate(new Palette.PaletteAsyncListener() {

                            @Override
                            public void onGenerated(Palette palette) {

                                Palette.Swatch vibrant = palette.getVibrantSwatch();
                                if (vibrant != null) {
                                    mCollapsingToolbar.setContentScrimColor(vibrant.getRgb());
                                }
                            }
                        });
                    }

                    @Override
                    public void onError() {

                    }
                });

        Picasso.with(getActivity())
                .load(mMovieDetail.getFullPosterPath())
                .error(mMovieErrorImg)
                .into(mMoviePoster);

        mMovieTitle.setText(mMovieDetail.getTitle());
        mMovieDate.setText(mMovieDetail.getYearFromReleaseDate());
        mMovieRating.setText("" + mMovieDetail.getVoteAverage());
        mMovieSynopsis.setText(mMovieDetail.getOverview());

        Log.i("movie id", "" + mMovieDetail.getMovieId());

        if(mMovieDetail.isFavorite()) {
            mFavoritebutton.setPressed(true);
        }
        else {
            mFavoritebutton.setPressed(false);
        }

        mFavoritebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Movie movie = Movie.select(mMovieDetail.getMovieId());
                        setFavorite(mMovieDetail, true);
            }
        });


        //fetchMovieData();

    }


    private void fetchMovieData(Long movie_id) {

        MoviesService
                .getMoviesApiClient()
                .getMovieData(movie_id, new retrofit.Callback<MovieData>() {
                    @Override
                    public void success(final MovieData movieData, Response response) {

                        /*Log.d("trailers", "" + movieData.getTrailers().getYoutube().size());
                        Log.d("reviews", "" + movieData.getReviews().getResults().size());
                        Log.d("tagline", "" + movieData.getTagline());
                        Log.d("homepage", "" + movieData.getHomepage());
                        Log.d("video", "" + movieData.getVideo());*/


                        //TODO: if videos.videos.size() > 0 , hasTrailers = true

                        //TODO: loop for, only if reponse has trailers
                        //If so, the backdrop image must show a play icon
                        for (final Youtube video : movieData.getTrailers().getYoutube()) {

                            //TODO: find a shorter way to to this
                            View mMovieTrailerItem = mView.inflate(getActivity(), R.layout.movie_trailer_item, null);
                            mMovieTrailerItem.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + video.getSource())));
                                }
                            });

                            TextView mMovieTrailerTitle = (TextView) mMovieTrailerItem.findViewById(R.id.movie_trailer_text_title);
                            mMovieTrailerTitle.setText(video.getName());

                            mMovieTrailerContainer.addView(mMovieTrailerItem);

                        }


                        //TODO: if hasTrailers = true, enable clicListener and show play button in backdrop
                        /*mMovieBackdrop.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + videos.videos.get(0).getKey())));
                            }
                        });*/
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        Toast.makeText(getActivity(), retrofitError.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }


    /*@Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args){
        String selection = null;
        String [] selectionArgs = null;
        if (args != null){
            selection = "id";
            selectionArgs = new String[]{String.valueOf(mPosition)};
        }
        return new CursorLoader(getActivity(),
                mUri,
                null,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor != null) {

            final Cursor data = cursor;
            data.moveToPosition(mPosition);

            mCollapsingToolbar.setTitle(data.getString(data.getColumnIndex("title")));
            mCollapsingToolbar.setExpandedTitleColor(mColorTransparent);

            Picasso.with(getActivity())
                    .load(data.getString(data.getColumnIndex("backdrop_path")))
                    .error(mMovieErrorImg)
                    .into(mMovieBackdrop, new Callback() {

                        @Override
                        public void onSuccess() {

                            Bitmap bitmap = ((BitmapDrawable) mMovieBackdrop.getDrawable()).getBitmap();
                            new Palette.Builder(bitmap).generate(new Palette.PaletteAsyncListener() {

                                @Override
                                public void onGenerated(Palette palette) {

                                    Palette.Swatch vibrant = palette.getVibrantSwatch();
                                    if (vibrant != null) {
                                        mCollapsingToolbar.setContentScrimColor(vibrant.getRgb());
                                    }
                                }
                            });
                        }

                        @Override
                        public void onError() {

                        }
                    });

            Picasso.with(getActivity())
                    .load(data.getString(data.getColumnIndex("poster_path")))
                    .error(mMovieErrorImg)
                    .into(mMoviePoster);

            mMovieTitle.setText(data.getString(data.getColumnIndex("title")));
            mMovieDate.setText(data.getString(data.getColumnIndex("release_date")));
            mMovieRating.setText(data.getString(data.getColumnIndex("vote_average")));
            mMovieSynopsis.setText(data.getString(data.getColumnIndex("overview")));

            mFavoritebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setFavorite(Movie.select(data.getInt(data.getColumnIndex("movie_id"))),true);
                }
            });

            fetchMovieData(data.getLong(data.getColumnIndex("movie_id")));


        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }*/

    public void setFavorite(Movie movie, Boolean favorite) {
        if(movie.isFavorite()) {
            movie.setFavorite(false);

            Toast.makeText(getActivity(),"REMOVED "+movie.getTitle(),Toast.LENGTH_LONG).show();
            Uri uri = ContentUris.withAppendedId(MovieColumns.CONTENT_URI, movie.getMovieId());
            String where = MovieColumns.MOVIE_ID + "=?";
            int rows = getActivity()
                    .getContentResolver()
                    .delete(MovieColumns.CONTENT_URI, where, new String[] {String.valueOf(movie.getMovieId())});

            Log.i("delete", "Rows deleted: " + rows);

            getActivity().getContentResolver().notifyChange(uri, null);

            mFavoritebutton.setPressed(false);
        }
        else {
            Toast.makeText(getActivity(),"ADDED "+movie.getTitle(),Toast.LENGTH_LONG).show();
            movie.setFavorite(true);
            //////movie.save();

            MovieContentValues values = new MovieContentValues();

            values.putMovieId(movie.getMovieId());
            values.putAdult(movie.getAdult());
            values.putBackdropPath(movie.getBackdropPath());
            values.putOriginalLanguage(movie.getOriginalLanguage());
            values.putOriginalTitle(movie.getOriginalTitle());
            values.putOverview(movie.getOverview());
            values.putReleaseDate(movie.getReleaseDate());
            values.putPosterPath(movie.getPosterPath());
            values.putPopularity(movie.getPopularity());
            values.putTitle(movie.getTitle());
            values.putVideo(movie.getVideo());
            values.putVoteAverage(movie.getVoteAverage());
            values.putVoteCount(movie.getVoteCount());
            values.putFavorite(movie.isFavorite());

            getActivity()
                    .getContentResolver()
                    .insert(MovieColumns.CONTENT_URI, values.values());

            mFavoritebutton.setPressed(true);
        }

Log.d("movie id", movie.getMovieId()+"");
        bus.post(new FavoriteMovieEvent(movie));

    }

    @OnClick(R.id.movie_detail_favorite_button)
    public void onFavoriteButtonClick(View view) {
        Log.d("clic","true");


    }


    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface mCallback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onFavoritedMovie(Movie movie);
    }


    public void onEvent(FavoriteMovieEvent event){
        event.getValue();
        Log.d("getvalue",""+event.getValue());
    }


}
