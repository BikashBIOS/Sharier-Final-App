package com.example.shareer.User;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shareer.ImageHandlerPages.ImageList;
import com.example.shareer.MultipleImagesPackage.FolderListClient;
import com.example.shareer.ItemClickListener;
import com.example.shareer.MultipleImagesPackage.MultipleImagesList;
import com.example.shareer.MultipleImagesPackage.MultipleImagesListClient;
import com.example.shareer.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyHolder>{

    Context context;
    List<ModelUser> usersList;
    SharedPreferences sharedPreferences;

    public UsersAdapter(Context context, List<ModelUser> usersList) {
        this.context = context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_users, parent,false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        /*String userName=usersList.get(position).getName();
        String userEmail=usersList.get(position).getEmail();*/

        final ModelUser currentUser=usersList.get(position);

        holder.mNameUser.setText(currentUser.getName());
        holder.mEmailUser.setText(currentUser.getEmail());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, ImageList.class);// I am Changing here
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("userUid",currentUser.getId());
                intent.putExtra("userName", currentUser.getName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView mNameUser, mEmailUser;
        public ItemClickListener listener;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            mNameUser=itemView.findViewById(R.id.usernameTv);
            mEmailUser=itemView.findViewById(R.id.useremailIv);
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
