package com.example.shareer.ImageHandlerPages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shareer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ImageDetailUser extends AppCompatActivity {

    TextView imgTitle, imgDescription,imgDownloadLink;
    ImageView imgDownload;
    DatabaseReference reference;
    String uId = "", downloadUri="", useridd="";
    FirebaseUser firebaseUser;

    ImageButton copyLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail_user);
        getSupportActionBar().setTitle("Image Details");
        imgTitle = findViewById(R.id.imgTitle);
        imgDescription = findViewById(R.id.imgDetails);
        imgDownload = findViewById(R.id.detailImageView);
        imgDownloadLink=findViewById(R.id.downloadlink);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        useridd=firebaseUser.getUid();

        uId = getIntent().getStringExtra("imgKeyUser");

        copyLink=findViewById(R.id.copyLink);

        copyLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String urlLink=imgDownloadLink.getText().toString();
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT,urlLink);
                intent.setType("text/plain");
                startActivity(intent);
            }
        });

        downloadUri=getIntent().getStringExtra("DownloadUriUser");
        imgDownloadLink.setText(downloadUri);
        imgDownloadLink.setMovementMethod(LinkMovementMethod.getInstance());
        reference = FirebaseDatabase.getInstance().getReference("Users").child(useridd).child("Uploads").child(uId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ImageUploadHandler uploadHandler = snapshot.getValue(ImageUploadHandler.class);
                imgTitle.setText(uploadHandler.getmName());
                imgDescription.setText(uploadHandler.getmDesc());
                Picasso.get().load(uploadHandler.getmImageUri()).into(imgDownload);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}