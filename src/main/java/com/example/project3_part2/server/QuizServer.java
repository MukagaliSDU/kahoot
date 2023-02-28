package com.example.project3_part2.server;

import com.example.project3_part2.quiz.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class QuizServer extends Application {
    private Quiz quiz;
    private ArrayList<Question> questions;
    private int currentQuestionIndex = -1;
    private int port;
    private Set<UserThread> userThreads = new HashSet<>();
    private Set<String> userNames = new HashSet<>();
    private Map<String, Integer> scores = new HashMap<>();
    private Map<String, String> userAnswers = new HashMap<>();
    private String dirPath = "C:\\Users\\Asus\\Downloads";
    private Stage window;
    private final int seconds = 30;
    private int time = 0;

    private Text timerText;
    private Thread timer = new Thread(() -> {
        try {
            while (currentQuestionIndex < questions.size()) {
                for (time = 0; time <= seconds; time++) {
                    Thread.sleep(1000);
                    if (time >= 0) {
                        timerText.setText(String.format("%02ds", seconds - time - 1));
                    }
                }

                Platform.runLater(() -> {
                    showResult();
                });
                Thread.sleep(4000);
            }
        } catch (Exception exe) {
            System.out.println(exe);
        }
    });
    private StackPane currentStackPane;
    private final double W = 800., H = 500.;

    public QuizServer() {}

    public void execute() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Quiz Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New user connected");

                UserThread newUser = new UserThread(socket, this);
                userThreads.add(newUser);
                newUser.start();


            }

        } catch (IOException ex) {
            System.out.println("Error in the server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    void broadcastAll(String message) {
        for (UserThread userThread : userThreads) {
            userThread.sendMessage(message);
        }
    }

    void broadcast(String message, UserThread excludeUser) {
        for (UserThread userThread : userThreads) {
            if (userThread != excludeUser) {
                userThread.sendMessage(message);
            }
        }
    }

    void addUserName(String userName) {
        userNames.add(userName);
        scores.put(userName, 0);

        Platform.runLater(() -> {
            Text name = new Text("\n\n".repeat(userNames.size() - 1) + String.format("%d.%s", userNames.size(), userName));
            currentStackPane.getChildren().add(name);
        });
    }

    boolean hasUsers() {
        return !this.userNames.isEmpty();
    }

    Set<String> getUserNames() {
        return this.userNames;
    }

    int getScore(String userName) {
        return scores.get(userName);
    }

    void addScore(String userName) {
        scores.put(userName, scores.get(userName) + 1);
    }

    Map<String, Integer> getScores() {
        return scores;
    }

    boolean isQuizWorking() {
        return currentQuestionIndex + 1 < questions.size() && currentQuestionIndex >= 0;
    }

    boolean isQuizEnd() {
        return currentQuestionIndex + 1 >= questions.size();
    }

    void saveUserAnswer(String userName, String userAnswer) {
        userAnswers.put(userName, userAnswer);
        if (userAnswers.size() == userThreads.size()) {
            System.out.println("asdhf;lkajrpeqwioulksdjf");
            time = -4;
            Platform.runLater(() -> {
                showResult();
            });
        }
    }

    public void calculateScores() {
        Question question = questions.get(currentQuestionIndex);
        String answer = question.getAnswer();
        broadcastAll("answer");
        broadcastAll(answer);

        for (String userName : userAnswers.keySet()) {
            String userAnswer = userAnswers.get(userName);
            if (question instanceof Test) {
                switch (userAnswer) {
                    case "Red":
                        userAnswer = ((Test) question).getOptionAt(0);
                        break;
                    case "Yellow":
                        userAnswer = ((Test) question).getOptionAt(1);
                        break;
                    case "Blue":
                        userAnswer = ((Test) question).getOptionAt(2);
                        break;
                    case "Green":
                        userAnswer = ((Test) question).getOptionAt(3);
                        break;
                }
            }
            userAnswers.remove(userName);
            if (userAnswer.equals(answer)) {
                scores.put(userName, scores.get(userName) + 1);
            }
        }
    }




    public StackPane scoresList() {
        StackPane stackPane = new StackPane();

        stackPane.setStyle("-fx-background-color: #FF00FF");
        List<Integer> scoresValues = new ArrayList<Integer>(scores.values());
        Collections.sort(scoresValues);

        int i = 0;
        for (String userName : userNames) {
            Text userScore = new Text("\n\n".repeat(i++) + String.format("%s -> %d", userName, scores.get(userName)));
            stackPane.getChildren().addAll(userScore);
        }

        stackPane.setAlignment(Pos.CENTER);

        return stackPane;
    }

    public void showFinalResult() {
        StackPane stackPane = new StackPane();

        stackPane.setStyle("-fx-background-color: #00FF00");

        Text t1 = new Text("Quiz End\n\n\n");
        t1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, 30));
        stackPane.getChildren().addAll(t1);

        int maxScore = questions.size();
        for (int i = 1; i <= userNames.size(); i++) {
            while (!scores.isEmpty()) {
                for (String userName : scores.keySet()) {
                    if (maxScore == scores.get(userName)) {

                        Text t2 = new Text("\n\n".repeat(i) + String.format("%d.%s : %d", i, userName, scores.get(userName)));
                        t2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, 22));
                        stackPane.getChildren().addAll(t2);

                        scores.remove(userName);
                    }
                }
                i++;
                maxScore--;
            }
        }
        broadcastAll("quiz end");

        stackPane.setAlignment(Pos.CENTER);

        window.setScene(new Scene(stackPane, W, H));
    }

    public void showResult() {
        calculateScores();
        window.setScene(new Scene(scoresList(), W, H));
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                Platform.runLater(() -> {
                    if (currentQuestionIndex + 1 < questions.size()) {
                        showQuestion(++currentQuestionIndex);
                    } else {
                        currentQuestionIndex++;
                        showFinalResult();
                    }
                });
            } catch (Exception exe) {
                System.out.println(exe);
            }
        }).start();
    }

    public void showQuestion(int ind) {
        window.setScene(new Scene(currentQuestion(ind), W, H));
    }

    public BorderPane currentQuestion(int ind) {
        BorderPane borderPane = new BorderPane();
        VBox vbox = new VBox();

        timerText = new Text(String.format("%02ds", seconds));
        timerText.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, 22));

        Label textLabel = new Label((ind + 1) + ") " + questions.get(ind).getDescription());
        textLabel.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, 22));
        textLabel.setWrapText(true);

        if (questions.get(ind) instanceof Test) {
            broadcastAll("Test");

            Test test = (Test) questions.get(ind);

            Button btnRed = kahootButton(test.getOptionAt(0), "red");
            Button btnYellow = kahootButton(test.getOptionAt(1), "yellow");
            Button btnBlue = kahootButton(test.getOptionAt(2), "blue");
            Button btnGreen = kahootButton(test.getOptionAt(3), "green");
            HBox hBox1 = new HBox(10);
            hBox1.getChildren().addAll(btnRed, btnYellow);
            HBox hBox2 = new HBox(10);
            hBox2.getChildren().addAll(btnBlue, btnGreen);
            VBox vBox = new VBox(10);
            vBox.getChildren().addAll(hBox1, hBox2);

            borderPane.setBottom(vBox);
        } else if (questions.get(ind) instanceof FillIn) {
            broadcastAll("FillIn");

            Text text = new Text("Please type your answer in to the TextField");
            text.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, 22));
            text.setStyle("-fx-font-size: 30;");
            borderPane.setBottom(text);
        } else if (questions.get(ind) instanceof TrueFalse) {
            broadcastAll("TrueFalse");

            Text text = new Text("Choose true or false");
            text.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, 22));
            text.setStyle("-fx-font-size: 30;");
            borderPane.setBottom(text);
        }



        vbox.getChildren().addAll(timerText);
        vbox.setAlignment(Pos.BASELINE_CENTER);

        borderPane.setTop(textLabel);
        borderPane.setCenter(vbox);

        return borderPane;
    }

    public Button kahootButton(String labelText, String btnColor) {
        Button button = new Button(labelText);
        button.setStyle("-fx-background-color: " + btnColor);
        button.setMinHeight(100);
        button.setMinWidth(W/2.);
//        button.setStyle("-fx-background-color: #020608");
        button.setTextFill(Color.WHITE);
        button.setWrapText(true);
        button.setPadding(new Insets(10));
        Font font = Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, 22);
        button.setFont(font);
        return  button;
    }

    public StackPane waitingPage() {
        StackPane stackPane = new StackPane();

        Image image = new Image("C:\\Users\\Asus\\Downloads\\images.png");
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(200);
        imageView.setFitHeight(100);

        Label label = new Label("Game code :" + port);
        label.setStyle("-fx-text-fill:#678ce0; -fx-font-size: 20;");
        Button btnStart = new Button("START QUIZ");
        VBox vBox = new VBox(10);
        btnStart.setStyle("-fx-background-color:#333333");
        btnStart.setTextFill(Color.WHITE);
        btnStart.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 15));
        vBox.getChildren().addAll(imageView, label, btnStart);
        vBox.setAlignment(Pos.BASELINE_CENTER);
        stackPane.getChildren().add(vBox);
        stackPane.setStyle("-fx-background-color: #f4b840");
        new Thread(() -> {
            QuizServer server = this;
            server.execute();
        }).start();

        btnStart.setOnAction(e -> {
            broadcastAll("quiz start");
            currentQuestionIndex++;
            showQuestion(currentQuestionIndex);
            timer.start();
        });

        currentStackPane = stackPane;

        return stackPane;
    }

    public StackPane writePortNumber() {
        StackPane stackPane = new StackPane();

        TextField textField = new TextField();
        textField.setPromptText("Enter PIN CODE for client");
        textField.setMaxWidth(W / 3);
        textField.setMinHeight(40);
        textField.setAlignment(Pos.CENTER);
        Button button = new Button("Enter");
        button.setMaxWidth(W / 3);
        button.setMinHeight(40);
        button.setStyle("-fx-background-color:#333333");
        button.setTextFill(Color.WHITE);
        button.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 15));
        VBox vBox = new VBox(12);
        vBox.setMaxWidth(W / 2);
        vBox.setMaxHeight(H / 2);
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(textField, button);

        stackPane.getChildren().addAll(vBox);
        stackPane.setStyle("-fx-background-color: #855de3");

        button.setOnAction(e -> {
            String text = textField.getText();
            if (text.length() == 4) {
                try {
                    port = Integer.parseInt(text);
                    window.setScene(new Scene(waitingPage(), W, H));
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

    public StackPane chooseFile() {
        StackPane stackPane = new StackPane();

        Button button = new Button("Choose a file");
        button.setMaxWidth(W / 3);
        button.setMinHeight(40);
        button.setStyle("-fx-background-color:#333333");
        button.setTextFill(Color.WHITE);
        button.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 16));
        Color color = new Color(0.9, 0.2 ,0.6,0.8);
        stackPane.setStyle("-fx-background-color: #612878");
        stackPane.getChildren().addAll(button);
        stackPane.setAlignment(Pos.CENTER);

        button.setOnAction(e -> {
            String path = chooseFilePath();

            try {
                quiz = Quiz.readQuizFile(path);
                questions = quiz.getQuestions();
                window.setScene(new Scene(writePortNumber(), W, H));
            } catch (Exception exe) {
                System.out.println(exe);
            }
        });

        currentStackPane = stackPane;

        return stackPane;
    }

    public String chooseFilePath() {
        FileChooser fileChooser = new FileChooser();
        try {
            fileChooser.initialDirectoryProperty().set(new File(dirPath));
        } catch (Exception e) {
            System.out.println(e);
        }
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showOpenDialog(window);

        return file.getPath();
    }

    @Override
    public void start(Stage stage) throws Exception {
        window = stage;
        window.setScene(new Scene(chooseFile(), W, H));
        window.setTitle("Project 3 SERVER");
        window.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
