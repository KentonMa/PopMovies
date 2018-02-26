package com.example.kenton.popmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Kenton on 07/05/2017.
 */
public class Movie implements Parcelable {
    // Used as key in sending extra data in intents
    public static final String EXTRA_MOVIE = "extra_movie";

    private String mTitle;
    private String mPosterUrl;
    private String mOverview;
    private double mRating;
    private String mReleaseDate;

    public Movie(String title,
                 String poster_url,
                 String overview,
                 double rating,
                 String release_date) {
        this.mTitle = title;
        this.mPosterUrl = poster_url;
        this.mOverview = overview;
        this.mRating = rating;
        this.mReleaseDate = release_date;
    }

    protected Movie(Parcel in) {
        mTitle = in.readString();
        mPosterUrl = in.readString();
        mOverview = in.readString();
        mRating = in.readDouble();
        mReleaseDate = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getTitle() {
        return mTitle;
    }

    public String getPosterUrl() {
        return mPosterUrl;
    }

    public String getOverview() {
        return mOverview;
    }

    public double getRating() {
        return mRating;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mPosterUrl);
        dest.writeString(mOverview);
        dest.writeDouble(mRating);
        dest.writeString(mReleaseDate);
    }
}
