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

package com.example.android.popularmovies.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.model.Movie;
import com.example.android.popularmovies.ui.activities.MovieDetailActivity;
import com.example.android.popularmovies.ui.fragments.MovieDetailFragment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieHolder> {

    private final Context mContext;
    private final List<Movie> mMovieList;
    private Cursor mCursor;

    public MovieListAdapter(Context context, List<Movie> movieList) {
        this.mContext = context;
        this.mMovieList = movieList;
        setHasStableIds(true);
    }

    public long getItemId(int position) {
        return position;
    }

    /*@Override
    public int getItemCount() {
        return mMovieList.size();
    }*/

    @Override
    public int getItemCount() {
        if ( null == mCursor ) return 0;
        return mCursor.getCount();
    }

    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, final int position) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.movie_list_item, parent, false);
            return new MovieHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieHolder holder, final int position) {

        // CURSOR !!!!
        mCursor.moveToPosition(position);

        //final Movie movie = mMovieList.get(position);

        holder.mMovieFooter.setBackgroundColor(holder.mColorTransparent);
        holder.mMovieTitle.setTextColor(holder.mColorWhite);

        Picasso.with(mContext)
                //.load(movie.getFullPosterPath())
                .load(mCursor.getString(mCursor.getColumnIndex("poster_path")))
                .error(R.drawable.no_poster)
                .into(holder.mMoviePoster, new Callback() {

                    @Override
                    public void onSuccess() {

                        // Apply palette vibrant colors to movie item footer and title
                        // TODO: prevent color blinking when new items are loading to the grid

                        // In some random cases, when I rotate the device or clic an item to see the movie details
                        // the app' window background changes it's color. Is this a Palette bug?
                        // or I'm doing something wrong?

                        Bitmap bitmap = ((BitmapDrawable) holder.mMoviePoster.getDrawable()).getBitmap();
                        new Palette.Builder(bitmap).generate(new Palette.PaletteAsyncListener() {

                            @Override
                            public void onGenerated(Palette palette) {

                                Palette.Swatch vibrant = palette.getVibrantSwatch();
                                if (vibrant != null) {
                                    holder.mMovieFooter.setBackgroundColor(vibrant.getRgb());
                                    holder.mMovieTitle.setTextColor(vibrant.getBodyTextColor());
                                }
                            }
                        });
                    }

                    @Override
                    public void onError() {

                    }
                });

        if(mCursor.getString(mCursor.getColumnIndex("title")) != null) {
            //holder.mMovieTitle.setText(movie.getTitle());
            holder.mMovieTitle.setText(mCursor.getString(mCursor.getColumnIndex("title")));
        }
    }

    public final class MovieHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.movie_item_footer) View mMovieFooter;
        @Bind(R.id.movie_item_image_poster) ImageView mMoviePoster;
        @Bind(R.id.movie_item_text_title) TextView mMovieTitle;

        @BindColor(R.color.transparent) int mColorTransparent;
        @BindColor(android.R.color.white) int mColorWhite;

        public MovieHolder(View vi) {
            super(vi);
            ButterKnife.bind(this, vi);

            vi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(v.getContext(), MovieDetailActivity.class);
                    intent.putExtra(MovieDetailFragment.MOVIE, mCursor.getPosition());
                    mContext.startActivity(intent);
                }
            });

        }
    }

    /*public void add(List<Movie> movieList) {
        mMovieList.addAll(movieList);
        notifyDataSetChanged();
    }*/

    public void clear() {
        mMovieList.clear();
        this.notifyDataSetChanged();
    }

    //CURSOR
    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        this.notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return mCursor;
    }

}