package com.example.shareer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.shareer.AppIntro.AppIntroActivity;
import com.example.shareer.Client.LoginClient;
import com.example.shareer.User.LoginUser;
import com.example.shareer.User.RegisterUser;

public class MainActivity extends AppCompatActivity {

    // This is the staring Activity of Application

    Button ChoiceUser, ChoiceClient;
    private long backPressedTime;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ChoiceClient=findViewById(R.id.choiceclient);
        ChoiceUser=findViewById(R.id.choiceuser);

        ChoiceUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AppIntroActivity.class));
            }
        });

        ChoiceClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, LoginClient.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime+2000>System.currentTimeMillis()){
            backToast.cancel();
            super.onBackPressed();
        }
        else{
            backToast=Toast.makeText(getBaseContext(), "Press Back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime=System.currentTimeMillis();
    }
}