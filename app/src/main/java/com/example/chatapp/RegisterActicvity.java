package com.example.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class RegisterActicvity extends AppCompatActivity {
    private EditText Email,Password;
    private ProgressDialog progressBar;
    private Button CreateAccount;
    private TextView AlreadyHaveAccount;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_acticvity);
        FirebaseApp.initializeApp(this);
        firebaseAuth=FirebaseAuth.getInstance();

        //Referencing Firebase RealTimeDatabase
        RootRef= FirebaseDatabase.getInstance().getReference();

        //Inititalizing Views
        intitializeviews();

        //setting up listener

        onclicklistener();
    }

    private void sendusertologinActivity()
    {
        Intent intent=new Intent(RegisterActicvity.this,LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_slide_in,R.anim.left_slide_in);
    }

    private void SendUserToSettingActivity()
    {
        Intent intent=new Intent(RegisterActicvity.this,SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void onclicklistener() {
        AlreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               sendusertologinActivity();
            }
        });
        CreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=Email.getText().toString();
                String password=Password.getText().toString();

                if(TextUtils.isEmpty(email) && TextUtils.isEmpty(password))
                {
                    Toast.makeText(RegisterActicvity.this,getString(R.string.warning),Toast.LENGTH_SHORT).show();
                }
                else
                {
                    progressBar.setTitle(getString(R.string.progressbartitle));
                    progressBar.setMessage(getString(R.string.waitmeassage));
                    progressBar.setCanceledOnTouchOutside(true);
                    progressBar.show();


                     firebaseAuth.createUserWithEmailAndPassword(email,password)
                             .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                 @Override
                                 public void onComplete(@NonNull Task<AuthResult> task) {
                                     if(task.isSuccessful())
                                     {
                                         String CurrentUserId=firebaseAuth.getCurrentUser().getUid();
                                         RootRef.child("User").child(CurrentUserId).setValue("");

                                         //sending user to mainactivity
                                         SendUserToSettingActivity();
                                       Toast.makeText(RegisterActicvity.this,getString(R.string.successful_Task),Toast.LENGTH_SHORT).show();
                                       progressBar.dismiss();
                                     }
                                     else
                                     {
                                         String Error=task.getException().getMessage();
                                         Toast.makeText(RegisterActicvity.this,Error,Toast.LENGTH_LONG).show();
                                         progressBar.dismiss();
                                     }
                                 }
                             });
                }
            }
        });
    }

    private void intitializeviews() {
        Email=(EditText)findViewById(R.id.register_email_view);
        Password=(EditText)findViewById(R.id.register_password_view);
        CreateAccount=(Button)findViewById(R.id.register_button_view);
        AlreadyHaveAccount=(TextView)findViewById(R.id.Already_Have_account_text_view);
        progressBar=new ProgressDialog(this);
    }
}
