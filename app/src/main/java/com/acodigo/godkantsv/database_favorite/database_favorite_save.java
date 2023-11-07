package com.acodigo.godkantsv.database_favorite;

import androidx.annotation.NonNull;

public class database_favorite_save {

    private final String QuestionId;

    public database_favorite_save(String QuestionId) {
        this.QuestionId = QuestionId;
    }
    @NonNull
    public String toString() {
        return QuestionId;
    }
}