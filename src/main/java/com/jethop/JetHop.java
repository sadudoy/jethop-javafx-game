package com.jethop;

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

    public JetHop() {
        canvas = new Canvas(boardWidth, boardHeight);
        gc = canvas.getGraphicsContext2D();
        this.getChildren().add(canvas);

        backgroundImg = new Image(getClass().getResource("/com/jethop/BackgroundOne.png").toExternalForm());
        avatarImg = new Image(getClass().getResource("/com/jethop/CharacterOne.png").toExternalForm());
        pipeImg = new Image(getClass().getResource("/com/jethop/TopPipe.png").toExternalForm());

        // avatar and pipe variable will go here
        avatar = new Avatar(avatarImg);
    }

    public void render() {
        gc.clearRect(0, 0, boardWidth, boardHeight);
        gc.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight);
        gc.drawImage(avatar.img, avatar.x, avatar.y, avatar.width, avatar.height);
    }

    public void updateAvatar() {
         if (gameOver) return; 

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
                }
                    else {
                    velocityY = JUMP_STRENGTH;
                }
            }
        });
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
}
