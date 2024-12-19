package com.example.matchingcardsgame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;

public class VesselGame extends AppCompatActivity {

    private TextView timerText;
    private ImageView menuButton, homeButton, pauseButton;
    private GridLayout cardGrid;
    private CountDownTimer gameTimer;

    private int[] images = {
            R.drawable.vessel_image1, R.drawable.vessel_image2, R.drawable.vessel_image3,
            R.drawable.vessel_image4, R.drawable.vessel_image5, R.drawable.vessel_image6
    };

    private ArrayList<Integer> cards;
    private ArrayList<ImageView> matchedCards = new ArrayList<>();
    private ImageView firstCard = null;
    private ImageView secondCard = null;
    private boolean isClickable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vessel_game);

        timerText = findViewById(R.id.timerText);
        menuButton = findViewById(R.id.menuButton);
        homeButton = findViewById(R.id.homeButton);
        pauseButton = findViewById(R.id.pauseButton);
        cardGrid = findViewById(R.id.cardGrid);

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
            card.setImageResource(R.drawable.vessel_mainimage);
            card.setTag(cards.get(i));
            card.setOnClickListener(this::onCardClick);

            card.setClipToOutline(true);
            card.setBackgroundResource(R.drawable.rounded_card);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 300; // Вказуємо розмір
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
                    card.setImageResource(R.drawable.vessel_mainimage);
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
        gameTimer = new CountDownTimer(300000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int minutes = (int) (millisUntilFinished / 1000) / 60;
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                timerText.setText(String.format("%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                timerText.setText("Time's up!");
            }
        };
        gameTimer.start();
    }

    private void onMenuClick() {
    }

    private void onHomeClick() {
    }

    private void onPauseClick() {
        if (gameTimer != null) gameTimer.cancel();
    }
}