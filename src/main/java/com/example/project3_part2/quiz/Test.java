package com.example.project3_part2.quiz;

import java.util.ArrayList;
import java.util.Random;

public class Test extends Question {
    private String[] options;
    private int numOfOptions;
    private ArrayList<String> labels = new ArrayList<String>();

    public Test() {
    }

    public void setOptions(String[] options) {
        Random rand = new Random();
        labels.clear();
        for (int i = 0; i < options.length; i++) {

            labels.add("ABCDEFGHIJKLMN".charAt(i) + ") ");
            int randIndex = rand.nextInt(options.length - 1);
            String temp = options[randIndex];
            options[randIndex] = options[i];
            options[i] = temp;
        }

        this.options = options;
        numOfOptions = options.length;
    }

    public String getOptionAt(int index) {
        return options[index];
    }

    @Override
    public String toString() {
        String result = getDescription();
        for (int i = 0; i < numOfOptions; i++) {
            result += "\n" + labels.get(i) + options[i];
        }
        result += "\n---------------------------";
        return result;
    }
}