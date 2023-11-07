package com.acodigo.godkantsv.database_questions;

public class database_questions_models {

    private final String Id;
    private final String Question;
    private final String AnswerA;
    private final String AnswerB;
    private final String AnswerC;
    private final String AnswerD;
    private final String CorrectAnswer;
    private final String Sortable;

    public database_questions_models(String id, String question, String answerA, String answerB, String answerC, String answerD, String correctAnswer, String sortable) {
        Id = id;
        Question = question;
        AnswerA = answerA;
        AnswerB = answerB;
        AnswerC = answerC;
        AnswerD = answerD;
        CorrectAnswer = correctAnswer;
        Sortable = sortable;
    }

    public String getId() {
        return Id;
    }

    public String getQuestion() {
        return Question;
    }

    public String getAnswerA() {
        return AnswerA;
    }

    public String getAnswerB() {
        return AnswerB;
    }

    public String getAnswerC() {
        return AnswerC;
    }

    public String getAnswerD() {
        return AnswerD;
    }

    public String getCorrectAnswer() {
        return CorrectAnswer;
    }

    public String getSortable() {
        return Sortable;
    }
}