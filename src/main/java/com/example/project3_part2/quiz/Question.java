package com.example.project3_part2.quiz;

public abstract class Question {
    private String description;
    private String answer;

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getDescription() {
        return this.description;
    }

    public String getAnswer() {
        return this.answer;
    }
}
