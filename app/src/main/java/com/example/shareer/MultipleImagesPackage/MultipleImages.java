package com.example.shareer.MultipleImagesPackage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.anstrontechnologies.corehelper.AnstronCoreHelper;
import com.example.shareer.ImageHandlerPages.ImageUpload;
import com.example.shareer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.FileUtils;
import com.iceteck.silicompressorr.SiliCompressor;

import java.io.File;
import java.util.ArrayList;

public class MultipleImages extends AppCompatActivity {

    private final static int PICK_IMAGE=1;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    Button uploadImages, chooseImages, uploadoneImage;
    TextView imageAlert;
    TextInputLayout folderName;
    int upload_count=0;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReference2;
    AnstronCoreHelper coreHelper;

    ArrayList<Uri> imagesList=new ArrayList<Uri>();
    private Uri ImageUri;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_images);

        imageAlert=findViewById(R.id.textImageAlert);
        uploadImages=findViewById(R.id.imagesuploadbtn);
        uploadoneImage=findViewById(R.id.upload1imagebtn);
        chooseImages=findViewById(R.id.imageschoosebtn);
        folderName=findViewById(R.id.editfoldername);

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        coreHelper=new AnstronCoreHelper(this);

        uploadoneImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MultipleImages.this, FolderList.class));
            }
        });

        chooseImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

        uploadImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadmultiple();
                UploadMultipleImages();
            }
        });
    }

    private void UploadMultipleImages() {
        if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {

            progressDialog.show();
            firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            String userid = firebaseUser.getUid();
            String editFolderName = folderName.getEditText().getText().toString().trim();

            if (editFolderName.isEmpty()) {
                progressDialog.dismiss();
                Toast.makeText(MultipleImages.this, "Please Enter a Folder Name", Toast.LENGTH_SHORT).show();
            } else {
                databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid).child("Multiple").child(editFolderName);
                StorageReference ImageFolder = FirebaseStorage.getInstance().getReference("Users").child(userid).child(editFolderName);
                /*FolderModel folderModel=new FolderModel(editFolderName);*/
                databaseReference.child("Name").setValue(editFolderName);
                databaseReference2 = FirebaseDatabase.getInstance().getReference("Users").child(userid).child("Multiple").child(editFolderName).child("Images");

                for (upload_count = 0; upload_count < imagesList.size(); upload_count++) {
                    Uri IndividualImage = imagesList.get(upload_count);
                    File file = new File(SiliCompressor.with(this).compress(FileUtils.getPath(this, IndividualImage), new File(this.getCacheDir(), "temp")));
                    Uri urif = Uri.fromFile(file);
                    final StorageReference imageName = ImageFolder.child(urif.getLastPathSegment());

                    imageName.putFile(imagesList.get(upload_count)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String url = String.valueOf(uri);
                                    String imageKey = databaseReference.push().getKey();

                                    MultipleImagesHandler handler = new MultipleImagesHandler(url, imageKey);
                                    databaseReference2.child(imageKey).setValue(handler).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressDialog.dismiss();
                                            imageAlert.setText("Images Uploaded Successfully");
                                            uploadImages.setVisibility(View.GONE);
                                            imagesList.clear();
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            }
        }
    }


    public void uploadmultiple(){
        if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {

            progressDialog.show();
            firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            String userid = firebaseUser.getUid();
            String editFolderName = folderName.getEditText().getText().toString().trim();

            if (editFolderName.isEmpty()) {
                progressDialog.dismiss();
                Toast.makeText(MultipleImages.this, "Please Enter a Folder Name", Toast.LENGTH_SHORT).show();
            } else {
                databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid).child("Multiple").child(editFolderName);
                StorageReference ImageFolder = FirebaseStorage.getInstance().getReference("Users").child(userid).child(editFolderName);
                /*FolderModel folderModel=new FolderModel(editFolderName);*/
                databaseReference.child("Name").setValue(editFolderName);
                databaseReference2 = FirebaseDatabase.getInstance().getReference("Users").child(userid).child("Multiple").child(editFolderName).child("Images");

                for (upload_count = 0; upload_count < imagesList.size(); upload_count++) {
                    Uri IndividualImage = imagesList.get(upload_count);
                    File file = new File(SiliCompressor.with(this).compress(FileUtils.getPath(this, IndividualImage), new File(this.getCacheDir(), "temp")));
                    Uri urif = Uri.fromFile(file);
                    final StorageReference imageName = ImageFolder.child(urif.getLastPathSegment());

                    imageName.putFile(imagesList.get(upload_count)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String url = String.valueOf(uri);
                                    String imageKey = databaseReference.push().getKey();
                                        /*HashMap<String,String> hashMap=new HashMap<>();
                                        hashMap.put("ImgLink", url);
                                        hashMap.put("ImgKey", imageKey);*/
                                    MultipleImagesHandler handler = new MultipleImagesHandler(url, imageKey);
                                    databaseReference2.child(imageKey).setValue(handler).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressDialog.dismiss();
                                            imageAlert.setText("Images Uploaded Successfully");
                                            uploadImages.setVisibility(View.GONE);
                                            imagesList.clear();
                                        }
                                    });
                                        /*databaseReference.push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                progressDialog.dismiss();
                                                imageAlert.setText("Images Uploaded Successfully");
                                                uploadImages.setVisibility(View.GONE);
                                                imagesList.clear();
                                            }
                                        });*/
                                }
                            });
                        }
                    });
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==PICK_IMAGE){
            if (resultCode==RESULT_OK){
                if (data.getClipData()!=null){
                    int countClipData=data.getClipData().getItemCount();
                    int currentImageSelect=0;

                    while (currentImageSelect<countClipData){
                        ImageUri=data.getClipData().getItemAt(currentImageSelect).getUri();
                        imagesList.add(ImageUri);
                        currentImageSelect=currentImageSelect+1;

                    }
                    imageAlert.setVisibility(View.VISIBLE);
                    imageAlert.setText("You Have Selected "+imagesList.size()+" Images");
                    chooseImages.setVisibility(View.GONE);
                }
                else {
                    Toast.makeText(this, "Select multiple images", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do your stuff
                } else {
                    Toast.makeText(MultipleImages.this, "GET_ACCOUNTS Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }
}