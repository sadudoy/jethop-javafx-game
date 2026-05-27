package com.jethop;

import java.util.ArrayList;
import java.util.Random;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

public class JetHop extends Pane {
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
    ArrayList<Pipe> Pipes;
    AnimationTimer gameLoop;
    Random random = new Random();
    int frameCount = 0;
    final int PIPE_SPEED = 3;

    public JetHop() {
        canvas = new Canvas(boardWidth, boardHeight);
        gc = canvas.getGraphicsContext2D();
        this.getChildren().add(canvas);

        backgroundImg = new Image(getClass().getResource("/com/jethop/BackgroundOne.png").toExternalForm());
        avatarImg = new Image(getClass().getResource("/com/jethop/CharacterOne.png").toExternalForm());
        pipeImg = new Image(getClass().getResource("/com/jethop/TopPipe.png").toExternalForm());

        // avatar and pipe variable will go here
        avatar = new Avatar(avatarImg);
        pipes = new ArrayList<>();
    }

    public void render() {
        gc.clearRect(0, 0, boardWidth, boardHeight);
        gc.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight);
        gc.drawImage(avatar.img, avatar.x, avatar.y, avatar.width, avatar.height);
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
                updateAvatar(); // Member 2
                spawnAndMovePipes(); // Member 3
                // checkCollisions(); // Member 4
                render(); // Member 1
            }
        };

        gameLoop.start();
    }

    public void spawnAndMovePipes() {
        if (gameOver)
            return;
        frameCount++;
        if (frameCount % 100 == 0) {
            double randomY = random.nextInt(300) + 100;
            pipes.add(new Pipe(boardWidth, 0, 60, randomY, pipeImg));
            pipes.add(new Pipe(boardWidth, randomY + 150, 60, boardHeight -
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
        int height = 30;
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
