package com.example.mac.cinemabase;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;


public class RequestedMovie extends Activity {

    private final String TAG = "MyTag";

    private Movie movie;
    private NetworkImageView moviePosterView;
    private ImageLoader mImageLoader;
    private TextView awardsTextView;
    private TextView titleTextView;
    private TextView yearTextView;
    private TextView ratingTextView;
    private TextView timeTextView;
    private TextView genreTextView;
    private TextView plotTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requested_movie);

        initInstances();
        populateView();
    }

    private void initInstances(){
        movie = getIntent().getParcelableExtra("movie");
        moviePosterView = (NetworkImageView)findViewById(R.id.networkImageView);
        awardsTextView = (TextView)findViewById(R.id.awardsText);
        titleTextView = (TextView)findViewById(R.id.movieTitle);
        yearTextView = (TextView)findViewById(R.id.movieYear);
        ratingTextView = (TextView)findViewById(R.id.movieRating);
        timeTextView = (TextView)findViewById(R.id.movieTime);
        genreTextView = (TextView)findViewById(R.id.movieGenre);
        plotTextView = (TextView)findViewById(R.id.moviePlot);
    }

    private void populateView(){

        //add poster to view
        mImageLoader = MySingleton.getInstance(this).getmImageLoader();
        moviePosterView.setImageUrl(movie.getStringURL(), mImageLoader);
        awardsTextView.setText(movie.getAwards());
        titleTextView.setText(titleTextView.getText() + "  "+ movie.getTitle());
        yearTextView.setText(yearTextView.getText() + "  " + movie.getYear());
        ratingTextView.setText(ratingTextView.getText() + "  " + movie.getRating());
        timeTextView.setText(timeTextView.getText() + "  " + movie.getRuntime());
        genreTextView.setText(genreTextView.getText() + "  " + movie.getGenre());
        plotTextView.setText(plotTextView.getText()  + movie.getPlot());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_requested_movie, menu);
        return true;
    }



}