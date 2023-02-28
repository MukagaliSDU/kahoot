package com.example.project3_part2.server;

import com.example.project3_part2.quiz.Quiz;
import javafx.application.Platform;
import javafx.scene.layout.StackPane;
import javafx.application.Platform;
import javafx.scene.layout.StackPane;

import java.io.*;
import java.net.Socket;

public class UserThread extends Thread {
    private Socket socket;
    private QuizServer server;
    private PrintWriter writer;
    private BufferedReader reader;

    public UserThread(Socket socket, QuizServer server) {
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        while (true) {
            try {
                if (server.isQuizEnd()) {
                    break;
                }
                else if (reader == null) {
                    InputStream inputStream = socket.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    OutputStream outputStream = socket.getOutputStream();
                    writer = new PrintWriter(outputStream, true);

                    printUsers();

                    String userName = reader.readLine();
                    server.addUserName(userName);

                    String serverMessage = "New user connected: " + userName;
                    server.broadcast(serverMessage, this);
                }
                String[] userMessage = getMessage().split(": ");
                server.saveUserAnswer(userMessage[0], userMessage[1]);
            } catch (IOException ex) {
                System.out.println("Error in UserThread: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    void printUsers() {
        if (server.hasUsers()) {
            writer.println("Connected users: " + server.getUserNames());
        } else {
            writer.println("No other users connected");
        }
    }

    void sendMessage(String message) {
        writer.println(message);
    }

    String getMessage() {
        try {
            return reader.readLine();
        } catch (Exception exe) {
            System.out.println(exe);
        }
        return null;
    }
}
