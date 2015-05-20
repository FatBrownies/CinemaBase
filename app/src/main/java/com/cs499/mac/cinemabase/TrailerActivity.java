package com.cs499.mac.cinemabase;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.VideoView;



public class TrailerActivity extends ActionBarActivity {

    private VideoView trailerVideo;
    private MediaController mediaControls;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailer);

        String urlString = getIntent().getStringExtra("PREVIEW_URL");
        trailerVideo = (VideoView)findViewById(R.id.videoView);
        if(mediaControls == null){
            mediaControls = new MediaController(this);
        }

        loadingBar = new ProgressDialog(this);
        loadingBar.setTitle("Loading movie");
        loadingBar.setMessage("Loading...");
        loadingBar.setCancelable(false);
        loadingBar.show();

        Log.d("MyTag", "works");

        try {
            trailerVideo.setMediaController(mediaControls);
            trailerVideo.setVideoURI(Uri.parse(urlString));
        } catch (Exception e){
            Log.d("MyTag", "error loading trailer on trailer view");
            e.printStackTrace();
        }

        trailerVideo.requestFocus();
        trailerVideo.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.d("MyTag", what + " " + extra);
                loadingBar.dismiss();
                return false;
            }
        });

        trailerVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                loadingBar.dismiss();
                trailerVideo.start();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trailer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
