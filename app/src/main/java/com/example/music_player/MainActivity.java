package com.example.music_player;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    LinearLayout L1, L2;
    Animation DowntoTop, Fade;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_open);

        L1 = findViewById(R.id.l1);
        L2 = findViewById(R.id.l2);

        final Intent i = new Intent(MainActivity.this,HomeActivity.class);

        Thread thread = new Thread()
        {
            @Override
            public void run() {
                try {
                    sleep(5000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                finally {
                    startActivity(i);
                    finish();
                }
            }
        }; thread.start();

    }
}