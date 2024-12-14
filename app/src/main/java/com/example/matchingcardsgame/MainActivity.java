package com.example.matchingcardsgame;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private int[] songs = {R.raw.song1, R.raw.song2, R.raw.song3, R.raw.song4, R.raw.song5, R.raw.song6};
    private int currentSongIndex = 0;
    private SharedPreferences sharedPreferences;
    private SeekBar volumeSeekBar;
    private Spinner languageSpinner;
    private Button btn_save, btn_cancel, btn_settings;
    private VideoView background;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        String language = sharedPreferences.getString("language", "en");
        setLocale(language);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        btn_settings = findViewById(R.id.main_button_settings);
        background = findViewById(R.id.main_background);

        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.main_background);
        background.setVideoURI(videoUri);
        background.setOnPreparedListener(mediaPlayer -> mediaPlayer.setLooping(true));
        background.start();

        setupMediaPlayer();
        setupSettingsDialog();

        btn_settings.setOnClickListener(view -> dialog.show());
    }

    private void setupMediaPlayer() {
        mediaPlayer = MediaPlayer.create(this, songs[currentSongIndex]);
        mediaPlayer.setLooping(false);
        mediaPlayer.setOnCompletionListener(mp -> {
            currentSongIndex = (currentSongIndex + 1) % songs.length;
            mediaPlayer.reset();
            mediaPlayer = MediaPlayer.create(MainActivity.this, songs[currentSongIndex]);
            mediaPlayer.setOnCompletionListener(this::onSongComplete);
            mediaPlayer.start();
        });

        int savedVolume = sharedPreferences.getInt("volume", 50);
        setMediaPlayerVolume(savedVolume);
        mediaPlayer.start();
    }

    private void onSongComplete(MediaPlayer mp) {
        currentSongIndex = (currentSongIndex + 1) % songs.length;
        mediaPlayer.reset();
        mediaPlayer = MediaPlayer.create(this, songs[currentSongIndex]);
        mediaPlayer.setOnCompletionListener(this::onSongComplete);
        mediaPlayer.start();
    }

    private void setMediaPlayerVolume(int volume) {
        float volumeLevel = volume / 100f;
        mediaPlayer.setVolume(volumeLevel, volumeLevel);
    }

    private void setupSettingsDialog() {
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.custom_setting_box);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.settings_bg));

        btn_save = dialog.findViewById(R.id.save_button);
        btn_cancel = dialog.findViewById(R.id.cancel_button);
        volumeSeekBar = dialog.findViewById(R.id.volumeSeekBar);
        languageSpinner = dialog.findViewById(R.id.languageSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        String savedLanguage = sharedPreferences.getString("language", "en");
        if (savedLanguage.equals("pl")) {
            languageSpinner.setSelection(1);
        } else {
            languageSpinner.setSelection(0);
        }

        int savedVolume = sharedPreferences.getInt("volume", 50);
        volumeSeekBar.setProgress(savedVolume);

        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setMediaPlayerVolume(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btn_save.setOnClickListener(view -> {
            int selectedPosition = languageSpinner.getSelectedItemPosition();
            String selectedLanguage = selectedPosition == 1 ? "pl" : "en";

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("language", selectedLanguage);

            int volume = volumeSeekBar.getProgress();
            editor.putInt("volume", volume);
            editor.apply();

            setLocale(selectedLanguage);
            recreate();
            dialog.dismiss();
        });

        btn_cancel.setOnClickListener(view -> dialog.dismiss());
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        config.setLocale(locale);
        resources.updateConfiguration(config, dm);
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        super.onDestroy();
    }
}