package com.gyaanguru.quiz.model;

import java.io.Serializable;

public class Question implements Serializable {
    private final String question;
    private final String[] options;
    private final int correctIndex;
    private final String category;

    public Question(String question, String[] options, int correctIndex, String category) {
        this.question = question;
        this.options = options;
        this.correctIndex = correctIndex;
        this.category = category;
    }

    public String getQuestion() { return question; }
    public String[] getOptions() { return options; }
    public int getCorrectIndex() { return correctIndex; }
    public String getCategory() { return category; }
}
