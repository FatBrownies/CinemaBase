package com.example.mac.cinemabase;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;


public class RequestedMovie extends Activity {

    private final String TAG = "MyTag";

    private Movie movie;
    private NetworkImageView moviePosterView;
    private ImageLoader mImageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requested_movie);

        initInstanses();
        populateView();
    }

    private void initInstanses(){
        movie = getIntent().getParcelableExtra("movie");
        moviePosterView = (NetworkImageView)findViewById(R.id.networkImageView);
        mImageLoader = MySingleton.getInstance(this).getmImageLoader();
        moviePosterView.setImageUrl(movie.getStringURL(), mImageLoader);
    }

    private void populateView(){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_requested_movie, menu);
        return true;
    }



}