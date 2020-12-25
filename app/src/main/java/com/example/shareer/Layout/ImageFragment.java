package com.example.shareer.Layout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.shareer.ImageHandlerPages.ImageAdapterUser;
import com.example.shareer.ImageHandlerPages.ImageUploadHandler;
import com.example.shareer.ImageHandlerPages.ListofImagesUser;
import com.example.shareer.R;
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

public class ImageFragment extends Fragment {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_image, container, false);

        mAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        /*userId=getIntent().getStringExtra("imageUid");*/


        mRecyclerView=view.findViewById(R.id.irecyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);
        mUpload=new ArrayList<>();
        String folderId=getActivity().getIntent().getStringExtra("folderName");

        sharedPreferences=getActivity().getSharedPreferences("MyFolder", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("folderrId", folderId);
        editor.commit();
        databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Multiple").child(folderId).child("Images");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    ImageUploadHandler upload=postSnapshot.getValue(ImageUploadHandler.class);
                    mUpload.add(upload);
                }
                mAdapter=new ImageAdapterUser(getContext(),mUpload);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error:"+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}