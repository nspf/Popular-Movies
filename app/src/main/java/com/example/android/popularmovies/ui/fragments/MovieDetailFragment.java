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
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.example.android.popularmovies.data.model.Result;
import com.example.android.popularmovies.data.model.Youtube;
import com.example.android.popularmovies.data.provider.movie.MovieColumns;
import com.example.android.popularmovies.data.provider.movie.MovieContentValues;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MovieDetailFragment extends Fragment {

    private static final EventBus bus = EventBus.getDefault();
    public static final String MOVIE = "movie";
    //private static final int DETAIL_LOADER = 1;

    private Movie mMovieDetail;
    private List<Result> mReviews;
    private List<Youtube> mTrailers;
    private ShareActionProvider mShareActionProvider;
    MenuItem mMenuItemShare;

    private View mView;

    @Bind(R.id.movie_detail_image_backdrop)     ImageView mMovieBackdrop;
    @Bind(R.id.movie_detail_image_poster)       ImageView mMoviePoster;
    @Bind(R.id.movie_detail_text_title)         TextView mMovieTitle;
    @Bind(R.id.movie_detail_text_date)          TextView mMovieDate;
    @Bind(R.id.movie_detail_text_rating)        TextView mMovieRating;
    @Bind(R.id.movie_detail_text_synopsis)      TextView mMovieSynopsis;
    @Bind(R.id.movie_detail_collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbar;
    @Bind(R.id.movie_detail_trailer_container)  LinearLayout mMovieTrailerContainer;
    @Bind(R.id.movie_detail_review_container)  LinearLayout mMovieReviewContainer;
    @Bind(R.id.movie_detail_favorite_button) FloatingActionButton mFavoritebutton;

    @Nullable @Bind(R.id.movie_detail_toolbar)            Toolbar mToolbar;

    @BindDrawable(R.drawable.no_poster)         Drawable mMovieErrorImg;
    @BindColor(R.color.transparent) int mColorTransparent;

    public MovieDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMovieDetail = getArguments().getParcelable(MOVIE);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
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

        if (mToolbar != null) {
            ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
            ActionBar mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (mActionBar != null){
                mActionBar.setDisplayHomeAsUpEnabled(true);
            }
        }


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


        if(mMovieDetail.isFavorite()) {
            mFavoritebutton.setPressed(true);
        }
        else {
            mFavoritebutton.setPressed(false);
        }

        mFavoritebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFavorite(mMovieDetail, true);
            }
        });



        fetchMovieData(mMovieDetail.getMovieId().longValue());

    }


    private void fetchMovieData(Long movie_id) {

        MoviesService
                .getMoviesApiClient()
                .getMovieData(movie_id, new retrofit.Callback<MovieData>() {
                    @Override
                    public void success(final MovieData movieData, Response response) {

                        Log.d("tagline", "" + movieData.getTagline());
                        Log.d("homepage", "" + movieData.getHomepage());
                        Log.d("video", "" + movieData.getVideo());


                        if (movieData.getReviews().getResults().size() > 0) {
                            mReviews = movieData.getReviews().getResults();

                            for (final Result review : mReviews) {

                                View mMovieReviewItem = LayoutInflater.from(getActivity()).inflate(
                                        R.layout.movie_review_item, null);

                                TextView mMovieReviewAuthor = (TextView) mMovieReviewItem.findViewById(R.id.movie_review_text_author);
                                mMovieReviewAuthor.setText(review.getAuthor());

                                TextView mMovieReviewUrl = (TextView) mMovieReviewItem.findViewById(R.id.movie_review_text_url);
                                mMovieReviewUrl.setText(review.getUrl());

                                TextView mMovieReviewContent = (TextView) mMovieReviewItem.findViewById(R.id.movie_review_text_content);
                                mMovieReviewContent.setText(review.getContent());

                                mMovieReviewContent.setSingleLine(false);
                                mMovieReviewContent.setEllipsize(TextUtils.TruncateAt.END);
                                int n = 4; // TODO: declase this value in xml for flexible UI
                                mMovieReviewContent.setLines(n);

                                mMovieReviewContainer.addView(mMovieReviewItem);

                            }
                        }


                        if (movieData.getTrailers().getYoutube().size() > 0) {
                            mTrailers = movieData.getTrailers().getYoutube();

                            for (final Youtube video : mTrailers) {

                                View mMovieTrailerItem = LayoutInflater.from(getActivity()).inflate(
                                        R.layout.movie_trailer_item, null);

                                mMovieTrailerItem.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //TODO: make a share trailer intent function
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + video.getSource())));
                                    }
                                });

                                TextView mMovieTrailerTitle = (TextView) mMovieTrailerItem.findViewById(R.id.movie_trailer_text_title);
                                mMovieTrailerTitle.setText(video.getName());

                                mMovieTrailerContainer.addView(mMovieTrailerItem);
                            }

                            //Enable play button in backdrop image
                            mMovieBackdrop.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + mTrailers.get(0).getSource())));
                                }
                            });

                            //Share trailer
                            if (mShareActionProvider != null) {
                                mShareActionProvider.setShareIntent(createShareTrailerIntent(mTrailers.get(0).getSource()));
                                mMenuItemShare.setVisible(true);
                            }

                        }
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        Toast.makeText(getActivity(), retrofitError.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }


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

        bus.post(new FavoriteMovieEvent(movie));

    }

    /*@OnClick(R.id.movie_detail_favorite_button)
    public void onFavoriteButtonClick(View view) {
    }*/

    public void onEvent(FavoriteMovieEvent event){
        event.getValue();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }


    //Share menu

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_movie_detail_fragment, menu);

        // Retrieve the share menu item
        mMenuItemShare = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(mMenuItemShare);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        /*if (mForecast != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }*/
    }

    private Intent createShareTrailerIntent(String video_id) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mMovieDetail.getTitle() + "\n\n" + "http://www.youtube.com/watch?v=" + video_id);
        return shareIntent;
    }


}
