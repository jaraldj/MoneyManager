package com.example.moneymanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPassword;
    private Button btnLogin;
    private TextView forgotPass;
    private TextView signup;
    private ProgressDialog mDialog;

    //Firebade....
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null)
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        activity();
    }

    private void activity() {
        mEmail = findViewById(R.id.login_email);
        mPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btn_login);
        forgotPass = findViewById(R.id.forgot_password);
        signup = findViewById(R.id.sign_up);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email Required...");
                }

                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password Required....");
                };

                mDialog.setMessage("Processing...");
                mDialog.show();

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mDialog.dismiss();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            Toast.makeText(getApplicationContext(), "Login Completed", Toast.LENGTH_SHORT).show();

                        } else {
                            mDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ResetActivity.class));
            }
        });
    }


}
