package com.acodigo.godkantsv.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.acodigo.godkantsv.common.constants;
import com.acodigo.godkantsv.R;
import com.acodigo.godkantsv.adapters_and_items.adapter_folders;
import com.acodigo.godkantsv.adapters_and_items.item_folders;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import pl.droidsonroids.gif.GifImageView;

public class activity_choose_test extends AppCompatActivity implements AdapterView.OnItemClickListener {

    RelativeLayout relativeLayoutWait, relativeLayoutTests;
    TextView textViewHeader, textViewCounter;
    ListView listView;

    Integer integerTestNumber = 0;

    List<item_folders> listRowItem;
    adapter_folders listViewAdapter;

    InterstitialAd interstitialAd;

    GifImageView gifImageViewWait;
    LikeButton likeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_test);
        setToolBar();

        disableView();

        checkDatabaseUpdate();

        setContents();
        setTextViews();
        setTests();
        setGoogleAds();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(null);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_white);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }
    private void setContents() {
        relativeLayoutWait = findViewById(R.id.relativeLayoutWait);
        relativeLayoutTests = findViewById(R.id.relativeLayoutTests);
        textViewHeader = findViewById(R.id.textViewHeader);
        textViewCounter = findViewById(R.id.textViewCounter);
        listView = findViewById(R.id.listView);
        likeButton = findViewById(R.id.likeButton);
        gifImageViewWait = findViewById(R.id.gifImageViewWait);

        listView.setOnItemClickListener(this);

        likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                likeButton.setEnabled(false);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
            }
        });
        likeButton.setOnAnimationEndListener(likeButton -> {
            final String stringAppPackageName = getPackageName();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + stringAppPackageName)));
            } catch (android.content.ActivityNotFoundException a) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + stringAppPackageName)));
            } finally {
                likeButton.setLiked(false);
                likeButton.setEnabled(true);
            }
        });

        Glide.with(this).load(R.drawable.wait_primary_background_white).centerInside().into(gifImageViewWait);
    }
    @SuppressLint("SetTextI18n")
    private void setTextViews() {
        String stringTestModeText;
        if (constants.sharedPreferencesGetTestMode(activity_choose_test.this).equals("study")) {
            stringTestModeText = getString(R.string.mode_study);
        } else if (constants.sharedPreferencesGetTestMode(activity_choose_test.this).equals("test")) {
            stringTestModeText = getString(R.string.mode_test);
        } else {
            stringTestModeText = getString(R.string.mode_exam);
        }
        textViewHeader.setText(getString(R.string.choose_test) + " | " + stringTestModeText);
        textViewCounter.setText(getString(R.string.number_of_binders_colon) + " " + constants.sharedPreferencesGetLocalDatabaseFolders(activity_choose_test.this));
    }
    private void setTests() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    prepareListView();
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Log.e(constants.logDeveloperError, e.toString());
                }
                runOnUiThread(() -> enableView());
            }
        };
        thread.start();
    }
    private void setGoogleAds() {
        AdRequest adRequestInterstitial = new AdRequest.Builder().build();
        InterstitialAd.load(this, "ca-app-pub-7825425865018746/7447203505", adRequestInterstitial, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd Ad) {
                interstitialAd = Ad;
                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        startTest();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {

                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        interstitialAd = null;
                    }
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                interstitialAd = null;
            }
        });
    }

    private void checkDatabaseUpdate() {
        if (!constants.sharedPreferencesGetLatestLocalDatabaseVersion(activity_choose_test.this).equals(constants.sharedPreferencesLocalDatabaseVersion)) {
            int integerFolders = constants.sharedPreferencesGetLocalDatabaseFolders(activity_choose_test.this);
            for (int i = 0; i < integerFolders; i++) {
                int k = i + 1;
                constants.sharedPreferencesSetTestStatus(activity_choose_test.this, constants.sharedPreferencesGetTestMode(activity_choose_test.this) + k, "");
                constants.sharedPreferencesSetTestResult(activity_choose_test.this, constants.sharedPreferencesGetTestMode(activity_choose_test.this) + k, 0);
            }
        }
    }
    private void prepareListView() {
        int integerFolders = constants.sharedPreferencesGetLocalDatabaseFolders(activity_choose_test.this);
        int integerQuestions = constants.sharedPreferencesGetLocalDatabaseQuestions(activity_choose_test.this);
        listRowItem = new ArrayList<>();
        for (int i = 0; i < integerFolders; i++) {
            int k = i + 1;

            String stringTestStatus = constants.sharedPreferencesGetTestStatus(activity_choose_test.this, constants.sharedPreferencesGetTestMode(activity_choose_test.this) + k);
            String stringSubTitle;

            boolean booleanIsOpen = false;
            boolean booleanIsDone = false;
            boolean booleanIsSuccess;

            if (stringTestStatus.equals("")) {
                stringSubTitle = getString(R.string.unopened_yet);
                booleanIsOpen = true;
            } else if (stringTestStatus.equals("1")) {
                stringSubTitle = getString(R.string.started_unfinished);
            } else {
                stringSubTitle = stringTestStatus;
                booleanIsDone = true;
            }

            booleanIsSuccess = constants.sharedPreferencesGetTestResult(activity_choose_test.this, constants.sharedPreferencesGetTestMode(activity_choose_test.this) + k) >= 57;

            if (constants.sharedPreferencesGetIsDeveloper(activity_choose_test.this) || constants.sharedPreferencesGetIsFullTestMode(activity_choose_test.this)) {
                addToList(getString(R.string.file_default_title) + " " + k, stringSubTitle, booleanIsOpen, booleanIsDone, booleanIsSuccess, false);
            } else {
                if (i < 3) {
                    addToList(getString(R.string.file_default_title) + " " + k, stringSubTitle, booleanIsOpen, booleanIsDone, booleanIsSuccess, false);
                } else {
                    addToList(getString(R.string.file_default_title) + " " + k, stringSubTitle, false, booleanIsDone, booleanIsSuccess, true);
                }
            }
        }
        listViewAdapter = new adapter_folders(activity_choose_test.this, R.layout.item_folder, listRowItem);
        listView.setAdapter(listViewAdapter);

        Log.i(constants.logDeveloperInformation, integerFolders + " / " + integerQuestions);
    }

    private void enableView() {
        if (relativeLayoutWait != null && relativeLayoutTests != null) {
            relativeLayoutWait.setVisibility(View.GONE);
            relativeLayoutTests.setVisibility(View.VISIBLE);
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    private void disableView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        if (relativeLayoutWait != null && relativeLayoutTests != null) {
            relativeLayoutTests.setVisibility(View.GONE);
            relativeLayoutWait.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (constants.sharedPreferencesGetIsDeveloper(activity_choose_test.this) || constants.sharedPreferencesGetIsFullTestMode(activity_choose_test.this)) {
            integerTestNumber = position + 1;
            showGoogleAd();
        } else {
            if (position < 3) {
                integerTestNumber = position + 1;
                showGoogleAd();
            }
        }
    }
    private void addToList(String title, String description, Boolean open, Boolean done, Boolean success, Boolean lock) {
        item_folders item = new item_folders(title, description, open, done, success, lock);
        listRowItem.add(item);
    }

    private void showGoogleAd() {
        disableView();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (interstitialAd != null) {
                interstitialAd.show(activity_choose_test.this);
            } else {
                startTest();
            }
        }, 500);
    }

    private void startTest() {
        try {
            constants.sharedPreferencesSetTestName(activity_choose_test.this, getString(R.string.binder) + " " + integerTestNumber);
            constants.sharedPreferencesSetTestId(activity_choose_test.this, integerTestNumber);

            if (constants.sharedPreferencesGetTestStatus(activity_choose_test.this, constants.sharedPreferencesGetTestMode(activity_choose_test.this) + integerTestNumber).equals("")) {
                constants.sharedPreferencesSetTestStatus(activity_choose_test.this, constants.sharedPreferencesGetTestMode(activity_choose_test.this) + integerTestNumber, "1");
            }

            constants.sharedPreferencesSetLatestLocalDatabaseVersion(activity_choose_test.this, constants.sharedPreferencesLocalDatabaseVersion);
        } finally {
            startActivity(new Intent(getApplicationContext(), activity_test.class));
            finish();
        }
    }
}