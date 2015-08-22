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

package com.example.android.popularmovies.data.api;

import com.example.android.popularmovies.data.model.Movie;
import com.example.android.popularmovies.data.model.MovieData;
import com.example.android.popularmovies.data.model.Video;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;


public final class MoviesService {
    public static final String API_URL_BASE = "http://api.themoviedb.org/3";
    public static final String API_KEY = "64f4f7bd4da8f94c04844bdc7faf18a8";
    public static final String API_KEY_PARAM = "api_key";
    private static MoviesApi sMoviesService;

    public interface MoviesApi {
        @GET("/discover/movie")
        void getMovieList(
                @Query("sort_by") String param1,
                @Query("page") int param2,
                //Callback<MovieResults> cb);
                Callback<List<Movie>> cb);



                @GET("/movie/{id}/videos")
        void getMovieTrailers(
                @Path("id") int id,
                Callback<Video.Response> cb);

        @GET("/movie/{id}?append_to_response=trailers,reviews")
        void getMovieData(
                @Path("id") long id,
                Callback<MovieData> cb);

    }


    public static MoviesApi getMoviesApiClient() {
        Type listType = new TypeToken<List<Movie>>() {
        }.getType();
        if (sMoviesService == null) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(API_URL_BASE)
                    .setRequestInterceptor(new RequestInterceptor() {
                        @Override
                        public void intercept(RequestInterceptor.RequestFacade request) {
                            request.addQueryParam(API_KEY_PARAM, API_KEY);
                        }
                    })
                    .setConverter(new GsonConverter(new GsonBuilder().registerTypeAdapter(
                            listType, new MovieListDeserializer()).create()))
                    //.setLogLevel(RestAdapter.LogLevel.FULL)
                    .build();

            sMoviesService = restAdapter.create(MoviesApi.class);
        }

        return sMoviesService;
    }

}