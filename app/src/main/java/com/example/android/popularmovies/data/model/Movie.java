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

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import ollie.Model;
import ollie.annotation.Column;
import ollie.annotation.Table;

@Table("movies")
public class Movie extends Model implements Parcelable{

    /*
     * @Expose - expose the field to Gson
     * @SerializedName("") - name used by Gson
     * @Column( ) - column name used by simpleprovider
     */

    @SerializedName("movie_id")
    @Expose
    @Column("movie_id")
    public Long id;

    @Expose
    @Column("adult")
    public Boolean adult;


    @SerializedName("backdrop_path")
    @Expose
    @Column("backdrop_path")
    public String backdropPath;

    @SerializedName("original_language")
    @Column("original_language")
    @Expose
    public String originalLanguage;


    @SerializedName("original_title")
    @Column("original_title")
    @Expose
    public String originalTitle;


    @Column("overview")
    @Expose
    public String overview;


    @SerializedName("release_date")
    @Column("release_date")
    @Expose
    public String releaseDate;


    @SerializedName("poster_path")
    @Column("poster_path")
    @Expose
    public String posterPath;


    @Column("popularity")
    @Expose
    public Double popularity;

    @Column("title")
    @Expose
    public String title;


    @Column("video")
    @Expose
    public Boolean video;


    @SerializedName("vote_average")
    @Column("vote_average")
    @Expose
    public Double voteAverage;


    @SerializedName("vote_count")
    @Column("vote_count")
    @Expose
    public Integer voteCount;

    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    public static final String POSTER_185 = "w185";
    public static final String BACKDROP_300 = "w300";

    public Movie() {
        super();    // required by ActiveAndroid
    }


    private Movie(Parcel in) {
        adult = in.readByte() !=0;
        backdropPath = in.readString();
        //genreIds = (ArrayList<Integer>) in.readSerializable();
        id = in.readLong();
        originalLanguage = in.readString();
        overview = in.readString();
        releaseDate = in.readString();
        posterPath = in.readString();
        popularity = in.readDouble();
        title = in.readString();
        video = in.readByte() !=0;
        voteAverage = in.readDouble();
        voteCount = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeByte((byte) (getAdult() ? 1 : 0));
        out.writeString(getBackdropPath());
        out.writeLong(getMovieId());
        out.writeString(getOriginalLanguage());
        out.writeString(getOverview());
        out.writeString(getReleaseDate());
        out.writeString(getPosterPath());
        out.writeDouble(getPopularity());
        out.writeString(getTitle());
        out.writeByte((byte) (getVideo() ? 1 : 0));
        out.writeDouble(getVoteAverage());
        out.writeInt(getVoteCount());
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };


    public Boolean getAdult() {
        return adult;
    }

    public void setAdult(Boolean adult) {
        this.adult = adult;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    /*public List<Integer> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<Integer> genreIds) {
        this.genreIds = genreIds;
    }*/

    public Long getMovieId() {
        return id;
    }

    public void setMovieId(Long id) {
        this.id = id;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getVideo() {
        return video;
    }

    public void setVideo(Boolean video) {
        this.video = video;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    //

    public String getYearFromReleaseDate() {
        if(getReleaseDate() != null){
            String[] parts = this.getReleaseDate().split("-");
            return parts[0];
        }

        else return "";

    }

    public String getFullPosterPath() {
        return BASE_IMAGE_URL + POSTER_185 + getPosterPath();
    }

    public String getFullBackdropPath() {
        return BASE_IMAGE_URL + BACKDROP_300 + getBackdropPath();
    }

}