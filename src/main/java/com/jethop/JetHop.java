package com.jethop;

import javafx.scene.control.Button;//added
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

import java.util.ArrayList;
import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
import javafx.stage.Stage;

public class JetHop extends Pane {

    boolean isPaused = false;// added
    Button pauseButton;// added

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
    int score = 0;
    Text scoreText = new Text();

    int PipeGap = 350;

    @FXML Button startGameButton;
    @FXML Button exitButton;
    @FXML RadioButton characterOneRadioButton;
    @FXML RadioButton characterTwoRadioButton;
    @FXML RadioButton sceneOneRadioButton;
    @FXML RadioButton sceneTwoRadioButton;
    @FXML RadioButton easy;
    @FXML RadioButton medium;
    @FXML RadioButton hard;

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

        pauseButton=new Button("PAUSE");    //pause button added
        pauseButton.setPrefSize(75, 32);

       pauseButton.setStyle(
            "-fx-background-color: rgba(128,128,128,0.7); " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 12px; " +
            "-fx-font-weight: bold; " +
            "-fx-border-color: white; " +
            "-fx-border-width: 2; " +
            "-fx-background-radius: 6; " +
            "-fx-border-radius: 6; "
        );
        pauseButton.setLayoutX(880 );pauseButton.setLayoutY(10);// pause button position set- TOP right

        pauseButton.setOnAction(e->
        {
            isPaused = !isPaused;

            if (isPaused) {
                pauseButton.setText("RESUME");
                this.requestFocus();
            } else {
                pauseButton.setText("PAUSE");
                this.requestFocus();
            }
        });         
        //  Pause Butoton Action Event added- toggles between pausing and resuming the game loop, and updates the button text accordingly.  

        this.getChildren().addAll(canvas,scoreText,pauseButton);
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
        if (easy.isSelected()) PipeGap = 250;
        else if (medium.isSelected()) PipeGap = 200;
        else if (hard.isSelected()) PipeGap = 150;
        restartGame();
    }

   @FXML
    private void ChooseChar() {
        if(characterOneRadioButton.isSelected()) {
            avatarImg = new Image(getClass().getResource("/com/jethop/Images/CharacterOne.png").toExternalForm());
        } 
        else if(characterTwoRadioButton.isSelected()) {
            avatarImg = new Image(getClass().getResource("/com/jethop/Images/CharacterTwo.png").toExternalForm());
        }

        if(avatar != null)
            avatar.img = avatarImg;

        restartGame();
    }

    @FXML
    private void ChooseScene() {
        if(sceneOneRadioButton.isSelected()) {
            backgroundImg = new Image(getClass().getResource("/com/jethop/Images/BackgroundOne.png").toExternalForm());
        } 
        else if(sceneTwoRadioButton.isSelected()) {
            backgroundImg = new Image(getClass().getResource("/com/jethop/Images/BackgroundTwo.png").toExternalForm());
        }
        restartGame();
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

    // The setupControls method is responsible for handling user input. It listens for key presses and performs actions based on the key pressed. In this case, it checks for the spacebar to make the avatar jump and also checks for the 'P' key to toggle the pause state of the game. If the game is paused, it prevents any further actions until it is resumed.
    public void setupControls(Scene scene) {
    scene.setOnKeyPressed(e -> {

        if (e.getCode() == KeyCode.ESCAPE) {
            isPaused = !isPaused;
            pauseButton.setText(isPaused ? "RESUME" : "PAUSE");
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

//Start GameLoop is responsible for the main game loop, which continuously updates the game state and renders the graphics. It uses an AnimationTimer to call the handle method repeatedly, allowing for smooth animations and real-time updates. The loop checks if the game is paused and, if so, it only renders the current state without updating the game logic. If the game is not paused, it updates the avatar's position, spawns and moves pipes, checks for collisions, and renders the updated game state on each frame.
public void startGameLoop() {
    gameLoop = new AnimationTimer() {
        @Override
        public void handle(long now) {

            if (isPaused) {
                render(); // keep screen visible
                return;
            }

            updateAvatar();
            spawnAndMovePipes();
            checkCollisions();
            render();
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
        int width = 40;
        int height = 55;
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
