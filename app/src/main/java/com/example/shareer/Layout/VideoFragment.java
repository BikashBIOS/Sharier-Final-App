package com.example.shareer.Layout;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.shareer.Fullscreen;
import com.example.shareer.Member;
import com.example.shareer.R;
import com.example.shareer.Showvideo;
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

public class VideoFragment extends Fragment {

    DatabaseReference databaseReference, likesrefernce;
    RecyclerView recyclerView;
    FirebaseDatabase database;
    String name,url,downloadurl,folderName;
    FirebaseAuth firebaseAuth;
    SharedPreferences sharedPreferences;
    private  static final int PERMISSION_STORAGE_CODE = 1000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_video, container, false);

        firebaseAuth= FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String userid = firebaseUser.getUid();
        sharedPreferences=getActivity().getSharedPreferences("MyFolder", Context.MODE_PRIVATE);
        folderName=sharedPreferences.getString("folderrId", "");
        recyclerView = view.findViewById(R.id.recyclerview_ShowVideo);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Users").child(userid).child("Multiple").child(folderName).child("Videos");

        return view;
    }
    private void startDownloading(String downloadurl) {
        DownloadManager.Request request  = new DownloadManager.Request(Uri.parse(downloadurl));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle(name);
        request.setDescription("Downloading file...");


        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,name+".mp4");

        DownloadManager manager = (DownloadManager)getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }


    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<Member> options =
                new FirebaseRecyclerOptions.Builder<Member>()
                        .setQuery(databaseReference,Member.class)
                        .build();

        FirebaseRecyclerAdapter<Member, ViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Member, ViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ViewHolder holder, final int position, @NonNull Member model) {

                        holder.setExoplayer(getContext(), model.getName(), model.getVideourl());

                        holder.downloadBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                    if (getActivity().checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                            PackageManager.PERMISSION_DENIED){
                                        String permission = (Manifest.permission.WRITE_EXTERNAL_STORAGE);

                                        requestPermissions(new String[]{permission},PERMISSION_STORAGE_CODE);
                                    }else {
                                        downloadurl = getItem(position).getVideourl();
                                        startDownloading(downloadurl);
                                    }
                                }else {
                                    startDownloading(downloadurl);
                                }

                            }
                        });

                        holder.shareLink.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String urlLink=getItem(position).getVideourl();
                                Intent intent=new Intent();
                                intent.setAction(Intent.ACTION_SEND);
                                intent.putExtra(Intent.EXTRA_TEXT,urlLink);
                                intent.setType("text/plain");
                                startActivity(intent);
                            }
                        });
                        holder.setOnClicklistener(new ViewHolder.Clicklistener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                name = getItem(position).getName();
                                url = getItem(position).getVideourl();
                                Intent intent = new Intent(getContext(), Fullscreen.class);
                                intent.putExtra("nam",name);
                                intent.putExtra("ur",url);
                                startActivity(intent);
                            }

                            @Override
                            public void onItemLongClick(View view, int position) {

                                name = getItem(position).getName();
                                showDeleteDialog(name);
                            }
                        });
                    }
                    @NonNull
                    @Override
                    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item,parent,false);

                        return new ViewHolder(view);

                    }
                };

        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }



    private void showDeleteDialog(final String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete");
        builder.setMessage("Are you Sure to Delete this data");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Query query = databaseReference.orderByChild("name").equalTo(name);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            dataSnapshot1.getRef().removeValue();
                        }
                        Toast.makeText(getContext(), "Video Deleted", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        ///
                    }
                });

            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_STORAGE_CODE:{
                if (grantResults.length >0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    startDownloading(downloadurl);
                }
                else {
                    Toast.makeText(getContext(), "permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}