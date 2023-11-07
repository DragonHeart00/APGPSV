package com.acodigo.godkantsv.activities;

import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.net.Uri;

import com.acodigo.godkantsv.R;
import com.acodigo.godkantsv.common.constants;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import pl.droidsonroids.gif.GifImageView;

public class activity_main extends AppCompatActivity {

    Button buttonSignIn, buttonFreeTest, buttonAbout, buttonSubscribe;
    EditText editTextCode;

    String stringDatabaseName, stringDatabaseUsername, stringDatabaseUsernamePassword;
    String stringDeviceId, stringDeviceCodeId;
    String stringCode;
    String stringSharedPreferencesCode;

    Connection connection;

    GifImageView gifImageViewProgressBarSignIn;

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setAuthorizations();
        setContents();
        setIdentifiers();
    }
    @Override
    public void onBackPressed() {
        onAppExit();
    }

    private void setAuthorizations() {
        stringDatabaseName = constants.authorizationSqlDatabaseURL;
        stringDatabaseUsername = constants.authorizationSqlDatabaseUsername;
        stringDatabaseUsernamePassword = constants.authorizationSqlDatabasePassword;
    }
    private void setContents() {
        editTextCode = findViewById(R.id.editTextCode);
        buttonSignIn = findViewById(R.id.buttonSignIn);
        buttonFreeTest = findViewById(R.id.buttonFreeTest);
        buttonAbout = findViewById(R.id.buttonAbout);
        buttonSubscribe = findViewById(R.id.buttonSubscribe);
        gifImageViewProgressBarSignIn = findViewById(R.id.gifImageViewProgressBarSignIn);

        editTextCode.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        checkCredentials();
                        return true;
                    default:
                        break;
                }
            }
            return false;
        });

        buttonSignIn.setOnClickListener(v -> checkCredentials());
        buttonFreeTest.setOnClickListener(v -> navigateToSecondaryPageTrial());
        buttonAbout.setOnClickListener(v -> startActivity(new Intent(activity_main.this, activity_about.class)));
        buttonSubscribe.setOnClickListener(v -> messageShowSubscribe());

        editTextCode.setText(stringSharedPreferencesCode);
    }
    private void setIdentifiers(){
        stringDeviceId = constants.sharedPreferencesGetDeviceId(activity_main.this);
        stringDeviceCodeId = constants.sharedPreferencesGetDeviceCodeId(activity_main.this);
    }

    private void messageShowSubscribe() {
        try {
            final Dialog dialog = new Dialog(activity_main.this, R.style.FullHeightDialog);
            dialog.setContentView(R.layout.dialog_subscribe);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(false);

            Button buttonCall = dialog.findViewById(R.id.buttonCall);
            Button buttonEmail = dialog.findViewById(R.id.buttonEmail);
            Button buttonBack = dialog.findViewById(R.id.buttonClose);

            buttonCall.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + getString(R.string.support_mobile_phone)));
                startActivity(intent);
            });
            buttonEmail.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", getString(R.string.support_email_address), null));
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_language));
                startActivity(Intent.createChooser(intent, getString(R.string.choose_alternative_colon)));
            });
            buttonBack.setOnClickListener(v -> dialog.dismiss());

            dialog.show();
        } catch (Exception ex) {
            Log.e(constants.logDeveloperError, ex.toString());
        }
    }

    private void checkCredentials() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
        } catch (Exception e) {
            Log.e(constants.logDeveloperError, e.toString());
        } finally {
            if (editTextCode.getText().length() == 6) {
                if (constants.isInternetActive(activity_main.this)) {
                    stringCode = editTextCode.getText().toString();
                    checkLogIn(stringCode);
                } else {
                    Toast.makeText(activity_main.this, getString(R.string.check_your_internet_connection), Toast.LENGTH_SHORT).show();
                }
            } else if (editTextCode.getText().length() == 0) {
                Toast.makeText(activity_main.this, getString(R.string.please_enter_a_code_first), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity_main.this, getString(R.string.please_enter_a_valid_code_first), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onAppExit() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    private void enableView() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    private void disableView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void enableContents() {
        enableView();

        buttonSignIn.setClickable(true);
        buttonFreeTest.setClickable(true);
        buttonSubscribe.setClickable(true);

        editTextCode.setClickable(true);
        editTextCode.setEnabled(true);

        gifImageViewProgressBarSignIn.setVisibility(View.GONE);
    }
    private void disableContents() {
        disableView();

        buttonSignIn.setClickable(false);
        buttonFreeTest.setClickable(false);
        buttonSubscribe.setClickable(false);

        editTextCode.setClickable(false);
        editTextCode.setEnabled(false);

        gifImageViewProgressBarSignIn.setVisibility(View.VISIBLE);
    }

    private void resetUserData() {
        try {
            editTextCode.setText(null);
            constants.sharedPreferencesSetUserCode(activity_main.this, "");
        } finally {
            enableContents();
        }
    }

    private void navigateToSecondaryPage() {
        try {
            constants.sharedPreferencesSetIsFullTestMode(activity_main.this, true);
        } finally {
            startActivity(new Intent(activity_main.this, activity_secondary_page.class));
            finish();
        }
    }
    private void navigateToSecondaryPageTrial() {
        try {
            constants.sharedPreferencesSetIsFullTestMode(activity_main.this, false);
        } finally {
            startActivity(new Intent(activity_main.this, activity_secondary_page_trial.class));
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
    private Date dateGetDateFromInternet() throws IOException {
        String TIME_SERVER = "time-a.nist.gov";
        NTPUDPClient timeClient = new NTPUDPClient();
        InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
        TimeInfo timeInfo = timeClient.getTime(inetAddress);
        long returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();

        return new Date(returnTime);
    }

    private void checkLogIn(String stringUserCode) {
        disableContents();

        final String[] stringMessage = new String[1];

        AtomicReference<Boolean> booleanSuccess = new AtomicReference<>(false);
        AtomicReference<Boolean> booleanNew = new AtomicReference<>(false);

        new Thread(() -> {
            if (stringUserCode.trim().equals("")) {
                stringMessage[0] = getString(R.string.please_enter_a_code_first);
            } else {
                try {
                    Class.forName("net.sourceforge.jtds.jdbc.Driver");
                    connection = DriverManager.getConnection(stringDatabaseName, stringDatabaseUsername, stringDatabaseUsernamePassword);
                    if (connection == null) {
                        stringMessage[0] = getString(R.string.check_your_internet_connection);
                    } else {
                        Date dateFromInternet = dateGetDateFromInternet();
                        BigDecimal bigDecimalDate = bigDecimalGetDateFromInternet();

                        String query = "SELECT * from appGodKäntCodes where userCode = '" + stringUserCode + "'";
                        Statement stmt = connection.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                        if (rs.next()) {
                            String query1 = "SELECT * from appGodKäntCodes where userCode = '" + stringUserCode + "' and timeCode > '" + bigDecimalDate + "' and timeCode != '' and deviceId = '" + stringDeviceCodeId + "'";
                            Statement stmt1 = connection.createStatement();
                            ResultSet rs1 = stmt1.executeQuery(query1);
                            if (rs1.next()) {
                                constants.sharedPreferencesSetUserTime(activity_main.this, rs.getString("timeDisplay"));
                                stringMessage[0] = getString(R.string.login_succeed);
                                booleanSuccess.set(true);
                            } else {
                                String query2 = "SELECT * from appGodKäntCodes where userCode = '" + stringUserCode + "' and timeCode > '" + bigDecimalDate + "' and timeCode != '' and deviceId != '" + stringDeviceCodeId + "'";
                                Statement stmt2 = connection.createStatement();
                                ResultSet rs2 = stmt2.executeQuery(query2);
                                if (rs2.next()) {
                                    stringMessage[0] = getString(R.string.code_has_been_used_on_another_device);
                                    booleanSuccess.set(false);
                                } else {
                                    String query3 = "SELECT * from appGodKäntCodes where userCode = '" + stringUserCode + "' and timeCode < '" + bigDecimalDate + "' and timeCode IS NOT NULL";
                                    Statement stmt3 = connection.createStatement();
                                    ResultSet rs3 = stmt3.executeQuery(query3);
                                    if (rs3.next()) {
                                        stringMessage[0] = getString(R.string.code_has_expired);
                                        booleanSuccess.set(false);
                                    } else {
                                        String query4 = "SELECT * from appGodKäntCodes where userCode = '" + stringUserCode + "' and timeCode IS NULL";
                                        Statement stmt4 = connection.createStatement();
                                        ResultSet rs4 = stmt4.executeQuery(query4);
                                        if (rs4.next()) {
                                            int integerTimeGranted = rs.getInt("timeGranted");

                                            final long longNewDate = integerTimeGranted * 3600000L;
                                            dateFromInternet.setTime(dateFromInternet.getTime() + longNewDate);

                                            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd/MM/yyyy - hh:mm:ss", Locale.ENGLISH);
                                            String stringNewUserDate = simpleDateFormat1.format(dateFromInternet.getTime());

                                            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyyMMddhhmmss", Locale.ENGLISH);
                                            String stringNewDateAdmin = simpleDateFormat2.format(dateFromInternet.getTime());
                                            BigDecimal bigDecimalNewDateAdmin = new BigDecimal(stringNewDateAdmin);

                                            PreparedStatement ps = connection.prepareStatement("UPDATE appGodKäntCodes SET timeDisplay = ? ,timeCode = ? ,deviceId = ? WHERE userCode = '" + stringUserCode + "'");
                                            ps.setString(1, stringNewUserDate);
                                            ps.setBigDecimal(2, bigDecimalNewDateAdmin);
                                            ps.setString(3, stringDeviceCodeId);
                                            ps.executeUpdate();
                                            ps.close();

                                            if (integerTimeGranted > 24) {
                                                int integerFinalAdminTime = integerTimeGranted / 24;
                                                stringMessage[0] = getString(R.string.code_activated_for) + " " + integerFinalAdminTime + " " + getString(R.string.days);
                                            } else {
                                                stringMessage[0] = getString(R.string.code_activated_for) + " " + integerTimeGranted + " " + getString(R.string.hours);
                                            }

                                            constants.sharedPreferencesSetUserCode(activity_main.this, stringUserCode);
                                            constants.sharedPreferencesSetUserTime(activity_main.this, stringNewUserDate);

                                            booleanSuccess.set(true);
                                            booleanNew.set(true);
                                        }
                                    }
                                }
                            }
                        } else {
                            booleanSuccess.set(false);
                            stringMessage[0] = getString(R.string.invalid_credentials);
                        }
                        connection.close();
                    }
                } catch (Exception ex) {
                    booleanSuccess.set(false);
                    stringMessage[0] = ex.getMessage();
                }
            }

            runOnUiThread(() -> {
                if (!booleanSuccess.get()) {
                    try {
                        resetUserData();
                    } finally {
                        Toast.makeText(activity_main.this, stringMessage[0], Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (booleanNew.get()) {
                        Toast.makeText(activity_main.this, stringMessage[0], Toast.LENGTH_SHORT).show();
                    }
                    navigateToSecondaryPage();
                }
            });
        }).start();
    }
}