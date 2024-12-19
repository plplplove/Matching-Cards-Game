package com.example.matchingcardsgame;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private SharedPreferences sharedPreferences;
    private SeekBar volumeSeekBar;
    private Spinner languageSpinner;
    private Button btn_save, btn_cancel, btn_settings, btn_start;
    private TextView settingsTitle, settingsMusic, settingsLanguage;
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

        btn_start = findViewById(R.id.main_button_start);
        btn_settings = findViewById(R.id.main_button_settings);
        background = findViewById(R.id.main_background);

        // Фонове відео
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.main_background);
        background.setVideoURI(videoUri);
        background.setOnPreparedListener(mp -> mp.setLooping(true));
        background.start();

        setupMediaPlayer();

        setupSettingsDialog();

        btn_start.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SelectGameMenu.class);
            startActivity(intent);
            stopMainMusic(); // Зупиняємо song1
        });

        btn_settings.setOnClickListener(view -> dialog.show());
    }

    private void setupMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.song1);
            mediaPlayer.setLooping(true);
            int savedVolume = sharedPreferences.getInt("volume", 50);
            setMediaPlayerVolume(savedVolume);
        }
        mediaPlayer.start();
    }

    private void setMediaPlayerVolume(int volume) {
        float volumeLevel = volume / 100f;
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volumeLevel, volumeLevel);
        }
    }

    private void stopMainMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    private void resumeMainMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
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
        settingsTitle = dialog.findViewById(R.id.settingsTitle);
        settingsMusic = dialog.findViewById(R.id.settingsMusic);
        settingsLanguage = dialog.findViewById(R.id.settingsLanguage);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        String savedLanguage = sharedPreferences.getString("language", "en");
        languageSpinner.setSelection(savedLanguage.equals("pl") ? 1 : 0);

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
            editor.putInt("volume", volumeSeekBar.getProgress());
            editor.apply();

            setLocale(selectedLanguage);
            updateTextResources();
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

    private void updateTextResources() {
        btn_settings.setText(getString(R.string.main_button_settings));
        btn_start.setText(getString(R.string.main_button_start));
        settingsTitle.setText(getString(R.string.settings_title));
        settingsMusic.setText(getString(R.string.settings_music));
        settingsLanguage.setText(getString(R.string.settings_language));
        btn_save.setText(getString(R.string.settings_save));
        btn_cancel.setText(getString(R.string.settings_cancel));
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeMainMusic();
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