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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText LoginName, LoginPassword;
    Button LoginButton;
    ProgressDialog LoadingBar;
    String UserName, Password;
    String userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoadingBar = new ProgressDialog(this);
        LoginName = findViewById(R.id.loginName);
        LoginPassword = findViewById(R.id.loginPassword);
        LoginButton = findViewById(R.id.loginNowButton);

        LoadingBar.setTitle("Login Account..");
        LoadingBar.setMessage("Please wait..");
        LoadingBar.setCanceledOnTouchOutside(false);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UserName = LoginName.getText().toString();
                Password = LoginPassword.getText().toString();
                LoginAccount (UserName, Password);
            }
        });
    }

    private void LoginAccount(String user_name, String password) {

        //now check EditText is empty or not

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
            LoadingBar.show();

            final DatabaseReference mRef;
            mRef = FirebaseDatabase.getInstance().getReference();

            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.child("Users").child(user_name).exists())
                    {
                        LoadingBar.dismiss();
                        userPassword = snapshot.child("Users").child(user_name).child("password").getValue().toString();

                        //check
                        if(Password.equals(userPassword))
                        {
                            LoadingBar.dismiss();
                            Intent i = new Intent(LoginActivity.this,MusicPlayActivity.class);
                            startActivity(i);
                        }
                        else
                        {
                            LoadingBar.dismiss();
                            //if user exist but password is not correct
                            Toast.makeText(LoginActivity.this, "Please enter valid password..", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        LoadingBar.dismiss();
                        //user not exist
                        Toast.makeText(LoginActivity.this, "Username is not registered..", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}