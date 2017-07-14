package com.shwetak3e.loading;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class SplashActivity extends AppCompatActivity {


    Button load;
    Button unload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        load=(Button)findViewById(R.id.load);
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.context=" ( L )";
                startApp();
            }
        });
        unload=(Button)findViewById(R.id.unload);
        unload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.context=" ( U )";
                startApp();
            }
        });


        //This method will be executed once the timer is over
                //Start your app main activity


    }

    void startApp(){
        Intent i=new Intent(SplashActivity.this,MainActivity.class);
        SplashActivity.this.startActivity(i);

        // Close this activity
        finish();
    }
}

