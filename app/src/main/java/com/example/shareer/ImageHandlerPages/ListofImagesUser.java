package com.example.shareer.ImageHandlerPages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.shareer.HomePage;
import com.example.shareer.ListofPdf;
import com.example.shareer.MainActivity;
import com.example.shareer.R;
import com.example.shareer.User.UsersList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class ListofImagesUser extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase database;
    FirebaseStorage firebaseStorage;
    private RecyclerView mRecyclerView;
    private ImageAdapterUser mAdapter;
    private DatabaseReference databaseReference;
    private List<ImageUploadHandler> mUpload;
    ImageView imageView;
    String userId="";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listof_images_user);

        mAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        /*userId=getIntent().getStringExtra("imageUid");*/

        mRecyclerView=findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(ListofImagesUser.this));
        mRecyclerView.setHasFixedSize(true);
        mUpload=new ArrayList<>();
        databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Uploads");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {

                    ImageUploadHandler upload=postSnapshot.getValue(ImageUploadHandler.class);
                    mUpload.add(upload);
                }
                mAdapter=new ImageAdapterUser(ListofImagesUser.this,mUpload);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ListofImagesUser.this, "Error:"+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuLogout:
                mAuth.signOut();
                startActivity(new Intent(ListofImagesUser.this, MainActivity.class));
                finish();
                break;
            case R.id.uploadpdfs:
                startActivity(new Intent(ListofImagesUser.this, ListofPdf.class));

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void deleteImages()
    {
        databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Uploads");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {

                    ImageUploadHandler upload=postSnapshot.getValue(ImageUploadHandler.class);
                    mUpload.remove(upload);
                }
                mAdapter=new ImageAdapterUser(ListofImagesUser.this,mUpload);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ListofImagesUser.this, "Error:"+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    }
