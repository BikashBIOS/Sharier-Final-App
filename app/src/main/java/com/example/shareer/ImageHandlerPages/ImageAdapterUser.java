package com.example.shareer.ImageHandlerPages;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shareer.ItemClickListener;
import com.example.shareer.R;
import com.example.shareer.Showvideo;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

public class ImageAdapterUser extends RecyclerView.Adapter<ImageAdapterUser.ImageViewerHolder>{
    private Context mContext;
    private List<ImageUploadHandler> mUpload;
    DatabaseReference databaseReference;
    SharedPreferences sharedPreferences,sharedPreferences1;
    public ImageAdapterUser(Context context,List<ImageUploadHandler> uploads)
    {
        mContext=context;
        mUpload=uploads;
    }
    @NonNull
    @Override
    public ImageAdapterUser.ImageViewerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(mContext).inflate(R.layout.image_item,parent,false);
        return new ImageAdapterUser.ImageViewerHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapterUser.ImageViewerHolder holder, int position) {

        final ImageUploadHandler uploadCurrent=mUpload.get(position);
        String imgKey=uploadCurrent.getmImageUri();
        holder.textViewName.setText(uploadCurrent.getmName());
        Picasso.get().load(uploadCurrent.getmImageUri()).fit().centerCrop().into(holder.imageView);
        holder.mDescription.setText(uploadCurrent.getmImageUri());

        holder.shareLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String urlLink=holder.mDescription.getText().toString();
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT,urlLink);
                intent.setType("text/plain");
                mContext.startActivity(intent);
            }
        });

        holder.downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri= Uri.parse(holder.mDescription.getText().toString());
                DownloadManager manager=(DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request=new DownloadManager.Request(uri);
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE|DownloadManager.Request.NETWORK_WIFI);

                request.setTitle("File is downloading");

                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,holder.textViewName.getText().toString()+".png");
                request.setMimeType("*/*");
                manager.enqueue(request);


            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Delete");
                builder.setMessage("Are you Sure to Delete this data");
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sharedPreferences=mContext.getSharedPreferences("MyUser", Context.MODE_PRIVATE);
                        String userid=sharedPreferences.getString("userId", "");
                        sharedPreferences1=mContext.getSharedPreferences("Fname",Context.MODE_PRIVATE);
                        String fName=sharedPreferences1.getString("fileName","");
//                        FirebaseDatabase.getInstance().getReference("Users").child(userid).child("Uploads").removeValue();


                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                       String  muserid = firebaseUser.getUid();

                        Query query =  FirebaseDatabase.getInstance().getReference("Users").child(muserid).child("Multiple").child("Drawing").child("Images").orderByChild("imgLink").equalTo(imgKey);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                    dataSnapshot1.getRef().removeValue();
                                }
                                Toast.makeText(mContext, "Image Deleted", Toast.LENGTH_SHORT).show();
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

                return true;
            }
        });
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(mContext,ImageDetailUser.class);
//                intent.putExtra("imgKeyUser",uploadCurrent.getmKey());
//                intent.putExtra("DownloadUriUser", uploadCurrent.getmImageUri());
//                mContext.startActivity(intent);
//            }
//        });

    }
    @Override
    public int getItemCount() {
        return mUpload.size();
    }

    public class ImageViewerHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public TextView textViewName;
        public ImageView imageView;
        public TextView mDescription;
        public ItemClickListener listener;
        public ImageButton shareLink,downloadButton;
        public ImageViewerHolder(@NonNull View itemView) {
            super(itemView);

            textViewName=itemView.findViewById(R.id.title);
            imageView=itemView.findViewById(R.id.image_view_upload);
            mDescription=itemView.findViewById(R.id.descriptionIv);
            shareLink=itemView.findViewById(R.id.copyLinkUrl);
            downloadButton=itemView.findViewById(R.id.imgDownload);
        }
        public void setOnclickListener(ItemClickListener listener){
            this.listener=listener;
        }
        @Override
        public void onClick(View v) {
            listener.onClick(v,getAdapterPosition(),false);
        }
    }


}
