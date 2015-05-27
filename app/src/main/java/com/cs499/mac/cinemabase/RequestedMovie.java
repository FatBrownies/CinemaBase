package com.cs499.mac.cinemabase;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;


public class RequestedMovie extends Activity implements View.OnClickListener{

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
        buttonPressed();
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

    public void buttonPressed(){
        final Button login = (Button) findViewById(R.id.backB);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                //Toast toast = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_SHORT);
                //toast.show();
                // Simulates back button
                Intent MainActivityIntent = new Intent(RequestedMovie.this, MainActivity.class);
                startActivity(MainActivityIntent);
            }
        });

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

    @Override
    public void onClick(View view) {
        //TODO
    }
}