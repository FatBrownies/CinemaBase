package com.cs499.mac.cinemabase;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class OMDBRequest {

    private final String TAG = "MyTag";

    private Context context;
    private final String SERVER_URL = "http://www.omdbapi.com/?";
    private final String PLOT_LENGTH = "full";//for short plot replace with "short"
    private final String RESPONSE_TYPE = "json";
    private String movieTitle;
    private RequestQueue queue;
    private JsonObjectRequest jsObjRequest;

    public OMDBRequest(){}

    /**
     * Constructor for ombd request.
     * @param context from main activity
     */
    public OMDBRequest(Context context){
        this.context  = context;
        queue = MySingleton.getInstance(context).getRequestQueue();
        movieTitle = "";
    }
    /**
     * @param title of movie being searched
     */
    public void requestMovie(String title){
        Log.d(TAG, "requesting title " + title);
        movieTitle = title;
        String url = constructURL();
        final JSONObject jsObj = new JSONObject();

        final ProgressDialog mProgressDialog = new ProgressDialog(context);
        //only show progress dialog if we will display movie activity
        mProgressDialog.setMessage("Searching for movie...");
        mProgressDialog.show();

        jsObjRequest = new JsonObjectRequest(Request.Method.GET,
                url, jsObj,
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response){
                        Log.d(TAG, "json obj " + jsObj.toString());
                        Log.d(TAG, "RESPONSE: " + response);
                        parseObject(response);
                        mProgressDialog.dismiss();
                    }
                },
        new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Toast.makeText(context, "Error with network", Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
                return;
            }
        });

        queue.add(jsObjRequest);

    }

    private void parseObject(JSONObject jsObj){

        //create movie and attempt to parse
        Movie movie = new Movie(jsObj);
        boolean validMovie = movie.parse();

        //if json returned false display message and return
        if(!validMovie){
            Log.d(TAG, "Invalid movie");
            Toast.makeText(context,"Movie was not found",Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Initializing movie activity");
        Intent intent = new Intent(context, RequestedMovie.class);
        intent.putExtra("movie", movie);
        context.startActivity(intent);
    }

    public String constructURL(String query){
        movieTitle = query;
        return constructURL();
    }

    private String constructURL(){
        movieTitle = movieTitle.replace(" ", "+");
        String url =  SERVER_URL + "t=" + movieTitle +
                "&y=&plot=" + PLOT_LENGTH + "&r=" + RESPONSE_TYPE;
        Log.d(TAG, "Constructed request url: " + url);
        return url;
    }

}
