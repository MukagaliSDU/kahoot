package com.example.project3_part2.client;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class QuizClient extends Application {
    private String hostname;
    private int port;
    private String userName;
    Stage window;
    private StackPane currentStackPane;
    private boolean isStarted = false;
    private String questionType = null;
    final double W = 600., H = 600.;

    public void execute() {
        try {
            Socket socket = new Socket(hostname, port);

            System.out.println("Connected to the Quiz server");

            new WriteThread(socket, this).start();
            new ReadThread(socket, this).start();

        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O Error: " + ex.getMessage());
        }
    }

    void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    String getQuestionType() {
        return questionType;
    }

    boolean getStatus() {
        return isStarted;
    }

    void setStatus() {
        isStarted = !isStarted;
    }

    void setUserName(String userName) {
        this.userName = userName;
    }

    String getUserName() {
        return this.userName;
    }

    public StackPane waitingPage() {
        StackPane stackPane = new StackPane();

        QuizClient client = this;
        client.execute();

        currentStackPane = stackPane;

        return stackPane;
    }

    public StackPane enterName() {
        StackPane stackPane = new StackPane();
        TextField tf = new TextField();
        tf.setPromptText("Enter your name");
        tf.setMaxWidth(W / 3);
        tf.setMinHeight(40);
        tf.setAlignment(Pos.CENTER);
        Button btn = new Button("Enter");
        btn.setMaxWidth(W / 3);
        btn.setMinHeight(40);
        btn.setStyle("-fx-background-color:#333333");
        btn.setTextFill(Color.WHITE);
        btn.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, 16));
        VBox vBox = new VBox(10);
        vBox.setMaxWidth(W / 2);
        vBox.setMaxHeight(H / 2);
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(tf, btn);

        stackPane.getChildren().addAll(vBox);
        stackPane.setStyle("-fx-background-color: #3e147f");

        btn.setOnAction(e -> {
            userName = tf.getText();
            window.setScene(new Scene(waitingPage(), W, H));
        });

        currentStackPane = stackPane;

        return stackPane;
    }


    public StackPane quizEnd(Socket socket) {
        StackPane stackPane = new StackPane();
        VBox vBox = new VBox(10);
        Text text1 = new Text("Quiz end");
        vBox.getChildren().addAll(text1);
        vBox.setAlignment(Pos.CENTER);
        text1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, 22));
        text1.setStyle("-fx-text-fill:#678ce0; -fx-font-size: 40;");

        stackPane.getChildren().add(vBox);
        stackPane.setStyle("-fx-background-color: #f4b840");
        return stackPane;
    }

    public StackPane showAnswer(Socket socket, String answer) {
        StackPane stackPane = new StackPane();
        VBox vBox = new VBox(10);
        Text text1 = new Text("Correct answer is\n\n\n");
        Text text2 = new Text(answer);
        vBox.getChildren().addAll(text1, text2);
        vBox.setAlignment(Pos.CENTER);
        text1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, 22));
        text1.setStyle("-fx-text-fill:#678ce0; -fx-font-size: 40;");

        text2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, 22));
        text2.setStyle("-fx-font-size: 30;");

        stackPane.getChildren().add(vBox);
        stackPane.setStyle("-fx-background-color: #f4b840");
        return stackPane;
    }

    public StackPane fillInPane(Socket socket) {
        StackPane stackPane = new StackPane();
        TextField tf = new TextField();
        tf.setPromptText("Type your answer");
        tf.setMaxWidth(W / 3);
        tf.setMinHeight(40);
        tf.setAlignment(Pos.CENTER);
        Button btn = new Button("Send my answer");
        btn.setMaxWidth(W / 3);
        btn.setMinHeight(40);
        btn.setStyle("-fx-background-color:#333333");
        btn.setTextFill(Color.WHITE);
        btn.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, 16));
        VBox vBox = new VBox(10);
        vBox.setMaxWidth(W / 2);
        vBox.setMaxHeight(H / 2);
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(tf, btn);

        stackPane.getChildren().addAll(vBox);
        stackPane.setStyle("-fx-background-color: #999999");

        btn.setOnAction(e -> {
            String text = tf.getText();

            try {
                new WriteThread(socket, this, userName + ": " + text);
                window.setScene(new Scene(new StackPane(), W, H));
            } catch (Exception exe) {
                System.out.println(exe);
            }
        });

        currentStackPane = stackPane;

        return stackPane;
    }

    public StackPane trueFalsePane(Socket socket) {
        StackPane stackPane = new StackPane();
        Button btnTrue = kahootButton("green", "TRUE");
        Button btnFalse = kahootButton("red", "FALSE");

        HBox hBox = new HBox(10);
        hBox.getChildren().addAll(btnTrue, btnFalse);
        stackPane.getChildren().addAll(hBox);

        btnTrue.setOnAction(e -> {
            new WriteThread(socket, this,userName + ": True");
            window.setScene(new Scene(new StackPane(), W, H));
        });
        btnFalse.setOnAction(e -> {
            new WriteThread(socket, this,userName + ": False");
            window.setScene(new Scene(new StackPane(), W, H));
        });

        currentStackPane = stackPane;

        return stackPane;
    }

    public StackPane testPane(Socket socket) {
        StackPane stackPane = new StackPane();
        VBox vBox1 = new VBox(10);
        Button btnRed = kahootButton("red");
        Button btnBlue = kahootButton("blue");
        vBox1.getChildren().addAll(btnRed, btnBlue);
        VBox vBox2 = new VBox(10);
        Button btnYellow = kahootButton("yellow");
        Button btnGreen = kahootButton("green");
        vBox2.getChildren().addAll(btnYellow, btnGreen);

        HBox hBox = new HBox(10);
        hBox.getChildren().addAll(vBox1, vBox2);
        stackPane.getChildren().addAll(hBox);

        btnRed.setOnAction(e -> {
            new WriteThread(socket, this, userName + ": Red");
            window.setScene(new Scene(new StackPane(), W, H));
        });
        btnBlue.setOnAction(e -> {
            new WriteThread(socket, this,userName + ": Blue");
            window.setScene(new Scene(new StackPane(), W, H));
        });
        btnYellow.setOnAction(e -> {
            new WriteThread(socket, this,userName + ": Yellow");
            window.setScene(new Scene(new StackPane(), W, H));
        });
        btnGreen.setOnAction(e -> {
            new WriteThread(socket, this,userName + ": Green");
            window.setScene(new Scene(new StackPane(), W, H));
        });

        currentStackPane = stackPane;

        return stackPane;
    }

    public Button kahootButton(String btnColor, String text) {
        Button btn = new Button(text);
        btn.setMinWidth(W / 2. - 5);
        btn.setMinHeight(H / 2. - 5);
        btn.setStyle("-fx-background-color: " + btnColor);

        btn.setTextFill(Color.WHITE);
        btn.setWrapText(true);
        btn.setPadding(new Insets(10));

        Font font = Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, 15);
        btn.setFont(font);
        return btn;
    }

    public Button kahootButton(String btnColor) {
        return kahootButton(btnColor, "");
    }

    public StackPane joinToQuiz() {
        StackPane stackPane = new StackPane();
        TextField tf = new TextField();
        tf.setPromptText("Game PIN");
        tf.setMaxWidth(W / 3);
        tf.setMinHeight(40);
        tf.setAlignment(Pos.CENTER);
        Button btn = new Button("Enter");
        btn.setMaxWidth(W / 3);
        btn.setMinHeight(40);
        btn.setStyle("-fx-background-color:#333333");
        btn.setTextFill(Color.WHITE);
        btn.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, 16));
        VBox vBox = new VBox(10);
        vBox.setMaxWidth(W / 2);
        vBox.setMaxHeight(H / 2);
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(tf, btn);

        stackPane.getChildren().addAll(vBox);
        stackPane.setStyle("-fx-background-color: #3e147f");

        btn.setOnAction(e -> {
            String text = tf.getText();
            if (text.length() == 4) {
                try {
                    port = Integer.parseInt(tf.getText());
                    window.setScene(new Scene(enterName(), W, H));
                } catch (Exception exe) {
                    System.out.println("type 4 digit number");
                }
            } else {
                System.out.println("type 4 digit number");
            }
        });

        currentStackPane = stackPane;

        return stackPane;
    }

    @Override
    public void start(Stage stage) throws Exception {
        window = stage;
        window.setScene(new Scene(joinToQuiz(), W, H));
        window.setTitle("Project 3 CLIENT");
        window.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}




class ReadThread extends Thread {
    private BufferedReader reader;
    private Socket socket;
    private QuizClient client;

    public ReadThread(Socket socket, QuizClient client) {
        this.socket = socket;
        this.client = client;

        try {
            InputStream input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
        } catch (IOException ex) {
            System.out.println("Error getting input stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                String s = reader.readLine();
                if (client.getStatus()) {
                    if (s.equals("Test")) {
                        Platform.runLater(() -> {
                            client.window.setScene(new Scene(client.testPane(socket), client.W, client.H));
                        });
                    } else if (s.equals("FillIn")) {
                        Platform.runLater(() -> {
                            client.window.setScene(new Scene(client.fillInPane(socket), client.W, client.H));
                        });
                    } else if (s.equals("TrueFalse")) {
                        Platform.runLater(() -> {
                            client.window.setScene(new Scene(client.trueFalsePane(socket), client.W, client.H));
                        });
                    } else if (s.equals("answer")) {
                        String answer = reader.readLine();
                        Platform.runLater(() -> {
                            client.window.setScene(new Scene(client.showAnswer(socket, answer), client.W, client.H));
                        });
                    } else if (s.equals("quiz end")) {
                        System.out.println("+");
                        Platform.runLater(() -> {
                            client.window.setScene(new Scene(client.quizEnd(socket), client.W, client.H));
                        });
                        try {
                            Thread.sleep(5000);
                        } catch (Exception exe) {
                            System.out.println(exe);
                        }
                        Platform.runLater(() -> {
                            client.window.setScene(new Scene(client.joinToQuiz(), client.W, client.H));
                        });
                    }
                } else if (s.equals("quiz start")) {
                    client.setStatus();
                }
            } catch (IOException ex) {
                System.out.println("Error reading from server: " + ex.getMessage());
                ex.printStackTrace();
                break;
            }
        }
    }
}



class WriteThread extends Thread {
    private PrintWriter writer;
    private Socket socket;
    private QuizClient client;

    public WriteThread(Socket socket, QuizClient client) {
        this.socket = socket;
        this.client = client;

        try {
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
            writer.println(client.getUserName());
        } catch (IOException ex) {
            System.out.println("Error getting output stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public WriteThread(Socket socket, QuizClient client, String message) {
        this.socket = socket;
        this.client = client;

        try {
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
            writer.println(message);
        } catch (IOException ex) {
            System.out.println("Error getting output stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void run() {
//        System.out.println(client.getUserName());

        try {
//            new DataOutputStream(socket.getOutputStream()).writeUTF("\n"); // later change
//            new DataOutputStream(socket.getOutputStream()).writeUTF(client.getUserName());
        } catch (Exception exe) {
        }
    }
}