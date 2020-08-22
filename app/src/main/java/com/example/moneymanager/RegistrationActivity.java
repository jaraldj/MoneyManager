package com.example.moneymanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class RegistrationActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPassword;
    private Button btnReg;
    private TextView signup;

    private ProgressDialog mDialog;

    //Firebase...

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth=FirebaseAuth.getInstance();

        mDialog=new ProgressDialog(this);

       registration();
    }

    private void registration(){
        mEmail=findViewById(R.id.register_email);
        mPassword=findViewById(R.id.signup_password);
        btnReg=findViewById(R.id.btn_register);
        signup=findViewById(R.id.already_have_account);

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email=mEmail.getText().toString().trim();
                String password=mPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email Required....");
                }

                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password Required");
                }

                mDialog.setMessage("Processing..");
                mDialog.show();

                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            mDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Registration Completed",Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                        } else {
                            mDialog.dismiss();

                            Toast.makeText(getApplicationContext(),"Registration Failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
    }
}
