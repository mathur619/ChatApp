package com.example.chatapp;

import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private Toolbar mtoolbar;
    private TabLayout tablayout;
    private ViewPager viewPager;
    private FirebaseUser currentuser;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference RootRef;
    private static final String TAG=MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing Firebase
        FirebaseApp.initializeApp(this);
        firebaseAuth=FirebaseAuth.getInstance();
        currentuser=firebaseAuth.getCurrentUser();
        RootRef= FirebaseDatabase.getInstance().getReference();


         //Initializing Custom Toolbar
        mtoolbar=(Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));

        //Initializing tab layout
        tablayout=(TabLayout)findViewById(R.id.tablayout);
        viewPager=(ViewPager)findViewById(R.id.viewpager);

        tablayout.addTab(tablayout.newTab().setText(getString(R.string.chat)));
        tablayout.addTab(tablayout.newTab().setText(getString(R.string.status)));
        tablayout.addTab(tablayout.newTab().setText(getString(R.string.contacts)));
        tablayout.addTab(tablayout.newTab().setText("Requests"));
        tablayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final MyAdapter adapter=new MyAdapter(this,getSupportFragmentManager(),tablayout.getTabCount());
        viewPager.setAdapter(adapter);

        //handling tab click events

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tablayout));

        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //Verify User Existence
    }

    private void VerifyExistence() {
        String CurrentUserId=firebaseAuth.getCurrentUser().getUid();
        RootRef.child("User").child(CurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("name").exists())
                {
                    Toast.makeText(MainActivity.this,"Welcome",Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.d(TAG,"Send To Settings Activity");
                    SendUserToSettingsActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);

         getMenuInflater().inflate(R.menu.options_menu,menu);
         return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();

        switch (id)
        {
            case R.id.menu_find_friends:
                Intent Friendsintent=new Intent(MainActivity.this,FriendsActivity.class);
                startActivity(Friendsintent);
                break;
            case R.id.menu_settings_option:
                Intent intent=new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_slide_out,R.anim.slideoutleft);
                break;
            case R.id.menu_logout_option:
                firebaseAuth.signOut();
                Intent LoginActivityIntent=new Intent(MainActivity.this,LoginActivity.class);
                LoginActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(LoginActivityIntent);
                break;
            case R.id.menu_creategroup_option:
                CreateAlertDialog();
             default:
                 return false;
        }

        return super.onOptionsItemSelected(item);
    }

    private void CreateAlertDialog() {
        final EditText group=new EditText(MainActivity.this);
        group.setHint(getString(R.string.dialog_edit_Text));

        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
        builder.setTitle(getString(R.string.title_alertdialog));
        builder.setView(group);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
              String GroupTitle=group.getText().toString();

              if(TextUtils.isEmpty(GroupTitle))
              {
                  Toast.makeText(MainActivity.this,"Please Enter Group Name To Create it",Toast.LENGTH_SHORT).show();
              }
              else {

                  RootRef.child(getString(R.string.firebase_group)).child(GroupTitle).setValue("");
              }

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });
        builder.create().show();

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(currentuser==null)
        {
            Intent LoginActivityIntent=new Intent(MainActivity.this,LoginActivity.class);
            startActivity(LoginActivityIntent);
        }
        else
        {
            VerifyExistence();
        }
    }

    private void SendUserToSettingsActivity()
    {
        Intent intent=new Intent(MainActivity.this,SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }



}
