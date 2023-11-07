package com.acodigo.godkantsv.database_favorite;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class database_favorite extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "database_favorite.db";
    private static final String TABLE_NAME = "Favorite";
    private static final String KEY_QUESTION_ID = "Id";
    private static final String KEY_QUESTION_TEXT = "Question";
    private static final String KEY_ANSWER_A = "AnswerA";
    private static final String KEY_ANSWER_B = "AnswerB";
    private static final String KEY_ANSWER_C = "AnswerC";
    private static final String KEY_ANSWER_D = "AnswerD";
    private static final String KEY_CORRECT_ANSWER = "CorrectAnswer";
    private static final String KEY_SORTABLE = "Sortable";

    private static final String SQL_CREATE_TABLE = "CREATE TABLE Favorite ("
            + KEY_QUESTION_ID + "  STRING PRIMARY KEY, "
            + KEY_QUESTION_TEXT + " TEXT, " + KEY_ANSWER_A + "  TEXT, "
            + KEY_ANSWER_B + "  TEXT, " + KEY_ANSWER_C + " TEXT,"
            + KEY_ANSWER_D + "  TEXT, " + KEY_CORRECT_ANSWER + "  TEXT , " + KEY_SORTABLE + "  TEXT );";

    private static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public database_favorite(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @SuppressLint("SQLiteString")
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(SQL_DELETE_TABLE);
        this.onCreate(db);
    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        this.onUpgrade(db, oldVersion, newVersion);
    }

    public List<database_favorite_models> getAllQuestions() {
        List<database_favorite_models> listDatabaseFavoriteModels = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                database_favorite_models databaseFavoriteModels = new database_favorite_models();
                databaseFavoriteModels.setQuestionId(cursor.getString(0));
                databaseFavoriteModels.setQuestionText(cursor.getString(1));
                databaseFavoriteModels.setAnswerA(cursor.getString(2));
                databaseFavoriteModels.setAnswerB(cursor.getString(3));
                databaseFavoriteModels.setAnswerC(cursor.getString(4));
                databaseFavoriteModels.setAnswerD(cursor.getString(5));
                databaseFavoriteModels.setCorrectAnswer(cursor.getString(6));
                databaseFavoriteModels.setSortable(cursor.getString(7));
                listDatabaseFavoriteModels.add(databaseFavoriteModels);
            } while (cursor.moveToNext());
        }

        sqLiteDatabase.close();
        return listDatabaseFavoriteModels;
    }

    public database_favorite_models getQuestion(String question_id) {
        database_favorite_models databaseFavoriteModels = new database_favorite_models();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String[] columns = {KEY_QUESTION_ID, KEY_QUESTION_TEXT, KEY_ANSWER_A, KEY_ANSWER_B, KEY_ANSWER_C, KEY_ANSWER_D, KEY_CORRECT_ANSWER, KEY_SORTABLE};
        String selection = KEY_QUESTION_ID + " = ?";
        String[] selectionArgs = {String.valueOf(question_id)};

        @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        if (null != cursor) {
            cursor.moveToFirst();
            databaseFavoriteModels.setQuestionId(cursor.getString(0));
            databaseFavoriteModels.setQuestionText(cursor.getString(1));
            databaseFavoriteModels.setAnswerA(cursor.getString(2));
            databaseFavoriteModels.setAnswerB(cursor.getString(3));
            databaseFavoriteModels.setAnswerC(cursor.getString(4));
            databaseFavoriteModels.setAnswerD(cursor.getString(5));
            databaseFavoriteModels.setCorrectAnswer(cursor.getString(6));
            databaseFavoriteModels.setSortable(cursor.getString(7));
        }
        sqLiteDatabase.close();
        return databaseFavoriteModels;
    }

    public boolean checkQuestion(String id) {
        boolean isExisted = false;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor;
        try {
            cursor = sqLiteDatabase.rawQuery("SELECT * FROM Favorite WHERE Id = '" + id + "';", null);
            if (cursor == null) return false;
            if (cursor.getCount() > 0) {
                isExisted = true;
            }
            cursor.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return isExisted;
    }
    public void addQuestion(database_favorite_models databaseFavoriteModels) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_QUESTION_ID, databaseFavoriteModels.getQuestionId());
        contentValues.put(KEY_QUESTION_TEXT, databaseFavoriteModels.getQuestionText());
        contentValues.put(KEY_ANSWER_A, databaseFavoriteModels.getAnswerA());
        contentValues.put(KEY_ANSWER_B, databaseFavoriteModels.getAnswerB());
        contentValues.put(KEY_ANSWER_C, databaseFavoriteModels.getAnswerC());
        contentValues.put(KEY_ANSWER_D, databaseFavoriteModels.getAnswerD());
        contentValues.put(KEY_CORRECT_ANSWER, databaseFavoriteModels.getCorrectAnswer());
        contentValues.put(KEY_SORTABLE, databaseFavoriteModels.getSortable());

        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        sqLiteDatabase.close();
    }
    public void deleteQuestion(String questionId) {
        String[] stringQuestionId = {String.valueOf(questionId)};
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(TABLE_NAME, KEY_QUESTION_ID + " = ?", stringQuestionId);
        sqLiteDatabase.close();
    }
    public void deleteAllQuestions() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(TABLE_NAME, null, null);
        sqLiteDatabase.close();
    }
}