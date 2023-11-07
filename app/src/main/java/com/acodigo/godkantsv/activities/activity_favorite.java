package com.acodigo.godkantsv.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.Looper;
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
import android.graphics.Color;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.acodigo.godkantsv.R;
import com.acodigo.godkantsv.common.constants;
import com.acodigo.godkantsv.database_favorite.database_favorite_models;
import com.acodigo.godkantsv.database_favorite.database_favorite_save;
import com.acodigo.godkantsv.database_favorite.database_favorite;
import com.acodigo.godkantsv.database_images.database_images;
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.appbar.AppBarLayout;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.marcoscg.dialogsheet.DialogSheet;
import com.squareup.picasso.Picasso;

import android.view.Gravity;

import androidx.appcompat.app.AlertDialog;

import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import pl.droidsonroids.gif.GifImageView;

public class activity_favorite extends AppCompatActivity {

    AppBarLayout appBarLayout;
    RelativeLayout relativeLayoutWait, relativeLayoutTestBody, relativeLayoutImages;
    Button buttonAnswerA, buttonAnswerB, buttonAnswerC, buttonAnswerD, buttonNext, buttonPrevious;
    ImageView imageViewA, imageViewB;
    TextView textViewQuestionNumber, textViewQuestion, textViewTestName;

    EditText editTextMessage;
    Button buttonReport, buttonCancel;
    RadioGroup radioGroupReport;
    RadioButton radioButton1, radioButton2, radioButton3;

    Menu menuMain;

    String stringDatabaseUsername, stringDatabaseUsernamePassword, stringDatabaseName;
    String stringQuestionId;
    String stringImageAData, stringImageBData;
    String stringSentReport;

    String[][] stringArrayQuestions;

    Integer integerListQuestions;
    Integer integerIndex = -1;
    Integer integerImageAResource = 0, integerImageBResource = 0;

    Bitmap bitmapImageA, bitmapImageB;

    database_images databaseImages;
    database_favorite databaseFavorite;

    List<database_favorite_models> listDatabaseFavoriteModels;
    List<database_favorite_save> listDatabaseFavoriteSave;

    Connection connection;

    AdView adView;

    GifImageView gifImageViewWait;
    DialogSheet dialogSheet;
    KProgressHUD kProgressHUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(null);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_white);

        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        setAuthorizations();
        setContents();
        setLayout();
        setDatabase();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_test, menu);
        menuMain = menu;
        menuMain.findItem(R.id.show_right_answer).setVisible(false);
        menuMain.findItem(R.id.show_test_info).setVisible(false);
        menuMain.findItem(R.id.show_time).setVisible(false);
        menuMain.findItem(R.id.mark_answers).setVisible(false);
        menuMain.findItem(R.id.save_delete_question).setTitle(getString(R.string.delete_question));
        return true;
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_delete_question:
                deleteQuestionFromFavoriteList(stringQuestionId);
                return true;
            case R.id.empty_favorite:
                emptyFavorite();
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
        return true;
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
        textViewQuestion = findViewById(R.id.textViewQuestion);
        textViewQuestionNumber = findViewById(R.id.textViewQuestionNumber);
        textViewTestName = findViewById(R.id.textViewTestName);
        imageViewA = findViewById(R.id.imageViewA);
        imageViewB = findViewById(R.id.imageViewB);
        buttonAnswerA = findViewById(R.id.buttonAnswerA);
        buttonAnswerB = findViewById(R.id.buttonAnswerB);
        buttonAnswerC = findViewById(R.id.buttonAnswerC);
        buttonAnswerD = findViewById(R.id.buttonAnswerD);
        buttonNext = findViewById(R.id.buttonNext);
        buttonPrevious = findViewById(R.id.buttonPrevious);
        gifImageViewWait = findViewById(R.id.gifImageViewWait);

        textViewTestName.setText(getString(R.string.favorite_list));

        buttonAnswerA.setClickable(false);
        buttonAnswerB.setClickable(false);
        buttonAnswerC.setClickable(false);
        buttonAnswerD.setClickable(false);

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

        buttonNext.setOnClickListener(v -> {
            try {
                nextQuestion();
            } finally {
                checkQuestion();
            }
        });
        buttonPrevious.setOnClickListener(v -> {
            try {
                previousQuestion();
            } finally {
                checkQuestion();
            }
        });

        Glide.with(this).load(R.drawable.wait_primary_background_white).centerInside().into(gifImageViewWait);
    }
    private void setLayout() {
        relativeLayoutImages.getLayoutParams().height = constants.sharedPreferencesGetDisplayHeight(activity_favorite.this);
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
                onContentsReady();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                onContentsReady();
            }
        });
    }
    private void setDatabase() {
        boolean booleanIsSuccess = true;
        try {
            databaseImages = database_images.getInstance(this);
            databaseFavorite = new database_favorite(this);
        } catch (Exception ex) {
            ex.printStackTrace();
            booleanIsSuccess = false;
        } finally {
            if (booleanIsSuccess) {
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> new Thread(() -> {
                    listDatabaseFavoriteModels = databaseFavorite.getAllQuestions();

                    integerListQuestions = listDatabaseFavoriteModels.size();
                    stringArrayQuestions = new String[integerListQuestions][100];

                    runOnUiThread(this::setDatabaseReady);
                }).start(), 100);
            } else {
                finish();
            }
        }
    }
    private void setDatabaseReady() {
        populateDatabase();
        checkQuestion();
        onTestStarted();
        setGoogleAds();
    }
    private void onContentsReady() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            relativeLayoutWait.setVisibility(View.GONE);
            appBarLayout.setVisibility(View.VISIBLE);
            relativeLayoutTestBody.setVisibility(View.VISIBLE);
        }, 500);
    }
    private void onTestStarted() {
        buttonNext.performClick();
        Log.i(constants.logDeveloperInformation, "TEST_HAS_STARTED");
    }

    private void navigateToSecondaryPage() {
        startActivity(new Intent(getApplicationContext(), activity_secondary_page.class));
        finish();
    }

    private void messageNoInternetConnection() {
        dismissKProgressDialog();
        Toast.makeText(activity_favorite.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
    }

    private void populateDatabase() {
        listDatabaseFavoriteModels = databaseFavorite.getAllQuestions();
        listDatabaseFavoriteSave = new ArrayList<>();
        for (int i = 0; i < listDatabaseFavoriteModels.size(); i++) {
            database_favorite_models model = listDatabaseFavoriteModels.get(i);
            database_favorite_save save = new database_favorite_save(model.getQuestionId());
            listDatabaseFavoriteSave.add(save);
        }
    }
    private void checkQuestion() {
        if (integerIndex <= 0) {
            buttonPrevious.setEnabled(false);
            buttonPrevious.setBackgroundResource(R.drawable.shape_static_square_gray_light);
        } else {
            buttonPrevious.setEnabled(true);
            buttonPrevious.setBackgroundResource(R.drawable.shape_dynamic_square_primary);
        }
        if (integerIndex == listDatabaseFavoriteModels.size() - 1) {
            buttonNext.setEnabled(false);
            buttonNext.setBackgroundResource(R.drawable.shape_static_square_gray_light);
        } else {
            buttonNext.setEnabled(true);
            buttonNext.setBackgroundResource(R.drawable.shape_dynamic_square_primary);
        }
    }

    private void nextQuestion() {
        integerIndex++;
        showQuestion(integerIndex);
    }
    private void previousQuestion() {
        integerIndex--;
        showQuestion(integerIndex);
    }

    private void showKProgressDialog() {
        try {
            kProgressHUD = KProgressHUD.create(activity_favorite.this)
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
        final Dialog dialog = new Dialog(activity_favorite.this, R.style.FullHeightDialog);
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

    private void deleteQuestionFromFavoriteList(final String stringQuestionID) {
        TextView title = new TextView(activity_favorite.this);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);
        AlertDialog ald = new AlertDialog.Builder(activity_favorite.this).create();
        ald.setCanceledOnTouchOutside(false);
        ald.setMessage(getString(R.string.delete_question_ask));
        ald.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes), (dialog, which) -> {
            showKProgressDialog();
            new Thread(() -> {
                databaseFavorite.deleteQuestion(stringQuestionID);
                resetLists();
                populateDatabase();

                runOnUiThread(() -> {
                    try {
                        if (listDatabaseFavoriteModels.size() > 0) {
                            if (integerIndex >= listDatabaseFavoriteModels.size()) {
                                buttonPrevious.performClick();
                            } else {
                                showQuestion(integerIndex);
                            }
                            checkQuestion();
                        } else {
                            navigateToSecondaryPage();
                        }
                    } finally {
                        Toast.makeText(activity_favorite.this, getString(R.string.delete_question_done), Toast.LENGTH_SHORT).show();
                    }
                    dismissKProgressDialog();
                });
            }).start();
        });
        ald.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.no), (dialog, which) -> dialog.dismiss());
        ald.show();
        TextView messageText = ald.findViewById(android.R.id.message);
        assert messageText != null;
        messageText.setGravity(Gravity.CENTER);
        Button buttonPositive = ald.getButton(AlertDialog.BUTTON_POSITIVE);
        Button buttonNegative = ald.getButton(AlertDialog.BUTTON_NEGATIVE);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) buttonPositive.getLayoutParams();
        layoutParams.weight = 10;
        layoutParams.gravity = Gravity.END;
        buttonPositive.setLayoutParams(layoutParams);
        buttonNegative.setLayoutParams(layoutParams);
    }
    private void emptyFavorite() {
        TextView title = new TextView(activity_favorite.this);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);
        AlertDialog ald = new AlertDialog.Builder(activity_favorite.this).create();
        ald.setCanceledOnTouchOutside(false);
        ald.setMessage(getString(R.string.empty_favorite_list_ask));
        ald.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes), (dialog, which) -> {
            showKProgressDialog();
            new Thread(() -> {
                databaseFavorite.deleteAllQuestions();

                runOnUiThread(() -> {
                    try {
                        navigateToSecondaryPage();
                    } finally {
                        Toast.makeText(activity_favorite.this, getString(R.string.empty_favorite_list_done), Toast.LENGTH_SHORT).show();
                    }
                    dismissKProgressDialog();
                });
            }).start();
        });
        ald.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.no), (dialog, which) -> dialog.dismiss());
        ald.show();
        TextView messageText = ald.findViewById(android.R.id.message);
        assert messageText != null;
        messageText.setGravity(Gravity.CENTER);
        Button buttonPositive = ald.getButton(AlertDialog.BUTTON_POSITIVE);
        Button buttonNegative = ald.getButton(AlertDialog.BUTTON_NEGATIVE);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) buttonPositive.getLayoutParams();
        layoutParams.weight = 10;
        layoutParams.gravity = Gravity.END;
        buttonPositive.setLayoutParams(layoutParams);
        buttonNegative.setLayoutParams(layoutParams);
    }
    @SuppressLint("NonConstantResourceId")
    private void reportQuestion() {
        View view = View.inflate(activity_favorite.this, R.layout.report_view, null);
        dialogSheet = new DialogSheet(activity_favorite.this)
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

                if (stringSentReport.equals(getString(R.string.report_issue_3))) {
                    if (editTextMessage.getText().toString().equals("")) {
                        stringSentReport = getString(R.string.report_issue_null);
                    } else {
                        stringSentReport = editTextMessage.getText().toString();
                    }
                }
            } finally {
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> {
                    if (constants.isInternetActive(activity_favorite.this)) {
                        new Handler(Looper.getMainLooper()).postDelayed(() -> reportQuestion("SV [" + stringQuestionId + "]", textViewQuestion.getText().toString(), stringSentReport), 500);
                    } else {
                        messageNoInternetConnection();
                    }
                }, 100);
            }
        });
        buttonCancel.setOnClickListener(v -> dialogSheet.dismiss());
        dialogSheet.show();
    }

    private void resetLists() {
        listDatabaseFavoriteModels.clear();
        listDatabaseFavoriteSave.clear();
    }
    private void reloadButtons() {
        buttonAnswerA.setVisibility(View.GONE);
        buttonAnswerB.setVisibility(View.GONE);
        buttonAnswerC.setVisibility(View.GONE);
        buttonAnswerD.setVisibility(View.GONE);
        buttonAnswerA.setBackgroundResource(R.drawable.shape_static_square_white);
        buttonAnswerB.setBackgroundResource(R.drawable.shape_static_square_white);
        buttonAnswerC.setBackgroundResource(R.drawable.shape_static_square_white);
        buttonAnswerD.setBackgroundResource(R.drawable.shape_static_square_white);
    }
    private void setCurrentQuestion(String stringQuestion, String stringQuestionNumber, String stringAnswerA, String stringAnswerB, String stringAnswerC, String stringAnswerD) {
        textViewQuestion.setText(stringQuestion);
        textViewQuestionNumber.setText(stringQuestionNumber);
        buttonAnswerA.setText(stringAnswerA);
        buttonAnswerB.setText(stringAnswerB);
        buttonAnswerC.setText(stringAnswerC);
        buttonAnswerD.setText(stringAnswerD);
    }
    private void checkCorrectAnswer(String stringCorrectAnswer) {
        if (buttonAnswerA.getText().equals(stringCorrectAnswer)) {
            buttonAnswerA.setBackgroundResource(R.drawable.shape_dynamic_square_green_dark);
        }
        if (buttonAnswerB.getText().equals(stringCorrectAnswer)) {
            buttonAnswerB.setBackgroundResource(R.drawable.shape_dynamic_square_green_dark);
        }
        if (buttonAnswerC.getText().equals(stringCorrectAnswer)) {
            buttonAnswerC.setBackgroundResource(R.drawable.shape_dynamic_square_green_dark);
        }
        if (buttonAnswerD.getText().equals(stringCorrectAnswer)) {
            buttonAnswerD.setBackgroundResource(R.drawable.shape_dynamic_square_green_dark);
        }
    }
    private void checkButtonsVisibility() {
        if (!buttonAnswerA.getText().toString().equals("")) {
            buttonAnswerA.setVisibility(View.VISIBLE);
        }
        if (!buttonAnswerB.getText().toString().equals("")) {
            buttonAnswerB.setVisibility(View.VISIBLE);
        }
        if (!buttonAnswerC.getText().toString().equals("")) {
            buttonAnswerC.setVisibility(View.VISIBLE);
        }
        if (!buttonAnswerD.getText().toString().equals("")) {
            buttonAnswerD.setVisibility(View.VISIBLE);
        }
    }

    private boolean checkIfQuestionHasPassed(int id) {
        boolean booleanIsPassed = true;
        try {
            if (stringArrayQuestions[id][0].equals("1")) {
                Log.i(constants.logDeveloperInformation, "QUESTION_HAS_PASSED");
            }
        } catch (Exception ex) {
            Log.i(constants.logDeveloperInformation, ex.toString());
            booleanIsPassed = false;
        }
        return booleanIsPassed;
    }
    private void setQuestionAsPassed(int id, String alternativeA, String alternativeB, String alternativeC, String alternativeD) {
        stringArrayQuestions[id][0] = "1";
        stringArrayQuestions[id][10] = alternativeA;
        stringArrayQuestions[id][11] = alternativeB;
        stringArrayQuestions[id][12] = alternativeC;
        stringArrayQuestions[id][13] = alternativeD;
    }

    @SuppressLint("DefaultLocale")
    private void showQuestion(int integerQuestionNumber) {
        reloadButtons();

        String stringSavedQuestionId = listDatabaseFavoriteSave.get(integerQuestionNumber).toString();
        database_favorite_models data = databaseFavorite.getQuestion(stringSavedQuestionId);
        stringQuestionId = data.getQuestionId();
        String stringQuestion = data.getQuestionText();
        stringImageAData = databaseImages.getImageA(stringQuestionId);
        stringImageBData = databaseImages.getImageB(stringQuestionId);
        String stringAnswerA = data.getAnswerA();
        String stringAnswerB = data.getAnswerB();
        String stringAnswerC = data.getAnswerC();
        String stringAnswerD = data.getAnswerD();
        String stringCorrectAnswer = data.getCorrectAnswer();
        String stringSortable = data.getSortable();

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

        String stringQuestionNumber = String.format(Locale.ENGLISH, "%d/%d", integerQuestionNumber + 1, listDatabaseFavoriteModels.size());
        setCurrentQuestion(stringQuestion, stringQuestionNumber, stringAnswerA, stringAnswerB, stringAnswerC, stringAnswerD);

        if (checkIfQuestionHasPassed(integerQuestionNumber)) {
            buttonAnswerA.setText(stringArrayQuestions[integerQuestionNumber][10]);
            buttonAnswerB.setText(stringArrayQuestions[integerQuestionNumber][11]);
            buttonAnswerC.setText(stringArrayQuestions[integerQuestionNumber][12]);
            buttonAnswerD.setText(stringArrayQuestions[integerQuestionNumber][13]);
        } else {
            if (stringSortable.equals("Yes")) {
                String[] str = {stringAnswerA, stringAnswerB, stringAnswerC, stringAnswerD};
                Button[] buttons = new Button[4];
                buttons[0] = findViewById(R.id.buttonAnswerA);
                buttons[1] = findViewById(R.id.buttonAnswerB);
                buttons[2] = findViewById(R.id.buttonAnswerC);
                buttons[3] = findViewById(R.id.buttonAnswerD);
                List<Integer> setNames = new ArrayList<>();
                for (int k = 0; k < 4; k++) {
                    int x;
                    do {
                        Random r = new Random();
                        x = r.nextInt(str.length);
                    } while (setNames.contains(x));
                    setNames.add(x);
                    buttons[k].setText(str[x]);
                }
            } else {
                buttonAnswerA.setText(stringAnswerA);
                buttonAnswerB.setText(stringAnswerB);
                buttonAnswerC.setText(stringAnswerC);
                buttonAnswerD.setText(stringAnswerD);
            }
        }

        checkCorrectAnswer(stringCorrectAnswer);
        checkButtonsVisibility();

        setQuestionAsPassed(integerQuestionNumber, buttonAnswerA.getText().toString(), buttonAnswerB.getText().toString(), buttonAnswerC.getText().toString(), buttonAnswerD.getText().toString());
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
                    Toast.makeText(activity_favorite.this, getString(R.string.reported_successfully), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity_favorite.this, getString(R.string.unknown_error_occurred), Toast.LENGTH_SHORT).show();
                    Log.e(constants.logDeveloperError, stringError.get());
                }
            });
        }).start();
    }
}