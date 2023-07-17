package com.example.music_player;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.utilities.Utilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MusicPlayActivity extends AppCompatActivity {

    //=============== Object Properties ===============//
    TextView user_emotion, song_title, duration_time, remaining_time;
    ImageButton play, pause, next, previous;
    SeekBar duration_bar;
    ImageView album;

    String Emotion;

    //=============== Media player properties ===============//
    MediaPlayer mediaPlayer = new MediaPlayer();
    final Handler handler = new Handler();
    Runnable runnable;

    //=============== Database properties ===============//
    private DatabaseReference mRef;
    private Utilities utils;

    //=============== initialize the music playlist ===============//
    ArrayList<Music> musicList = new ArrayList<>();

    //=============== initialize the nowPlayingMusic ===============//
    int nowPlaying = 0;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);

        play = findViewById(R.id.playButton);
        pause = findViewById(R.id.pauseButton);
        next = findViewById(R.id.nextButton);
        previous = findViewById(R.id.previousButton);
        duration_bar = findViewById(R.id.durationBar);
        duration_time = findViewById(R.id.durationTime);
        remaining_time = findViewById(R.id.remainingTime);
        user_emotion = findViewById(R.id.userEmotion);
        song_title = findViewById(R.id.songTitle);

        FirebaseApp.initializeApp(this);

        /* >>>>>>>>>> load the music playlist <<<<<<<<<< */
        musicList.add(new Music(getRawUri(R.raw.angrysong1), "Billy Elish - Bad Guy",1));
        musicList.add(new Music(getRawUri(R.raw.angrysong2), "Doja Cat - Boss Bitch",2));
        musicList.add(new Music(getRawUri(R.raw.angrysong3), "Doja Cat - Say So ft. Nicki Minaj",3));
        musicList.add(new Music(getRawUri(R.raw.angrysong4), "Justin Bieber - Intentions ft. Quavo",4));
        musicList.add(new Music(getRawUri(R.raw.angrysong5), "Lady Gaga, Ariana Grande - Rain On Me",5));

        musicList.add(new Music(getRawUri(R.raw.happysong1), "Calum Scott - Dancing On My Own",6));
        musicList.add(new Music(getRawUri(R.raw.happysong2), "Joji - Slow Dancing In The Dark",7));
        musicList.add(new Music(getRawUri(R.raw.happysong3), "Joji - Yeah Right",8));
        musicList.add(new Music(getRawUri(R.raw.happysong4), "Kina - Get You The Moon ft. Snow",9));
        musicList.add(new Music(getRawUri(R.raw.happysong4), "Selena Gomez - Lose You To Love Me",10));

        musicList.add(new Music(getRawUri(R.raw.neutralsong1), "Lewis Capaldi - Someone You Loved",11));
        musicList.add(new Music(getRawUri(R.raw.neutralsong2), "Melanie Martinez - Play Date",12));
        musicList.add(new Music(getRawUri(R.raw.neutralsong3), "Post Malone, Swae Lee - Sunflower",13));
        musicList.add(new Music(getRawUri(R.raw.neutralsong4), "Sam Fischer - This City feat. Anne-Marie",14));
        musicList.add(new Music(getRawUri(R.raw.neutralsong5), "Tones and I - Dance Monkey",15));

        musicList.add(new Music(getRawUri(R.raw.sadsong1), "Arizona Zervas - Roxanne",16));
        musicList.add(new Music(getRawUri(R.raw.sadsong2), "Don Toliver - No Idea",17));
        musicList.add(new Music(getRawUri(R.raw.sadsong3), "Dua Lipa - Break My Heart",18));
        musicList.add(new Music(getRawUri(R.raw.sadsong4), "Future - Life Is Good ft. Drake",19));
        musicList.add(new Music(getRawUri(R.raw.sadsong5), "Marshmello & Halsey - Be Kind",20));

        /* Initialize the audio player */
        init();

        /* ==================== load the first music ==================== */
        try {
            loadMusic(musicList.get(nowPlaying));
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* ==================== When Play Button is clicked ==================== */
        play.setOnClickListener(view -> playMusic());

        /* ==================== When Pause Button is clicked ==================== */
        pause.setOnClickListener(view -> pauseMusic());


        /* ==================== When Next Button is clicked ==================== */
        next.setOnClickListener(view -> {
            /* Get the next music in playlist */
            try {
                goToNextMusic();
            } catch (IndexOutOfBoundsException e) {
                showToast("This is the last music.");
            }
        });


        /* ==================== When Previous Button is clicked ==================== */
        previous.setOnClickListener(view -> {
            /* Get the previous music in playlist */
            try {
                goToPreviousMusic();
            } catch (IndexOutOfBoundsException e) {
                showToast("This is the first music.");
            }
        });


        /* ==================== When Seek Bar is scrolled ==================== */
        duration_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    /* When drag on the seek bar, set progress to the seek bar */
                    mediaPlayer.seekTo(i);
                }

                /* Update the current position on display */
                duration_time.setText(seekBarTimeFormat(mediaPlayer.getCurrentPosition()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        /**==================================================================================================**/
        /* >>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Load form Firebase <<<<<<<<<<<<<<<<<<<<<<<<<<<<<< */
        mRef = FirebaseDatabase.getInstance().getReference().child("facial");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Emotion = Objects.requireNonNull(dataSnapshot.child("emotion").getValue()).toString();
                Emotion = dataSnapshot.child("emotion").getValue(String.class);
                Log.d(TAG, "Value is: " + Emotion);
                user_emotion.setText(Emotion);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read Emotion.", error.toException());
            }
        });

        /**==================================================================================================**/
    }


    /**
     * Initialize the audio player.
     */
    private void init() {
        /* Initialize media player */
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build());

        /* Set autoplay to next music after the music is finished. */
        mediaPlayer.setOnCompletionListener(mediaPlayer -> {
            try {
                goToNextMusic();
            } catch (IndexOutOfBoundsException e) {
                /* Hide the Pause Button & show the Play Button  */
//                showPlayButton();

                /* Reset media player position */
                mediaPlayer.seekTo(0);
            }
        });
    }

    /**
     * Load a music to audio player.
     *
     * @param music The music.
     */
    private void loadMusic(com.example.music_player.Music music) throws IOException {
        /* Reset the media player to idle. */
        mediaPlayer.reset();

        /* Set the music to media player */
        mediaPlayer.setDataSource(getApplicationContext(), music.getUri());

        /* Preparing the media player */
        mediaPlayer.prepare();

        /* Initialize runnable */
        runnable = new Runnable() {
            @Override
            public void run() {
                /* Set progress on seek bar */
                duration_bar.setProgress(mediaPlayer.getCurrentPosition());

                /* Handler post delay for 0.5s */
                handler.postDelayed(this, 500);
            }
        };

        /* Set seek bar max */
        duration_bar.setMax(mediaPlayer.getDuration());

        /* Get duration of media player, convert it to Seek Bar time format, then displaying it. */
        remaining_time.setText(seekBarTimeFormat(mediaPlayer.getDuration()));

        /* Update the music name */
        song_title.setText(music.getTitle());
    }


    /** ==================== Play Music ==================== **/
    private void playMusic() {
        /* Hide the Play Button & show the Pause Button */
        showPauseButton();

        /* Start the media player */
        mediaPlayer.start();

        /* Start handler */
        handler.postDelayed(runnable, 0);
    }

    /** ==================== Pause Music ==================== **/
    private void pauseMusic() {
        /* Hide the Pause Button & show the Play Button */
        showPlayButton();

        /* Pause the media player */
        mediaPlayer.pause();

        /* Stop handler */
        handler.removeCallbacks(runnable);
    }

    /** ==================== Display the Play Button and hide the Pause Button ==================== **/
    private void showPlayButton() {
        play.setVisibility(View.VISIBLE);
        pause.setVisibility(View.GONE);
    }

    /** ==================== Display the Pause Button and hide the Play Button ==================== **/
    private void showPauseButton() {
        pause.setVisibility(View.VISIBLE);
        play.setVisibility(View.GONE);
    }

    /** ==================== Stop the audio player ==================== **/
    private void stopMusic() {
        /* Hide the Pause Button & show the Play Button */
        showPlayButton();

        /* Stop the media player */
        mediaPlayer.stop();

        /* Stop handler */
        handler.removeCallbacks(runnable);
    }

    /** ==================== Play the previous music ==================== **/
    private void goToPreviousMusic() {
        if (nowPlaying == 0) {
            throw new IndexOutOfBoundsException();
        }

        /* Stop the media player & handler */
        stopMusic();

        /* Preparing the new music */
        nowPlaying -= 1;

        try {
            loadMusic(musicList.get(nowPlaying));
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* Playing the new music */
        playMusic();
    }

    /** ==================== Play the next music ==================== */
    private void goToNextMusic() {
        if (nowPlaying == musicList.size() - 1) {
            throw new IndexOutOfBoundsException();
        }

        /* Stop the media player & handler */
        stopMusic();

        /* Preparing the new music */
        nowPlaying += 1;

        try {
            loadMusic(musicList.get(nowPlaying));
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* Playing the new music */
        playMusic();
    }

    /** ==================== Convert time in milliseconds to seek bar format (mm:ss) ==================== */
    @SuppressLint("DefaultLocale")
    private String seekBarTimeFormat(int durationInMs) {
        long minutesDuration = TimeUnit.MILLISECONDS.toMinutes(durationInMs);
        long secondsDuration = TimeUnit.MILLISECONDS.toSeconds(durationInMs);

        return String.format("%02d:%02d",
                minutesDuration,
                secondsDuration - TimeUnit.MINUTES.toSeconds(minutesDuration));
    }

    /** ==================== Display a message with toast ==================== **/
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
                .show();
    }

    private Uri getRawUri(int rawId) {
        return Uri.parse("android.resource://" + getPackageName() + "/" + rawId);
    }

    @Override
    protected void onDestroy() {
        /* Stop the media player */
//        stopMusic();

        /* Destroy the media player. */
        mediaPlayer.release();

        /* Destroy the activity */
        super.onDestroy();
    }

    public void pauseButtonClick(View view) {
    }

    public void playButtonClick(View view) {
    }
}