package com.example.shareer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.shareer.ImageHandlerPages.ImageUpload;
import com.example.shareer.Layout.VideoFragment;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.nagihong.videocompressor.VideoCompressor;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class VideoActivity extends AppCompatActivity {

    TextInputLayout editText;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    Member member;
    ProgressBar progressBar;
    StorageTask storageTask;
    FirebaseAuth firebaseAuth;
    UploadTask uploadTask;
    Uri videoUri;
    String output, folderName;
    Button upload, showvideo;
    VideoView videoView;
    MediaController mediaController;
    TextView textshow,progressPercent;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        upload=findViewById(R.id.uploadvideo);
        textshow=findViewById(R.id.textshow);
        showvideo=findViewById(R.id.showvideo);
        videoView=findViewById(R.id.videoview_main);
        mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        videoView.start();
        progressPercent=findViewById(R.id.progressPercent);
        progressBar = findViewById(R.id.progressBar_main);

        member = new Member();
        firebaseAuth= FirebaseAuth.getInstance();
        editText=findViewById(R.id.et_video_name);
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String userid = firebaseUser.getUid();
        sharedPreferences=getApplicationContext().getSharedPreferences("MyFolder", Context.MODE_PRIVATE);
        folderName=sharedPreferences.getString("folderrId", "");
        storageReference = FirebaseStorage.getInstance().getReference("Videos");
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid).child("Multiple").child(folderName).child("Videos");

        findViewById(R.id.start)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(checkPermission()){
                            clickStart();
                            textshow.setVisibility(View.VISIBLE);
                    }
                }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uploadTask!=null && uploadTask.isInProgress())
                {
                    Toast.makeText(VideoActivity.this, "Upload in progress ....", Toast.LENGTH_SHORT).show();
                }
                else {
                    uploadVideoo();
                }
            }
        });

        showvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(VideoActivity.this, Showvideo.class));
            }
        });
    }
    void clickStart() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("video/*");
        startActivityForResult(intent, 123);
    }

    void showInput(String path) {
        ((TextView) findViewById(R.id.input)).setText(String.format(Locale.getDefault(), "input: %s", path));
    }

    void showOutput(String path) {
        ((TextView) findViewById(R.id.output)).setText(String.format(Locale.getDefault(), "output: %s", path));
    }

    void compressVideo(String path) {
        showInput(path);
        textshow.setText("Compressing your Video...");
        output = Environment.getExternalStorageDirectory() +"/Compressor"+ File.separator + System.currentTimeMillis() + ".mp4";

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    new VideoCompressor().compressVideo(VideoActivity.this, path, output);
                    findViewById(R.id.output).post(() -> {
                        showOutput(output);
                        hideProgress();
                        textshow.setText("Now you can Upload");
                        upload.setEnabled(true);
                    });
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(VideoActivity.this, "Action can't be perform", Toast.LENGTH_SHORT).show();
                }

            }
        }.start();

    }

    //new function
    private void uploadVideoo(){
        videoUri=Uri.fromFile(new File(output));
        String videoName=editText.getEditText().getText().toString();
        if(videoUri != null && !videoName.isEmpty()) {

            //progressBar.setVisibility(View.VISIBLE);/
            try {
                final StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + getExt(videoUri));
                uploadTask = reference.putFile(videoUri);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setProgress(0);
                                progressPercent.setText("0%");
                            }
                        }, 5000);

                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(VideoActivity.this, "Data saved", Toast.LENGTH_SHORT).show();

                                member.setName(videoName);
                                member.setVideourl(uri.toString());
                                String i = databaseReference.push().getKey();
                                databaseReference.child(i).setValue(member);
                                textshow.setText("Video Uploaded");

                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(VideoActivity.this, "Error1:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double prog = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressBar.setProgress((int) prog);
                        progressPercent.setText(String.valueOf((int)prog+"%"));
                    }
                });
            }
            catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(VideoActivity.this, "Please enter Video Title", Toast.LENGTH_SHORT).show();
        }


    }
    //


    private void uploadVideo(){
        videoUri=Uri.fromFile(new File(output));
        String videoName=editText.getEditText().getText().toString();
        if(videoUri != null && !videoName.isEmpty()) {

            progressBar.setVisibility(View.VISIBLE);

            final  StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + getExt(videoUri));
            uploadTask = reference.putFile(videoUri);

            Task<Uri> urltask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                }
            })
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        if (task.isSuccessful()) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(0);
                                }
                            }, 5000);
                            Uri downloadUrl = task.getResult();
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(VideoActivity.this, "Data saved", Toast.LENGTH_SHORT).show();

                            member.setName(videoName);
                            member.setVideourl(downloadUrl.toString());
                            String i = databaseReference.push().getKey();
                            databaseReference.child(i).setValue(member);
                            textshow.setText("Video Uploaded");
                        }else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(VideoActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        }else {
            Toast.makeText(VideoActivity.this, "Please enter Video Title", Toast.LENGTH_SHORT).show();
        }
    }

    private String getExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(videoUri));
    }

    void showProgress() {
        findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
    }

    void hideProgress() {
        findViewById(R.id.progressbar).setVisibility(View.GONE);
    }

    //============================ callback ================================================

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            if (null == data) return;
            String path = getPathFromGalleryUri(this, data.getData());
            videoView.setVideoPath(path);
            if (TextUtils.isEmpty(path)) return;
            showProgress();
            compressVideo(path);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 124) {
            boolean allPermissionsGranted = true;
            if (grantResults.length == 0) {
                allPermissionsGranted = false;
            } else {
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        allPermissionsGranted = false;
                    }
                }
            }
            if (!allPermissionsGranted) {
                Toast.makeText(this, "please give me a permission to save the output file", Toast.LENGTH_LONG).show();
            } else {
                clickStart();
            }
        }
    }

    private boolean checkPermission() {
        List<String> permissions = new LinkedList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissions.size() > 0) {
            String[] arr = new String[permissions.size()];
            permissions.toArray(arr);
            ActivityCompat.requestPermissions(this, arr, 124);
            return false;
        }
        return true;
    }

    public static String getPathFromGalleryUri(Context context, Uri contentUri) {
        Cursor cursor;
        String[] proj = {MediaStore.Images.Media.DATA};
        cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (null == cursor) return null;

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);

        cursor.close();
        return result;
    }
}