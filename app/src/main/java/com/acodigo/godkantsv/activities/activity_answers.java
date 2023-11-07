package com.acodigo.godkantsv.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.acodigo.godkantsv.R;
import com.acodigo.godkantsv.adapters_and_items.adapter_questions;
import com.acodigo.godkantsv.common.constants;
import com.acodigo.godkantsv.database_favorite.database_favorite;
import com.acodigo.godkantsv.database_favorite.database_favorite_models;
import com.acodigo.godkantsv.database_images.database_images;
import com.acodigo.godkantsv.database_questions.database_questions_models;
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.marcoscg.dialogsheet.DialogSheet;
import com.squareup.picasso.Picasso;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import pl.droidsonroids.gif.GifImageView;

public class activity_answers extends AppCompatActivity {

    AppBarLayout appBarLayout;
    RelativeLayout relativeLayoutWait, relativeLayoutTestBody, relativeLayoutImages;
    Button buttonNext, buttonPrevious;
    TextView textViewTestName, textViewQuestion, textViewQuestionNumber, textViewCorrectCounter, textViewWrongCounter, textViewMarkedCounter, textViewUnansweredCounter;
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
    String stringQuestionId;
    String stringImageAData, stringImageBData;
    String stringCorrectAnswer;
    String stringSentReport;

    String[][] stringArrayQuestions;

    Integer integerCurrentQuestionIndex = -1, integerCurrentQuestion = 0, integerTotalQuestions = 0, integerCorrect = 0, integerWrong = 0, integerMarked = 0, integerUnanswered = 0;
    Integer integerImageAResource = 0, integerImageBResource = 0;
    Integer integerSelectedQuestionIndex = 0;

    Bitmap bitmapImageA, bitmapImageB;

    ArrayList<String> arrayListQuestionsIds;
    ArrayList<String> arrayListQuestionsNumbers;
    ArrayList<String> arrayListQuestionsStatus;

    Boolean booleanSaveQuestion = false;
    Boolean booleanMarkAnswers = true;

    LinearLayoutManager linearLayoutManager;

    Connection connection;

    database_images databaseImages;
    database_favorite databaseFavorite;

    List<database_questions_models> listDatabaseTestModels;

    AdView adView;

    GifImageView gifImageViewWait;
    DialogSheet dialogSheet;
    KProgressHUD kProgressHUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answers);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(null);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_white);

        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        setAuthorizations();
        setContents();
        setLayout();
        setTextViews();
        setDatabase();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_answers, menu);
        menuMain = menu;
        if (!constants.sharedPreferencesGetIsFullTestMode(activity_answers.this)) {
            menuMain.findItem(R.id.favorite_list).setVisible(false);
            menuMain.findItem(R.id.report_question).setVisible(false);
        }
        return true;
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mark_answers:
                item.setChecked(!item.isChecked());
                booleanMarkAnswers = item.isChecked();
                checkMarkingChange();
                return true;
            case R.id.save_delete_question:
                checkToSaveOrDeleteFromFavoriteList();
                return true;
            case R.id.save_all_Marked_questions:
                addAllMarkedQuestionsToFavoriteList();
                return true;
            case R.id.save_all_wrong_questions:
                addAllWrongQuestionsToFavoriteList();
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

    private void enableView() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    private void disableView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void messageNoInternetConnection() {
        dismissKProgressDialog();
        Toast.makeText(activity_answers.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
    }

    private void setAuthorizations() {
        stringDatabaseName = constants.authorizationSqlDatabaseURL;
        stringDatabaseUsername = constants.authorizationSqlDatabaseUsername;
        stringDatabaseUsernamePassword = constants.authorizationSqlDatabasePassword;
    }
    private void setContents() {
        appBarLayout = findViewById(R.id.appBarLayout);
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

        radioGroupAnswers.setClickable(false);
        radioButtonAnswerA.setClickable(false);
        radioButtonAnswerB.setClickable(false);
        radioButtonAnswerC.setClickable(false);
        radioButtonAnswerD.setClickable(false);

        buttonNext.setOnClickListener(v -> showNextQuestion());
        buttonPrevious.setOnClickListener(v -> showPreviousQuestion());

        Glide.with(this).load(R.drawable.wait_primary_background_white).centerInside().into(gifImageViewWait);
    }
    private void setLayout() {
        relativeLayoutImages.getLayoutParams().height = constants.sharedPreferencesGetDisplayHeight(activity_answers.this);
    }
    private void setTextViews() {
        textViewTestName.setText(getString(R.string.my_result));

        textViewCorrectCounter.setTextColor(ContextCompat.getColor(activity_answers.this, R.color.colorGreenDark));
        textViewWrongCounter.setTextColor(ContextCompat.getColor(activity_answers.this, R.color.colorRedDark));
    }
    private void setDatabase() {
        boolean booleanIsSuccess = true;
        try {
            try {
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
                    listDatabaseTestModels = constants.sharedPreferencesGetLatestTestArrayA(activity_answers.this);

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
        setQuestionsCounters();
        setGoogleAds();
    }
    private void setQuestionsArrays() {
        integerTotalQuestions = listDatabaseTestModels.size();
        stringArrayQuestions = constants.sharedPreferencesGetLatestTestArrayB(activity_answers.this);

        arrayListQuestionsIds = new ArrayList<>();
        arrayListQuestionsNumbers = new ArrayList<>();
        arrayListQuestionsStatus = new ArrayList<>();

        arrayListQuestionsIds = constants.sharedPreferencesGetLatestTestArrayC(activity_answers.this);
        arrayListQuestionsNumbers = constants.sharedPreferencesGetLatestTestArrayD(activity_answers.this);
        arrayListQuestionsStatus = constants.sharedPreferencesGetLatestTestArrayE(activity_answers.this);

        showQuestion(++integerCurrentQuestionIndex);
    }
    private void setMarkingListAdapter() {
        adapter_questions adapterQuestions = new adapter_questions(activity_answers.this, integerSelectedQuestionIndex, arrayListQuestionsNumbers, arrayListQuestionsStatus);
        adapterQuestions.setOnItemClickListener(position -> {
            integerCurrentQuestionIndex = position;
            showQuestion(integerCurrentQuestionIndex);
        });

        recyclerView.setAdapter(adapterQuestions);
    }
    private void setGoogleAds() {
        MobileAds.initialize(this, initializationStatus -> {
        });
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

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
            buttonNext.setEnabled(false);
            buttonNext.setBackgroundResource(R.drawable.shape_static_square_gray_light);
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> buttonNext.setEnabled(true), 50);
            buttonNext.setBackgroundResource(R.drawable.shape_dynamic_square_primary);
        }
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
            kProgressHUD = KProgressHUD.create(activity_answers.this)
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
        final Dialog dialog = new Dialog(activity_answers.this, R.style.FullHeightDialog);
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

    private void checkToSaveOrDeleteFromFavoriteList() {
        if (booleanSaveQuestion) {
            addNewQuestionToFavoriteList();
        } else {
            deleteQuestionFromFavoriteList();
        }
    }
    private void checkQuestionInFavorite(String id) {
        try {
            booleanSaveQuestion = !databaseFavorite.checkQuestion(id);
        } catch (Exception ex) {
            Log.e(constants.logDeveloperError, ex.toString());
            booleanSaveQuestion = false;
        }
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
            Toast.makeText(activity_answers.this, getString(R.string.add_question_done), Toast.LENGTH_SHORT).show();
        }
    }
    private void deleteQuestionFromFavoriteList() {
        TextView title = new TextView(activity_answers.this);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);
        AlertDialog ald = new AlertDialog.Builder(activity_answers.this).create();
        ald.setCanceledOnTouchOutside(false);
        ald.setMessage(getString(R.string.delete_question_ask));
        ald.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes), (dialog, which) -> {
            if (databaseFavorite.checkQuestion(stringQuestionId)) {
                databaseFavorite.deleteQuestion(stringQuestionId);
            }
            Toast.makeText(activity_answers.this, getString(R.string.delete_question_done), Toast.LENGTH_SHORT).show();
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
    private void addAllMarkedQuestionsToFavoriteList() {
        showKProgressDialog();

        AtomicBoolean isChanged = new AtomicBoolean(false);
        AtomicBoolean isMarked = new AtomicBoolean(false);
        new Thread(() -> {
            for (int i = 0; i < integerTotalQuestions; i++) {
                try {
                    if (stringArrayQuestions[i][3].equals("1")) {
                        if (!databaseFavorite.checkQuestion(listDatabaseTestModels.get(i).getId())) {
                            database_favorite_models favorite = new database_favorite_models();
                            favorite.setQuestionId(listDatabaseTestModels.get(i).getId());
                            favorite.setQuestionText(listDatabaseTestModels.get(i).getQuestion());
                            favorite.setAnswerA(listDatabaseTestModels.get(i).getAnswerA());
                            favorite.setAnswerB(listDatabaseTestModels.get(i).getAnswerB());
                            favorite.setAnswerC(listDatabaseTestModels.get(i).getAnswerC());
                            favorite.setAnswerD(listDatabaseTestModels.get(i).getAnswerD());
                            favorite.setCorrectAnswer(listDatabaseTestModels.get(i).getCorrectAnswer());
                            favorite.setSortable(listDatabaseTestModels.get(i).getSortable());
                            databaseFavorite.addQuestion(favorite);
                            isChanged.set(true);
                        }
                        isMarked.set(true);
                    }
                } catch (Exception ex) {
                    Log.i(constants.logDeveloperInformation, "QUESTION_HAS_NO_IDENTIFIER");
                }
            }

            runOnUiThread(() -> {
                if (isChanged.get()) {
                    Toast.makeText(activity_answers.this, getString(R.string.marked_questions_added_to_favorite_list), Toast.LENGTH_SHORT).show();
                } else {
                    if (!isMarked.get()) {
                        Toast.makeText(activity_answers.this, getString(R.string.there_is_no_marked_questions), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity_answers.this, getString(R.string.marked_questions_exists_already), Toast.LENGTH_SHORT).show();
                    }
                }
                dismissKProgressDialog();
            });
        }).start();
    }
    private void addAllWrongQuestionsToFavoriteList() {
        showKProgressDialog();

        AtomicBoolean isChanged = new AtomicBoolean(false);
        AtomicBoolean isWrong = new AtomicBoolean(false);
        new Thread(() -> {
            for (int i = 0; i < integerTotalQuestions; i++) {
                try {
                    if (stringArrayQuestions[i][2].equals("2")) {
                        if (!databaseFavorite.checkQuestion(listDatabaseTestModels.get(i).getId())) {
                            database_favorite_models favorite = new database_favorite_models();
                            favorite.setQuestionId(listDatabaseTestModels.get(i).getId());
                            favorite.setQuestionText(listDatabaseTestModels.get(i).getQuestion());
                            favorite.setAnswerA(listDatabaseTestModels.get(i).getAnswerA());
                            favorite.setAnswerB(listDatabaseTestModels.get(i).getAnswerB());
                            favorite.setAnswerC(listDatabaseTestModels.get(i).getAnswerC());
                            favorite.setAnswerD(listDatabaseTestModels.get(i).getAnswerD());
                            favorite.setCorrectAnswer(listDatabaseTestModels.get(i).getCorrectAnswer());
                            favorite.setSortable(listDatabaseTestModels.get(i).getSortable());
                            databaseFavorite.addQuestion(favorite);
                            isChanged.set(true);
                        }
                        isWrong.set(true);
                    }
                } catch (Exception ex) {
                    Log.i(constants.logDeveloperInformation, "QUESTION_HAS_NO_IDENTIFIER");
                }
            }

            runOnUiThread(() -> {
                if (isChanged.get()) {
                    Toast.makeText(activity_answers.this, getString(R.string.wrong_questions_added_to_favorite_list), Toast.LENGTH_SHORT).show();
                } else {
                    if (!isWrong.get()) {
                        Toast.makeText(activity_answers.this, getString(R.string.there_is_no_wrong_questions), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity_answers.this, getString(R.string.wrong_questions_exists_already), Toast.LENGTH_SHORT).show();
                    }
                }
                dismissKProgressDialog();
            });
        }).start();
    }
    @SuppressLint("NonConstantResourceId")
    private void reportQuestion() {
        View view = View.inflate(activity_answers.this, R.layout.report_view, null);
        dialogSheet = new DialogSheet(activity_answers.this)
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
                        if (constants.isInternetActive(activity_answers.this)) {
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

    private void setCurrentQuestion(int index) {
        try {
            integerSelectedQuestionIndex = index;
        } finally {
            setMarkingListAdapter();
            checkCurrentQuestionVisibility(index);
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

        textViewCorrectCounter.setText(String.format(Locale.ENGLISH, "%d", integerCorrect));
        textViewWrongCounter.setText(String.format(Locale.ENGLISH, "%d", integerWrong));

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
        whiteRadioGroup();

        int selectedId = radioGroupAnswers.getCheckedRadioButtonId();
        radioButtonSelectedAnswer = findViewById(selectedId);

        if (radioButtonSelectedAnswer.getText().equals(listDatabaseTestModels.get(integerCurrentQuestionIndex).getCorrectAnswer())) {
            if (booleanMarkAnswers) {
                radioButtonSelectedAnswer.setBackgroundResource(R.drawable.shape_dynamic_square_green_dark);
            }
            setQuestionStatus(integerCurrentQuestionIndex, "1");
        } else {
            if (booleanMarkAnswers) {
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
            setQuestionStatus(integerCurrentQuestionIndex, "2");
        }

        setQuestionAsAnswered(integerCurrentQuestionIndex, radioButtonSelectedAnswer.getText().toString());
        setQuestionsCounters();
    }

    @SuppressLint("DefaultLocale")
    private void showQuestion(int index) {
        boolean booleanSuccess = true;
        try {
            disableView();
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
            }
        } catch (Exception ex) {
            booleanSuccess = false;
        } finally {
            enableView();
            if (booleanSuccess) {
                checkQuestionsCounter();
            } else {
                constants.sharedPreferencesSetLatestTestArrayA(activity_answers.this, null);
                Toast.makeText(activity_answers.this, getString(R.string.unknown_error_occurred), Toast.LENGTH_SHORT).show();

                onBackPressed();
            }
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
                    Toast.makeText(activity_answers.this, getString(R.string.reported_successfully), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity_answers.this, getString(R.string.unknown_error_occurred), Toast.LENGTH_SHORT).show();
                    Log.e(constants.logDeveloperError, stringError.get());
                }
            });
        }).start();
    }
}