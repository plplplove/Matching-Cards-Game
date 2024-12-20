package com.example.matchingcardsgame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;

public class BlurryfaceGame extends AppCompatActivity {

    private TextView timerText;
    private ImageView menuButton, homeButton, pauseButton;
    private GridLayout cardGrid;
    private CountDownTimer gameTimer;

    private int[] images = {
            R.drawable.blurryface_image1, R.drawable.blurryface_image2, R.drawable.blurryface_image3,
            R.drawable.blurryface_image4, R.drawable.blurryface_image5, R.drawable.blurryface_image6
    };

    private ArrayList<Integer> cards;
    private ArrayList<ImageView> matchedCards = new ArrayList<>();
    private ImageView firstCard = null;
    private ImageView secondCard = null;
    private boolean isClickable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blurryface_game);

        timerText = findViewById(R.id.timerText);
        cardGrid = findViewById(R.id.cardGrid);
        menuButton = findViewById(R.id.menuButton);
        homeButton = findViewById(R.id.homeButton);
        pauseButton = findViewById(R.id.pauseButton);

        setupCards();
        startTimer();

        menuButton.setOnClickListener(v -> onMenuClick());
        homeButton.setOnClickListener(v -> onHomeClick());
        pauseButton.setOnClickListener(v -> onPauseClick());
    }

    private void setupCards() {
        cards = new ArrayList<>();
        for (int img : images) {
            cards.add(img);
            cards.add(img);
        }
        Collections.shuffle(cards);

        for (int i = 0; i < cards.size(); i++) {
            ImageView card = new ImageView(this);
            card.setImageResource(R.drawable.blurryface_mainimage);
            card.setTag(cards.get(i));
            card.setOnClickListener(this::onCardClick);

            card.setClipToOutline(true);
            card.setBackgroundResource(R.drawable.rounded_card);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 300;
            params.height = 300;
            params.setMargins(10, 10, 10, 10);

            card.setLayoutParams(params);
            cardGrid.addView(card);
        }
    }

    private void onCardClick(View view) {
        if (!isClickable) return;

        ImageView card = (ImageView) view;

        if (matchedCards.contains(card)) return;

        if (card == firstCard) return;

        flipCard(card, true);

        if (firstCard == null) {
            firstCard = card;
        } else if (secondCard == null) {
            secondCard = card;

            if (firstCard.getTag().equals(secondCard.getTag())) {
                matchedCards.add(firstCard);
                matchedCards.add(secondCard);
                firstCard = null;
                secondCard = null;

                if (matchedCards.size() == cards.size()) {
                    gameTimer.cancel();
                    showGameWonDialog();
                }
            } else {
                isClickable = false;
                cardGrid.postDelayed(() -> {
                    shakeCard(firstCard);
                    shakeCard(secondCard);
                    flipCard(firstCard, false);
                    flipCard(secondCard, false);
                    firstCard = null;
                    secondCard = null;
                    isClickable = true;
                }, 1000);
            }
        }
    }

    private void flipCard(final ImageView card, boolean showFront) {
        ObjectAnimator flipOut = ObjectAnimator.ofFloat(card, "rotationY", 0f, 90f);
        flipOut.setDuration(200);
        flipOut.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator flipIn = ObjectAnimator.ofFloat(card, "rotationY", 90f, 0f);
        flipIn.setDuration(200);
        flipIn.setInterpolator(new DecelerateInterpolator());

        flipOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (showFront) {
                    card.setImageResource((int) card.getTag());
                } else {
                    card.setImageResource(R.drawable.blurryface_mainimage);
                }
                flipIn.start();
            }
        });

        flipOut.start();
    }

    private void shakeCard(final ImageView card) {
        ObjectAnimator shake = ObjectAnimator.ofFloat(card, "translationX", -10f, 10f);
        shake.setDuration(100);
        shake.setRepeatCount(4);
        shake.setRepeatMode(ObjectAnimator.REVERSE);
        shake.start();
    }

    private void startTimer() {
        gameTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int minutes = (int) (millisUntilFinished / 1000) / 60;
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                timerText.setText(String.format("%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                timerText.setText("00:00");
                showGameLostDialog();
            }
        };
        gameTimer.start();
    }

    private void showCustomDialog(int layoutId, Runnable onConfirm, Runnable onCancel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = LayoutInflater.from(this).inflate(layoutId, null);
        builder.setView(customView);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        dialog.setCancelable(false);

        Button yesButton = customView.findViewById(R.id.yesButton);
        Button noButton = customView.findViewById(R.id.noButton);
        Button continueButton = customView.findViewById(R.id.continueButton);
        Button exitButton = customView.findViewById(R.id.exitButton);

        if (yesButton != null) yesButton.setOnClickListener(v -> {
            if (onConfirm != null) onConfirm.run();
            dialog.dismiss();
        });

        if (noButton != null) noButton.setOnClickListener(v -> {
            if (onCancel != null) onCancel.run();
            dialog.dismiss();
        });

        if (continueButton != null) continueButton.setOnClickListener(v -> {
            if (onCancel != null) onCancel.run();
            dialog.dismiss();
        });

        if (exitButton != null) exitButton.setOnClickListener(v -> {
            if (onConfirm != null) onConfirm.run();
            dialog.dismiss();
        });

        dialog.show();
    }


    private void onMenuClick() {
        if (gameTimer != null) gameTimer.cancel();

        showCustomDialog(R.layout.dialog_exit_menu, () -> {
            finish();
        }, () -> {
            if (gameTimer != null) startTimer();
        });
    }

    private void onHomeClick() {
        if (gameTimer != null) gameTimer.cancel();

        showCustomDialog(R.layout.dialog_exit_home,
                () -> {
                    Intent intent = new Intent(BlurryfaceGame.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                },
                () -> {
                    if (gameTimer != null) startTimer();
                }
        );
    }

    private void onPauseClick() {
        if (gameTimer != null) gameTimer.cancel();

        showCustomDialog(R.layout.dialog_pause, () -> {
            showCustomDialog(R.layout.dialog_exit_menu, () -> finish(), null);
        }, () -> {
            if (gameTimer != null) startTimer();
        });
    }


    private void showGameWonDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = LayoutInflater.from(this).inflate(R.layout.dialog_game_won, null);
        builder.setView(customView);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        dialog.setCancelable(false);

        Button retryButton = customView.findViewById(R.id.retryButton);
        Button menuButton = customView.findViewById(R.id.menuButton);

        retryButton.setOnClickListener(v -> {
            dialog.dismiss();
            restartGame();
        });

        menuButton.setOnClickListener(v -> {
            dialog.dismiss();
            goToMenu();
        });

        dialog.show();
    }

    private void showGameLostDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = LayoutInflater.from(this).inflate(R.layout.dialog_game_lost, null);
        builder.setView(customView);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        dialog.setCancelable(false);

        Button retryButton = customView.findViewById(R.id.retryButton);
        Button menuButton = customView.findViewById(R.id.menuButton);

        retryButton.setOnClickListener(v -> {
            dialog.dismiss();
            restartGame();
        });

        menuButton.setOnClickListener(v -> {
            dialog.dismiss();
            goToMenu();
        });

        dialog.show();
    }

    private void restartGame() {
        Intent intent = new Intent(BlurryfaceGame.this, BlurryfaceGame.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void goToMenu() {
        Intent intent = new Intent(BlurryfaceGame.this, SelectGameMenu.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}