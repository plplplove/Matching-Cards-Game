package com.example.matchingcardsgame;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;

public class SettingsDialog {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Dialog dialog;
    private SeekBar volumeSeekBar;
    private Spinner languageSpinner;
    private Button btnSave, btnCancel;

    private OnVolumeChangeListener volumeChangeListener;

    public SettingsDialog(Context context, OnVolumeChangeListener listener) {
        this.volumeChangeListener = listener;
        sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_setting_box);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(context.getDrawable(R.drawable.settings_bg));

        volumeSeekBar = dialog.findViewById(R.id.volumeSeekBar);
        languageSpinner = dialog.findViewById(R.id.languageSpinner);
        btnSave = dialog.findViewById(R.id.save_button);
        btnCancel = dialog.findViewById(R.id.cancel_button);

        loadSettings();

        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                editor.putInt("volume", progress);
                editor.apply();
                if (volumeChangeListener != null) {
                    volumeChangeListener.onVolumeChanged(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnSave.setOnClickListener(v -> saveSettings(context));

        btnCancel.setOnClickListener(v -> dialog.dismiss());
    }

    private void loadSettings() {
        int volume = sharedPreferences.getInt("volume", 50);
        volumeSeekBar.setProgress(volume);

        String language = sharedPreferences.getString("language", "en");
        languageSpinner.setSelection(language.equals("pl") ? 1 : 0);
    }

    private void saveSettings(Context context) {
        String selectedLanguage = languageSpinner.getSelectedItemPosition() == 1 ? "pl" : "en";
        editor.putString("language", selectedLanguage);
        editor.apply();

        if (context instanceof BaseActivity) {
            ((BaseActivity) context).setLocale(selectedLanguage);
        }

        dialog.dismiss();
    }

    public void show() {
        dialog.show();
    }

    public interface OnVolumeChangeListener {
        void onVolumeChanged(int volume);
    }
}