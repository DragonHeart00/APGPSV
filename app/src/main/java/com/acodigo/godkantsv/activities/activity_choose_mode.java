package com.acodigo.godkantsv.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.acodigo.godkantsv.R;
import com.acodigo.godkantsv.common.constants;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import pl.droidsonroids.gif.GifImageView;

public class activity_choose_mode extends AppCompatActivity {

    Button buttonModeStudy, buttonModeTest, buttonModeExam;
    ImageView imageViewBackground;

    Boolean booleanUserRewarded = false;

    RewardedAd rewardedAd;

    GifImageView gifImageViewWait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_mode);

        setContents();
        setGoogleAds();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void setContents() {
        buttonModeStudy = findViewById(R.id.buttonModeStudy);
        buttonModeTest = findViewById(R.id.buttonModeTest);
        buttonModeExam = findViewById(R.id.buttonModeExam);
        imageViewBackground = findViewById(R.id.imageViewBackground);
        gifImageViewWait = findViewById(R.id.gifImageViewWait);

        buttonModeStudy.setOnClickListener(v -> setTestMode("study"));
        buttonModeTest.setOnClickListener(v -> setTestMode("test"));
        buttonModeExam.setOnClickListener(v -> setTestMode("exam"));

        if (!constants.sharedPreferencesGetIsFullTestMode(activity_choose_mode.this)) {
            onGoogleAdDone();

            buttonModeTest.setEnabled(false);
            buttonModeTest.setBackground(ContextCompat.getDrawable(activity_choose_mode.this, R.drawable.shape_static_circle_gray_light));
            buttonModeExam.setEnabled(false);
            buttonModeExam.setBackground(ContextCompat.getDrawable(activity_choose_mode.this, R.drawable.shape_static_circle_gray_light));
        } else {
            Glide.with(this).load(R.drawable.wait_white_background_green_dark).centerInside().into(gifImageViewWait);
        }
    }
    private void setGoogleAds() {
        if (constants.sharedPreferencesGetIsFullTestMode(activity_choose_mode.this)) {
            AdRequest adRequest = new AdRequest.Builder().build();

            RewardedAd.load(this, "ca-app-pub-7825425865018746/3878913609", adRequest, new RewardedAdLoadCallback() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    rewardedAd = null;
                    onGoogleAdDone();
                }

                @Override
                public void onAdLoaded(@NonNull RewardedAd Ad) {
                    rewardedAd = Ad;
                    rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdShowedFullScreenContent() {
                            rewardedAd = null;
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            Log.e(constants.logDeveloperError, adError.toString());
                            navigateToExam();
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            if (booleanUserRewarded) {
                                navigateToExam();
                            } else {
                                reloadGoogleAds();
                            }
                        }
                    });
                    onGoogleAdDone();
                }
            });
        }
    }

    private void setTestMode(String stringTestMode) {
        try {
            constants.sharedPreferencesSetTestMode(activity_choose_mode.this, stringTestMode);
        } finally {
            if (stringTestMode.equals("study") || stringTestMode.equals("test")) {
                navigateToChooseTest();
            } else if (stringTestMode.equals("exam")) {
                if (rewardedAd != null) {
                    messageShowGoogleAd();
                } else {
                    navigateToExam();
                }
            }
        }
    }

    private void showGoogleAd() {
        if (rewardedAd != null) {
            Activity activityContext = activity_choose_mode.this;
            rewardedAd.show(activityContext, rewardItem -> booleanUserRewarded = true);
        }
    }

    private void reloadGoogleAds() {
        try {
            booleanUserRewarded = false;

            buttonModeExam.setVisibility(View.GONE);
            gifImageViewWait.setVisibility(View.VISIBLE);

            setGoogleAds();
        } catch (Exception ex) {
            Log.e(constants.logDeveloperError, ex.toString());
        }
    }
    private void onGoogleAdDone() {
        gifImageViewWait.setVisibility(View.GONE);
        buttonModeExam.setVisibility(View.VISIBLE);
    }

    private void disableView() {
        buttonModeExam.setVisibility(View.GONE);
        gifImageViewWait.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void messageShowGoogleAd() {
        try {
            TextView title = new TextView(activity_choose_mode.this);
            title.setPadding(10, 10, 10, 10);
            title.setGravity(Gravity.CENTER);
            title.setTextColor(Color.WHITE);
            title.setTextSize(20);
            AlertDialog ald = new AlertDialog.Builder(activity_choose_mode.this).create();
            ald.setCanceledOnTouchOutside(false);
            ald.setMessage(getString(R.string.reward_ad_message));
            ald.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes), (dialog, which) -> showGoogleAd());
            ald.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.no), (dialog, which) -> dialog.dismiss());
            ald.show();
            TextView messageText = ald.findViewById(android.R.id.message);
            assert messageText != null;
            messageText.setGravity(Gravity.CENTER);
            Button btnPositive = ald.getButton(AlertDialog.BUTTON_POSITIVE);
            Button btnNegative = ald.getButton(AlertDialog.BUTTON_NEGATIVE);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnPositive.getLayoutParams();
            layoutParams.weight = 10;
            layoutParams.gravity = Gravity.END;
            btnPositive.setLayoutParams(layoutParams);
            btnNegative.setLayoutParams(layoutParams);
        } catch (Exception ex) {
            Log.e(constants.logDeveloperError, ex.toString());
        }
    }

    private void navigateToChooseTest() {
        startActivity(new Intent(getApplicationContext(), activity_choose_test.class));
    }
    private void navigateToExam() {
        try {
            disableView();

            constants.sharedPreferencesSetTestName(activity_choose_mode.this, getString(R.string.mode_exam));
            constants.sharedPreferencesSetTestId(activity_choose_mode.this, 0);
        } finally {
            startActivity(new Intent(getApplicationContext(), activity_test.class));
            finish();
        }
    }
}