package com.example.shareer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListofPdfClient extends AppCompatActivity {

    ListView pdfview;
    DatabaseReference databaseReference;
    List<PdfUploadHandler> uploadPdfs;
    FirebaseUser firebaseUser;
    String userID,folderName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listof_pdf_client);

//        getSupportActionBar().setTitle("Your PDFs");

        pdfview=findViewById(R.id.listview);
        uploadPdfs=new ArrayList<>();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        viewAllFiles();
        pdfview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PdfUploadHandler pdfUploadHandler=uploadPdfs.get(i);
                Intent intent=new Intent();
                Uri file = Uri.parse(pdfUploadHandler.getUrl());
                intent.setDataAndType(file, "application/pdf");
                Log.d("stoopid", file.toString());
                startActivity(intent);
            }
        });
    }
    private void viewAllFiles() {

        userID=getIntent().getStringExtra("myuserid");
        folderName=getIntent().getStringExtra("myfoldername");
        databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(userID).child("Multiple").child(folderName).child("Document");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                    PdfUploadHandler pdfUploadHandler=postSnapshot.getValue(com.example.shareer.PdfUploadHandler.class);
                    uploadPdfs.add(pdfUploadHandler);
                }
                String[] uploads=new String[uploadPdfs.size()];
                for (int i=0;i<uploads.length;i++){
                    uploads[i]=uploadPdfs.get(i).getName();
                }


                Log.d("uploads", Arrays.toString(uploads));
                ArrayAdapter<String> adapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,uploads){
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        return super.getView(position, convertView, parent);
                    }
                };
                pdfview.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}