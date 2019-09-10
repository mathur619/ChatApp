package com.example.chatapp;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NavUtils;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    private EditText username,userstatus;
    private ImageView profile_image;
    private Button SavePreference;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference Rootref;
    private static final int GalleryPicker=123;
    private String downloadurl;
    private StorageReference mstorageref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");
        //Initializing Firebase

        firebaseAuth=FirebaseAuth.getInstance();
        Rootref=FirebaseDatabase.getInstance().getReference();
        mstorageref= FirebaseStorage.getInstance().getReference().child("Profileimage");

        //Initializing views
        Initializeviews();

        //setonclicklistener

        onclicklistener();

        SetOldData();

    }

    private void SetOldData() {
        String CurrentUserId=firebaseAuth.getCurrentUser().getUid();
         Rootref.child("User").child(CurrentUserId).addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if(dataSnapshot.exists() && dataSnapshot.child("name").exists() && dataSnapshot.child("image").exists())
                 {
                    username.setText(dataSnapshot.child("name").getValue().toString());
                    userstatus.setText(dataSnapshot.child("status").getValue().toString());
                    downloadurl=dataSnapshot.child("image").getValue().toString();
                     Picasso.get().load(downloadurl).fit().into(profile_image);
                 }
                 else if(dataSnapshot.exists() && dataSnapshot.child("name").exists())
                 {
                     username.setText(dataSnapshot.child("name").getValue().toString());
                     userstatus.setText(dataSnapshot.child("status").getValue().toString());
                 }
                 else
                 {
                   Toast.makeText(SettingsActivity.this,getString(R.string.settings_update_request),Toast.LENGTH_LONG).show();
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });
    }

    private void onclicklistener() {
        SavePreference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name=username.getText().toString();
                String Status=userstatus.getText().toString();
                String CurrentUserId=firebaseAuth.getCurrentUser().getUid();

                if(TextUtils.isEmpty(Name) && TextUtils.isEmpty(Status) && downloadurl!=null)
                {
                    Toast.makeText(SettingsActivity.this,getString(R.string.warning),Toast.LENGTH_SHORT).show();
                }
                else
                {
                    HashMap<String,String>Data=new HashMap<>();
                    Data.put("name",Name);
                    Data.put("status",Status);
                    Data.put("uid",CurrentUserId);
                    Data.put("image",downloadurl);

                    Rootref.child("User").child(CurrentUserId).setValue(Data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                SendUserToMainActivity();
                                Toast.makeText(SettingsActivity.this,"Profile Updated",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                String Error=task.getException().getMessage();
                                Toast.makeText(SettingsActivity.this,Error,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,GalleryPicker);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GalleryPicker && resultCode==RESULT_OK  && data!=null)
        {
           Uri Imageuri=data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK)
            {
                Uri resultUri=result.getUri();

                final StorageReference Filepath=mstorageref.child(firebaseAuth.getCurrentUser().getUid() + ".jpeg");
                Filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                downloadurl=uri.toString();
                                Picasso.get().load(downloadurl).fit().into(profile_image);
                            }
                        });
                    }
                });
            }
        }
    }

    private void SendUserToMainActivity()
    {
        Intent intent=new Intent(SettingsActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void Initializeviews() {
        username=(EditText)findViewById(R.id.set_user_name);
        userstatus=(EditText)findViewById(R.id.set_user_status);
        SavePreference=(Button)findViewById(R.id.save_preference);
        profile_image=(ImageView)findViewById(R.id.set_profile_image);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==android.R.id.home)
        {
            NavUtils.navigateUpFromSameTask(this);
            overridePendingTransition(R.anim.right_slide_in,R.anim.left_slide_in);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
