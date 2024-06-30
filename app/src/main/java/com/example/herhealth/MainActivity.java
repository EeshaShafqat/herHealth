package com.example.herhealth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {


    private CircleImageView image;
    private CircleImageView emptyImageView;



    private CardView card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image = findViewById(R.id.image);
        card = findViewById(R.id.card);
        emptyImageView = findViewById(R.id.emptyImageView);


        Animation animationSet = AnimationUtils.loadAnimation(this, R.anim.jump_animation);
        card.startAnimation(animationSet);


        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(1000); // Adjust the duration as needed
        alphaAnimation.setFillAfter(true); // Keep the view visible after the animation

        // Set an animation listener to detect the end of the animation
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {


                // Animation starts
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Animation ends
                // You can perform any action here when the animation is finished


                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

                // Animation repeats (if specified)
            }
        });

        // Apply the animation set to the emptyImageView
        card.startAnimation(animationSet);

        // Apply the AlphaAnimation to fade in the actualImageView
        emptyImageView.startAnimation(alphaAnimation);



    }





}