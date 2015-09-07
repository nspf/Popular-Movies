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

package com.example.android.popularmovies.data.provider.movie;

import android.content.Context;
import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.example.android.popularmovies.data.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code movie} table.
 */
public class MovieContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return MovieColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable MovieSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(Context context, @Nullable MovieSelection where) {
        return context.getContentResolver().update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    public MovieContentValues putMovieId(@Nullable Integer value) {
        mContentValues.put(MovieColumns.MOVIE_ID, value);
        return this;
    }

    public MovieContentValues putMovieIdNull() {
        mContentValues.putNull(MovieColumns.MOVIE_ID);
        return this;
    }

    public MovieContentValues putAdult(@Nullable Boolean value) {
        mContentValues.put(MovieColumns.ADULT, value);
        return this;
    }

    public MovieContentValues putAdultNull() {
        mContentValues.putNull(MovieColumns.ADULT);
        return this;
    }

    public MovieContentValues putBackdropPath(@Nullable String value) {
        mContentValues.put(MovieColumns.BACKDROP_PATH, value);
        return this;
    }

    public MovieContentValues putBackdropPathNull() {
        mContentValues.putNull(MovieColumns.BACKDROP_PATH);
        return this;
    }

    public MovieContentValues putOriginalLanguage(@Nullable String value) {
        mContentValues.put(MovieColumns.ORIGINAL_LANGUAGE, value);
        return this;
    }

    public MovieContentValues putOriginalLanguageNull() {
        mContentValues.putNull(MovieColumns.ORIGINAL_LANGUAGE);
        return this;
    }

    public MovieContentValues putOriginalTitle(@Nullable String value) {
        mContentValues.put(MovieColumns.ORIGINAL_TITLE, value);
        return this;
    }

    public MovieContentValues putOriginalTitleNull() {
        mContentValues.putNull(MovieColumns.ORIGINAL_TITLE);
        return this;
    }

    public MovieContentValues putOverview(@Nullable String value) {
        mContentValues.put(MovieColumns.OVERVIEW, value);
        return this;
    }

    public MovieContentValues putOverviewNull() {
        mContentValues.putNull(MovieColumns.OVERVIEW);
        return this;
    }

    public MovieContentValues putReleaseDate(@Nullable String value) {
        mContentValues.put(MovieColumns.RELEASE_DATE, value);
        return this;
    }

    public MovieContentValues putReleaseDateNull() {
        mContentValues.putNull(MovieColumns.RELEASE_DATE);
        return this;
    }

    public MovieContentValues putPosterPath(@Nullable String value) {
        mContentValues.put(MovieColumns.POSTER_PATH, value);
        return this;
    }

    public MovieContentValues putPosterPathNull() {
        mContentValues.putNull(MovieColumns.POSTER_PATH);
        return this;
    }

    public MovieContentValues putPopularity(@Nullable Double value) {
        mContentValues.put(MovieColumns.POPULARITY, value);
        return this;
    }

    public MovieContentValues putPopularityNull() {
        mContentValues.putNull(MovieColumns.POPULARITY);
        return this;
    }

    public MovieContentValues putTitle(@Nullable String value) {
        mContentValues.put(MovieColumns.TITLE, value);
        return this;
    }

    public MovieContentValues putTitleNull() {
        mContentValues.putNull(MovieColumns.TITLE);
        return this;
    }

    public MovieContentValues putVideo(@Nullable Boolean value) {
        mContentValues.put(MovieColumns.VIDEO, value);
        return this;
    }

    public MovieContentValues putVideoNull() {
        mContentValues.putNull(MovieColumns.VIDEO);
        return this;
    }

    public MovieContentValues putVoteAverage(@Nullable Double value) {
        mContentValues.put(MovieColumns.VOTE_AVERAGE, value);
        return this;
    }

    public MovieContentValues putVoteAverageNull() {
        mContentValues.putNull(MovieColumns.VOTE_AVERAGE);
        return this;
    }

    public MovieContentValues putVoteCount(@Nullable Integer value) {
        mContentValues.put(MovieColumns.VOTE_COUNT, value);
        return this;
    }

    public MovieContentValues putVoteCountNull() {
        mContentValues.putNull(MovieColumns.VOTE_COUNT);
        return this;
    }

    public MovieContentValues putFavorite(@Nullable Boolean value) {
        mContentValues.put(MovieColumns.FAVORITE, value);
        return this;
    }

    public MovieContentValues putFavoriteNull() {
        mContentValues.putNull(MovieColumns.FAVORITE);
        return this;
    }
}
