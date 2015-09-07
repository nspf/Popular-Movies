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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.api.MoviesService;
import com.example.android.popularmovies.data.event.FavoriteMovieEvent;
import com.example.android.popularmovies.data.model.Genre;
import com.example.android.popularmovies.data.model.Movie;
import com.example.android.popularmovies.data.model.MovieData;
import com.example.android.popularmovies.data.model.MovieHelper;
import com.example.android.popularmovies.data.model.Result;
import com.example.android.popularmovies.data.model.Youtube;
import com.example.android.popularmovies.ui.widgets.ImageViewKeepRatio;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.BindBool;
import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MovieDetailFragment extends Fragment {

    private static final EventBus bus = EventBus.getDefault();
    public static final String MOVIE = "movie";
    public static final String YOUTUBE_VIDEO_URL = "http://www.youtube.com/watch?v=";
    public static final String TMDB_MOVIE_URL = "https://www.themoviedb.org/movie/";

    private Movie               mMovieDetail;
    private MovieHelper         mMovieHelper;
    private List<Result>        mReviews;
    private List<Youtube>       mTrailers;
    private List<String>        mGenres;
    private ShareActionProvider mShareActionProvider;
    private MenuItem            mMenuItemShare;

    @Bind(R.id.movie_detail_image_backdrop)             ImageViewKeepRatio mMovieBackdrop;
    @Bind(R.id.movie_detail_image_backdrop_play)        ImageView mMovieBackdropPlay;
    @Bind(R.id.movie_detail_image_poster)               ImageView mMoviePoster;
    @Bind(R.id.movie_detail_text_title)                 TextView mMovieTitle;
    @Bind(R.id.movie_detail_text_date)                  TextView mMovieDate;
    @Bind(R.id.movie_detail_text_duration)              TextView mMovieDuration;
    @Bind(R.id.movie_detail_text_genres)                TextView mMovieGenres;
    @Bind(R.id.movie_detail_text_rating)                TextView mMovieRating;
    @Bind(R.id.movie_detail_text_votecount)             TextView mMovieVoteCount;
    @Bind(R.id.movie_detail_rating)                     RatingBar mMovieRatingBar;
    @Bind(R.id.movie_detail_text_tagline)               TextView mMovieTagline;
    @Bind(R.id.movie_detail_text_synopsis)              TextView mMovieSynopsis;
    @Bind(R.id.movie_detail_collapsing_toolbar)         CollapsingToolbarLayout mCollapsingToolbar;
    @Bind(R.id.movie_detail_trailer_container)          LinearLayout mMovieTrailerContainer;
    @Bind(R.id.movie_detail_review_container)           LinearLayout mMovieReviewContainer;
    @Bind(R.id.movie_detail_favorite_button)            FloatingActionButton mFavoritebutton;

    @Nullable @Bind(R.id.movie_detail_toolbar)          Toolbar mToolbar;

    @BindDrawable(R.drawable.no_poster)                 Drawable mMovieErrorImg;
    @BindColor(R.color.transparent)                     int mColorTransparent;
    @BindString(R.string.movie_detail_minutes)                              String mMinutes;
    @BindBool(R.bool.movie_detail_toolbar_enabled)      boolean mToolbarEnabled;


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
        bus.register(this);
    }

    @Override
    public void onStop() {
        bus.unregister(this);
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mToolbar != null && mToolbarEnabled) {
            ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
            ActionBar mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (mActionBar != null){
                mActionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        mMovieHelper = new MovieHelper(getActivity());

        mCollapsingToolbar.setTitle(mMovieDetail.getTitle());

        if (mToolbarEnabled) {
            mCollapsingToolbar.setExpandedTitleColor(mColorTransparent);
        }

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
        mMovieRatingBar.setRating(Float.valueOf("" + mMovieDetail.getVoteAverage()) / 2);
        mMovieSynopsis.setText(mMovieDetail.getOverview());

        setFavoriteButton(mMovieDetail.isFavorite());
        mFavoritebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFavorite();
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

                        if (movieData.getRuntime() != null) {
                            mMovieDuration.setText(movieData.getRuntime() + " " + mMinutes);
                        }

                        mGenres = new ArrayList<>();

                        for (final Genre genre : movieData.getGenres()) {
                            mGenres.add(genre.getName());
                        }

                        mMovieGenres.setText(TextUtils.join(", ", mGenres));
                        mMovieTagline.setText(movieData.getTagline());
                        mMovieVoteCount.setText(movieData.getVoteCount().toString());


                        //Show movie reviews if available
                        if (movieData.getReviews().getResults().size() > 0) {
                            mReviews = movieData.getReviews().getResults();
                            loadMovieReviews();
                        }


                        //Show Youtube movie trailers if available
                        if (movieData.getTrailers().getYoutube().size() > 0) {
                            mTrailers = movieData.getTrailers().getYoutube();
                            loadMovieTrailers();

                            //Enable play button in backdrop image
                            mMovieBackdropPlay.setVisibility(View.VISIBLE);
                            mMovieBackdropPlay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    playYoutubeTrailerIntent(mTrailers.get(0).getSource());
                                }
                            });

                            //Share trailer option
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


    private void loadMovieReviews() {
        for (final Result review : mReviews) {

            View mMovieReviewItem = LayoutInflater.from(getActivity()).inflate(
                    R.layout.movie_review_item, null);

            TextView mMovieReviewAuthor = (TextView) mMovieReviewItem.findViewById(R.id.movie_review_text_author);
            mMovieReviewAuthor.setText(review.getAuthor());

            TextView mMovieReviewUrl = (TextView) mMovieReviewItem.findViewById(R.id.movie_review_text_url);
            mMovieReviewUrl.setText(review.getUrl());

            TextView mMovieReviewContent = (TextView) mMovieReviewItem.findViewById(R.id.movie_review_text_content);
            mMovieReviewContent.setText(review.getContent());

            mMovieReviewContainer.addView(mMovieReviewItem);
        }
    }

    private void loadMovieTrailers() {
        for (final Youtube video : mTrailers) {

            View mMovieTrailerItem = LayoutInflater.from(getActivity()).inflate(
                    R.layout.movie_trailer_item, null);

            mMovieTrailerItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playYoutubeTrailerIntent(video.getSource());
                }
            });

            TextView mMovieTrailerTitle = (TextView) mMovieTrailerItem.findViewById(R.id.movie_trailer_text_title);
            mMovieTrailerTitle.setText(video.getName());

            mMovieTrailerContainer.addView(mMovieTrailerItem);
        }
    }


    public void setFavorite() {
        Boolean isFavorite = mMovieHelper.setFavorite(mMovieDetail);
        mMovieDetail.setFavorite(!mMovieDetail.isFavorite());
        setFavoriteButton(isFavorite);
        bus.post(new FavoriteMovieEvent(mMovieDetail));
    }

    public void setFavoriteButton(Boolean isFavorite) {
        if(isFavorite) {
            mFavoritebutton.setPressed(true);
            mFavoritebutton.setImageResource(R.drawable.ic_bookmark_check_white_24dp);
        }
        else {
            mFavoritebutton.setPressed(false);
            mFavoritebutton.setImageResource(R.drawable.ic_bookmark_outline_white_24dp);
        }
    }


    @SuppressWarnings("unused")
    public void onEvent(FavoriteMovieEvent event){
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_movie_detail, menu);

        mMenuItemShare = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(mMenuItemShare);

        // Only show share trailer menu if there are available trailers
        if (mTrailers != null) {
            mShareActionProvider.setShareIntent(createShareTrailerIntent(mTrailers.get(0).getSource()));
            mMenuItemShare.setVisible(true);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {

            case R.id.open_in_browser:
                viewMovieInBrowserIntent(""+mMovieDetail.getMovieId());
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }


    private Intent createShareTrailerIntent(String video_id) {
        return new Intent(Intent.ACTION_SEND)
                .setType("text/plain")
                .putExtra(Intent.EXTRA_TEXT, mMovieDetail.getTitle() + "\n\n" + YOUTUBE_VIDEO_URL + video_id);
    }

    private void playYoutubeTrailerIntent(String youtube_id) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_VIDEO_URL + youtube_id)));

    }

    private void viewMovieInBrowserIntent(String video_id) {
        String url = TMDB_MOVIE_URL + video_id;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

}