package com.example.shareer.Client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.shareer.R;
import com.example.shareer.User.LoginUser;
import com.example.shareer.User.RegisterUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.regex.Pattern;

public class RegisterClient extends AppCompatActivity {

    private FirebaseAuth mAuth;
    DatabaseReference reference;
    Button registerClient;
    TextInputLayout clientname, clientemail, clientpassword, clientcpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_client);

        mAuth = FirebaseAuth.getInstance();
        registerClient = findViewById(R.id.clientregister);
        clientname = findViewById(R.id.ed_clientname);
        clientemail = findViewById(R.id.ed_clientemail);
        clientpassword = findViewById(R.id.ed_clientpassword);
        clientcpassword = findViewById(R.id.ed_clientcpassword);

        registerClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateClientName() | !validateClientEmail() | !validatePasswordClient()) {
                    Toast.makeText(RegisterClient.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                } else {
                    String input_ClientName = clientname.getEditText().getText().toString();
                    String input_ClientEmail = clientemail.getEditText().getText().toString().trim();
                    String input_ClientPassword = clientpassword.getEditText().getText().toString();

                    clientRegister(input_ClientName, input_ClientEmail, input_ClientPassword);
                }
            }
        });
    }

    private void clientRegister(final String clientName, final String clientEmail, final String clientPassword) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering...");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(clientEmail, clientPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            assert firebaseUser != null;
                            String userid = firebaseUser.getUid();
                            String typeClient = "Client";

                            reference = FirebaseDatabase.getInstance().getReference("Clients").child(userid);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("Name", clientName);
                            hashMap.put("Email", clientEmail);
                            hashMap.put("Password", clientPassword);
                            hashMap.put("Type", typeClient);

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterClient.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegisterClient.this, LoginClient.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterClient.this, "Registration Failed!!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean validateClientName() {
        String txt_clientname = clientname.getEditText().getText().toString().trim();
        if (txt_clientname.isEmpty()) {
            clientname.setError("Name can't be Empty");
            return false;
        } else if (txt_clientname.length() > 16) {
            clientname.setError("User Name too Long");
            return false;
        } else {
            clientname.setError(null);
            return true;
        }
    }

    private boolean validateClientEmail() {
        String txt_ClientEmail = clientemail.getEditText().getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(txt_ClientEmail).matches()) {
            clientemail.setError("Invalid Email");
            clientemail.setFocusable(true);
            return false;
        } else {
            clientemail.setError(null);
            return true;
        }
    }

    private boolean validatePasswordClient(){
        String txt_ClientPassword=clientpassword.getEditText().getText().toString().trim();
        String txt_ClientCPassword=clientcpassword.getEditText().getText().toString().trim();
        if (txt_ClientPassword.length()<6){
            clientpassword.setError("Password should be more than 6 characters");
            return false;
        }
        else if(!txt_ClientPassword.equals(txt_ClientCPassword)){
            clientcpassword.setError("Passwords don't match");
            clientpassword.setError("Passwords don't match");
            return false;
        }
        else{
            clientpassword.setError(null);
            clientcpassword.setError(null);
            return true;
        }
    }

    public void moveToLoginClient(View view) {
        startActivity(new Intent(RegisterClient.this, LoginClient.class));
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RegisterClient.this, LoginClient.class));
    }
}