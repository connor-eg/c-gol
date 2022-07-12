package com.vanityblade.cgol;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class CGoLGame extends Application {
    //Layout
    BorderPane gameRoot = new BorderPane(); //The game window is arranged in this
    //Big picture layout
    GameBoard gameBoard;
    BorderPane bottomArea; //Contains things besides the game area that the user can interact with
    GameInfoBar gameInfoBar;
    //Bottom area layout
    HBox buttons; //These buttons are generally for testing and should be replaced with something better.
    Button stepButton = new Button("step");
    Button randomizeButton = new Button("randomize");
    Button animatorButton = new Button("Go!");
    //Top area layout

    @Override
    public void start(Stage stage) {
        //Board initialization
        gameBoard = new GameBoard();
        bottomArea = new BorderPane();
        gameInfoBar = new GameInfoBar(gameBoard.getWidth());
        gameRoot.setTop(gameInfoBar); //The game info (generations left/target cells) is placed at the top
        gameRoot.setCenter(gameBoard); //The canvas is placed in the center of the game board
        gameRoot.setBottom(bottomArea); //The player's controls are down here
        /* Bottom area setup */
        buttons = new HBox(stepButton, randomizeButton, animatorButton); //Represent some actions the player can take
        bottomArea.setBottom(buttons); //Add the buttons to the bottom area
        buttons.setStyle("-fx-padding: 6px");
        buttons.setSpacing(4);
        //Other variables
        var ref = new Object() { //Pieces used in lambda expressions
            boolean autoMode = false; //Whether the game is currently playing itself
        };

        //Scene initialization
        Scene scene = new Scene(gameRoot);
        stage.setTitle("C-GoL: The Competitive Game of Life!");
        stage.setScene(scene);
        stage.show();

        //Animator
        AnimationTimer animationTimer = new AnimationTimer() {
            private long timeSinceLastUpdate;
            private long lastTime = 0;
            @Override
            public void handle(long l) {
                if(lastTime == 0) {
                    lastTime = l;
                    return;
                }
                timeSinceLastUpdate = l - lastTime;
                if(timeSinceLastUpdate > 200000000L){
                    lastTime = l;
                    gameBoard.step();
                }
            }
        };

        /* Event handlers */
        //Button stuff
        stepButton.setOnAction(event -> gameBoard.step()); //Step button FIXME: replace with GO button that auto-steps
        randomizeButton.setOnAction(event -> gameBoard.randomize()); //Randomize the board TODO: maybe keep this???
        animatorButton.setOnAction(event -> {
            if(ref.autoMode) { //Turning off the auto runner
                animatorButton.setText("Go!");
                ref.autoMode = false;
                animationTimer.stop();
            } else {
                animatorButton.setText("Stop!");
                ref.autoMode = true;
                animationTimer.start();
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}