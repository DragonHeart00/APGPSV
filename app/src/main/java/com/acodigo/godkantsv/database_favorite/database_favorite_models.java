package com.acodigo.godkantsv.database_favorite;

public class database_favorite_models {

    private String QuestionId;
    private String QuestionText;
    private String AnswerA;
    private String AnswerB;
    private String AnswerC;
    private String AnswerD;
    private String CorrectAnswer;
    private String Sortable;

    public String getQuestionId() {
        return QuestionId;
    }
    public void setQuestionId(String QuestionID) {
        this.QuestionId = QuestionID;
    }

    public String getQuestionText() {
        return QuestionText;
    }
    public void setQuestionText(String QuestionText) {
        this.QuestionText = QuestionText;
    }

    public String getAnswerA() {
        return AnswerA;
    }
    public void setAnswerA(String AnswerA) {
        this.AnswerA = AnswerA;
    }

    public String getAnswerB() {
        return AnswerB;
    }
    public void setAnswerB(String AnswerB) {
        this.AnswerB = AnswerB;
    }

    public String getAnswerC() {
        return AnswerC;
    }
    public void setAnswerC(String AnswerC) {
        this.AnswerC = AnswerC;
    }

    public String getAnswerD() {
        return AnswerD;
    }
    public void setAnswerD(String AnswerD) {
        this.AnswerD = AnswerD;
    }

    public String getCorrectAnswer() {
        return CorrectAnswer;
    }
    public void setCorrectAnswer(String CorrectAnswer) {
        this.CorrectAnswer = CorrectAnswer;
    }

    public String getSortable() {
        return Sortable;
    }
    public void setSortable(String Sortable) {
        this.Sortable = Sortable;
    }
}