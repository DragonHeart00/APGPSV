package com.acodigo.godkantsv.database_images;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.acodigo.godkantsv.common.constants;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.Objects;

public class database_images_locator extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "database_images.db";
    private final Context context;

    public database_images_locator(Context context) {
        super(context, DATABASE_NAME, Objects.requireNonNull(context.getExternalFilesDir(null)).getAbsolutePath(), null, constants.sharedPreferencesLocalDatabaseVersion);
        this.context = context;
        setForcedUpgrade();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e(constants.logDeveloperInformation, "Updating database from " + oldVersion + " to " + newVersion);
        context.deleteDatabase(DATABASE_NAME);
    }
}