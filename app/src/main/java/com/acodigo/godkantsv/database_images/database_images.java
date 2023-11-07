package com.acodigo.godkantsv.database_images;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class database_images {

    private final SQLiteOpenHelper sqLiteOpenHelper;
    private SQLiteDatabase sqLiteDatabase;
    private static database_images databaseImages;

    private database_images(Context context) {
        this.sqLiteOpenHelper = new database_images_locator(context);
    }

    public static database_images getInstance(Context context) {
        if (databaseImages == null) {
            databaseImages = new database_images(context);
        }
        return databaseImages;
    }

    public void openDatabase() {
        this.sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
    }
    public void closeDatabase() {
        if (sqLiteDatabase != null) {
            this.sqLiteDatabase.close();
        }
    }

    public String getImageA(String image) {
        openDatabase();
        String data = null;
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT Image1 FROM Images WHERE Id = ?", new String[]{image});
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            data = cursor.getString(cursor.getColumnIndex("Image1"));
        }
        cursor.close();
        closeDatabase();
        return data;
    }
    public String getImageB(String image) {
        openDatabase();
        String data = null;
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT Image2 FROM Images WHERE Id = ?", new String[]{image});
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            data = cursor.getString(cursor.getColumnIndex("Image2"));
        }
        cursor.close();
        closeDatabase();
        return data;
    }
}