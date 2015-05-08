package com.example.mac.cinemabase;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class HelpPage extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_help_page);

        initHelp();
    }

    public void initHelp(){
        final Button login = (Button) findViewById(R.id.social_login_button);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Toast toast = Toast.makeText(getApplicationContext(),"logging to facebook",Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        final Button logout = (Button) findViewById(R.id.social_logout_button);
        logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Toast toast = Toast.makeText(getApplicationContext(),"logging out of facebook",Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        final Button movieDB = (Button) findViewById(R.id.movie_data_base);
        movieDB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Toast toast = Toast.makeText(getApplicationContext(),"data base X",Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
}
