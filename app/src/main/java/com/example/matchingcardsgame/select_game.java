package com.example.matchingcardsgame;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class select_game extends AppCompatActivity {

    private TextView title;
    private ImageView record, menuBack, menuSettings;
    private Button playButton;
    private ViewPager2 viewPager;
    private MediaPlayer mediaPlayer;
    private int currentSongResId;
    private SettingsDialog settingsDialog;

    private List<Integer> albumImages = Arrays.asList(
            R.drawable.album1,
            R.drawable.album2,
            R.drawable.album3,
            R.drawable.album4,
            R.drawable.album5
    );

    private List<Integer> backgroundColors = Arrays.asList(
            Color.parseColor("#E7E7E7"),
            Color.parseColor("#7A7070"),
            Color.parseColor("#989074"),
            Color.parseColor("#6786B8"),
            Color.parseColor("#D2CFBD")
    );

    private List<Integer> buttonColors = Arrays.asList(
            Color.parseColor("#5A7696"),
            Color.parseColor("#8D0000"),
            Color.parseColor("#020202"),
            Color.parseColor("#FD8DC8"),
            Color.parseColor("#873132")
    );

    private List<Integer> songResIds = Arrays.asList(
            R.raw.song2,
            R.raw.song3,
            R.raw.song4,
            R.raw.song5,
            R.raw.song6
    );

    private List<Integer> extendedAlbumImages;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_game);

        settingsDialog = new SettingsDialog(this, this::updateVolume);

        mediaPlayer = MediaPlayer.create(this, R.raw.song2);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        record = findViewById(R.id.record);
        title = findViewById(R.id.menuTitle);
        playButton = findViewById(R.id.playButton);
        menuBack = findViewById(R.id.menuBack);
        menuSettings = findViewById(R.id.menuSettings);
        viewPager = findViewById(R.id.viewPager);

        setupInfiniteScrollData();

        AlbumAdapter adapter = new AlbumAdapter(extendedAlbumImages);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1, false);

        ObjectAnimator rotation = ObjectAnimator.ofFloat(record, "rotation", 0f, 360f);
        rotation.setDuration(2000);
        rotation.setRepeatCount(ObjectAnimator.INFINITE);
        rotation.start();

        viewPager.setPageTransformer(new RecordPageTransformer());

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                int realPosition = (position - 1 + albumImages.size()) % albumImages.size();

                findViewById(R.id.main).setBackgroundColor(backgroundColors.get(realPosition));
                playButton.setBackgroundColor(buttonColors.get(realPosition));
                title.setTextColor(buttonColors.get(realPosition));
                menuBack.setColorFilter(buttonColors.get(realPosition));
                menuSettings.setColorFilter(buttonColors.get(realPosition));

                playMusic(realPosition);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);

                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    int position = viewPager.getCurrentItem();

                    if (position == 0) {
                        viewPager.setCurrentItem(extendedAlbumImages.size() - 2, false);
                    } else if (position == extendedAlbumImages.size() - 1) {
                        viewPager.setCurrentItem(1, false);
                    }
                }
            }
        });

        menuBack.setOnClickListener(view -> {
            Intent intent = new Intent(select_game.this, MainActivity.class);
            stopMusic();
            startActivity(intent);
        });

        menuSettings.setOnClickListener(view -> settingsDialog.show());

        playButton.setOnClickListener(v -> {
            Intent intent = new Intent(select_game.this, MainActivity.class);
            stopMusic();
            startActivity(intent);
        });
    }

    private void setupInfiniteScrollData() {
        extendedAlbumImages = new ArrayList<>(albumImages);
        extendedAlbumImages.add(0, albumImages.get(albumImages.size() - 1));
        extendedAlbumImages.add(albumImages.get(0));
    }

    private void playMusic(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        currentSongResId = songResIds.get(position);
        mediaPlayer = MediaPlayer.create(this, currentSongResId);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    private void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void updateVolume(int volume) {
        float volumeLevel = volume / 100f;
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volumeLevel, volumeLevel);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopMusic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMusic();
    }

    private class RecordPageTransformer implements ViewPager2.PageTransformer {
        @Override
        public void transformPage(View page, float position) {
            int pageWidth = page.getWidth();

            if (position < -1 || position > 1) {
                record.setAlpha(0f);
            } else {
                record.setAlpha(1 - Math.abs(position));
                record.setTranslationX(-position * pageWidth / 2);
                float scaleFactor = 1 - Math.abs(position) * 0.2f;
                record.setScaleX(scaleFactor);
                record.setScaleY(scaleFactor);
            }
        }
    }
}