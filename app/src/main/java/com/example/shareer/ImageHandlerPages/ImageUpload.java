package com.example.shareer.ImageHandlerPages;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.net.sip.SipRegistrationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anstrontechnologies.corehelper.AnstronCoreHelper;
import com.example.shareer.R;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.iceteck.silicompressorr.FileUtils;
import com.iceteck.silicompressorr.SiliCompressor;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;


public class ImageUpload extends AppCompatActivity {

    private  static  final int PICK_IMAGE_REQUEST=1;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    private Button mButtonChooseImage;
    private Button mButtonUpload;
    private TextView mTextViewShowUpload;
    private EditText mEditTextFileName,mDescription;
    private ProgressBar progressBar;
    private ImageView mImageView;

    private Uri mImageUri;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference, storageReference2;
    AnstronCoreHelper coreHelper;
    private DatabaseReference databaseReference;

    private StorageTask storageTask;
    public static final int write=1;
    String folderName;
    String userid;
    SharedPreferences preferences,sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);
//        getSupportActionBar().setTitle("Upload Image");

        mButtonChooseImage=findViewById(R.id.imgchoose);
        mButtonUpload=findViewById(R.id.btnupload);
        mTextViewShowUpload=findViewById(R.id.showupload);
        mEditTextFileName=findViewById(R.id.filename);
        progressBar=findViewById(R.id.progressbar);
        mImageView=findViewById(R.id.imageview);
        mDescription=findViewById(R.id.mdesc);
        coreHelper=new AnstronCoreHelper(this);

        folderName=getIntent().getStringExtra("FolderName");
        if (folderName!=null)
        {
            mDescription.append(folderName);
        }
        else
        {
            mDescription.append("Folder1");
        }


        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        userid = firebaseUser.getUid();



        preferences=getApplicationContext().getSharedPreferences("MyUser", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("userId",userid );
        editor.commit();


        String fileName=mEditTextFileName.getText().toString();
        sharedPreferences=getApplicationContext().getSharedPreferences("Fname",MODE_PRIVATE);
        SharedPreferences.Editor editor1=sharedPreferences.edit();
        editor1.putString("fileName",fileName);
        editor1.commit();

        storageReference= FirebaseStorage.getInstance().getReference("ImageCompresss");
        databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(userid).child("Uploads");


        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (storageTask!=null && storageTask.isInProgress())
                {
                    Toast.makeText(ImageUpload.this, "Upload in progress ....", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    /*uploadandcompress();*/
                    UploadImage();
                }
                //uploadandcompress();
            }
        });

        mTextViewShowUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageActivity();
            }
        });
    }

    private void UploadImage() {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED){
                String[] permission={Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, write);

            }
            else {
                String fName = mDescription.getText().toString().trim();
                if (fName.isEmpty()) {
                    Toast.makeText(this, "Enter Folder Name", Toast.LENGTH_SHORT).show();
                } else {

                    //pair list
                    Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
                    OutputStream outputstream;
                    String time = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());
                    File sdcard = Environment.getExternalStorageDirectory();
                    File directory = new File(sdcard + "/Compressor");
                    directory.mkdirs();
                    String fileName = time + "." + getFileExtension(mImageUri);
                    File outfile = new File(directory, fileName);

                    try {
                        outputstream = new FileOutputStream(outfile);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputstream);
                        outputstream.flush();
                        outputstream.close();
                        Uri urif = Uri.fromFile(outfile);
                        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid).child("Multiple").child(fName);
                        databaseReference.child("Name").setValue(fName);
                        DatabaseReference  databaseReference2 = FirebaseDatabase.getInstance().getReference("Users").child(userid).child("Multiple").child(fName).child("Images");
                        final StorageReference fileReference = storageReference.child(coreHelper.getFileNameFromUri(urif));

                        storageTask = fileReference.putFile(urif).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setProgress(0);
                                    }
                                }, 5000);
                                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String uploadId = databaseReference.push().getKey();

                                        ImageUploadHandler upload = new ImageUploadHandler(mEditTextFileName.getText().toString().trim(), uri.toString(), mDescription.getText().toString(), uploadId);
                                        databaseReference2.child(uploadId).setValue(upload);
                                        Toast.makeText(ImageUpload.this, "Upload Successful", Toast.LENGTH_SHORT).show();


                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ImageUpload.this, "Error1:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double prog = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                progressBar.setProgress((int) prog);
                            }
                        });

                        Toast.makeText(ImageUpload.this, "Compress Successful", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(ImageUpload.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        }
    }

    private void openFileChooser()
    {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            mImageUri=data.getData();

            Picasso.get().load(mImageUri).into(mImageView);
        }
    }

    private String getFileExtension(Uri uri)
    {
        ContentResolver cr=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void uploadandcompress() {
        if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {
            if (mImageUri != null) {
                final File file = new File(SiliCompressor.with(this).compress(FileUtils.getPath(this, mImageUri), new File(this.getCacheDir(), "temp")));
                Uri urif = Uri.fromFile(file);
                final StorageReference fileReference=storageReference.child(coreHelper.getFileNameFromUri(urif));

                storageTask=fileReference.putFile(urif).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Handler handler= new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setProgress(0);
                            }
                        },5000);
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String uploadId = databaseReference.push().getKey();

                                    ImageUploadHandler upload = new ImageUploadHandler(mEditTextFileName.getText().toString().trim(), uri.toString(), mDescription.getText().toString(), uploadId);
                                    databaseReference.child(uploadId).setValue(upload);
                                    Toast.makeText(ImageUpload.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                                }
                            });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ImageUpload.this, "Error1:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double prog = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressBar.setProgress((int) prog);
                    }
                });

            }
        }
    }

    /*private void uploadFile()
    {
        if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {
            if (mImageUri != null) {
                final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));
                File file = new File(SiliCompressor.with(this).compress(FileUtils.getPath(this, mImageUri), new File(this.getCacheDir(), "temp")));
                Uri uri = Uri.fromFile(file);
                storageTask = fileReference.child(coreHelper.getFileNameFromUri(uri)).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setProgress(0);
                            }
                        }, 5000);
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String uploadId = databaseReference.push().getKey();

                                ImageUploadHandler upload = new ImageUploadHandler(mEditTextFileName.getText().toString().trim(), uri.toString(), mDescription.getText().toString(), uploadId);
                                databaseReference.child(uploadId).setValue(upload);
                                Toast.makeText(ImageUpload.this, "Upload Successful", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ImageUpload.this, "Error1:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double prog = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressBar.setProgress((int) prog);
                    }
                });
            } else {
                Toast.makeText(this, "No file selected !!!!", Toast.LENGTH_SHORT).show();
            }
        }
    }*/
    private void openImageActivity()
    {
        Intent intent=new Intent(ImageUpload.this,ListofImagesUser.class);
        startActivity(intent);
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
                    Toast.makeText(ImageUpload.this, "GET_ACCOUNTS Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }
}