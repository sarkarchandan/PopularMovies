package com.udacity.project.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class MovieTrailer encapsulates all required attribute of the type MovieTrailer.
 * Created by chandan on 31.03.17.
 */
public class MovieTrailer implements Parcelable{
    private String movieTrailerTMDBId;
    private String movieTrailerKey;
    private String movieTrailerURL;


    public MovieTrailer(String movieTrailerTMDBId, String movieTrailerKey, String movieTrailerURL) {
        this.movieTrailerTMDBId = movieTrailerTMDBId;
        this.movieTrailerKey = movieTrailerKey;
        this.movieTrailerURL = movieTrailerURL;
    }

    private MovieTrailer(Parcel parcel){
        movieTrailerTMDBId = parcel.readString();
        movieTrailerKey = parcel.readString();
        movieTrailerURL = parcel.readString();
    }

    public String getMovieTrailerTMDBId() {
        return movieTrailerTMDBId;
    }

    public void setMovieTrailerTMDBId(String movieTrailerTMDBId) {
        this.movieTrailerTMDBId = movieTrailerTMDBId;
    }

    public String getMovieTrailerKey() {
        return movieTrailerKey;
    }

    public void setMovieTrailerKey(String movieTrailerKey) {
        this.movieTrailerKey = movieTrailerKey;
    }

    public String getMovieTrailerURL() {
        return movieTrailerURL;
    }

    public void setMovieTrailerURL(String movieTrailerURL) {
        this.movieTrailerURL = movieTrailerURL;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(movieTrailerTMDBId);
        parcel.writeString(movieTrailerKey);
        parcel.writeString(movieTrailerURL);
    }

    public static final Creator<MovieTrailer> CREATOR = new Parcelable.Creator<MovieTrailer>(){
        @Override
        public MovieTrailer createFromParcel(Parcel parcel) {
            return new MovieTrailer(parcel);
        }

        @Override
        public MovieTrailer[] newArray(int i) {
            return new MovieTrailer[0];
        }
    };
}
