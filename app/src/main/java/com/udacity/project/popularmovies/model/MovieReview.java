package com.udacity.project.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class MovieReview encapsulates all required attribute of the type MovieReview.
 * Created by chandan on 31.03.17.
 */
public class MovieReview implements Parcelable{
    private String movieReviewTMDBId;
    private String movieReviewAuthor;
    private String movieReviewContent;

    public MovieReview(String movieReviewTMDBId, String movieReviewAuthor, String movieReviewContent) {
        this.movieReviewTMDBId = movieReviewTMDBId;
        this.movieReviewAuthor = movieReviewAuthor;
        this.movieReviewContent = movieReviewContent;
    }

    private MovieReview(Parcel parcel){
        movieReviewTMDBId = parcel.readString();
        movieReviewAuthor = parcel.readString();
        movieReviewContent = parcel.readString();
    }

    public String getMovieReviewTMDBId() {
        return movieReviewTMDBId;
    }

    public void setMovieReviewTMDBId(String movieReviewTMDBId) {
        this.movieReviewTMDBId = movieReviewTMDBId;
    }

    public String getMovieReviewAuthor() {
        return movieReviewAuthor;
    }

    public void setMovieReviewAuthor(String movieReviewAuthor) {
        this.movieReviewAuthor = movieReviewAuthor;
    }

    public String getMovieReviewContent() {
        return movieReviewContent;
    }

    public void setMovieReviewContent(String movieReviewContent) {
        this.movieReviewContent = movieReviewContent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(movieReviewTMDBId);
        parcel.writeString(movieReviewAuthor);
        parcel.writeString(movieReviewContent);
    }

    public static final Creator<MovieReview> CREATOR = new Parcelable.Creator<MovieReview>(){
        @Override
        public MovieReview createFromParcel(Parcel parcel) {
            return new MovieReview(parcel);
        }

        @Override
        public MovieReview[] newArray(int i) {
            return new MovieReview[0];
        }
    };
}
