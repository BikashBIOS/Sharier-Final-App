package com.example.shareer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.anstrontechnologies.corehelper.AnstronCoreHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

public class PdfUpload extends AppCompatActivity {

    Button btn_upload,viewPDF;
    EditText editPdfName;
    private FirebaseAuth firebaseAuth;

    StorageReference storageReference;
    DatabaseReference databaseReference;
    String folderName;
    String userid;
    AnstronCoreHelper coreHelper;
    private StorageTask storageTask;
    UploadTask uploadTask;
    PdfUploadHandler uploadHandler;
    String folderShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_upload);
//        getSupportActionBar().setTitle("Upload PDF");

        btn_upload=findViewById(R.id.upload);
        viewPDF=findViewById(R.id.view);
        editPdfName=findViewById(R.id.name);
        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        folderName=getIntent().getStringExtra("FolderName");



         userid = firebaseUser.getUid();

        storageReference= FirebaseStorage.getInstance().getReference("document");
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid).child("Multiple").child(folderName).child("Document");

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPdf();
            }
        });
        viewPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                folderShare=folderName;
                //startActivity(new Intent(PdfUpload.this,ListofPdf.class));

                Intent intent=new Intent(PdfUpload.this,ListofPdf.class);
                intent.putExtra("folderShare",folderShare);
                startActivity(intent);
            }
        });
    }

    private void selectPdf() {

        Intent intent=new Intent();
        intent.setType("application/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select PDF File"),1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            uploadPdfFile(data.getData());
        }
    }

    /**
     Upload document
     */

    private void uploadPdfFile(Uri data) {
        String fName = getIntent().getStringExtra("FolderName");
        if (data.equals(null))
        {
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
        }
        else {
            final ProgressDialog progressDialog=new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference reference=storageReference.child("UploadPDF/"+System.currentTimeMillis()+getExt(data));
            reference.putFile(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uri=taskSnapshot.getStorage().getDownloadUrl();
                            while(!uri.isComplete());
                            Uri url=uri.getResult();
                            String pdfKey=databaseReference.push().getKey();

                            PdfUploadHandler pdfUploadHandler=new PdfUploadHandler(editPdfName.getText().toString(),url.toString(),pdfKey);
                            databaseReference.child(pdfKey).setValue(pdfUploadHandler);
                            Toast.makeText(PdfUpload.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress=(100*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded: "+(int)progress+" %");
                        }
                    });

//            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "."+".pdf");
//
//            uploadTask=fileReference.putFile(data);
//
//            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            uploadHandler.setName(editPdfName.getText().toString());
//                            uploadHandler.setUrl(uri.toString());
//                            String i = databaseReference.push().getKey();
//                            databaseReference.child(i).setValue(uploadHandler);
//                        }
//                    });
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(PdfUpload.this, "Something went wrong", Toast.LENGTH_SHORT).show();
//                }
//            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
//                    double progress=(100*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
//                    progressDialog.setMessage("Uploaded: "+(int)progress+" %");
//                }
//            });

//            storageTask = fileReference.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    Task<Uri> uri=taskSnapshot.getStorage().getDownloadUrl();
//                    while(!uri.isComplete());
//                    Uri url=uri.getResult();
//                    String pdfKey=databaseReference.push().getKey();
//
//                    PdfUploadHandler pdfUploadHandler=new PdfUploadHandler(editPdfName.getText().toString(),url.toString(),pdfKey);
//                    databaseReference.child(pdfKey).setValue(pdfUploadHandler);
//                    Toast.makeText(PdfUpload.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
//                    progressDialog.dismiss();
//                }
//            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
//
//                    double progress=(100*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
//                    progressDialog.setMessage("Uploaded: "+(int)progress+" %");
//                }
//            });

        }

    }

    private String getExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}