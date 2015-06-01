package com.cs499.mac.cinemabase;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class HelpPage extends ActionBarActivity {

    Button buttonSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_help_page);

        initHelp();
    }

    public void initHelp(){
        final Button login = (Button) findViewById(R.id.support_button);
        buttonSend = (Button) findViewById(R.id.support_button);

        buttonSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String to = "cinemabase.help@gmail.com";
                String subject = "Submit trouble ticket";
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{ to});
                email.putExtra(Intent.EXTRA_SUBJECT, subject);

                //need this to prompts email client only
                email.setType("message/rfc822");

                startActivity(email);
            }
        });

    }
}
