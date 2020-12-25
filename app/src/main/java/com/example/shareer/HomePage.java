package com.example.shareer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.shareer.ImageHandlerPages.ImageUpload;
import com.example.shareer.ImageHandlerPages.ListofImagesUser;
import com.example.shareer.Layout.MainPage;
import com.example.shareer.MultipleImagesPackage.FolderList;
import com.example.shareer.MultipleImagesPackage.MultipleImages;
import com.example.shareer.User.LoginUser;
import com.example.shareer.User.UsersList;
import com.google.firebase.auth.FirebaseAuth;

public class HomePage extends AppCompatActivity {

    /**
     * This page is currently not used in the project
     */
    Button AddPdf, AddImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        //getSupportActionBar().setTitle("User Actions");

        AddPdf=findViewById(R.id.addpdf);
        AddImage=findViewById(R.id.addimage);

        AddPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomePage.this, VideoActivity.class));
            }
        });

        AddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomePage.this, UsersList.class));
            }
        });
    }

    public void moveToImageListPage(View view) {
        startActivity(new Intent(HomePage.this, FolderList.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuLogout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HomePage.this, LoginUser.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

            case R.id.uploadpdfs:
                Intent intent=new Intent(HomePage.this,PdfUpload.class);
                startActivity(intent);
                return true;


        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}