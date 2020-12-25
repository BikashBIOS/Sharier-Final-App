package com.example.shareer.Client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.shareer.MainActivity;
import com.example.shareer.R;
import com.example.shareer.User.UsersList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginClient extends AppCompatActivity {

    TextInputLayout clientemaillogin, clientpasswordlogin;
    Button loginClient;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_client);

        clientemaillogin=findViewById(R.id.ed_clientemaillogin);
        clientpasswordlogin=findViewById(R.id.ed_clientpasswordlogin);
        loginClient=findViewById(R.id.clientlogin);
        mAuth=FirebaseAuth.getInstance();

        loginClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validatePasswordClientLogin() | !validateClientEmailLogin()){
                    Toast.makeText(LoginClient.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                }
                else{
                    String input_ClientEmailLogin=clientemaillogin.getEditText().getText().toString().trim();
                    String input_ClientPasswordLogin=clientpasswordlogin.getEditText().getText().toString();

                    clientLogin(input_ClientEmailLogin,input_ClientPasswordLogin);
                }
            }
        });
    }

    private void clientLogin(final String clientEmailLogin, final String clientPasswordLogin) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging In...");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(clientEmailLogin, clientPasswordLogin)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginClient.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginClient.this, UsersList.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginClient.this, "Login Failed !!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean validateClientEmailLogin(){
        String txt_ClientEmailLogin=clientemaillogin.getEditText().getText().toString().trim();
        if (txt_ClientEmailLogin.isEmpty()){
            clientemaillogin.setError("Email can't be Empty");
            return false;
        }
        else if (!txt_ClientEmailLogin.contains("@")){
            clientemaillogin.setError("Email should contain @");
            return false;
        }
        else if (!txt_ClientEmailLogin.endsWith(".com")){
            clientemaillogin.setError("Email should contain .com");
            return false;
        }
        else{
            clientemaillogin.setError(null);
            return true;
        }
    }

    private boolean validatePasswordClientLogin(){
        String txt_ClientPasswordLogin=clientpasswordlogin.getEditText().getText().toString().trim();

        if (txt_ClientPasswordLogin.length()<6){
            clientpasswordlogin.setError("Password should be more than 6 characters");
            return false;
        }
        else{
            clientpasswordlogin.setError(null);
            return true;
        }
    }

    public void moveToRegisterClient(View view) {
        startActivity(new Intent(LoginClient.this, RegisterClient.class));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}