package com.vanityblade.cgol;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

public class CGoLGame extends Application {
    //Large-scale layout
    BorderPane root = new BorderPane();
    GameBoard gameBoard;
    BorderPane bottomArea;
    //Bottom area layout
    HBox buttons;
    Button stepButton = new Button("step");
    Button randomizeButton = new Button("randomize");

    @Override
    public void start(Stage stage) {
        //Board initialization
        gameBoard = new GameBoard();
        bottomArea = new BorderPane();
        //TODO: create a top element that shows the remaining number of steps and empty square goal
        root.setCenter(gameBoard); //The canvas is placed in the center of the game board
        root.setBottom(bottomArea); //The player's controls are down here
        /* Bottom area setup */
        buttons = new HBox(stepButton, randomizeButton); //Represent some actions the player can take
        bottomArea.setBottom(buttons); //Add the buttons to the bottom area
        buttons.setStyle("-fx-padding: 5px");

        //Scene initialization
        Scene scene = new Scene(root);
        stage.setTitle("C-GoL: The Competitive Game of Life!");
        stage.setScene(scene);
        stage.show();

        /* Event handlers */
        stepButton.setOnAction(event -> gameBoard.step()); //Step button FIXME: replace with GO button that auto-steps
        randomizeButton.setOnAction(event -> gameBoard.randomize()); //Randomize the board TODO: maybe keep this???
    }

    public static void main(String[] args) {
        launch();
    }
}