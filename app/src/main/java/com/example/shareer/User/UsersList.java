package com.example.shareer.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.shareer.Client.LoginClient;
import com.example.shareer.R;
import com.example.shareer.ViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsersList extends AppCompatActivity {

    RecyclerView recyclerView;
    UsersAdapter usersAdapter;
    List<ModelUser> usersList;
    FirebaseAuth firebaseAuth;
    SearchView searchView;
    DatabaseReference databaseReference;

    @Override
    protected void onStart() {
        super.onStart();
            final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            databaseReference= FirebaseDatabase.getInstance().getReference("Users");

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    usersList.clear();

                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                        ModelUser modelUser=dataSnapshot.getValue(ModelUser.class);
                        usersList.add(modelUser);

                    /*if (!modelUser.getUid().equals(firebaseUser.getUid())){

                    }*/
                        usersAdapter=new UsersAdapter(UsersList.this,usersList);
                        recyclerView.setAdapter(usersAdapter);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        if (searchView!=null)
        {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    search(newText);
                    return true;
                }
            });
        }
    }

    private void search(String str) {
        ArrayList<ModelUser> users=new ArrayList<>();

        for (ModelUser modelUser:usersList)
        {
            if (modelUser.getName().toLowerCase().contains(str.toLowerCase()))
            {
                users.add(modelUser);
            }
        }

        UsersAdapter myAdapter= new UsersAdapter(getApplicationContext(),users);
        recyclerView.setAdapter(myAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);
//        getSupportActionBar().setTitle("User List");

        recyclerView=findViewById(R.id.userlistrecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(UsersList.this));
        recyclerView.setHasFixedSize(true);
        usersList=new ArrayList<>();
        firebaseAuth=FirebaseAuth.getInstance();

        searchView=findViewById(R.id.userName);
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
                startActivity(new Intent(UsersList.this, LoginClient.class));
                finish();
                break;

        }
        return true;
    }


}