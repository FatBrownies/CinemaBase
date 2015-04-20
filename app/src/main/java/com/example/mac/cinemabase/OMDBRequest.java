package com.example.mac.cinemabase;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * Created by Victor on 4/20/2015.
 */
public class OMDBRequest {

    private final String TAG = "MyTag";

    private Context context;
    private final String SERVER_URL = "http://www.omdbapi.com/?";
    private final String PLOT_LENGTH = "full";//for short plot replace with "short"
    private final String RESPONSE_TYPE = "json";

    private String movieTitle;
    private RequestQueue queue;
    private JsonObjectRequest jsObjRequest;

    /**
     * Constructor for ombd request.
     * @param context from main activity
     */
    public OMDBRequest(Context context){
        this.context  = context;
        queue = Volley.newRequestQueue(context);
        movieTitle = "";
    }

    /**
     * @param title of movie being searched
     */
    public void requestMovie(String title){
        movieTitle = title;
        String url = constructURL();
        JSONObject jsObj = new JSONObject();

        jsObjRequest = new JsonObjectRequest(Request.Method.GET,
                url, jsObj,
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response){
                        Log.d(TAG, "RESPONSE: " + response);
                    }
                },
        new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Log.d(TAG, "Error " + error);
            }
        });

        queue.add(jsObjRequest);

    }

    private String constructURL(){
        return SERVER_URL + "t=" + movieTitle +
                "&y=&plot=" + PLOT_LENGTH + "&r=" + RESPONSE_TYPE;
    }

}
