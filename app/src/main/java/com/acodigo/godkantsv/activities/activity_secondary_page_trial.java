package com.acodigo.godkantsv.activities;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.acodigo.godkantsv.R;
import com.acodigo.godkantsv.common.constants;
import com.codemybrainsout.ratingdialog.RatingDialog;

import java.util.concurrent.atomic.AtomicBoolean;

import pl.droidsonroids.gif.GifImageView;

public class activity_secondary_page_trial extends AppCompatActivity {

    Button buttonStart, buttonLatestResult, buttonAbout, buttonBack;

    Boolean booleanIsWorking = false;

    GifImageView gifImageViewWaitLatestResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondary_page_trial);

        setContents();

        messageRateUsOnGooglePlay();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    public void onResume() {
        super.onResume();
        if (!booleanIsWorking) {
            enableView();
        }
    }

    private void setContents() {
        buttonStart = findViewById(R.id.buttonStart);
        buttonLatestResult = findViewById(R.id.buttonLatestResult);
        buttonAbout = findViewById(R.id.buttonAbout);
        buttonBack = findViewById(R.id.buttonBack);
        gifImageViewWaitLatestResult = findViewById(R.id.gifImageViewWaitLatestResult);

        buttonStart.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), activity_choose_mode.class)));
        buttonLatestResult.setOnClickListener(v -> checkLatestResult());
        buttonAbout.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), activity_about.class)));
        buttonBack.setOnClickListener(v -> onBackPressed());
    }

    private void enableView() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        gifImageViewWaitLatestResult.setVisibility(View.GONE);
        buttonLatestResult.setVisibility(View.VISIBLE);
    }
    private void disableView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void messageRateUsOnGooglePlay() {
        try {
            final RatingDialog ratingDialog = new RatingDialog.Builder(this)
                    .session(4)
                    .title(getString(R.string.rate_us_ask))
                    .positiveButtonText(getString(R.string.not_now))
                    .negativeButtonText(getString(R.string.do_not_remind_me))
                    .positiveButtonTextColor(R.color.colorPrimary)
                    .negativeButtonTextColor(R.color.colorRedDark)
                    .ratingBarColor(R.color.colorPrimary)
                    .onRatingBarFormSumbit(feedback -> Log.i(constants.logDeveloperInformation, "FEEDBACK: " + feedback)).build();
            ratingDialog.setCancelable(false);
            ratingDialog.setCanceledOnTouchOutside(false);
            ratingDialog.show();
        } catch (Exception ex) {
            Log.e(constants.logDeveloperError, ex.toString());
        }
    }

    private void onCheckLatestResultStart() {
        booleanIsWorking = true;

        buttonLatestResult.setVisibility(View.GONE);
        gifImageViewWaitLatestResult.setVisibility(View.VISIBLE);

        disableView();
    }
    private void checkLatestResult() {
        AtomicBoolean booleanEmpty = new AtomicBoolean(false);
        AtomicBoolean booleanUpdated = new AtomicBoolean(false);

        onCheckLatestResultStart();

        new Thread(() -> {
            if (constants.sharedPreferencesGetLatestTestArrayA(activity_secondary_page_trial.this) == null) {
                booleanEmpty.set(true);
            }

            if (!constants.sharedPreferencesGetLatestLocalDatabaseVersion(activity_secondary_page_trial.this).equals(constants.sharedPreferencesLocalDatabaseVersion)) {
                booleanUpdated.set(true);
                constants.sharedPreferencesSetLatestTestArrayA(activity_secondary_page_trial.this, null);
            }

            runOnUiThread(() -> {
                if (booleanEmpty.get()) {
                    Toast.makeText(activity_secondary_page_trial.this, getString(R.string.latest_tests_unavailable), Toast.LENGTH_SHORT).show();
                    enableView();
                } else {
                    if (booleanUpdated.get()) {
                        Toast.makeText(activity_secondary_page_trial.this, getString(R.string.database_has_been_updated), Toast.LENGTH_SHORT).show();
                        enableView();
                    } else {
                        startActivity(new Intent(activity_secondary_page_trial.this, activity_answers.class));
                    }
                }
                booleanIsWorking = false;
            });
        }).start();
    }
}