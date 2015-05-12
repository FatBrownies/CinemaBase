package com.cs499.mac.cinemabase;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * class implements Parcelable to be able to pass objects between activities
 */
public class Movie implements Parcelable {

    private final String TAG = "MyTag";

    private String title;
    private String year;
    private String rating;
    private String runtime;
    private String genre;
    private String plot;
    private String awards;
    private String stringURL;

    private JSONObject jsObj;

    public Movie(JSONObject jsObj){
        title = "";
        year = "";
        rating = "";
        runtime = "";
        genre = "";
        plot = "";
        awards = "";
        stringURL = "";
        this.jsObj = jsObj;
    }

    public Movie(Parcel in){
        //array which holds data of object
        String[] objectData = new String[8];

        //load array with parcel data
        in.readStringArray(objectData);
        this.title = objectData[0];
        this.year = objectData[1];
        this.rating = objectData[2];
        this.runtime = objectData[3];
        this.genre = objectData[4];
        this.plot = objectData[5];
        this.awards = objectData[6];
        this.stringURL = objectData[7];
    }

    /**
     * Load from a Json object into class instances
     * @return true if parsed object correctly
     */
    public boolean parse(){
        try {
            if(jsObj == null) {
                Log.d(TAG,"json object was null");
                return false;
            }

            //check if the query was successfull
            boolean result = jsObj.getBoolean("Response");
            if(result == false){
                Log.d(TAG,"query returned false");
                return false;
            }

            title = jsObj.getString("Title");
            year = jsObj.getString("Year");
            rating = jsObj.getString("imdbRating");
            runtime = jsObj.getString("Runtime");
            genre = jsObj.getString("Genre");

            //if movie has multiple genre's we only use the first one
            int indexOfGenre = genre.indexOf(',');
            if(indexOfGenre != -1) {
                genre = genre.substring(0, genre.indexOf(','));
            }
            Log.d(TAG,genre);
            plot = jsObj.getString("Plot");
            awards = jsObj.getString("Awards");
            stringURL = jsObj.getString("Poster");
        } catch (JSONException e) {
            Log.e(TAG,"json string parsing error " + e.getLocalizedMessage());
            return false;
        }
        return true;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray( new String[]{
                this.title,
                this.year,
                this.rating,
                this.runtime,
                this.genre,
                this.plot,
                this.awards,
                this.stringURL
        });
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>(){

        @Override
        public Movie createFromParcel(Parcel source){
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size){
            return new Movie[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getRating() {
        return rating;
    }

    public String getRuntime() {
        return runtime;
    }

    public String getGenre() {
        return genre;
    }

    public String getPlot() {
        if(plot.equals("N/A"))
            return "We have no plot for this movie... sorry";

        return plot;
    }

    public String getAwards() {
        if(awards.equals("N/A"))
            return "No Awards or Nominations";
        return awards;
    }

    public String getStringURL(){
        return stringURL;
    }


    public String toString(){
        return "Title: " + title + " Year: " + year +
                " Rating: " + rating + " Runtime: " + runtime +
                " Genre: " + genre + " Plot: " + plot +
                " Awards " + awards + "\nURL: " + stringURL;

    }

}
