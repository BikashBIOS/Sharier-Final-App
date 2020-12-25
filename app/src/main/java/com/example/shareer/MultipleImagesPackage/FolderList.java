package com.example.shareer.MultipleImagesPackage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.shareer.ImageHandlerPages.ImageUpload;
import com.example.shareer.MainActivity;
import com.example.shareer.PdfUpload;
import com.example.shareer.R;
import com.example.shareer.User.LoginUser;
import com.example.shareer.User.UsersList;
import com.example.shareer.UserGuide;
import com.example.shareer.VideoActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FolderList extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView recyclerViewFolder;
    FolderAdapter foldersAdapter;
    List<FolderModel> foldersList;
    FirebaseAuth mAuth;
    FloatingActionButton btnImage,btnVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_list);
        //getSupportActionBar().setTitle("Your Folders");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);
        mAuth=FirebaseAuth.getInstance();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(this, drawer,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
//        TextView userNameTextView = headerView.findViewById(R.id.user_profile_name);
//        CircleImageView profileImageView = headerView.findViewById(R.id.user_profile_image);

        recyclerViewFolder=findViewById(R.id.folderlistrecyclerview);
        recyclerViewFolder.setLayoutManager(new LinearLayoutManager(FolderList.this));
        recyclerViewFolder.setHasFixedSize(true);
        foldersList=new ArrayList<>();
        btnImage=findViewById(R.id.imgBtn_main);
     //   btnVideo=findViewById(R.id.vidBtn_main);

        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FolderList.this, ImageUpload.class);
                startActivity(intent);
            }
        });

//        btnVideo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(FolderList.this, VideoActivity.class);
//                startActivity(intent);
//            }
//        });

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Multiple");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foldersList.clear();

                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    FolderModel modelFolder=dataSnapshot.getValue(FolderModel.class);
                    foldersList.add(modelFolder);

                    /*if (!modelUser.getUid().equals(firebaseUser.getUid())){

                    }*/
                    foldersAdapter=new FolderAdapter(FolderList.this,foldersList);
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
                mAuth.signOut();
                startActivity(new Intent(FolderList.this, LoginUser.class));
                finish();
                break;
            case R.id.uploadpdfs:
                Intent intent=new Intent(FolderList.this, PdfUpload.class);
                startActivity(intent);
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id=menuItem.getItemId();
        if (id==R.id.createFolder)
        {
            Intent intent=new Intent(FolderList.this,ImageUpload.class);
            startActivity(intent);
        }

        if (id==R.id.userList)
        {
            Intent intent=new Intent(FolderList.this, UsersList.class);
            startActivity(intent);
        }
        if (id==R.id.logOut)
        {
            FirebaseAuth.getInstance().signOut();
            Intent intent=new Intent(FolderList.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        if (id==R.id.help)
        {
            Intent intent=new Intent(FolderList.this, UserGuide.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}