package com.vanityblade.cgol;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

public class CGoLGame extends Application {
    GameBoard gameBoard;
    HBox bottomBar;
    BorderPane root;

    @Override
    public void start(Stage stage) throws IOException {
        gameBoard = new GameBoard();
        bottomBar = new HBox();
        root = new BorderPane(gameBoard);
        root.setBottom(bottomBar);
        Scene scene = new Scene(root);
        stage.setTitle("C-GoL: The Competitive Game of Life!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}