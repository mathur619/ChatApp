package com.example.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private Button LoginButton,PhoneLogin;
    private EditText Email,Password;
    private TextView forgetpassword,NewAccount;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(this);
        firebaseAuth=FirebaseAuth.getInstance();

        //Intitalizing Views
        initializeviews();
        //Setting on click listener to views
        onclicklistener();
    }


    private void onclicklistener() {
        NewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,RegisterActicvity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_slide_in,R.anim.left_slide_in);
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=Email.getText().toString();
                String password=Password.getText().toString();

                if(TextUtils.isEmpty(email) && TextUtils.isEmpty(password))
                {
                    Toast.makeText(LoginActivity.this,getString(R.string.warning),Toast.LENGTH_SHORT).show();
                }
                else
                {
                    progressDialog.setTitle(getString(R.string.signin));
                    progressDialog.setMessage(getString(R.string.pleasewait));
                    progressDialog.setCanceledOnTouchOutside(true);
                    progressDialog.show();

                    firebaseAuth.signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful())
                                    {

                                      SendUserToMainActivity();
                                      Toast.makeText(LoginActivity.this,getString(R.string.Loggedin),Toast.LENGTH_SHORT).show();
                                      progressDialog.dismiss();
                                    }
                                    else
                                    {
                                        String Error=task.getException().getMessage();
                                        Toast.makeText(LoginActivity.this,Error,Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                }
            }
        });
    }
    private void SendUserToMainActivity()
    {
        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void initializeviews() {
        LoginButton=(Button)findViewById(R.id.login_button_view);
        PhoneLogin=(Button)findViewById(R.id.phone_login_button_view);
        Email=(EditText)findViewById(R.id.login_email_view);
        Password=(EditText)findViewById(R.id.login_password_view);
        forgetpassword=(TextView)findViewById(R.id.forget_password_text_view);
        NewAccount=(TextView)findViewById(R.id.Need_New_account_text_view);
        progressDialog=new ProgressDialog(this);
    }
}
