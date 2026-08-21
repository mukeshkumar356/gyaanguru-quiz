package com.gyaanguru.quiz;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        View centerGroup = findViewById(R.id.center_group);
        View devRow      = findViewById(R.id.tv_dev);

        // Logo: scale + fade in
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(centerGroup, "scaleX", 0.6f, 1.05f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(centerGroup, "scaleY", 0.6f, 1.05f, 1f);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(centerGroup, "alpha", 0f, 1f);
        scaleX.setDuration(700);
        scaleY.setDuration(700);
        fadeIn.setDuration(500);
        scaleX.setInterpolator(new OvershootInterpolator(1.2f));
        scaleY.setInterpolator(new OvershootInterpolator(1.2f));
        fadeIn.setInterpolator(new DecelerateInterpolator());

        AnimatorSet logoSet = new AnimatorSet();
        logoSet.playTogether(scaleX, scaleY, fadeIn);
        logoSet.start();

        // Developer credit + photo slides up after 600ms
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            devRow.setTranslationY(30f);
            ObjectAnimator devFade  = ObjectAnimator.ofFloat(devRow, "alpha", 0f, 1f);
            ObjectAnimator devSlide = ObjectAnimator.ofFloat(devRow, "translationY", 30f, 0f);
            devFade.setDuration(500);
            devSlide.setDuration(500);
            devFade.setInterpolator(new DecelerateInterpolator());
            devSlide.setInterpolator(new DecelerateInterpolator());
            AnimatorSet devSet = new AnimatorSet();
            devSet.playTogether(devFade, devSlide);
            devSet.start();
        }, 600);

        // Launch home after 2.4 seconds
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            finish();
        }, 2400);
    }
}
