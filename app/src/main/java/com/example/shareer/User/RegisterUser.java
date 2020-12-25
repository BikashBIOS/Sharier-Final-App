package com.example.shareer.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shareer.MultipleImagesPackage.FolderList;
import com.example.shareer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterUser extends AppCompatActivity {

    private FirebaseAuth mAuth;
    DatabaseReference reference;
    Button registerUser;
    TextInputLayout username,useremail,userpassword,usercpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();
        registerUser=findViewById(R.id.userregister);
        username=findViewById(R.id.ed_username);
        useremail=findViewById(R.id.ed_useremail);
        userpassword=findViewById(R.id.ed_userpassword);
        usercpassword=findViewById(R.id.ed_usercpassword);

        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateUserName() | !validatePassword() | !validateUserEmail()){
                    Toast.makeText(RegisterUser.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                }
                else{
                    String input_UserName=username.getEditText().getText().toString();
                    String input_UserEmail=useremail.getEditText().getText().toString().trim();
                    String input_UserPassword=userpassword.getEditText().getText().toString();

                    userRegister(input_UserName,input_UserEmail,input_UserPassword);
                }
            }
        });
    }

    public void userRegister(final String userName, final String userEmail, final String userPassword){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering...");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            assert firebaseUser != null;
                            String userid = firebaseUser.getUid();
                            String typeUser="User";

                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("Name", userName);
                            hashMap.put("Email", userEmail);
                            hashMap.put("Password", userPassword);
                            hashMap.put("Type", typeUser);

                            ModelUser modelUser=new ModelUser(userName, userEmail, userPassword, userid);

                            reference.child(userid).setValue(modelUser);

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterUser.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegisterUser.this, FolderList.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterUser.this, "Registration Failed!!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean validateUserName(){
        String txt_username=username.getEditText().getText().toString().trim();
        if (txt_username.isEmpty()){
            username.setError("Name can't be Empty");
            return false;
        }
        else if (txt_username.length()>16){
            username.setError("User Name too Long");
            return false;
        }
        else {
            username.setError(null);
            return true;
        }
    }

    private boolean validateUserEmail(){
        String txt_UserEmail=useremail.getEditText().getText().toString().trim();
        if (txt_UserEmail.isEmpty()){
            useremail.setError("Email can't be Empty");
            return false;
        }
        else if (!txt_UserEmail.contains("@")){
            useremail.setError("Email should contain @");
            return false;
        }
        else if (!txt_UserEmail.endsWith(".com")){
            useremail.setError("Email should contain .com");
            return false;
        }
        else{
            useremail.setError(null);
            return true;
        }
    }

    private boolean validatePassword(){
        String txt_UserPassword=userpassword.getEditText().getText().toString().trim();
        String txt_UserCPassword=usercpassword.getEditText().getText().toString().trim();
        if (txt_UserPassword.length()<6){
            userpassword.setError("Password should be more than 6 characters");
            return false;
        }
        else if(!txt_UserPassword.equals(txt_UserCPassword)){
            usercpassword.setError("Passwords don't match");
            userpassword.setError("Passwords don't match");
            return false;
        }
        else{
            userpassword.setError(null);
            usercpassword.setError(null);
            return true;
        }
    }

    public void moveToLoginUser(View view) {
        startActivity(new Intent(RegisterUser.this, LoginUser.class));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}