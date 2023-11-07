package com.acodigo.godkantsv;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.acodigo.godkantsv.activities.activity_main;
import com.acodigo.godkantsv.activities.activity_secondary_page;
import com.acodigo.godkantsv.common.constants;
import com.acodigo.godkantsv.database_favorite.database_favorite;
import com.acodigo.godkantsv.database_favorite.database_favorite_models;
import com.acodigo.godkantsv.database_favorite.database_favorite_save;
import com.acodigo.godkantsv.database_images.database_images;
import com.acodigo.godkantsv.database_questions.database_questions;
import com.acodigo.godkantsv.database_questions.database_questions_models;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import pl.droidsonroids.gif.GifImageView;

public class activity_splash_screen extends AppCompatActivity {

    String stringDatabaseName, stringDatabaseUsername, stringDatabaseUsernamePassword;
    String stringDeviceId, stringDeviceCodeId;
    String stringSharedPreferencesCode;

    List<String> listDatabaseTestIds;

    Connection connection;

    GifImageView gifImageViewProgressbarMain;

    database_questions databaseTest;
    database_images databaseImages;
    database_favorite databaseFavorite;

    List<database_questions_models> listDatabaseTestModels;
    List<database_favorite_models> listDatabaseFavoriteModels;
    List<database_favorite_save> listDatabaseFavoriteSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getSharedPreferences();
        getDisplaySize();

        setAuthorizations();
        setConstants();
        setContents();
        setIdentifiers();

        checkInternetConnection();
    }
    @Override
    public void onBackPressed() {
        onAppExit();
    }

    private void getSharedPreferences() {
        stringSharedPreferencesCode = constants.sharedPreferencesGetUserCode(activity_splash_screen.this);
    }
    private void getDisplaySize() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float floatHeight = displayMetrics.heightPixels / displayMetrics.density;
        double doubleHeight = 0.13392857142857142 * floatHeight;
        final float scale = getResources().getDisplayMetrics().density;
        int pixels = (int) ((int) doubleHeight * scale + 0.5f);

        constants.sharedPreferencesSetDisplayHeight(activity_splash_screen.this, Math.max(pixels, 180));
    }

    private void setAuthorizations() {
        stringDatabaseName = constants.authorizationSqlDatabaseURL;
        stringDatabaseUsername = constants.authorizationSqlDatabaseUsername;
        stringDatabaseUsernamePassword = constants.authorizationSqlDatabasePassword;
    }
    @SuppressLint("HardwareIds")
    private void setConstants() {
        try {
            constants.sharedPreferencesSetDeviceId(activity_splash_screen.this, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
            constants.sharedPreferencesSetDeviceCodeId(activity_splash_screen.this, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID) + "-Android-SV");
        } catch (Exception ex) {
            constants.sharedPreferencesSetDeviceId(activity_splash_screen.this, android.os.Build.MODEL);
            constants.sharedPreferencesSetDeviceCodeId(activity_splash_screen.this, android.os.Build.MODEL + "-Android-SV");
        }
    }
    private void setContents() {
        gifImageViewProgressbarMain = findViewById(R.id.gifImageViewProgressbarMain);
    }
    private void setIdentifiers(){
        stringDeviceId = constants.sharedPreferencesGetDeviceId(activity_splash_screen.this);
        stringDeviceCodeId = constants.sharedPreferencesGetDeviceCodeId(activity_splash_screen.this);
    }

    private void setDatabase() {
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> new Thread(() -> {
            boolean booleanIsSuccess = true;
            try {
                databaseTest = database_questions.getInstance(this);
                databaseImages = database_images.getInstance(this);

                databaseImages.openDatabase();
            } catch (Exception ex) {
                ex.printStackTrace();
                booleanIsSuccess = false;
            }

            boolean finalBooleanIsSuccess = booleanIsSuccess;
            runOnUiThread(() -> {
                if (finalBooleanIsSuccess) {
                    try {
                        constants.sharedPreferencesSetLocalDatabaseFolders(activity_splash_screen.this, databaseTest.getFolders());
                        constants.sharedPreferencesSetLocalDatabaseQuestions(activity_splash_screen.this, databaseTest.getQuestions());
                    } finally {
                        new Thread(() -> {
                            checkDatabase();

                            runOnUiThread(this::checkNavigationPage);
                        }).start();
                    }
                } else {
                    onAppExit();
                }
            });
        }).start(), 200);
    }
    private void checkDatabase() {
        boolean booleanIsSuccess = true;
        try {
            listDatabaseTestModels = new ArrayList<>();
            listDatabaseFavoriteModels = new ArrayList<>();
            listDatabaseFavoriteSave = new ArrayList<>();

            listDatabaseTestIds = new ArrayList<>();

            listDatabaseTestModels = databaseTest.getAllQuestions(4, 0);

            databaseFavorite = new database_favorite(this);
            listDatabaseFavoriteModels = databaseFavorite.getAllQuestions();

            for (int i = 0; i < listDatabaseFavoriteModels.size(); i++) {
                database_favorite_models model = listDatabaseFavoriteModels.get(i);
                database_favorite_save save = new database_favorite_save(model.getQuestionId());
                listDatabaseFavoriteSave.add(save);
            }

            for (int i = 0; i < listDatabaseTestModels.size(); i++) {
                listDatabaseTestIds.add(listDatabaseTestModels.get(i).getId());
            }
        } catch (Exception ex) {
            Log.e(constants.logDeveloperError, ex.toString());
            booleanIsSuccess = false;
            onAppExit();
        } finally {
            if (booleanIsSuccess) {
                final int size = listDatabaseFavoriteModels.size();
                if (size > 0) {
                    for (int i = 0; i < size; i++) {
                        String stringSavedQuestionId = listDatabaseFavoriteSave.get(i).toString();
                        database_favorite_models data = databaseFavorite.getQuestion(stringSavedQuestionId);
                        String stringQuestionId = data.getQuestionId();

                        if (!listDatabaseTestIds.contains(stringQuestionId)) {
                            databaseFavorite.deleteQuestion(stringSavedQuestionId);
                        }
                    }
                }
            }
        }
    }

    private void checkInternetConnection() {
        if (constants.isInternetActive(activity_splash_screen.this)) {
            checkStatus();
        } else {
            messageAppNeedsInternetConnection();
        }
    }
    private void checkNavigationPage() {
        if (!constants.sharedPreferencesGetIsAvailable(activity_splash_screen.this)) {
            checkAtStart();
        } else {
            navigateToSecondaryPage();
        }
    }

    private void navigateToMain() {
        try {
            constants.sharedPreferencesSetUserCode(activity_splash_screen.this, "");
        } finally {
            startActivity(new Intent(activity_splash_screen.this, activity_main.class));
            finish();
        }
    }
    private void navigateToSecondaryPage() {
        try {
            constants.sharedPreferencesSetIsFullTestMode(activity_splash_screen.this, true);
        } finally {
            startActivity(new Intent(activity_splash_screen.this, activity_secondary_page.class));
            finish();
        }
    }

    private void onAppExit() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    private void messageAppNeedsInternetConnection() {
        try {
            gifImageViewProgressbarMain.setVisibility(View.GONE);
            AlertDialog.Builder builder = new AlertDialog.Builder(activity_splash_screen.this);
            builder.setTitle(getString(R.string.information));
            builder.setMessage(getString(R.string.application_warning_internet));
            builder.setPositiveButton(getString(R.string.okay), (dialog, which) -> onAppExit());
            AlertDialog alert = builder.create();
            alert.setCanceledOnTouchOutside(false);
            alert.setCancelable(false);
            alert.show();
        } catch (Exception ex) {
            Log.e(constants.logDeveloperError, ex.toString());
        }
    }
    private void messageAppNeedsUpdate() {
        try {
            gifImageViewProgressbarMain.setVisibility(View.GONE);
            AlertDialog.Builder builder = new AlertDialog.Builder(activity_splash_screen.this);
            builder.setTitle(getString(R.string.update));
            builder.setMessage(getString(R.string.update_available));
            builder.setPositiveButton(getString(R.string.update_now), (dialog, which) -> {
                final String stringAppPackageName = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + stringAppPackageName)));
                } catch (android.content.ActivityNotFoundException a) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + stringAppPackageName)));
                }
            });
            builder.setNegativeButton(getString(R.string.exit), (dialog, which) -> onAppExit());
            AlertDialog alert = builder.create();
            alert.setCanceledOnTouchOutside(false);
            alert.setCancelable(false);
            alert.show();
        } catch (Exception ex) {
            Log.e(constants.logDeveloperError, ex.toString());
        }
    }
    private void messageAppIsDisabled(String message) {
        try {
            gifImageViewProgressbarMain.setVisibility(View.GONE);
            AlertDialog.Builder builder = new AlertDialog.Builder(activity_splash_screen.this);
            builder.setTitle(getString(R.string.information));
            builder.setMessage(message);
            builder.setPositiveButton(getString(R.string.okay), (dialog, which) -> onAppExit());
            AlertDialog alert = builder.create();
            alert.setCanceledOnTouchOutside(false);
            alert.setCancelable(false);
            alert.show();
        } catch (Exception ex) {
            Log.e(constants.logDeveloperError, ex.toString());
        }
    }
    private void messageShowError(String message) {
        try {
            gifImageViewProgressbarMain.setVisibility(View.GONE);
            AlertDialog.Builder builder = new AlertDialog.Builder(activity_splash_screen.this);
            builder.setMessage(message);
            builder.setCancelable(false);
            builder.setPositiveButton(getString(R.string.close), (dialog, which) -> onAppExit());
            AlertDialog alert = builder.create();
            alert.setCancelable(false);
            alert.setCanceledOnTouchOutside(false);
            alert.show();
        } catch (Exception ex) {
            Log.e(constants.logDeveloperError, ex.toString());
        }
    }

    private BigDecimal bigDecimalGetDateFromInternet() throws IOException {
        String TIME_SERVER = "time-a.nist.gov";
        NTPUDPClient timeClient = new NTPUDPClient();
        InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
        TimeInfo timeInfo = timeClient.getTime(inetAddress);
        long returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
        Date dateFromInternet = new Date(returnTime);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss", Locale.ENGLISH);
        String date = simpleDateFormat.format(dateFromInternet.getTime());

        return new BigDecimal(date);
    }

    private void checkStatus() {
        final String[] stringError = {""};
        final String[] stringMessage = {""};

        AtomicReference<Boolean> booleanSuccess = new AtomicReference<>(false);
        AtomicReference<Boolean> booleanStatus = new AtomicReference<>(true);
        AtomicReference<Boolean> booleanUpdate = new AtomicReference<>(false);

        new Thread(() -> {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                connection = DriverManager.getConnection(stringDatabaseName, stringDatabaseUsername, stringDatabaseUsernamePassword);
                if (connection == null) {
                    stringError[0] = getString(R.string.check_your_internet_connection);
                } else {
                    String query = "SELECT * from appGodKäntAdministration where appName = 'GodKäntAndroid'";
                    Statement stmt = connection.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if (rs.next()) {
                        if (rs.getInt("appStatus") == 0) {
                            booleanStatus.set(false);
                            stringMessage[0] = rs.getString("appMessage");
                        } else {
                            if (rs.getInt("appVersionSwedish") > constants.sharedPreferencesAppVersion) {
                                booleanUpdate.set(true);
                            } else {
                                constants.sharedPreferencesSetIsDeveloper(activity_splash_screen.this, stringDeviceId.equals(rs.getString("developerIdSwedish")));
                                constants.sharedPreferencesSetIsAvailable(activity_splash_screen.this, rs.getInt("appAvailability") == 1);
                            }
                        }
                        booleanSuccess.set(true);
                    } else {
                        booleanSuccess.set(false);
                        stringError[0] = getString(R.string.unknown_error_occurred);
                    }
                    connection.close();
                }
            } catch (Exception ex) {
                booleanSuccess.set(false);
                stringError[0] = ex.toString();
            }

            runOnUiThread(() -> {
                if (booleanSuccess.get()) {
                    if (booleanStatus.get()) {
                        if (!booleanUpdate.get()) {
                            setDatabase();
                        } else {
                            messageAppNeedsUpdate();
                        }
                    } else {
                        messageAppIsDisabled(stringMessage[0]);
                    }
                } else {
                    messageShowError(stringError[0]);
                }
            });
        }).start();
    }
    private void checkAtStart() {
        AtomicReference<Boolean> booleanSuccess = new AtomicReference<>(false);

        new Thread(() -> {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                connection = DriverManager.getConnection(stringDatabaseName, stringDatabaseUsername, stringDatabaseUsernamePassword);
                if (connection == null) {
                    booleanSuccess.set(false);
                } else {
                    BigDecimal bigDecimalDate = bigDecimalGetDateFromInternet();

                    String query = "SELECT * from appGodKäntCodes where deviceId = '" + stringDeviceCodeId + "' and timeCode > '" + bigDecimalDate + "'";
                    Statement stmt = connection.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if (rs.next()) {
                        constants.sharedPreferencesSetUserTime(activity_splash_screen.this, rs.getString("timeDisplay"));
                        booleanSuccess.set(true);
                    } else {
                        booleanSuccess.set(false);
                    }
                    connection.close();
                }
            } catch (Exception ex) {
                booleanSuccess.set(false);
            }

            runOnUiThread(() -> {
                if (booleanSuccess.get()) {
                    navigateToSecondaryPage();
                } else {
                    navigateToMain();
                }
            });
        }).start();
    }
}