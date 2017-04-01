package com.udacity.project.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Class Movie encapsulates all required attribute of the type Movie.
 * Created by chandan on 25/01/2017.
 */
public class Movie implements Parcelable {

    private int movieTMDBId;
    private String movieOriginalTitle;
    private String moviePosterUrl;
    private String movieBackDropUrl;
    private String movieWebUrl;
    private String moviePlotSynopsis;
    private float movieRating;
    private String movieReleaseDate;
    private List<MovieTrailer> movieTrailers;
    private List<MovieReview> movieReviews;

    public Movie(int movieTMDBId, String movieOriginalTitle, String moviePosterUrl, String movieBackDropUrl, String movieWebUrl, String moviePlotSynopsis, float movieRating, String movieReleaseDate, List<MovieTrailer> movieTrailers, List<MovieReview> movieReviews) {
        this.movieTMDBId = movieTMDBId;
        this.movieOriginalTitle = movieOriginalTitle;
        this.moviePosterUrl = moviePosterUrl;
        this.movieBackDropUrl = movieBackDropUrl;
        this.movieWebUrl = movieWebUrl;
        this.moviePlotSynopsis = moviePlotSynopsis;
        this.movieRating = movieRating;
        this.movieReleaseDate = movieReleaseDate;
        this.movieTrailers = movieTrailers;
        this.movieReviews = movieReviews;
    }

    private Movie(Parcel parcel) {
        movieTMDBId = parcel.readInt();
        movieOriginalTitle = parcel.readString();
        moviePosterUrl = parcel.readString();
        movieBackDropUrl = parcel.readString();
        movieWebUrl = parcel.readString();
        moviePlotSynopsis = parcel.readString();
        movieRating = parcel.readFloat();
        movieReleaseDate = parcel.readString();
        movieReviews = parcel.readArrayList(MovieReview.class.getClassLoader());
        movieTrailers = parcel.readArrayList(MovieTrailer.class.getClassLoader());
    }

    public int getMovieTMDBId() {
        return movieTMDBId;
    }

    public void setMovieTMDBId(int movieTMDBId) {
        this.movieTMDBId = movieTMDBId;
    }

    public String getMovieOriginalTitle() {
        return movieOriginalTitle;
    }

    public void setMovieOriginalTitle(String movieOriginalTitle) {
        this.movieOriginalTitle = movieOriginalTitle;
    }

    public String getMoviePosterUrl() {
        return moviePosterUrl;
    }

    public void setMoviePosterUrl(String moviePosterUrl) {
        this.moviePosterUrl = moviePosterUrl;
    }

    public String getMovieBackDropUrl() {
        return movieBackDropUrl;
    }

    public void setMovieBackDropUrl(String movieBackDropUrl) {
        this.movieBackDropUrl = movieBackDropUrl;
    }

    public String getMovieWebUrl() {
        return movieWebUrl;
    }

    public void setMovieWebUrl(String movieWebUrl) {
        this.movieWebUrl = movieWebUrl;
    }

    public String getMoviePlotSynopsis() {
        return moviePlotSynopsis;
    }

    public void setMoviePlotSynopsis(String moviePlotSynopsis) {
        this.moviePlotSynopsis = moviePlotSynopsis;
    }

    public float getMovieRating() {
        return movieRating;
    }

    public void setMovieRating(float movieRating) {
        this.movieRating = movieRating;
    }

    public String getMovieReleaseDate() {
        return movieReleaseDate;
    }

    public void setMovieReleaseDate(String movieReleaseDate) {
        this.movieReleaseDate = movieReleaseDate;
    }

    public List<MovieTrailer> getMovieTrailers() {
        return movieTrailers;
    }

    public void setMovieTrailers(List<MovieTrailer> movieTrailers) {
        this.movieTrailers = movieTrailers;
    }

    public List<MovieReview> getMovieReviews() {
        return movieReviews;
    }

    public void setMovieReviews(List<MovieReview> movieReviews) {
        this.movieReviews = movieReviews;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(movieTMDBId);
        parcel.writeString(movieOriginalTitle);
        parcel.writeString(moviePosterUrl);
        parcel.writeString(movieBackDropUrl);
        parcel.writeString(movieWebUrl);
        parcel.writeString(moviePlotSynopsis);
        parcel.writeFloat(movieRating);
        parcel.writeString(movieReleaseDate);
        parcel.writeList(movieReviews);
        parcel.writeList(movieTrailers);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>(){

        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[0];
        }
    };
}
