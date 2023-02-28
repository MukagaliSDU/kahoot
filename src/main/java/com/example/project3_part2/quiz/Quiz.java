package com.example.project3_part2.quiz;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Quiz {
    private String name;
    private ArrayList<Question> questions = new ArrayList<Question>();

    public Quiz() {

    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addQuestion(Question question) {
        questions.add(question);
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public static Quiz readQuizFile(String filePath) throws FileNotFoundException {
        Quiz q = new Quiz();
        File file = new File(filePath);
        Scanner scanner = new Scanner(file);
        ArrayList<String> strLines = new ArrayList<>();
        q.setName(filePath.substring(filePath.lastIndexOf("\\"), filePath.lastIndexOf(".txt")));

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            if (line.length() == 0) {

                if (strLines.size() == 2) {
                    FillIn fillIn = new FillIn();
                    fillIn.setDescription(strLines.get(0).replaceAll("blank", "_____"));
                    fillIn.setAnswer(strLines.get(1));
                    q.addQuestion(fillIn);
                } else if (strLines.size() > 2) {
                    Test test = new Test();
                    String[] array = new String[strLines.size()];
                    array = strLines.toArray(array);
                    test.setDescription(array[0]);
                    test.setOptions(Arrays.copyOfRange(array, 1, array.length));
                    test.setAnswer(array[1]);
                    q.addQuestion(test);
                }
                strLines.clear();
            } else {
                strLines.add(line);
            }
        }
        scanner.close();

        System.out.println("Quiz successfully created. " + q.questions.size() + " questions.");

        return q;
    }

    public void start() {
        String s = getName();
        System.out.println("WELCOME TO  \""+ s +"\" QUIZ ");
        System.out.println("==============================================");
        int correctAnswerCount = 0;
        for (Question question : questions) {
            System.out.println(question);
            System.out.print("Enter the correct choice: ");
            Scanner scanner = new Scanner(System.in);
            String userAnswer = scanner.nextLine();
            while (isInvalidAnswer(userAnswer) && question instanceof Test) {
                System.out.print("Invalid choice! Try again (Ex: A, B, ...): ");
                userAnswer = scanner.nextLine();
            }

            if (question instanceof Test) {
                userAnswer = ((Test) question).getOptionAt(userAnswer.charAt(0) - 65);
            }

            if (userAnswer.equals(question.getAnswer())) {
                System.out.println("Correct");
                correctAnswerCount++;
            } else {
                System.out.println("Incorrect");
            }
            System.out.println("____________________________________________");
        }
        System.out.printf("Correct Answers: %d/%d (%s)", correctAnswerCount, questions.size(), (correctAnswerCount * 100.0 / questions.size()) + "%");
    }

    private boolean isInvalidAnswer(String answer) {
        if (answer.length() != 1) {
            return true;
        }
        if (65 > answer.charAt(0) || answer.charAt(0) > 68) {
            return true;
        }
        return false;
    }
}

