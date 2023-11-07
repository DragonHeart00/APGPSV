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
import com.acodigo.godkantsv.database_favorite.database_favorite_models;
import com.acodigo.godkantsv.database_favorite.database_favorite;
import com.codemybrainsout.ratingdialog.RatingDialog;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import pl.droidsonroids.gif.GifImageView;

public class activity_secondary_page extends AppCompatActivity {

    Button buttonStart, buttonFavorite, buttonLatestResult, buttonInfo, buttonAbout;

    Boolean booleanIsWorking = false;

    GifImageView gifImageViewWaitFavorite, gifImageViewWaitLatestResult;

    database_favorite databaseFavorite;

    List<database_favorite_models> listDatabaseFavoriteModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondary_page);

        setContents();

        messageRateUsOnGooglePlay();
    }
    @Override
    public void onBackPressed() {
        onAppExit();
    }
    public void onResume() {
        super.onResume();
        if (!booleanIsWorking) {
            enableView();
        }
    }

    private void setContents() {
        databaseFavorite = new database_favorite(this);
        listDatabaseFavoriteModels = databaseFavorite.getAllQuestions();

        buttonStart = findViewById(R.id.buttonStart);
        buttonFavorite = findViewById(R.id.buttonFavorite);
        buttonLatestResult = findViewById(R.id.buttonLatestResult);
        buttonInfo = findViewById(R.id.buttonInfo);
        buttonAbout = findViewById(R.id.buttonAbout);
        gifImageViewWaitFavorite = findViewById(R.id.gifImageViewWaitFavorite);
        gifImageViewWaitLatestResult = findViewById(R.id.gifImageViewWaitLatestResult);

        buttonStart.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), activity_choose_mode.class)));
        buttonFavorite.setOnClickListener(v -> checkFavoriteList());
        buttonLatestResult.setOnClickListener(v -> checkLatestResult());
        buttonInfo.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), activity_code_info.class)));
        buttonAbout.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), activity_about.class)));

        if (constants.sharedPreferencesGetIsAvailable(activity_secondary_page.this)) {
            buttonInfo.setEnabled(false);
            buttonInfo.setVisibility(View.GONE);
        }
    }

    private void onAppExit() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    private void enableView() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        gifImageViewWaitFavorite.setVisibility(View.GONE);
        buttonFavorite.setVisibility(View.VISIBLE);

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

    private void onCheckFavoriteListStart() {
        booleanIsWorking = true;

        buttonFavorite.setVisibility(View.GONE);
        gifImageViewWaitFavorite.setVisibility(View.VISIBLE);

        disableView();
    }
    private void checkFavoriteList() {
        AtomicBoolean booleanEmpty = new AtomicBoolean(true);

        onCheckFavoriteListStart();

        new Thread(() -> {
            listDatabaseFavoriteModels.clear();
            databaseFavorite = new database_favorite(activity_secondary_page.this);
            listDatabaseFavoriteModels = databaseFavorite.getAllQuestions();
            booleanEmpty.set(listDatabaseFavoriteModels.size() <= 0);

            runOnUiThread(() -> {
                if (!booleanEmpty.get()) {
                    startActivity(new Intent(getApplicationContext(), activity_favorite.class));
                } else {
                    Toast.makeText(activity_secondary_page.this, getString(R.string.favorite_list_empty), Toast.LENGTH_SHORT).show();
                    enableView();
                }
                booleanIsWorking = false;
            });
        }).start();
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
            if (constants.sharedPreferencesGetLatestTestArrayA(activity_secondary_page.this) == null) {
                booleanEmpty.set(true);
            }

            if (!constants.sharedPreferencesGetLatestLocalDatabaseVersion(activity_secondary_page.this).equals(constants.sharedPreferencesLocalDatabaseVersion)) {
                booleanUpdated.set(true);
                constants.sharedPreferencesSetLatestTestArrayA(activity_secondary_page.this, null);
            }

            runOnUiThread(() -> {
                if (booleanEmpty.get()) {
                    Toast.makeText(activity_secondary_page.this, getString(R.string.latest_tests_unavailable), Toast.LENGTH_SHORT).show();
                    enableView();
                } else {
                    if (booleanUpdated.get()) {
                        Toast.makeText(activity_secondary_page.this, getString(R.string.database_has_been_updated), Toast.LENGTH_SHORT).show();
                        enableView();
                    } else {
                        startActivity(new Intent(activity_secondary_page.this, activity_answers.class));
                    }
                }
                booleanIsWorking = false;
            });
        }).start();
    }
}