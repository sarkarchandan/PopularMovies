package com.udacity.project.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class Movie is the template for the movie object which will encapsulate all required and useful attribute of the type Movie in our
 * app.
 * Created by chandan on 25/01/2017.
 */
public class Movie implements Parcelable {

    private int movieId;
    private String movieOriginalTitle;
    private String moviePosterUrl;
    private String movieBackDropUrl;
    private String movieWebUrl;
    private String moviePlotSynopsis;
    private float movieRating;
    private String movieReleaseDate;


    /**
     * Constructor for creating Movie instances
     * @param movieId
     * @param movieOriginalTitle
     * @param moviePosterUrl
     * @param movieBackDropUrl
     * @param movieWebUrl
     * @param moviePlotSynopsis
     * @param movieRating
     * @param movieReleaseDate
     */
    public Movie(int movieId, String movieOriginalTitle, String moviePosterUrl, String movieBackDropUrl, String movieWebUrl,String moviePlotSynopsis, float movieRating, String movieReleaseDate) {
        this.movieId = movieId;
        this.movieOriginalTitle = movieOriginalTitle;
        this.moviePosterUrl = moviePosterUrl;
        this.movieBackDropUrl = movieBackDropUrl;
        this.movieWebUrl = movieWebUrl;
        this.moviePlotSynopsis = moviePlotSynopsis;
        this.movieRating = movieRating;
        this.movieReleaseDate = movieReleaseDate;
    }

    /**
     * This private constructor is to be used by the createFromParcel() method of the Parcelable.Creator interface
     * to return a Movie instance of parcel. And behind the scene it decodes the Parcel object and restores the values
     * of the attributes of our custom class.
     * @param parcel
     */
    private Movie(Parcel parcel) {
        movieId = parcel.readInt();
        movieOriginalTitle = parcel.readString();
        moviePosterUrl = parcel.readString();
        movieBackDropUrl = parcel.readString();
        movieWebUrl = parcel.readString();
        moviePlotSynopsis = parcel.readString();
        movieRating = parcel.readFloat();
        movieReleaseDate = parcel.readString();
    }


    /**Getter and Setter methods for the Movie class instance variables*/

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
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

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * We are taking the data from the instance variables and writing to the Parcel object.
     * @param parcel //Parcel object
     * @param i //correspond to the position
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(movieId);
        parcel.writeString(movieOriginalTitle);
        parcel.writeString(moviePosterUrl);
        parcel.writeString(movieBackDropUrl);
        parcel.writeString(movieWebUrl);
        parcel.writeString(moviePlotSynopsis);
        parcel.writeFloat(movieRating);
        parcel.writeString(movieReleaseDate);
    }

    /**
     * Parcelable.Creator method receives the Parcel and decodes the contents that are written to Parcel.
     */
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
