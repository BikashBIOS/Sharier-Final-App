package com.example.shareer.MultipleImagesPackage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shareer.ItemClickListener;
import com.example.shareer.Layout.MainPage;
import com.example.shareer.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.MyHolder>{

    Context context;
    List<FolderModel> foldersList;
    SharedPreferences sharedPreferences;

    public FolderAdapter(Context context, List<FolderModel> foldersList) {
        this.context = context;
        this.foldersList = foldersList;
    }

    @NonNull
    @Override
    public FolderAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_folder, parent,false);

        return new FolderAdapter.MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderAdapter.MyHolder holder, int position) {
        final FolderModel currentFolder=foldersList.get(position);

        holder.folderName.setText(currentFolder.getName());

        String folderName=currentFolder.getName();
        sharedPreferences=context.getSharedPreferences("MyFolderName", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("folderId", folderName);
        editor.commit();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, MainPage.class);
                intent.putExtra("folderName",currentFolder.getName());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return foldersList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView folderName;
        public ItemClickListener listener;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            folderName=itemView.findViewById(R.id.foldernameTv);
        }
        public void setOnclickListener(ItemClickListener listener){
            this.listener=listener;
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view,getAdapterPosition(),false);
        }
    }
}
