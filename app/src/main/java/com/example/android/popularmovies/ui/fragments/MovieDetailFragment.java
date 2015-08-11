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

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.model.Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.ButterKnife;

public class MovieDetailFragment extends Fragment {

    public static final String MOVIE = "movie";

    private Movie mMovieDetail;

    @Bind(R.id.movie_detail_image_backdrop)     ImageView mMovieBackdrop;
    @Bind(R.id.movie_detail_image_poster)       ImageView mMoviePoster;
    @Bind(R.id.movie_detail_text_title)         TextView mMovieTitle;
    @Bind(R.id.movie_detail_text_date)          TextView mMovieDate;
    @Bind(R.id.movie_detail_text_rating)        TextView mMovieRating;
    @Bind(R.id.movie_detail_text_synopsis)      TextView mMovieSynopsis;
    @Bind(R.id.movie_detail_toolbar)            Toolbar mToolbar;
    @Bind(R.id.movie_detail_collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbar;

    @BindDrawable(R.drawable.no_poster)         Drawable mMovieErrorImg;
    @BindColor(R.color.transparent) int mColorTransparent;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMovieDetail = getArguments().getParcelable(MOVIE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);

        ActionBar mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (mActionBar != null){
            mActionBar.setDisplayHomeAsUpEnabled(true);
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
        mMovieRating.setText(""+mMovieDetail.getVoteAverage());
        mMovieSynopsis.setText(mMovieDetail.getOverview());

    }

}
