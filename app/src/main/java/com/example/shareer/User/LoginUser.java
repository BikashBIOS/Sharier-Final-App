package com.example.shareer.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.shareer.AppIntro.AppIntroActivity;
import com.example.shareer.HomePage;
import com.example.shareer.Layout.MainPage;
import com.example.shareer.MainActivity;
import com.example.shareer.MultipleImagesPackage.FolderList;
import com.example.shareer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginUser extends AppCompatActivity {

    TextInputLayout useremaillogin, userpasswordlogin;
    Button loginUser;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onStart() {
        super.onStart();

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null){
            Intent intent=new Intent(LoginUser.this,FolderList.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);

        useremaillogin=findViewById(R.id.ed_useremaillogin);
        userpasswordlogin=findViewById(R.id.ed_userpasswordlogin);
        loginUser=findViewById(R.id.userlogin);
        mAuth=FirebaseAuth.getInstance();

        loginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validatePasswordLogin() | !validateUserEmailLogin()){
                    Toast.makeText(LoginUser.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                }
                else{
                    String input_UserEmailLogin=useremaillogin.getEditText().getText().toString().trim();
                    String input_UserPasswordLogin=userpasswordlogin.getEditText().getText().toString();

                    userLogin(input_UserEmailLogin,input_UserPasswordLogin);
                }
            }
        });
    }

    private void userLogin(final String userEmailLogin, final String userPasswordLogin) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging In...");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(userEmailLogin, userPasswordLogin)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginUser.this, "Login Successfull", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginUser.this, AppIntroActivity.class);//change here
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginUser.this, "Login Failed !!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean validateUserEmailLogin(){
        String txt_UserEmailLogin=useremaillogin.getEditText().getText().toString().trim();
        if (txt_UserEmailLogin.isEmpty()){
            useremaillogin.setError("Email can't be Empty");
            return false;
        }
        else if (!txt_UserEmailLogin.contains("@")){
            useremaillogin.setError("Email should contain @");
            return false;
        }
        else if (!txt_UserEmailLogin.endsWith(".com")){
            useremaillogin.setError("Email should contain .com");
            return false;
        }
        else{
            useremaillogin.setError(null);
            return true;
        }
    }

    private boolean validatePasswordLogin(){
        String txt_UserPasswordLogin=userpasswordlogin.getEditText().getText().toString().trim();
        if (txt_UserPasswordLogin.length()<6){
            userpasswordlogin.setError("Password should be more than 6 characters");
            return false;
        }
        else{
            userpasswordlogin.setError(null);
            return true;
        }
    }

    public void moveToRegisterUser(View view) {
        startActivity(new Intent(LoginUser.this, RegisterUser.class));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}