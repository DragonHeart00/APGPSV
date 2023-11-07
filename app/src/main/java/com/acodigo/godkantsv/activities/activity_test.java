package com.acodigo.godkantsv.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Locale;
import java.util.Objects;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.acodigo.godkantsv.R;
import com.acodigo.godkantsv.adapters_and_items.adapter_questions;
import com.acodigo.godkantsv.common.constants;
import com.acodigo.godkantsv.database_favorite.database_favorite_models;
import com.acodigo.godkantsv.database_favorite.database_favorite;
import com.acodigo.godkantsv.database_images.database_images;
import com.acodigo.godkantsv.database_questions.database_questions;
import com.acodigo.godkantsv.database_questions.database_questions_models;
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.marcoscg.dialogsheet.DialogSheet;
import com.squareup.picasso.Picasso;

import android.os.Handler;
import android.os.SystemClock;
import android.view.Gravity;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import pl.droidsonroids.gif.GifImageView;

public class activity_test extends AppCompatActivity {

    AppBarLayout appBarLayout;
    SwitchCompat switchMark;
    RelativeLayout relativeLayoutWait, relativeLayoutTestBody, relativeLayoutImages;
    Button buttonNext, buttonPrevious;
    TextView textViewTestName, textViewQuestion, textViewQuestionNumber, textViewCorrectCounter, textViewWrongCounter, textViewMarkedCounter, textViewUnansweredCounter, textViewTime;
    ImageView imageViewA, imageViewB;
    RadioGroup radioGroupAnswers;
    RadioButton radioButtonSelectedAnswer, radioButtonAnswerA, radioButtonAnswerB, radioButtonAnswerC, radioButtonAnswerD;
    RecyclerView recyclerView;

    RadioGroup radioGroupReport;
    RadioButton radioButton1, radioButton2, radioButton3;
    Button buttonReport, buttonCancel;
    EditText editTextMessage;

    Menu menuMain;

    String stringDatabaseUsername, stringDatabaseUsernamePassword, stringDatabaseName;
    String stringTestName;
    String stringQuestionId;
    String stringImageAData, stringImageBData;
    String stringCorrectAnswer;
    String stringSentReport;
    String stringTestMode;

    String[][] stringArrayQuestions;

    Integer integerTestNumber = 1;
    Integer integerMilliSeconds = 0, integerSeconds = 0, integerMinutes = 0;
    Integer integerCurrentQuestionIndex = -1, integerCurrentQuestion = 0, integerTotalQuestions = 0, integerCorrect = 0, integerWrong = 0, integerMarked = 0, integerUnanswered = 0;
    Integer integerImageAResource = 0, integerImageBResource = 0;
    Integer integerSelectedQuestionIndex = 0;

    Bitmap bitmapImageA, bitmapImageB;

    ArrayList<String> arrayListQuestionsIds;
    ArrayList<String> arrayListQuestionsNumbers;
    ArrayList<String> arrayListQuestionsStatus;

    Long longUpdateTime = 0L;
    Long longMilliSecondsTime = 0L;
    Long longStartTime = 0L;
    final Long longTimeBuffer = 0L;

    Boolean booleanFullTestMode = false;
    Boolean booleanSaveQuestion = false;
    Boolean booleanShowTime = true;
    Boolean booleanMarkAnswers = true;

    LinearLayoutManager linearLayoutManager;

    SharedPreferences sharedPreferences;

    Intent intentTestDone;

    Handler handlerTime;
    Runnable runnable;

    AlertDialog alertDialog;

    Connection connection;

    database_questions databaseTest;
    database_images databaseImages;
    database_favorite databaseFavorite;

    List<database_questions_models> listDatabaseTestModels;

    AdView adView;
    InterstitialAd interstitialAd;

    GifImageView gifImageViewWait;
    DialogSheet dialogSheet;
    KProgressHUD kProgressHUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(null);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_white);

        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        setAuthorizations();
        setSharedPreferences();
        setContents();
        setLayout();
        setTextViews();
        setDatabase();
    }
    @Override
    public void onBackPressed() {
        onAppBack();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_test, menu);
        menuMain = menu;
        menu.findItem(R.id.empty_favorite).setVisible(false);
        if (!constants.sharedPreferencesGetTestMode(activity_test.this).equals("study")) {
            menuMain.findItem(R.id.show_right_answer).setVisible(false);
            menuMain.findItem(R.id.mark_answers).setVisible(false);
        } else {
            menuMain.findItem(R.id.show_time).setVisible(false);
        }
        if (!constants.sharedPreferencesGetIsFullTestMode(activity_test.this)) {
            menuMain.findItem(R.id.save_delete_question).setVisible(false);
            menuMain.findItem(R.id.report_question).setVisible(false);
        }
        return true;
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_right_answer:
                showRightAnswer();
                return true;
            case R.id.show_test_info:
                showTestInfo();
                return true;
            case R.id.show_time:
                item.setChecked(!item.isChecked());
                booleanShowTime = item.isChecked();
                setTimerVisibility();
                return true;
            case R.id.mark_answers:
                item.setChecked(!item.isChecked());
                booleanMarkAnswers = item.isChecked();
                checkMarkingChange();
                return true;
            case R.id.save_delete_question:
                checkToSaveOrDeleteFromFavoriteList();
                return true;
            case R.id.report_question:
                reportQuestion();
                return true;
            default:
                return false;
        }
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuMain.findItem(R.id.show_time).setChecked(booleanShowTime);
        try {
            checkQuestionInFavorite(stringQuestionId);
        } finally {
            if (booleanSaveQuestion) {
                try {
                    menuMain.findItem(R.id.save_delete_question).setTitle(getString(R.string.save_question));
                } catch (Exception E) {
                    Log.e(constants.logDeveloperError, E.toString());
                }
            } else {
                try {
                    menuMain.findItem(R.id.save_delete_question).setTitle(getString(R.string.delete_question));
                } catch (Exception E) {
                    Log.e(constants.logDeveloperError, E.toString());
                }
            }
        }
        return true;
    }

    private void onAppBack() {
        TextView title = new TextView(activity_test.this);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);
        alertDialog = new AlertDialog.Builder(activity_test.this).create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setMessage(getString(R.string.cancel_test_ask));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes), (dialog, which) -> {
            if (!stringTestMode.equals("exam")) {
                startActivity(new Intent(activity_test.this, activity_choose_test.class));
            } else {
                startActivity(new Intent(activity_test.this, activity_choose_mode.class));
            }
            finish();
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.no), (dialog, which) -> dialog.dismiss());
        alertDialog.show();
        TextView messageText = alertDialog.findViewById(android.R.id.message);
        assert messageText != null;
        messageText.setGravity(Gravity.CENTER);
        Button btnPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button btnNegative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnPositive.getLayoutParams();
        layoutParams.weight = 10;
        layoutParams.gravity = Gravity.END;
        btnPositive.setLayoutParams(layoutParams);
        btnNegative.setLayoutParams(layoutParams);
    }

    private void enableView() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    private void disableView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void messageNoInternetConnection() {
        dismissKProgressDialog();
        Toast.makeText(activity_test.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
    }
    private void messageTenMinutesLeft() {
        try {
            if (alertDialog != null) {
                alertDialog.dismiss();
            }

            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            }
        } finally {
            try {
                TextView title = new TextView(activity_test.this);
                title.setPadding(10, 10, 10, 10);
                title.setGravity(Gravity.CENTER);
                title.setTextColor(Color.WHITE);
                title.setTextSize(20);
                alertDialog = new AlertDialog.Builder(activity_test.this).create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setMessage(getString(R.string.ten_minutes_left));
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.close), (dialog, which) -> alertDialog.dismiss());
                alertDialog.show();
                TextView messageText = alertDialog.findViewById(android.R.id.message);
                assert messageText != null;
                messageText.setGravity(Gravity.CENTER);
                final Button btnPositive = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnPositive.getLayoutParams();
                layoutParams.gravity = Gravity.END;
                btnPositive.setLayoutParams(layoutParams);
            } catch (Exception ex) {
                Log.e(constants.logDeveloperError, ex.toString());
            }
        }
    }
    private void messageTimeIsUp() {
        try {
            if (alertDialog != null) {
                alertDialog.dismiss();
            }

            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            }
        } finally {
            try {
                TextView title = new TextView(activity_test.this);
                title.setPadding(10, 10, 10, 10);
                title.setGravity(Gravity.CENTER);
                title.setTextColor(Color.WHITE);
                title.setTextSize(20);
                alertDialog = new AlertDialog.Builder(activity_test.this).create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setMessage(getString(R.string.time_is_up));
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.end), (dialog, which) -> onTestFinish());
                alertDialog.show();
                TextView messageText = alertDialog.findViewById(android.R.id.message);
                assert messageText != null;
                messageText.setGravity(Gravity.CENTER);
                final Button btnPositive = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnPositive.getLayoutParams();
                layoutParams.gravity = Gravity.END;
                btnPositive.setLayoutParams(layoutParams);
            } catch (Exception ex) {
                Log.e(constants.logDeveloperError, ex.toString());
            }
        }
    }
    private void messageEndTest() {
        try {
            if (alertDialog != null) {
                alertDialog.dismiss();
            }
        } finally {
            try {
                DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            onTestFinish();
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            dialog.dismiss();
                            break;
                    }
                };
                String stringMessage;
                if (integerMarked == 0 && integerUnanswered == 0) {
                    stringMessage = getString(R.string.end_test_ask);
                } else if (integerMarked != 0 && integerUnanswered == 0) {
                    stringMessage = getString(R.string.you_have_left_colon) + "\n\n(" + integerMarked + ") " + getString(R.string.marked_questions) + "\n\n" + getString(R.string.end_test_ask);
                } else if (integerMarked == 0) {
                    stringMessage = getString(R.string.you_have_left_colon) + "\n\n(" + integerUnanswered + ") " + getString(R.string.unanswered_questions) + "\n\n" + getString(R.string.end_test_ask);
                } else {
                    stringMessage = getString(R.string.you_have_left_colon) + "\n\n(" + integerMarked + ") " + getString(R.string.marked_questions) + "\n\n(" + integerUnanswered + ") " + getString(R.string.unanswered_questions) + "\n\n" + getString(R.string.end_test_ask);
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(activity_test.this);
                builder.setCancelable(false);
                builder.setMessage(stringMessage).setPositiveButton(getString(R.string.yes), dialogClickListener).setNegativeButton(getString(R.string.no), dialogClickListener).show();
            } catch (Exception ex) {
                Log.e(constants.logDeveloperError, ex.toString());
            }
        }
    }

    private void setAuthorizations() {
        stringDatabaseName = constants.authorizationSqlDatabaseURL;
        stringDatabaseUsername = constants.authorizationSqlDatabaseUsername;
        stringDatabaseUsernamePassword = constants.authorizationSqlDatabasePassword;
    }
    private void setSharedPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        booleanFullTestMode = constants.sharedPreferencesGetIsFullTestMode(activity_test.this);
        stringTestName = constants.sharedPreferencesGetTestName(activity_test.this);
        stringTestMode = constants.sharedPreferencesGetTestMode(activity_test.this);
        integerTestNumber = constants.sharedPreferencesGetTestId(activity_test.this);
    }
    private void setContents() {
        appBarLayout = findViewById(R.id.appBarLayout);
        switchMark = findViewById(R.id.switchMark);
        relativeLayoutWait = findViewById(R.id.relativeLayoutWait);
        relativeLayoutTestBody = findViewById(R.id.relativeLayoutTestBody);
        relativeLayoutImages = findViewById(R.id.relativeLayoutImages);
        textViewTestName = findViewById(R.id.textViewTestName);
        textViewQuestion = findViewById(R.id.textViewQuestion);
        textViewQuestionNumber = findViewById(R.id.textViewQuestionNumber);
        textViewCorrectCounter = findViewById(R.id.textViewCorrectCounter);
        textViewWrongCounter = findViewById(R.id.textViewWrongCounter);
        textViewMarkedCounter = findViewById(R.id.textViewMarkedCounter);
        textViewUnansweredCounter = findViewById(R.id.textViewUnansweredCounter);
        textViewTime = findViewById(R.id.textViewTime);
        radioGroupAnswers = findViewById(R.id.radioGroupAnswers);
        radioButtonAnswerA = findViewById(R.id.radioButtonAnswerA);
        radioButtonAnswerB = findViewById(R.id.radioButtonAnswerB);
        radioButtonAnswerC = findViewById(R.id.radioButtonAnswerC);
        radioButtonAnswerD = findViewById(R.id.radioButtonAnswerD);
        buttonNext = findViewById(R.id.buttonNext);
        buttonPrevious = findViewById(R.id.buttonPrevious);
        imageViewA = findViewById(R.id.imageViewA);
        imageViewB = findViewById(R.id.imageViewB);
        recyclerView = findViewById(R.id.recyclerView);
        gifImageViewWait = findViewById(R.id.gifImageViewWait);

        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        switchMark.setOnClickListener(v -> setQuestionMarkingStatus(integerCurrentQuestionIndex));

        imageViewA.setOnClickListener(v -> {
            if (integerImageAResource != 0) {
                showDialogImage(1);
            }
        });
        imageViewB.setOnClickListener(v -> {
            if (integerImageBResource != 0) {
                showDialogImage(2);
            }
        });

        radioButtonAnswerA.setOnClickListener(view -> setSelectedAnswer());
        radioButtonAnswerB.setOnClickListener(view -> setSelectedAnswer());
        radioButtonAnswerC.setOnClickListener(view -> setSelectedAnswer());
        radioButtonAnswerD.setOnClickListener(view -> setSelectedAnswer());

        buttonNext.setOnClickListener(v -> showNextQuestion());
        buttonPrevious.setOnClickListener(v -> showPreviousQuestion());

        Glide.with(this).load(R.drawable.wait_primary_background_white).centerInside().into(gifImageViewWait);
    }
    private void setLayout() {
        relativeLayoutImages.getLayoutParams().height = constants.sharedPreferencesGetDisplayHeight(activity_test.this);
    }
    private void setTextViews() {
        textViewTestName.setText(getTestInformation());

        if (stringTestMode.equals("study")) {
            textViewCorrectCounter.setTextColor(ContextCompat.getColor(activity_test.this, R.color.colorGreenDark));
            textViewWrongCounter.setTextColor(ContextCompat.getColor(activity_test.this, R.color.colorRedDark));

            textViewTime.setVisibility(View.GONE);
        } else {
            textViewCorrectCounter.setText(null);
            textViewWrongCounter.setText(null);
        }
    }
    private void setDatabase() {
        boolean booleanIsSuccess = true;
        try {
            listDatabaseTestModels = new ArrayList<>();
            try {
                databaseTest = database_questions.getInstance(this);
                databaseImages = database_images.getInstance(this);
                databaseFavorite = new database_favorite(this);
            } catch (Exception ex) {
                ex.printStackTrace();
                booleanIsSuccess = false;
            }
        } finally {
            if (booleanIsSuccess) {
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> new Thread(() -> {
                    switch (stringTestMode) {
                        case "study":
                            listDatabaseTestModels = databaseTest.getAllQuestions(1, integerTestNumber);
                            break;
                        case "test":
                            listDatabaseTestModels = databaseTest.getAllQuestions(2, integerTestNumber);
                            break;
                        case "exam":
                            listDatabaseTestModels = databaseTest.getAllQuestions(3, integerTestNumber);
                            break;
                    }

                    runOnUiThread(this::setDatabaseReady);
                }).start(), 100);
            } else {
                finish();
            }
        }
    }
    private void setDatabaseReady() {
        setQuestionsArrays();
        setMarkingListAdapter();
        setTimerVisibility();
        setTimerAndStart();
        setQuestionsCounters();
        setGoogleAds();
    }
    private void setQuestionsArrays() {
        integerTotalQuestions = listDatabaseTestModels.size();
        stringArrayQuestions = new String[integerTotalQuestions][100];

        arrayListQuestionsIds = new ArrayList<>();
        arrayListQuestionsNumbers = new ArrayList<>();
        arrayListQuestionsStatus = new ArrayList<>();

        for (int i = 0; i < integerTotalQuestions; i++) {
            arrayListQuestionsIds.add(listDatabaseTestModels.get(i).getId());
            arrayListQuestionsNumbers.add(String.valueOf(i + 1));
            arrayListQuestionsStatus.add("0");
        }

        showQuestion(++integerCurrentQuestionIndex);
    }
    private void setMarkingListAdapter() {
        adapter_questions adapterQuestions = new adapter_questions(activity_test.this, integerSelectedQuestionIndex, arrayListQuestionsNumbers, arrayListQuestionsStatus);
        adapterQuestions.setOnItemClickListener(position -> {
            integerCurrentQuestionIndex = position;
            showQuestion(integerCurrentQuestionIndex);
        });

        recyclerView.setAdapter(adapterQuestions);
    }
    private void setTimerAndStart() {
        if (!stringTestMode.equals("study")) {
            longStartTime = SystemClock.uptimeMillis();
            handlerTime = new Handler(Looper.getMainLooper());
            handlerTime.postDelayed(runnable = new Runnable() {
                @SuppressLint("SetTextI18n")
                public void run() {
                    try {
                        longMilliSecondsTime = SystemClock.uptimeMillis() - longStartTime;
                        longUpdateTime = longTimeBuffer + longMilliSecondsTime;
                        integerSeconds = (int) (longUpdateTime / 1000);
                        integerMinutes = integerSeconds / 60;
                        integerSeconds = integerSeconds % 60;
                        integerMilliSeconds = (int) (longUpdateTime % 1000);
                        if (integerMinutes == 40 && integerSeconds == 0) {
                            messageTenMinutesLeft();
                            textViewTime.setTextColor(Color.RED);
                        } else if (integerMinutes == 50 && integerSeconds == 0) {
                            messageTimeIsUp();
                            handlerTime.removeCallbacksAndMessages(null);
                        }
                        if (integerMinutes != 50) {
                            textViewTime.setText(String.format(Locale.ENGLISH, "%02d", integerMinutes) + ":" + String.format(Locale.ENGLISH, "%02d", integerSeconds));
                            handlerTime.postDelayed(this, 0);
                        }
                    } catch (Exception ex) {
                        Log.e(constants.logDeveloperError, ex.toString());
                    }
                }
            }, 0);
        }
    }
    private void setGoogleAds() {
        MobileAds.initialize(this, initializationStatus -> {
        });
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        AdRequest adRequestInterstitial = new AdRequest.Builder().build();
        InterstitialAd.load(this, "ca-app-pub-7825425865018746/7447203505", adRequestInterstitial, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd Ad) {
                interstitialAd = Ad;
                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        startActivity(intentTestDone);
                        finish();
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

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                onGoogleAdDone();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                onGoogleAdDone();
            }
        });
    }

    private void onGoogleAdDone() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            relativeLayoutWait.setVisibility(View.GONE);
            appBarLayout.setVisibility(View.VISIBLE);
            relativeLayoutTestBody.setVisibility(View.VISIBLE);
        }, 500);
    }

    private void checkQuestionsCounter() {
        if (integerCurrentQuestionIndex <= 0) {
            buttonPrevious.setEnabled(false);
            buttonPrevious.setBackgroundResource(R.drawable.shape_static_square_gray_light);
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> buttonPrevious.setEnabled(true), 50);
            buttonPrevious.setBackgroundResource(R.drawable.shape_dynamic_square_primary);
        }
        if (integerCurrentQuestionIndex == listDatabaseTestModels.size() - 1 || integerCurrentQuestionIndex == listDatabaseTestModels.size()) {
            buttonNext.setText(getString(R.string.end));
            buttonNext.setBackgroundResource(R.drawable.shape_dynamic_square_green_dark);
        } else {
            buttonNext.setText(getString(R.string.next));
            buttonNext.setBackgroundResource(R.drawable.shape_dynamic_square_primary);
        }

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> buttonNext.setEnabled(true), 50);
    }

    private void showNextQuestion() {
        buttonNext.setEnabled(false);

        showQuestion(++integerCurrentQuestionIndex);
        checkCurrentQuestionVisibility(integerCurrentQuestionIndex);
    }
    private void showPreviousQuestion() {
        buttonPrevious.setEnabled(false);

        showQuestion(--integerCurrentQuestionIndex);
        checkCurrentQuestionVisibility(integerCurrentQuestionIndex);
    }

    private void showKProgressDialog() {
        try {
            kProgressHUD = KProgressHUD.create(activity_test.this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setCancellable(false)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f)
                    .show();
        } catch (Exception ex) {
            Log.e(constants.logDeveloperError, ex.toString());
        }
    }
    private void dismissKProgressDialog() {
        if (kProgressHUD != null) {
            try {
                kProgressHUD.dismiss();
            } catch (Exception ex) {
                Log.e(constants.logDeveloperError, ex.toString());
            }
        }
    }
    private void dismissDialogSheet() {
        if (dialogSheet != null) {
            try {
                dialogSheet.dismiss();
            } catch (Exception ex) {
                Log.e(constants.logDeveloperError, ex.toString());
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void showDialogImage(int id) {
        final Dialog dialog = new Dialog(activity_test.this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.dialog_image);
        dialog.setTitle(null);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);

        ImageView imageViewA = dialog.findViewById(R.id.imageViewA);
        ImageView imageViewB = dialog.findViewById(R.id.imageViewB);

        Button buttonPrevious = dialog.findViewById(R.id.buttonPrevious);
        Button buttonNext = dialog.findViewById(R.id.buttonNext);
        Button buttonBack = dialog.findViewById(R.id.buttonBack);

        if (integerImageAResource != 0 && integerImageBResource != 0) {
            buttonPrevious.setVisibility(View.VISIBLE);
            buttonNext.setVisibility(View.VISIBLE);
        } else {
            buttonPrevious.setVisibility(View.GONE);
            buttonNext.setVisibility(View.GONE);
        }

        if (id == 1) {
            setDialogImage(1, imageViewA, imageViewA, imageViewB, buttonPrevious, buttonNext);
        } else {
            setDialogImage(2, imageViewB, imageViewA, imageViewB, buttonPrevious, buttonNext);
        }

        imageViewA.setOnTouchListener(new ImageMatrixTouchHandler(dialog.getContext()));
        imageViewB.setOnTouchListener(new ImageMatrixTouchHandler(dialog.getContext()));

        buttonPrevious.setOnClickListener(v -> setDialogImage(1, imageViewA, imageViewA, imageViewB, buttonPrevious, buttonNext));
        buttonNext.setOnClickListener(v -> setDialogImage(2, imageViewB, imageViewA, imageViewB, buttonPrevious, buttonNext));
        buttonBack.setOnClickListener(v12 -> dialog.dismiss());

        dialog.show();
    }
    private void setDialogImage(int id, ImageView imageView, ImageView imageViewA, ImageView imageViewB, Button buttonPrevious, Button buttonNext) {
        if (id == 1) {
            imageViewA.setVisibility(View.VISIBLE);
            imageViewB.setVisibility(View.GONE);

            Glide.with(imageView).load(bitmapImageA).into(imageView);

            buttonPrevious.setEnabled(false);
            buttonNext.setEnabled(true);
        } else {
            imageViewB.setVisibility(View.VISIBLE);
            imageViewA.setVisibility(View.GONE);

            Glide.with(imageView).load(bitmapImageB).into(imageView);

            buttonPrevious.setEnabled(true);
            buttonNext.setEnabled(false);
        }
    }

    private void showTestInfo() {
        try {
            TextView title = new TextView(activity_test.this);
            title.setPadding(10, 10, 10, 10);
            title.setGravity(Gravity.CENTER);
            title.setTextColor(Color.WHITE);
            title.setTextSize(20);
            final AlertDialog ald = new AlertDialog.Builder(activity_test.this).create();
            ald.setCanceledOnTouchOutside(false);
            ald.setMessage(getTestInformation());
            ald.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.close), (dialog, which) -> ald.dismiss());
            ald.show();
            TextView messageText = ald.findViewById(android.R.id.message);
            assert messageText != null;
            messageText.setGravity(Gravity.CENTER);
            final Button btnPositive = ald.getButton(AlertDialog.BUTTON_NEUTRAL);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnPositive.getLayoutParams();
            layoutParams.gravity = Gravity.END;
            btnPositive.setLayoutParams(layoutParams);
        } catch (Exception ex) {
            Log.e(constants.logDeveloperError, ex.toString());
        }
    }
    private void showRightAnswer() {
        clearRadioGroup();

        if (radioButtonAnswerA.getText().equals(listDatabaseTestModels.get(integerCurrentQuestionIndex).getCorrectAnswer())) {
            radioButtonAnswerA.setBackgroundResource(R.drawable.shape_dynamic_square_green_dark);
            radioButtonAnswerA.setChecked(true);
        }
        if (radioButtonAnswerB.getText().equals(listDatabaseTestModels.get(integerCurrentQuestionIndex).getCorrectAnswer())) {
            radioButtonAnswerB.setBackgroundResource(R.drawable.shape_dynamic_square_green_dark);
            radioButtonAnswerB.setChecked(true);
        }
        if (radioButtonAnswerC.getText().equals(listDatabaseTestModels.get(integerCurrentQuestionIndex).getCorrectAnswer())) {
            radioButtonAnswerC.setBackgroundResource(R.drawable.shape_dynamic_square_green_dark);
            radioButtonAnswerC.setChecked(true);
        }
        if (radioButtonAnswerD.getText().equals(listDatabaseTestModels.get(integerCurrentQuestionIndex).getCorrectAnswer())) {
            radioButtonAnswerD.setBackgroundResource(R.drawable.shape_dynamic_square_green_dark);
            radioButtonAnswerD.setChecked(true);
        }

        setSelectedAnswer();
    }
    private void setTimerVisibility() {
        if (!stringTestMode.equals("study")) {
            if (booleanShowTime) {
                textViewTime.setVisibility(View.VISIBLE);
            } else {
                textViewTime.setVisibility(View.GONE);
            }
        }
    }

    private void checkToSaveOrDeleteFromFavoriteList() {
        if (booleanSaveQuestion) {
            addNewQuestionToFavoriteList();
        } else {
            deleteQuestionFromFavoriteList();
        }
    }
    private void checkQuestionInFavorite(String id) {
        booleanSaveQuestion = !databaseFavorite.checkQuestion(id);
    }
    private void addNewQuestionToFavoriteList() {
        try {
            if (!databaseFavorite.checkQuestion(stringQuestionId)) {
                database_favorite_models favorite = new database_favorite_models();
                favorite.setQuestionId(listDatabaseTestModels.get(integerCurrentQuestionIndex).getId());
                favorite.setQuestionText(listDatabaseTestModels.get(integerCurrentQuestionIndex).getQuestion());
                favorite.setAnswerA(listDatabaseTestModels.get(integerCurrentQuestionIndex).getAnswerA());
                favorite.setAnswerB(listDatabaseTestModels.get(integerCurrentQuestionIndex).getAnswerB());
                favorite.setAnswerC(listDatabaseTestModels.get(integerCurrentQuestionIndex).getAnswerC());
                favorite.setAnswerD(listDatabaseTestModels.get(integerCurrentQuestionIndex).getAnswerD());
                favorite.setCorrectAnswer(listDatabaseTestModels.get(integerCurrentQuestionIndex).getCorrectAnswer());
                favorite.setSortable(listDatabaseTestModels.get(integerCurrentQuestionIndex).getSortable());
                databaseFavorite.addQuestion(favorite);
            }
        } finally {
            Toast.makeText(activity_test.this, getString(R.string.add_question_done), Toast.LENGTH_SHORT).show();
        }
    }
    private void deleteQuestionFromFavoriteList() {
        TextView title = new TextView(activity_test.this);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);
        AlertDialog ald = new AlertDialog.Builder(activity_test.this).create();
        ald.setCanceledOnTouchOutside(false);
        ald.setMessage(getString(R.string.delete_question_ask));
        ald.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes), (dialog, which) -> {
            if (databaseFavorite.checkQuestion(stringQuestionId)) {
                databaseFavorite.deleteQuestion(stringQuestionId);
            }
            Toast.makeText(activity_test.this, getString(R.string.delete_question_done), Toast.LENGTH_SHORT).show();
        });
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
    }
    @SuppressLint("NonConstantResourceId")
    private void reportQuestion() {
        View view = View.inflate(activity_test.this, R.layout.report_view, null);
        dialogSheet = new DialogSheet(activity_test.this)
                .setTitle(R.string.report_question)
                .setView(view)
                .setCancelable(false)
                .setSingleLineTitle(true)
                .setColoredNavigationBar(false);
        View inflatedView = dialogSheet.getInflatedView();
        radioGroupReport = inflatedView.findViewById(R.id.radioGroupReport);
        radioButton1 = inflatedView.findViewById(R.id.radioButton1);
        radioButton2 = inflatedView.findViewById(R.id.radioButton2);
        radioButton3 = inflatedView.findViewById(R.id.radioButton3);
        editTextMessage = inflatedView.findViewById(R.id.editTextMessage);
        buttonReport = inflatedView.findViewById(R.id.buttonReport);
        buttonCancel = inflatedView.findViewById(R.id.buttonCancel);
        stringSentReport = getString(R.string.report_issue_1);
        radioGroupReport.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radioButton1:
                    stringSentReport = getString(R.string.report_issue_1);
                    editTextMessage.setVisibility(View.GONE);
                    break;
                case R.id.radioButton2:
                    stringSentReport = getString(R.string.report_issue_2);
                    editTextMessage.setVisibility(View.GONE);
                    break;
                case R.id.radioButton3:
                    stringSentReport = getString(R.string.report_issue_3);
                    editTextMessage.setText(null);
                    editTextMessage.setVisibility(View.VISIBLE);
                    break;
            }
        });
        buttonReport.setOnClickListener(v -> {
            try {
                dismissKProgressDialog();
                showKProgressDialog();
            } finally {
                try {
                    if (stringSentReport.equals(getString(R.string.report_issue_3))) {
                        if (editTextMessage.getText().toString().equals("")) {
                            stringSentReport = getString(R.string.report_issue_null);
                        } else {
                            stringSentReport = editTextMessage.getText().toString();
                        }
                    }
                } catch (Exception ex) {
                    Log.e(constants.logDeveloperError, ex.toString());
                } finally {
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(() -> {
                        if (constants.isInternetActive(activity_test.this)) {
                            new Handler(Looper.getMainLooper()).postDelayed(() -> reportQuestion("SV [" + stringQuestionId + "]", textViewQuestion.getText().toString(), stringSentReport), 500);
                        } else {
                            messageNoInternetConnection();
                        }
                    }, 100);
                }
            }
        });
        buttonCancel.setOnClickListener(v -> dialogSheet.dismiss());
        dialogSheet.show();
    }

    private void clearRadioGroup() {
        radioGroupAnswers.clearCheck();

        radioButtonAnswerA.setBackgroundResource(R.drawable.shape_static_square_white);
        radioButtonAnswerB.setBackgroundResource(R.drawable.shape_static_square_white);
        radioButtonAnswerC.setBackgroundResource(R.drawable.shape_static_square_white);
        radioButtonAnswerD.setBackgroundResource(R.drawable.shape_static_square_white);
    }
    private void whiteRadioGroup() {
        radioButtonAnswerA.setBackgroundResource(R.drawable.shape_static_square_white);
        radioButtonAnswerB.setBackgroundResource(R.drawable.shape_static_square_white);
        radioButtonAnswerC.setBackgroundResource(R.drawable.shape_static_square_white);
        radioButtonAnswerD.setBackgroundResource(R.drawable.shape_static_square_white);
    }
    private void resetRadioGroup() {
        radioGroupAnswers.clearCheck();

        radioButtonAnswerA.setBackgroundResource(R.drawable.shape_static_square_white);
        radioButtonAnswerB.setBackgroundResource(R.drawable.shape_static_square_white);
        radioButtonAnswerC.setBackgroundResource(R.drawable.shape_static_square_white);
        radioButtonAnswerD.setBackgroundResource(R.drawable.shape_static_square_white);

        radioButtonAnswerA.setVisibility(View.VISIBLE);
        radioButtonAnswerB.setVisibility(View.VISIBLE);
        radioButtonAnswerC.setVisibility(View.VISIBLE);
        radioButtonAnswerD.setVisibility(View.VISIBLE);
    }

    private String getTestInformation() {
        String stringTestModeText;
        if (stringTestMode.equals("study")) {
            stringTestModeText = stringTestName + " [" + getString(R.string.mode_study) + "]";
        } else if (stringTestMode.equals("test")) {
            stringTestModeText = stringTestName + " [" + getString(R.string.mode_test) + "]";
        } else {
            stringTestModeText = stringTestName;
        }
        return stringTestModeText;
    }

    private boolean checkIfQuestionHasPassed(int index) {
        boolean booleanIsPassed = false;
        try {
            if (stringArrayQuestions[index][0].equals("1")) {
                Log.i(constants.logDeveloperInformation, "QUESTION_HAS_PASSED");
                booleanIsPassed = true;
            }
        } catch (Exception ex) {
            Log.i(constants.logDeveloperInformation, ex.toString());
            booleanIsPassed = false;
        }
        return booleanIsPassed;
    }
    private boolean checkIfQuestionIsAnswered(int index) {
        boolean booleanIsAnswered = false;
        try {
            if (!stringArrayQuestions[index][1].equals("")) {
                Log.i(constants.logDeveloperInformation, "QUESTION_IS_ANSWERED");
                booleanIsAnswered = true;
            }
        } catch (Exception ex) {
            Log.i(constants.logDeveloperInformation, ex.toString());
            booleanIsAnswered = false;
        }
        return booleanIsAnswered;
    }
    private boolean checkIfQuestionIsMarked(int index) {
        boolean booleanIsMarked = false;
        try {
            if (stringArrayQuestions[index][3].equals("1")) {
                Log.i(constants.logDeveloperInformation, "QUESTION_IS_MARKED");
                booleanIsMarked = true;
            }
        } catch (Exception ex) {
            Log.i(constants.logDeveloperInformation, ex.toString());
            booleanIsMarked = false;
        }
        return booleanIsMarked;
    }

    private void setCurrentQuestion(int index) {
        try {
            integerSelectedQuestionIndex = index;
        } finally {
            setMarkingListAdapter();
            checkCurrentQuestionVisibility(index);
        }
    }
    private void setQuestionMarkingStatus(int index) {
        try {
            if (switchMark.isChecked()) {
                stringArrayQuestions[index][3] = "1";
            } else {
                stringArrayQuestions[index][3] = "0";
            }
        } finally {
            updateQuestionsStatus(index);
        }
    }
    private void updateQuestionsStatus(int index) {
        try {
            if (switchMark.isChecked()) {
                arrayListQuestionsStatus.set(index, "2");
            } else {
                if (checkIfQuestionIsAnswered(index)) {
                    arrayListQuestionsStatus.set(index, "1");
                } else {
                    arrayListQuestionsStatus.set(index, "0");
                }
            }
            setQuestionsCounters();
            setMarkingListAdapter();
            checkCurrentQuestionVisibility(index);
        } catch (Exception ex) {
            ex.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(ex);
            finish();
        }
    }
    private void checkCurrentQuestionVisibility(int index) {
        try {
            linearLayoutManager.scrollToPositionWithOffset(index, 0);
        } catch (Exception ex) {
            ex.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(ex);
            finish();
        }
    }

    private void setQuestionAsPassed(int index, String alternativeA, String alternativeB, String alternativeC, String alternativeD) {
        stringArrayQuestions[index][0] = "1";
        stringArrayQuestions[index][10] = alternativeA;
        stringArrayQuestions[index][11] = alternativeB;
        stringArrayQuestions[index][12] = alternativeC;
        stringArrayQuestions[index][13] = alternativeD;
    }
    private void setQuestionAsAnswered(int index, String answer) {
        stringArrayQuestions[index][1] = answer;
        Log.i(constants.logDeveloperInformation, answer);
    }
    private void setQuestionStatus(int index, String status) {
        stringArrayQuestions[index][2] = status;
        Log.i(constants.logDeveloperInformation, status);
    }
    private void markSelectedAnswer(int index) {
        try {
            if (stringArrayQuestions[index][0].equals("1") && !stringArrayQuestions[index][1].equals("")) {
                if (radioButtonAnswerA.getText().toString().equals(stringArrayQuestions[index][1])) {
                    radioButtonAnswerA.setChecked(true);
                }
                if (radioButtonAnswerB.getText().toString().equals(stringArrayQuestions[index][1])) {
                    radioButtonAnswerB.setChecked(true);
                }
                if (radioButtonAnswerC.getText().toString().equals(stringArrayQuestions[index][1])) {
                    radioButtonAnswerC.setChecked(true);
                }
                if (radioButtonAnswerD.getText().toString().equals(stringArrayQuestions[index][1])) {
                    radioButtonAnswerD.setChecked(true);
                }

                int selectedId = radioGroupAnswers.getCheckedRadioButtonId();
                radioButtonSelectedAnswer = findViewById(selectedId);

                if (radioButtonSelectedAnswer != null) {
                    setSelectedAnswer();
                }
            }
        } catch (Exception ex) {
            Log.i(constants.logDeveloperInformation, ex.toString());
        }
    }
    private void checkMarkingChange() {
        int selectedId = radioGroupAnswers.getCheckedRadioButtonId();
        radioButtonSelectedAnswer = findViewById(selectedId);

        if (radioButtonSelectedAnswer != null) {
            setSelectedAnswer();
        }
    }
    private void checkAlternativesVisibility() {
        if (radioButtonAnswerA.getText().toString().equals("")) {
            radioButtonAnswerA.setVisibility(View.GONE);
        }
        if (radioButtonAnswerB.getText().toString().equals("")) {
            radioButtonAnswerB.setVisibility(View.GONE);
        }
        if (radioButtonAnswerC.getText().toString().equals("")) {
            radioButtonAnswerC.setVisibility(View.GONE);
        }
        if (radioButtonAnswerD.getText().toString().equals("")) {
            radioButtonAnswerD.setVisibility(View.GONE);
        }
    }
    @SuppressLint("SetTextI18n")
    private void setQuestionsCounters() {
        integerCorrect = 0;
        integerWrong = 0;

        for (int i = 0; i < integerTotalQuestions; i++) {
            try {
                if (stringArrayQuestions[i][2].equals("1")) {
                    integerCorrect++;
                }
                if (stringArrayQuestions[i][2].equals("2")) {
                    integerWrong++;
                }
            } catch (Exception ex) {
                Log.i(constants.logDeveloperInformation, ex.toString());
            }
        }

        if (stringTestMode.equals("study")) {
            textViewCorrectCounter.setText(String.format(Locale.ENGLISH, "%d", integerCorrect));
            textViewWrongCounter.setText(String.format(Locale.ENGLISH, "%d", integerWrong));
        }

        integerMarked = 0;
        integerUnanswered = integerTotalQuestions - (integerCorrect + integerWrong);

        for (int i = 0; i < integerTotalQuestions; i++) {
            if (arrayListQuestionsStatus.get(i).equals("2")) {
                integerMarked++;
            }
        }

        textViewMarkedCounter.setText(getString(R.string.marked_colon) + " " + String.format(Locale.ENGLISH, "%d", integerMarked));
        textViewUnansweredCounter.setText(getString(R.string.unanswered_colon) + " " +String.format(Locale.ENGLISH, "%d", integerUnanswered));
    }
    private void setSelectedAnswer() {
        try {
            whiteRadioGroup();

            int selectedId = radioGroupAnswers.getCheckedRadioButtonId();
            radioButtonSelectedAnswer = findViewById(selectedId);

            if (radioButtonSelectedAnswer.getText().equals(listDatabaseTestModels.get(integerCurrentQuestionIndex).getCorrectAnswer())) {
                try {
                    if (constants.sharedPreferencesGetTestMode(activity_test.this).equals("study") && booleanMarkAnswers) {
                        radioButtonSelectedAnswer.setBackgroundResource(R.drawable.shape_dynamic_square_green_dark);
                    }
                } finally {
                    setQuestionStatus(integerCurrentQuestionIndex, "1");
                }
            } else {
                try {
                    if (constants.sharedPreferencesGetTestMode(activity_test.this).equals("study") && booleanMarkAnswers) {
                        radioButtonSelectedAnswer.setBackgroundResource(R.drawable.shape_dynamic_square_red_dark);

                        if (radioButtonAnswerA.getText().equals(listDatabaseTestModels.get(integerCurrentQuestionIndex).getCorrectAnswer())) {
                            radioButtonAnswerA.setBackgroundResource(R.drawable.shape_dynamic_square_green_dark);
                        }
                        if (radioButtonAnswerB.getText().equals(listDatabaseTestModels.get(integerCurrentQuestionIndex).getCorrectAnswer())) {
                            radioButtonAnswerB.setBackgroundResource(R.drawable.shape_dynamic_square_green_dark);
                        }
                        if (radioButtonAnswerC.getText().equals(listDatabaseTestModels.get(integerCurrentQuestionIndex).getCorrectAnswer())) {
                            radioButtonAnswerC.setBackgroundResource(R.drawable.shape_dynamic_square_green_dark);
                        }
                        if (radioButtonAnswerD.getText().equals(listDatabaseTestModels.get(integerCurrentQuestionIndex).getCorrectAnswer())) {
                            radioButtonAnswerD.setBackgroundResource(R.drawable.shape_dynamic_square_green_dark);
                        }
                    }
                } finally {
                    setQuestionStatus(integerCurrentQuestionIndex, "2");
                }
            }
            setQuestionAsAnswered(integerCurrentQuestionIndex, radioButtonSelectedAnswer.getText().toString());
            setQuestionsCounters();
            updateQuestionsStatus(integerCurrentQuestionIndex);
        } catch (Exception ex) {
            FirebaseCrashlytics.getInstance().recordException(ex);
        }
    }

    @SuppressLint("DefaultLocale")
    private void showQuestion(int index) {
        disableView();
        try {
            if (index < integerTotalQuestions) {
                resetRadioGroup();
                setCurrentQuestion(index);

                integerCurrentQuestion = listDatabaseTestModels.indexOf(listDatabaseTestModels.get(index)) + 1;

                textViewQuestionNumber.setText(String.format(Locale.ENGLISH, "%d/%d", integerCurrentQuestion, integerTotalQuestions));
                textViewQuestion.setText(listDatabaseTestModels.get(index).getQuestion());

                stringQuestionId = listDatabaseTestModels.get(index).getId();
                stringCorrectAnswer = listDatabaseTestModels.get(index).getCorrectAnswer();

                checkQuestionInFavorite(stringQuestionId);

                stringImageAData = databaseImages.getImageA(stringQuestionId);
                if (!stringImageAData.equals("")) {
                    byte[] byteDecodedImageAData = Base64.decode(stringImageAData, Base64.DEFAULT);
                    bitmapImageA = BitmapFactory.decodeByteArray(byteDecodedImageAData, 0, byteDecodedImageAData.length);
                    Glide.with(imageViewA).load(bitmapImageA).into(imageViewA);
                    imageViewA.setClickable(true);
                    integerImageAResource = 1;
                } else {
                    Picasso.get().load(R.drawable.icon_null_image).fit().into(imageViewA);
                    imageViewA.setClickable(false);
                    integerImageAResource = 0;
                }

                stringImageBData = databaseImages.getImageB(stringQuestionId);
                if (!stringImageBData.equals("")) {
                    byte[] byteDecodedImageBData = Base64.decode(stringImageBData, Base64.DEFAULT);
                    bitmapImageB = BitmapFactory.decodeByteArray(byteDecodedImageBData, 0, byteDecodedImageBData.length);
                    Glide.with(imageViewB).load(bitmapImageB).into(imageViewB);
                    imageViewB.setClickable(true);
                    integerImageBResource = 1;
                } else {
                    Picasso.get().load(R.drawable.icon_null_image).fit().into(imageViewB);
                    imageViewB.setClickable(false);
                    integerImageBResource = 0;
                }

                switchMark.setChecked(checkIfQuestionIsMarked(index));
                if (checkIfQuestionHasPassed(index)) {
                    radioButtonAnswerA.setText(stringArrayQuestions[index][10]);
                    radioButtonAnswerB.setText(stringArrayQuestions[index][11]);
                    radioButtonAnswerC.setText(stringArrayQuestions[index][12]);
                    radioButtonAnswerD.setText(stringArrayQuestions[index][13]);
                } else {
                    if (listDatabaseTestModels.get(index).getSortable().equals("Yes")) {
                        String[] str = {listDatabaseTestModels.get(index).getAnswerA(), listDatabaseTestModels.get(index).getAnswerB(), listDatabaseTestModels.get(index).getAnswerC(), listDatabaseTestModels.get(index).getAnswerD()};
                        RadioButton[] radioButton = new RadioButton[4];
                        radioButton[0] = findViewById(R.id.radioButtonAnswerA);
                        radioButton[1] = findViewById(R.id.radioButtonAnswerB);
                        radioButton[2] = findViewById(R.id.radioButtonAnswerC);
                        radioButton[3] = findViewById(R.id.radioButtonAnswerD);
                        List<Integer> setNames = new ArrayList<>();
                        for (int i = 0; i < 4; i++) {
                            int x;
                            do {
                                Random r = new Random();
                                x = r.nextInt(str.length);
                            } while (setNames.contains(x));
                            setNames.add(x);
                            radioButton[i].setText(str[x]);
                        }
                    } else {
                        radioButtonAnswerA.setText(listDatabaseTestModels.get(index).getAnswerA());
                        radioButtonAnswerB.setText(listDatabaseTestModels.get(index).getAnswerB());
                        radioButtonAnswerC.setText(listDatabaseTestModels.get(index).getAnswerC());
                        radioButtonAnswerD.setText(listDatabaseTestModels.get(index).getAnswerD());
                    }
                }

                checkAlternativesVisibility();
                markSelectedAnswer(index);
                setQuestionAsPassed(index, radioButtonAnswerA.getText().toString(), radioButtonAnswerB.getText().toString(), radioButtonAnswerC.getText().toString(), radioButtonAnswerD.getText().toString());
            } else {
                --integerCurrentQuestionIndex;
                messageEndTest();
            }
        } finally {
            checkQuestionsCounter();
        }
        enableView();
    }

    private void onTestFinish() {
        disableView();
        final Dialog dialog = new Dialog(activity_test.this, R.style.FullHeightDialog);
        try {
            try {
                dialog.setContentView(R.layout.dialog_done);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                dialog.show();
            } catch (Exception ex) {
                Log.e(constants.logDeveloperError, ex.toString());
            }

            int stringDatabaseUsername = integerTotalQuestions - (integerWrong + integerCorrect);
            intentTestDone = new Intent(activity_test.this, activity_done.class);
            intentTestDone.putExtra("TEST_ID", getTestInformation());
            intentTestDone.putExtra("TOTAL_QUESTIONS", String.valueOf(integerTotalQuestions));
            intentTestDone.putExtra("CORRECT_COUNTER", String.valueOf(integerCorrect));
            intentTestDone.putExtra("WRONG_COUNTER", String.valueOf(integerWrong));
            intentTestDone.putExtra("UNANSWERED_COUNTER", String.valueOf(stringDatabaseUsername));

            if (!constants.sharedPreferencesGetTestMode(activity_test.this).equals("study")) {
                intentTestDone.putExtra("TIME", textViewTime.getText().toString());
            }

            constants.sharedPreferencesSetLatestTestArrayA(activity_test.this, listDatabaseTestModels);
            constants.sharedPreferencesSetLatestTestArrayB(activity_test.this, stringArrayQuestions);
            constants.sharedPreferencesSetLatestTestArrayC(activity_test.this, arrayListQuestionsIds);
            constants.sharedPreferencesSetLatestTestArrayD(activity_test.this, arrayListQuestionsNumbers);
            constants.sharedPreferencesSetLatestTestArrayE(activity_test.this, arrayListQuestionsStatus);
            constants.sharedPreferencesSetTestResult(activity_test.this, constants.sharedPreferencesGetTestMode(activity_test.this) + integerTestNumber, integerCorrect);
            constants.sharedPreferencesSetTestStatus(activity_test.this, constants.sharedPreferencesGetTestMode(activity_test.this) + integerTestNumber, getString(R.string.latest_result) + ": " + integerCorrect + "/" + integerTotalQuestions);
            constants.sharedPreferencesSetLatestLocalDatabaseVersion(activity_test.this, constants.sharedPreferencesLocalDatabaseVersion);
        } finally {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                try {
                    if (interstitialAd != null) {
                        enableView();
                        dialog.dismiss();
                        interstitialAd.show(activity_test.this);
                    } else {
                        dialog.dismiss();
                        startActivity(intentTestDone);
                        finish();
                    }
                } catch (Exception ex) {
                    Log.e(constants.logDeveloperError, ex.toString());
                }
            }, 500);
        }
    }

    private void reportQuestion(String qId, String qString, String qIssue) {
        AtomicReference<String> stringError = new AtomicReference<>("");
        AtomicReference<Boolean> booleanIsSuccess = new AtomicReference<>(false);

        new Thread(() -> {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                connection = DriverManager.getConnection(stringDatabaseName, stringDatabaseUsername, stringDatabaseUsernamePassword);
                if (connection == null) {
                    stringError.set(getString(R.string.check_your_internet_connection));
                } else {
                    String queryCheck = "SELECT * FROM appGodKäntReports WHERE questionId = '" + qId + "'";
                    Statement stmtCheck = connection.createStatement();
                    ResultSet rsCheck = stmtCheck.executeQuery(queryCheck);
                    if (!rsCheck.next()) {
                        String queryInsert = "INSERT INTO appGodKäntReports (questionId, questionStatus, questionString, questionIssue) VALUES ('" + qId + "', '" + 0 + "', N'" + qString + "', N'" + qIssue + "')";
                        PreparedStatement st = connection.prepareStatement(queryInsert);
                        st.executeUpdate();
                    } else {
                        Log.i(constants.logDeveloperInformation, getString(R.string.already_reported));
                    }
                    booleanIsSuccess.set(true);
                }
                connection.close();
            } catch (Exception ex) {
                booleanIsSuccess.set(false);
                stringError.set(ex.getMessage());
            }

            runOnUiThread(() -> {
                dismissKProgressDialog();
                dismissDialogSheet();
                if (booleanIsSuccess.get()) {
                    Toast.makeText(activity_test.this, getString(R.string.reported_successfully), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity_test.this, getString(R.string.unknown_error_occurred), Toast.LENGTH_SHORT).show();
                    Log.e(constants.logDeveloperError, stringError.get());
                }
            });
        }).start();
    }
}