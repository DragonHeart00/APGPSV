package com.acodigo.godkantsv.database_questions;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class database_questions {

    private final SQLiteOpenHelper sqLiteOpenHelper;
    private SQLiteDatabase sqLiteDatabase;
    private static database_questions databaseQuestions;

    public database_questions(Context context) {
        this.sqLiteOpenHelper = new database_questions_locator(context);
    }

    public static database_questions getInstance(Context context) {
        if (databaseQuestions == null) {
            databaseQuestions = new database_questions(context);
        }
        return databaseQuestions;
    }

    public List<database_questions_models> getAllQuestions(int mode, int folder) {
        this.sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
        List<database_questions_models> listQuestions = new ArrayList<>();
        Cursor cursor;

        try {
            if (mode == 1) {
                cursor = sqLiteDatabase.rawQuery(String.format(Locale.ENGLISH, "SELECT * FROM Questions WHERE Folder = " + folder + " LIMIT %d", 70), null);
            } else if (mode == 2) {
                cursor = sqLiteDatabase.rawQuery(String.format(Locale.ENGLISH, "SELECT * FROM Questions WHERE Folder = " + folder + " ORDER BY Random() LIMIT %d", 70), null);
            } else if (mode == 3) {
                cursor = sqLiteDatabase.rawQuery(String.format(Locale.ENGLISH, "SELECT * FROM Questions ORDER BY Random() LIMIT %d", 70), null);
            } else if (mode == 4) {
                cursor = sqLiteDatabase.rawQuery("SELECT * FROM Questions", null);
            } else {
                cursor = sqLiteDatabase.rawQuery(String.format(Locale.ENGLISH, "SELECT * FROM Questions WHERE Folder = " + 1 + " LIMIT %d", 70), null);
            }
            if (cursor == null) return null;
            cursor.moveToFirst();
            do {
                String stringId = cursor.getString(cursor.getColumnIndex("Id"));
                String stringQuestion = cursor.getString(cursor.getColumnIndex("Question"));
                String stringAnswerA = cursor.getString(cursor.getColumnIndex("AnswerA"));
                String stringAnswerB = cursor.getString(cursor.getColumnIndex("AnswerB"));
                String stringAnswerC = cursor.getString(cursor.getColumnIndex("AnswerC"));
                String stringAnswerD = cursor.getString(cursor.getColumnIndex("AnswerD"));
                String stringCorrectAnswer = cursor.getString(cursor.getColumnIndex("CorrectAnswer"));
                String stringSortable = cursor.getString(cursor.getColumnIndex("Sortable"));

                database_questions_models question = new database_questions_models(stringId, stringQuestion, stringAnswerA, stringAnswerB, stringAnswerC, stringAnswerD, stringCorrectAnswer, stringSortable);
                listQuestions.add(question);
            }
            while (cursor.moveToNext());
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        sqLiteDatabase.close();
        return listQuestions;
    }

    public int getFolders() {
        this.sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
        int result = 0;
        Cursor cursor;

        try {
            cursor = sqLiteDatabase.rawQuery("SELECT Folders FROM Counters;", null);
            if (cursor == null) return 0;
            cursor.moveToNext();
            do {
                result = cursor.getInt(cursor.getColumnIndex("Folders"));
            } while (cursor.moveToNext());
            cursor.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        sqLiteDatabase.close();
        return result;
    }
    public int getQuestions() {
        this.sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        int result = 0;
        Cursor cursor;
        try {
            cursor = sqLiteDatabase.rawQuery("SELECT Questions FROM Counters;", null);
            if (cursor == null) return 0;
            cursor.moveToNext();
            do {
                result = cursor.getInt(cursor.getColumnIndex("Questions"));
            } while (cursor.moveToNext());
            cursor.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        sqLiteDatabase.close();
        return result;
    }
}