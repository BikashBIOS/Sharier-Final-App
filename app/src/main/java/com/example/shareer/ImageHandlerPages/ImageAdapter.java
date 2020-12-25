package com.example.shareer.ImageHandlerPages;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewerHolder>{
    private Context mContext;
    private List<ImageUploadHandler> mUpload;
    SharedPreferences sharedPreferences,sharedPreferences1;
    public ImageAdapter(Context context,List<ImageUploadHandler> uploads)
    {
        mContext=context;
        mUpload=uploads;
    }
    @NonNull
    @Override
    public ImageViewerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(mContext).inflate(R.layout.image_item,parent,false);
        return new ImageViewerHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewerHolder holder, int position) {

        final ImageUploadHandler uploadCurrent=mUpload.get(position);
        holder.textViewName.setText(uploadCurrent.getmName());
        Picasso.get().load(uploadCurrent.getmImageUri()).fit().centerCrop().into(holder.imageView);
        holder.mDescription.setText(uploadCurrent.getmImageUri());

        holder.shareUrl.setOnClickListener(new View.OnClickListener() {
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

        holder.downloadImg.setOnClickListener(new View.OnClickListener() {
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


        
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(mContext,ImageDetailActivity.class);
//                intent.putExtra("imgKey",uploadCurrent.getmKey());
//                intent.putExtra("DownloadUri", uploadCurrent.getmImageUri());
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
        public ImageButton shareUrl,downloadImg;
        public ItemClickListener listener;
        public ImageViewerHolder(@NonNull View itemView) {
            super(itemView);

            textViewName=itemView.findViewById(R.id.title);
            imageView=itemView.findViewById(R.id.image_view_upload);
            mDescription=itemView.findViewById(R.id.descriptionIv);
            shareUrl=itemView.findViewById(R.id.copyLinkUrl);
            downloadImg=itemView.findViewById(R.id.imgDownload);
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
