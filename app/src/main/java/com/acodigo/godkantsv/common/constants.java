package com.acodigo.godkantsv.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import androidx.preference.PreferenceManager;

import com.acodigo.godkantsv.database_questions.database_questions_models;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class constants {
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor sharedPreferencesEditor;

    public static final String logDeveloperError = "DEVELOPER_ERROR";
    public static final String logDeveloperInformation = "DEVELOPER_INFORMATION";

    public static final Integer sharedPreferencesAppVersion = 46;
    public static final Integer sharedPreferencesLocalDatabaseVersion = 34;

    public static final String authorizationSqlDatabaseURL = "jdbc:jtds:sqlserver://acodigo.database.windows.net:1433/acodigo";
    public static final String authorizationSqlDatabaseUsername = "GodKänt";
    public static final String authorizationSqlDatabasePassword = "@H-@Mg2g?qbw_v8r2%XNZXQ8UeY#XTw@}";

    public static final String sharedPreferencesDisplayHeight = "sharedPreferencesDisplayHeight";
    public static Integer sharedPreferencesGetDisplayHeight(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(constants.sharedPreferencesDisplayHeight, 180);
    }
    public static void sharedPreferencesSetDisplayHeight(Context context, Integer integer) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putInt(constants.sharedPreferencesDisplayHeight, integer);
        sharedPreferencesEditor.apply();
    }

    public static final String sharedPreferencesDeviceId = "sharedPreferencesDeviceId";
    public static String sharedPreferencesGetDeviceId(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(constants.sharedPreferencesDeviceId, "");
    }
    public static void sharedPreferencesSetDeviceId(Context context, String string) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString(constants.sharedPreferencesDeviceId, string);
        sharedPreferencesEditor.apply();
    }

    public static final String sharedPreferencesDeviceCodeId = "sharedPreferencesDeviceCodeId";
    public static String sharedPreferencesGetDeviceCodeId(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(constants.sharedPreferencesDeviceCodeId, "");
    }
    public static void sharedPreferencesSetDeviceCodeId(Context context, String string) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString(constants.sharedPreferencesDeviceCodeId, string);
        sharedPreferencesEditor.apply();
    }

    public static final String sharedPreferencesIsFullTestMode = "sharedPreferencesIsFullTestMode";
    public static boolean sharedPreferencesGetIsFullTestMode(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(constants.sharedPreferencesIsFullTestMode, false);
    }
    public static void sharedPreferencesSetIsFullTestMode(Context context, Boolean bool) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putBoolean(constants.sharedPreferencesIsFullTestMode, bool);
        sharedPreferencesEditor.apply();
    }

    public static final String sharedPreferencesIsAvailable = "sharedPreferencesIsAvailable";
    public static boolean sharedPreferencesGetIsAvailable(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(constants.sharedPreferencesIsAvailable, false);
    }
    public static void sharedPreferencesSetIsAvailable(Context context, Boolean bool) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putBoolean(constants.sharedPreferencesIsAvailable, bool);
        sharedPreferencesEditor.apply();
    }

    public static final String sharedPreferencesIsDeveloper = "sharedPreferencesIsDeveloper";
    public static boolean sharedPreferencesGetIsDeveloper(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(constants.sharedPreferencesIsDeveloper, false);
    }
    public static void sharedPreferencesSetIsDeveloper(Context context, Boolean bool) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putBoolean(constants.sharedPreferencesIsDeveloper, bool);
        sharedPreferencesEditor.apply();
    }

    public static final String sharedPreferencesLocalDatabaseFolders = "sharedPreferencesLocalDatabaseFolders";
    public static Integer sharedPreferencesGetLocalDatabaseFolders(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(constants.sharedPreferencesLocalDatabaseFolders, 0);
    }
    public static void sharedPreferencesSetLocalDatabaseFolders(Context context, Integer integer) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putInt(constants.sharedPreferencesLocalDatabaseFolders, integer);
        sharedPreferencesEditor.apply();
    }

    public static final String sharedPreferencesLocalDatabaseQuestions = "sharedPreferencesLocalDatabaseQuestions";
    public static Integer sharedPreferencesGetLocalDatabaseQuestions(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(constants.sharedPreferencesLocalDatabaseQuestions, 0);
    }
    public static void sharedPreferencesSetLocalDatabaseQuestions(Context context, Integer integer) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putInt(constants.sharedPreferencesLocalDatabaseQuestions, integer);
        sharedPreferencesEditor.apply();
    }

    public static final String sharedPreferencesCode = "sharedPreferencesCode";
    public static String sharedPreferencesGetUserCode(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(constants.sharedPreferencesCode, "");
    }
    public static void sharedPreferencesSetUserCode(Context context, String string) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString(constants.sharedPreferencesCode, string);
        sharedPreferencesEditor.apply();
    }

    public static final String sharedPreferencesUserTime = "sharedPreferencesUserTime";
    public static String sharedPreferencesGetUserTime(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(constants.sharedPreferencesUserTime, "");
    }
    public static void sharedPreferencesSetUserTime(Context context, String string) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString(constants.sharedPreferencesUserTime, string);
        sharedPreferencesEditor.apply();
    }

    public static final String sharedPreferencesTestMode = "sharedPreferencesTestMode";
    public static String sharedPreferencesGetTestMode(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(constants.sharedPreferencesTestMode, "");
    }
    public static void sharedPreferencesSetTestMode(Context context, String string) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString(constants.sharedPreferencesTestMode, string);
        sharedPreferencesEditor.apply();
    }

    public static final String sharedPreferencesTestName = "sharedPreferencesTestName";
    public static String sharedPreferencesGetTestName(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(constants.sharedPreferencesTestName, "");
    }
    public static void sharedPreferencesSetTestName(Context context, String string) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString(constants.sharedPreferencesTestName, string);
        sharedPreferencesEditor.apply();
    }

    public static final String sharedPreferencesTestId = "sharedPreferencesTestId";
    public static Integer sharedPreferencesGetTestId(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(constants.sharedPreferencesTestId, 0);
    }
    public static void sharedPreferencesSetTestId(Context context, Integer integer) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putInt(constants.sharedPreferencesTestId, integer);
        sharedPreferencesEditor.apply();
    }

    public static final String sharedPreferencesLatestTestArrayA = "sharedPreferencesLatestTestArrayA";
    public static List<database_questions_models> sharedPreferencesGetLatestTestArrayA(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        List<database_questions_models> list = null;
        String serializedObject = sharedPreferences.getString(constants.sharedPreferencesLatestTestArrayA, "");
        if (serializedObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<database_questions_models>>() {
            }.getType();
            list = gson.fromJson(serializedObject, type);
        }
        return list;
    }
    public static void sharedPreferencesSetLatestTestArrayA(Context context, List<database_questions_models> list) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferencesEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        sharedPreferencesEditor.putString(constants.sharedPreferencesLatestTestArrayA, json);
        sharedPreferencesEditor.apply();
    }

    public static final String sharedPreferencesLatestTestArrayB = "sharedPreferencesLatestTestArrayB";
    public static String[][] sharedPreferencesGetLatestTestArrayB(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String[][] string = null;
        String serializedObject = sharedPreferences.getString(constants.sharedPreferencesLatestTestArrayB, "");
        if (serializedObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<String[][]>() {
            }.getType();
            string = gson.fromJson(serializedObject, type);
        }
        return string;
    }
    public static void sharedPreferencesSetLatestTestArrayB(Context context, String[][] string) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferencesEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(string);
        sharedPreferencesEditor.putString(constants.sharedPreferencesLatestTestArrayB, json);
        sharedPreferencesEditor.apply();
    }

    public static final String sharedPreferencesLatestTestArrayC = "sharedPreferencesLatestTestArrayC";
    public static ArrayList<String> sharedPreferencesGetLatestTestArrayC(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        ArrayList<String> string = null;
        String serializedObject = sharedPreferences.getString(constants.sharedPreferencesLatestTestArrayC, "");
        if (serializedObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            string = gson.fromJson(serializedObject, type);
        }
        return string;
    }
    public static void sharedPreferencesSetLatestTestArrayC(Context context, ArrayList<String> string) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferencesEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(string);
        sharedPreferencesEditor.putString(constants.sharedPreferencesLatestTestArrayC, json);
        sharedPreferencesEditor.apply();
    }

    public static final String sharedPreferencesLatestTestArrayD = "sharedPreferencesLatestTestArrayD";
    public static ArrayList<String> sharedPreferencesGetLatestTestArrayD(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        ArrayList<String> string = null;
        String serializedObject = sharedPreferences.getString(constants.sharedPreferencesLatestTestArrayD, "");
        if (serializedObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            string = gson.fromJson(serializedObject, type);
        }
        return string;
    }
    public static void sharedPreferencesSetLatestTestArrayD(Context context, ArrayList<String> string) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferencesEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(string);
        sharedPreferencesEditor.putString(constants.sharedPreferencesLatestTestArrayD, json);
        sharedPreferencesEditor.apply();
    }

    public static final String sharedPreferencesLatestTestArrayE = "sharedPreferencesLatestTestArrayE";
    public static ArrayList<String> sharedPreferencesGetLatestTestArrayE(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        ArrayList<String> string = null;
        String serializedObject = sharedPreferences.getString(constants.sharedPreferencesLatestTestArrayE, "");
        if (serializedObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            string = gson.fromJson(serializedObject, type);
        }
        return string;
    }
    public static void sharedPreferencesSetLatestTestArrayE(Context context, ArrayList<String> string) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferencesEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(string);
        sharedPreferencesEditor.putString(constants.sharedPreferencesLatestTestArrayE, json);
        sharedPreferencesEditor.apply();
    }

    public static final String sharedPreferencesLatestLocalDatabaseVersion = "sharedPreferencesLatestLocalDatabaseVersion";
    public static Integer sharedPreferencesGetLatestLocalDatabaseVersion(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(constants.sharedPreferencesLatestLocalDatabaseVersion, 0);
    }
    public static void sharedPreferencesSetLatestLocalDatabaseVersion(Context context, Integer integer) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putInt(constants.sharedPreferencesLatestLocalDatabaseVersion, integer);
        sharedPreferencesEditor.apply();
    }

    public static final String sharedPreferencesTestStatus = "sharedPreferencesTestStatus";
    public static String sharedPreferencesGetTestStatus(Context context, String string) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(constants.sharedPreferencesTestStatus + "[" + string + "]", "");
    }
    public static void sharedPreferencesSetTestStatus(Context context, String stringA, String stringB) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString(constants.sharedPreferencesTestStatus + "[" + stringA + "]", stringB);
        sharedPreferencesEditor.apply();
    }

    public static final String sharedPreferencesLatestResult = "sharedPreferencesLatestResult";
    public static Integer sharedPreferencesGetTestResult(Context context, String string) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(constants.sharedPreferencesLatestResult + "[" + string + "]", 0);
    }
    public static void sharedPreferencesSetTestResult(Context context, String string, Integer integer) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putInt(constants.sharedPreferencesLatestResult + "[" + string + "]", integer);
        sharedPreferencesEditor.apply();
    }

    public static boolean isInternetActive(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] networks = Objects.requireNonNull(cm).getAllNetworks();
        boolean hasInternet = false;
        if (networks.length > 0) {
            for (Network network : networks) {
                NetworkCapabilities nc = cm.getNetworkCapabilities(network);
                if (Objects.requireNonNull(nc).hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))
                    hasInternet = true;
            }
        }
        return hasInternet;
    }
}