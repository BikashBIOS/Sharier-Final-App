package com.example.shareer.MultipleImagesPackage;

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
import android.widget.Button;
import android.widget.EditText;

import com.example.shareer.Client.LoginClient;
import com.example.shareer.R;
import com.example.shareer.User.LoginUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FolderListClient extends AppCompatActivity {

    RecyclerView recyclerViewFolder;
    FolderAdapterClient foldersAdapter;
    List<FolderModel> foldersList;
    String userId="";
    SharedPreferences sharedPreferences;
    FirebaseAuth firebaseAuth;
    EditText findFolder;
    Button searchImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_list_client);
        getSupportActionBar().setTitle("User Folders");
        firebaseAuth=FirebaseAuth.getInstance();

        recyclerViewFolder=findViewById(R.id.folderlistrecyclerviewclient);
        recyclerViewFolder.setLayoutManager(new LinearLayoutManager(FolderListClient.this));
        recyclerViewFolder.setHasFixedSize(true);
        foldersList=new ArrayList<>();
        findFolder=findViewById(R.id.folderName);
        searchImage=findViewById(R.id.searchImage);
        userId=getIntent().getStringExtra("userUid");

        sharedPreferences=getSharedPreferences("MyUser", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("mUserId", userId);
        editor.commit();

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(userId).child("Multiple");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foldersList.clear();

                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    FolderModel modelFolder=dataSnapshot.getValue(FolderModel.class);
                    foldersList.add(modelFolder);

                    foldersAdapter=new FolderAdapterClient(FolderListClient.this,foldersList);
                    recyclerViewFolder.setAdapter(foldersAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                firebaseAuth.signOut();
                startActivity(new Intent(FolderListClient.this, LoginClient.class));
                finish();
                break;

        }
        return true;
    }
}