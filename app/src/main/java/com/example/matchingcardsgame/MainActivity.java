package com.example.matchingcardsgame;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.VideoView;

public class MainActivity extends BaseActivity {

    private MediaPlayer mediaPlayer;
    private SettingsDialog settingsDialog;
    private Button btnStart, btnSettings;
    private VideoView background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settingsDialog = new SettingsDialog(this, this::updateVolume);

        btnStart = findViewById(R.id.main_button_start);
        btnSettings = findViewById(R.id.main_button_settings);
        background = findViewById(R.id.main_background);

        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.main_background);
        background.setVideoURI(videoUri);
        background.setOnPreparedListener(mp -> mp.setLooping(true));
        background.start();

        setupMediaPlayer();

        btnStart.setOnClickListener(v -> {
            stopMainMusic();
            startActivity(new android.content.Intent(MainActivity.this, select_game.class));
        });

        btnSettings.setOnClickListener(v -> settingsDialog.show());
    }

    private void setupMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.song1);
            mediaPlayer.setLooping(true);
            updateVolume(getSavedVolume());
            mediaPlayer.start();
        }
    }

    private void stopMainMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    private void updateVolume(int volume) {
        float volumeLevel = volume / 100f;
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volumeLevel, volumeLevel);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null) mediaPlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopMainMusic();
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}