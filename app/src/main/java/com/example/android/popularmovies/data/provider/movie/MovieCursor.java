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

import android.database.Cursor;
import android.support.annotation.Nullable;

import com.example.android.popularmovies.data.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code movie} table.
 */
public class MovieCursor extends AbstractCursor implements MovieModel {
    public MovieCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(MovieColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code movie_id} value.
     * Can be {@code null}.
     */
    @Nullable
    public Integer getMovieId() {
        return getIntegerOrNull(MovieColumns.MOVIE_ID);
    }

    /**
     * Get the {@code adult} value.
     * Can be {@code null}.
     */
    @Nullable
    public Boolean getAdult() {
        return getBooleanOrNull(MovieColumns.ADULT);
    }

    /**
     * Get the {@code backdrop_path} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getBackdropPath() {
        return getStringOrNull(MovieColumns.BACKDROP_PATH);
    }

    /**
     * Get the {@code original_language} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getOriginalLanguage() {
        return getStringOrNull(MovieColumns.ORIGINAL_LANGUAGE);
    }

    /**
     * Get the {@code original_title} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getOriginalTitle() {
        return getStringOrNull(MovieColumns.ORIGINAL_TITLE);
    }

    /**
     * Get the {@code overview} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getOverview() {
        return getStringOrNull(MovieColumns.OVERVIEW);
    }

    /**
     * Get the {@code release_date} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getReleaseDate() {
        return getStringOrNull(MovieColumns.RELEASE_DATE);
    }

    /**
     * Get the {@code poster_path} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getPosterPath() {
        return getStringOrNull(MovieColumns.POSTER_PATH);
    }

    /**
     * Get the {@code popularity} value.
     * Can be {@code null}.
     */
    @Nullable
    public Double getPopularity() {
        return getDoubleOrNull(MovieColumns.POPULARITY);
    }

    /**
     * Get the {@code title} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getTitle() {
        return getStringOrNull(MovieColumns.TITLE);
    }

    /**
     * Get the {@code video} value.
     * Can be {@code null}.
     */
    @Nullable
    public Boolean getVideo() {
        return getBooleanOrNull(MovieColumns.VIDEO);
    }

    /**
     * Get the {@code vote_average} value.
     * Can be {@code null}.
     */
    @Nullable
    public Double getVoteAverage() {
        return getDoubleOrNull(MovieColumns.VOTE_AVERAGE);
    }

    /**
     * Get the {@code vote_count} value.
     * Can be {@code null}.
     */
    @Nullable
    public Integer getVoteCount() {
        return getIntegerOrNull(MovieColumns.VOTE_COUNT);
    }

    /**
     * Get the {@code favorite} value.
     * Can be {@code null}.
     */
    @Nullable
    public Boolean getFavorite() {
        return getBooleanOrNull(MovieColumns.FAVORITE);
    }
}
