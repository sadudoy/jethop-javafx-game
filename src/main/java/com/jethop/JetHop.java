package com.jethop;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class JetHop extends Pane{
    int boardWidth = 960;
    int boardHeight = 600;

    Canvas canvas;
    GraphicsContext gc;

    Image backgroundImg;
    Image avatarImg;
    Image pipeImg;

    public JetHop() {
        canvas = new Canvas(boardWidth, boardHeight);
        gc = canvas.getGraphicsContext2D();
        this.getChildren().add(canvas); 

        backgroundImg = new Image(getClass().getResource("/com/jethop/BackgroundOne.png").toExternalForm());
        avatarImg = new Image(getClass().getResource("/com/jethop/CharacterOne.png").toExternalForm());
        pipeImg = new Image(getClass().getResource("/com/jethop/TopPipe.png").toExternalForm());
        
        // avatar and pipe variable will go here
    }

    public void render() {
        gc.clearRect(0, 0, boardWidth, boardHeight);
        gc.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight);
        gc.drawImage(avatar.img, avatar.x, avatar.y, avatar.width, avatar.height);
    }
}
