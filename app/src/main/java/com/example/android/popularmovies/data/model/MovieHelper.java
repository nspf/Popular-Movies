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

package com.example.android.popularmovies.data.model;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;

import com.example.android.popularmovies.data.provider.movie.MovieColumns;
import com.example.android.popularmovies.data.provider.movie.MovieContentValues;

public class MovieHelper {

    private Context mContext;

    public MovieHelper(Context context) {
        mContext = context;
    }

    public Boolean setFavorite(Movie movie) {

        if(movie.isFavorite()) {
            movie.setFavorite(false);

            Uri uri = ContentUris.withAppendedId(MovieColumns.CONTENT_URI, movie.getMovieId());
            String where = MovieColumns.MOVIE_ID + "=?";
            mContext
                    .getContentResolver()
                    .delete(MovieColumns.CONTENT_URI, where, new String[] {String.valueOf(movie.getMovieId())});

            mContext.getContentResolver().notifyChange(uri, null);

        }
        else {
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

            mContext
                    .getContentResolver()
                    .insert(MovieColumns.CONTENT_URI, values.values());

        }
        return movie.isFavorite();
    }

}
