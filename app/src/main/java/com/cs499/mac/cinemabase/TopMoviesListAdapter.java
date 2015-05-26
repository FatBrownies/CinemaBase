package com.cs499.mac.cinemabase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;


public class TopMoviesListAdapter extends BaseAdapter {

    private final String TAG = "MyTag";

    private Activity mContext;
    private Movie[] movies;
    private LayoutInflater mLayoutInflater = null;
    private ImageLoader mImageLoader;

    public TopMoviesListAdapter(Activity context, Movie[] movies){
        this.mContext = context;
        this.movies = movies;
        mLayoutInflater = (LayoutInflater) mContext.
                getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        mImageLoader = MySingleton.getInstance(mContext).getmImageLoader();
    }

    @Override
    public int getCount() {
        return movies.length;
    }

    @Override
    public Movie getItem(int position) {
        if(position >= 0 && position < movies.length){
            return movies[position];
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = mContext.getLayoutInflater()
                    .inflate(R.layout.list_movie_item, null);
        }

        Movie movie = getItem(position);

        if(movie != null) {

            TextView textRank = (TextView)convertView.findViewById(R.id.movieRank);
            textRank.setText((position+1) + "");

            //get image thumbnail
            NetworkImageView thumbnail = (NetworkImageView) convertView.
                    findViewById(R.id.movieImageThumbnail);
            thumbnail.setImageUrl(movie.getStringURL(), mImageLoader);

            TextView movieTitle = (TextView) convertView.findViewById(R.id.movieListTitle);
            movieTitle.setText(movie.getTitle() + " (" + movie.getYear() + ")");
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Movie m = getItem(position);
                Intent intent = new Intent(mContext, RequestedMovie.class);
                if(m == null){
                    Log.d(TAG,"movie is null");
                    return;
                }
                intent.putExtra("movie",m);
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

}
