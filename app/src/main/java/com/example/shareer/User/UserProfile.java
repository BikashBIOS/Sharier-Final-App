package com.example.shareer.User;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.shareer.R;
import com.google.android.material.textfield.TextInputLayout;

public class UserProfile extends AppCompatActivity {

    TextInputLayout folderName;
    Button enterFolder;
    String folderNamee;
    String usernamee, useridd;
    TextView userName, userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        folderName=findViewById(R.id.searchfoldername);
        enterFolder=findViewById(R.id.enterfoldername);
        userName=findViewById(R.id.usernametextview);
        userID=findViewById(R.id.useridtextview);

        folderNamee=folderName.getEditText().getText().toString();
        useridd=getIntent().getStringExtra("userUid");
        usernamee=getIntent().getStringExtra("userName");
        userName.setText(usernamee);
        userID.setText(useridd);

    }
}