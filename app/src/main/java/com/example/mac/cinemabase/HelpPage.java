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
        final Button login = (Button) findViewById(R.id.support_button);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Toast toast = Toast.makeText(getApplicationContext(),"Submit trouble ticket",Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }
}
