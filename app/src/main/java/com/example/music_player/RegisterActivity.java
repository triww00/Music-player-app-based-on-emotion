package com.example.music_player;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText register_name, register_password;
    Button regButton;
    ProgressDialog LoadingBar;
    String UserName, Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register_name = findViewById(R.id.registerName);
        register_password = findViewById(R.id.registerPassword);

        regButton = findViewById(R.id.registerNowButton);

        LoadingBar = new ProgressDialog(this);

        LoadingBar.setTitle("Creating Account..");
        LoadingBar.setMessage("Please wait..");
        LoadingBar.setCanceledOnTouchOutside(false);

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserName = register_name.getText().toString();
                Password = register_password.getText().toString();

                CreateNewAccount(UserName,Password);
            }
        });
    }

    private void CreateNewAccount(String user_name, String password) {

        //now check it is empty or not
        if(TextUtils.isEmpty(user_name))
        {
            Toast.makeText(this, "Please enter your name..", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please enter password..", Toast.LENGTH_SHORT).show();
        }
        else
        {
            //start createing account
            LoadingBar.show();

            final DatabaseReference mRef;
            mRef = FirebaseDatabase.getInstance().getReference();

            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.child("Users").child(user_name).exists())
                    {
                        //if user exist
                        LoadingBar.dismiss();
                        Toast.makeText(RegisterActivity.this, "Username already exist!", Toast.LENGTH_SHORT).show();
                    }
                    else if(snapshot.child("Users").child(user_name).child("phone").exists())
                    {
                        //if number phone exist
                        LoadingBar.dismiss();
                        Toast.makeText(RegisterActivity.this, "Number phone already registered!", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        //if user not exist, create account in database
                        HashMap<String,Object> userdata = new HashMap<>();
                        userdata.put("name",user_name);
                        userdata.put("password",password);

                        mRef.child("Users").child(user_name).updateChildren(userdata)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful())
                                        {
                                            LoadingBar.dismiss();
                                            Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(RegisterActivity.this,HomeActivity.class);
                                            startActivity(i);
                                        }
                                        else
                                        {
                                            LoadingBar.dismiss();
                                            Toast.makeText(RegisterActivity.this, "Please try again later", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}