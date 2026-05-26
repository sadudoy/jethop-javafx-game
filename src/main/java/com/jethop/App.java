package com.jethop;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        JetHop game = new JetHop();
        Scene scene = new Scene(game, game.boardWidth, game.boardHeight);

        game.setupControls(scene);

        stage.setTitle("JETHOP");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        game.requestFocus(); 

        game.startGameLoop();
    }

    public static void main(String[] args) {
        
    }
}