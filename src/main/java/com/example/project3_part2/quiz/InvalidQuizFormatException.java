package com.example.project3_part2.quiz;

public class InvalidQuizFormatException extends Exception {
    public InvalidQuizFormatException(String fileName) {
        System.out.println("Wrong " + fileName);
    }
}
