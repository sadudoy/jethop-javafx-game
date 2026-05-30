package com.jethop;

import javafx.scene.control.Button;//added
import java.util.ArrayList;
import java.util.Random;
import javafx.animation.AnimationTimer;
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

    

    pauseButton=new Button("Pause");    //pause button added

    pauseButton.setLayoutX(10);pauseButton.setLayoutY(10);// pause button position set- TOP Left

    pauseButton.setOnAction(e->
    {
        isPaused = !isPaused;

        if (isPaused) {
            pauseButton.setText("Resume");
        } else {
            pauseButton.setText("Pause");
        }
    });         
    //  Pause Butoton Action Event added- toggles between pausing and resuming the game loop, and updates the button text accordingly.  

    this.getChildren().addAll(canvas,scoreText,pauseButton);

    

    public JetHop() {
        canvas = new Canvas(boardWidth, boardHeight);
        gc = canvas.getGraphicsContext2D();
        this.getChildren().addAll(canvas, scoreText);

        backgroundImg = new Image(getClass().getResource("/com/jethop/Images/BackgroundOne.png").toExternalForm());
        avatarImg = new Image(getClass().getResource("/com/jethop/Images/CharacterOne.png").toExternalForm());
        pipeImg = new Image(getClass().getResource("/com/jethop/Images/Pipe.png").toExternalForm());

        avatar = new Avatar(avatarImg);
        pipes = new ArrayList<>();
    }

    public void render() {
        gc.clearRect(0, 0, boardWidth, boardHeight);
        gc.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight);
        gc.drawImage(avatar.img, avatar.x, avatar.y, avatar.width, avatar.height);
        for (Pipe p : pipes) {
            gc.drawImage(p.img, p.x, p.y, p.width, p.height);
        }
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Areal", FontWeight.BOLD, 25));
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

    // public void setupControls(Scene scene) {
    //     scene.setOnKeyPressed(e -> {
    //         if (e.getCode() == KeyCode.SPACE) {
    //             if (gameOver) {
    //                 restartGame();
    //             } else {
    //                 velocityY = JUMP_STRENGTH;
    //             }
    //         }
    //     });
    // }

    // The setupControls method is responsible for handling user input. It listens for key presses and performs actions based on the key pressed. In this case, it checks for the spacebar to make the avatar jump and also checks for the 'P' key to toggle the pause state of the game. If the game is paused, it prevents any further actions until it is resumed.
    public void setupControls(Scene scene) {
    scene.setOnKeyPressed(e -> {

        if (e.getCode() == KeyCode.P) {
            isPaused = !isPaused;
            pauseButton.setText(isPaused ? "Resume" : "Pause");
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
    // public void startGameLoop() {
    //     gameLoop = new AnimationTimer() {
    //         @Override
    //         public void handle(long now) {
    //             updateAvatar();
    //             spawnAndMovePipes();
    //             checkCollisions();
    //             render();
    //         }
    //     };
    //     gameLoop.start();
    // }


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
            pipes.add(new Pipe(boardWidth, randomY + 250, 60, boardHeight -
                    (randomY + 150), pipeImg));
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
