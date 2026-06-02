package com.jethop;

import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import java.util.ArrayList;
import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class JetHop extends Pane {

    boolean isPaused = false;

    int boardWidth = 960;
    int boardHeight = 600;

    Canvas canvas;
    GraphicsContext gc;

    Image backgroundImg;
    Image avatarImg;
    Image pipeImg;

    Avatar avatar;
    double velocityY = 0;
    final double GRAVITY = 0.5;
    final double JUMP_STRENGTH = -8;

    ArrayList<Pipe> pipes;
    AnimationTimer gameLoop;
    Random random = new Random();
    int frameCount = 0;
    final int PIPE_SPEED = 3;

    boolean gameOver = false;
    boolean gameOverPopupShown = false; 
    Stage pauseStage; 
    Stage gameOverStage; 
    int score = 0;
    static int highScore = 0;
    Text scoreText = new Text();

    int PipeGap = 350;

    @FXML Button startGameButton;
    @FXML Button exitButton;
    @FXML Button mainMenuButton;
    @FXML Button retryButton;
    @FXML Button pauseButton;
    @FXML RadioButton characterOneRadioButton;
    @FXML RadioButton characterTwoRadioButton;
    @FXML RadioButton sceneOneRadioButton;
    @FXML RadioButton sceneTwoRadioButton;
    @FXML RadioButton easy;
    @FXML RadioButton medium;
    @FXML RadioButton hard;
    @FXML Text gameHighScore;
    @FXML Text gameScore;
    @FXML Text mainMenuHighScore;

    ToggleGroup tgrpChar = new ToggleGroup();
    ToggleGroup tgrpScene = new ToggleGroup();
    ToggleGroup tgrpMode = new ToggleGroup();

    public JetHop() {
        canvas = new Canvas(boardWidth, boardHeight);
        gc = canvas.getGraphicsContext2D();

        backgroundImg = new Image(getClass().getResource("/com/jethop/Images/BackgroundOne.png").toExternalForm());
        avatarImg = new Image(getClass().getResource("/com/jethop/Images/CharacterOne.png").toExternalForm());
        pipeImg = new Image(getClass().getResource("/com/jethop/Images/Pipe.png").toExternalForm());

        avatar = new Avatar(avatarImg);
        pipes = new ArrayList<>();

        pauseButton = new Button("PAUSE"); 
        pauseButton.setPrefSize(75, 32);

        pauseButton.setStyle(
                "-fx-background-color: rgba(128,128,128,0.7); " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 12px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-border-color: white; " +
                        "-fx-border-width: 2; " +
                        "-fx-background-radius: 6; " +
                        "-fx-border-radius: 6; ");
        pauseButton.setLayoutX(880);
        pauseButton.setLayoutY(10);

        pauseButton.setOnAction(e -> {
            isPaused = !isPaused;

            if (isPaused) {
                pauseButton.setText("RESUME");
                showPausePopup();
                this.requestFocus();
            } else {
                pauseButton.setText("PAUSE");
                hidePausePopup();
                this.requestFocus();
            }
        });
        this.getChildren().addAll(canvas, scoreText, pauseButton);
    }

    @FXML
    private void resumeAction(ActionEvent event) {
        Stage popup = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        popup.close();
    }

    private void showPausePopup() {
        if (pauseStage == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("PauseWindow.fxml"));
                Parent root = loader.load();

                pauseStage = new Stage();
                pauseStage.initStyle(StageStyle.TRANSPARENT);

                Scene popupScene = new Scene(root);
                popupScene.setFill(Color.TRANSPARENT);

                popupScene.setOnKeyPressed(e -> {
                    if (e.getCode() == KeyCode.ESCAPE) {
                        pauseStage.hide(); 
                    }
                });

                pauseStage.setScene(popupScene);
                pauseStage.initOwner(this.getScene().getWindow());
                pauseStage.initModality(Modality.WINDOW_MODAL);

                pauseStage.setOnHidden(event -> {
                    if (isPaused) {
                        isPaused = false;
                        pauseButton.setText("PAUSE");
                        this.requestFocus();
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (!pauseStage.isShowing()) {
            pauseStage.show();
        }
    }

    @FXML
    public void initialize() {
    
        if (mainMenuHighScore != null) {
            mainMenuHighScore.setText(String.valueOf(highScore));
        }
    }

    @FXML
    private void hidePausePopup() {
        if (pauseStage != null && pauseStage.isShowing()) {
            pauseStage.hide();
        }
    }

    private void showGameOverPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GameOverWindow.fxml"));
            Parent root = loader.load();

            JetHop controller = loader.getController();

            controller.gameScore.setText(String.valueOf(score));
            controller.gameHighScore.setText(String.valueOf(highScore));

            gameOverStage = new Stage();
            gameOverStage.initStyle(StageStyle.TRANSPARENT);

            Scene popupScene = new Scene(root);
            popupScene.setFill(Color.TRANSPARENT);

            gameOverStage.setScene(popupScene);
            gameOverStage.initOwner(this.getScene().getWindow());
            gameOverStage.initModality(Modality.WINDOW_MODAL);
            gameOverStage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void startGameMethod(ActionEvent event) {

        JetHop game = new JetHop();

        game.avatarImg = this.avatarImg;
        game.backgroundImg = this.backgroundImg;
        game.avatar.img = this.avatarImg;
        game.PipeGap = this.PipeGap;

        Scene scene2 = new Scene(game, game.boardWidth, game.boardHeight);
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

        stage.setScene(scene2);
        stage.show();

        game.setupControls(scene2);
        game.requestFocus();
        game.startGameLoop();
    }

    @FXML
    private void exitGame(ActionEvent event) {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void difficulty() {
        if (easy.isSelected())
            PipeGap = 250;
        else if (medium.isSelected())
            PipeGap = 200;
        else if (hard.isSelected())
            PipeGap = 150;
        restartGame();
    }

    @FXML
    private void ChooseChar() {
        if (characterOneRadioButton.isSelected()) {
            avatarImg = new Image(getClass().getResource("/com/jethop/Images/CharacterOne.png").toExternalForm());
        } else if (characterTwoRadioButton.isSelected()) {
            avatarImg = new Image(getClass().getResource("/com/jethop/Images/CharacterTwo.png").toExternalForm());
        }

        if (avatar != null)
            avatar.img = avatarImg;

        restartGame();
    }

    @FXML
    private void ChooseScene() {
        if (sceneOneRadioButton.isSelected()) {
            backgroundImg = new Image(getClass().getResource("/com/jethop/Images/BackgroundOne.png").toExternalForm());
        } else if (sceneTwoRadioButton.isSelected()) {
            backgroundImg = new Image(getClass().getResource("/com/jethop/Images/BackgroundTwo.png").toExternalForm());
        }
        restartGame();
    }

    @FXML
    private void returnToMainMenu(ActionEvent event) throws java.io.IOException {

        Stage popup = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        Stage mainStage = (Stage) popup.getOwner();

        if (mainStage.getScene().getRoot() instanceof JetHop) {
            JetHop originalGame = (JetHop) mainStage.getScene().getRoot();

            if (originalGame.gameLoop != null) {
                originalGame.gameLoop.stop(); 
            }
            originalGame.isPaused = true;
        }

        javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("HomeScreenWindow.fxml"));

        mainStage.setScene(new javafx.scene.Scene(root, 960, 600));
        popup.close();
    }

    @FXML
    private void retryGame(ActionEvent event) {

        Stage popup = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        popup.close();
        restartGame();
        this.requestFocus();
    }

    public void render() {
        gc.clearRect(0, 0, boardWidth, boardHeight);
        gc.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight);
        gc.drawImage(avatar.img, avatar.x, avatar.y, avatar.width, avatar.height);
        for (Pipe p : pipes) {
            gc.drawImage(p.img, p.x, p.y, p.width, p.height);
        }
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 25));
        gc.fillText("" + score, 20, 40);
    }

    public void updateAvatar() {
        if (gameOver)
            return;

        velocityY += GRAVITY;
        avatar.y += velocityY;

        if (avatar.y < 0) {
            avatar.y = 0;
            velocityY = 0;
        }
    }

    public void setupControls(Scene scene) {
        scene.setOnKeyPressed(e -> {

            if (e.getCode() == KeyCode.ESCAPE) {
                isPaused = !isPaused;
                pauseButton.setText(isPaused ? "RESUME" : "PAUSE");
                if (isPaused)
                    showPausePopup();
                else
                    hidePausePopup(); // added
                return;
            }

            if (isPaused) {
                return;
            }

            if (e.getCode() == KeyCode.SPACE) {
                if (gameOver) {
                    restartGame();
                } else {
                    velocityY = JUMP_STRENGTH;
                }
            }
        });
    }

    public void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {

                if (isPaused) {
                    render(); 
                    return;
                }

                updateAvatar();
                spawnAndMovePipes();
                checkCollisions();
                render();

                if (gameOver && !gameOverPopupShown) {
                    gameOverPopupShown = true;
                    showGameOverPopup();
                }
            }
        };

        gameLoop.start();
    }

    public void restartGame() {
        avatar.y = 300;
        velocityY = 0;
        pipes.clear();
        score = 0;
        frameCount = 0;
        gameOver = false;
        gameOverPopupShown = false; 
        if (gameOverStage != null)
            gameOverStage.hide();
    }

    public void checkCollisions() {
        if (gameOver)
            return;

        if (avatar.y + avatar.height > boardHeight) {
            gameOver = true;
        }

        for (Pipe p : pipes) {

            if (avatar.x < p.x + p.width && avatar.x + avatar.width > p.x &&
                    avatar.y < p.y + p.height && avatar.y + avatar.height > p.y) {
                gameOver = true;
            }

            if (!p.passed && avatar.x > p.x + p.width) {
                p.passed = true;
                score++;
                if (score > highScore) {
                    highScore = score;
                }
                scoreText.setText("" + score);
            }

        }
    }

    public void spawnAndMovePipes() {
        if (gameOver)
            return;
        frameCount++;
        if (frameCount % 100 == 0) {
            double randomY = random.nextInt(300) + 100;
            pipes.add(new Pipe(boardWidth, 0, 60, randomY, pipeImg));
            pipes.add(new Pipe(boardWidth, randomY + PipeGap, 60, boardHeight -
                    (randomY + PipeGap), pipeImg));
        }
        for (int i = 0; i < pipes.size(); i++) {
            Pipe p = pipes.get(i);
            p.x -= PIPE_SPEED;
            if (p.x + p.width < 0) {
                pipes.remove(i);
                i--;
            }
        }
    }

    class Avatar {
        int x = 100;
        int y = 300;
        int width = 50;
        int height = 65;
        Image img;

        Avatar(Image img) {
            this.img = img;
        }
    }

    class Pipe {
        double x, y, width, height;
        boolean passed = false;
        Image img;

        Pipe(double x, double y, double width, double height, Image img) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.img = img;
        }
    }
}
