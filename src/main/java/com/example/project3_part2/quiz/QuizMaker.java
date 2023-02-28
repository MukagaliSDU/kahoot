package com.example.project3_part2.quiz;

import java.io.FileNotFoundException;

public class QuizMaker {
    public static void main(String[] args) throws InvalidQuizFormatException, FileNotFoundException {
        String fileName = args[0];
        String filePath = "C:\\Users\\User\\IdeaProjects\\QuizWithFX\\src\\main\\java\\com\\example\\quizwithfx\\txt" + fileName;
        try {
            Quiz q = Quiz.readQuizFile(filePath);
        } catch (FileNotFoundException e) {
            System.out.println(e);
        }
    }
}
