package com.vanityblade.cgol;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class CGoLGame extends Application {
    //Layout
    VBox gameRoot = new VBox(); //The game window is arranged in this
    //Big picture layout
    GameBoard gameBoard;
    BorderPane bottomArea; //Contains things besides the game area that the user can interact with
    GameInfoBar gameInfoBar;
    //Bottom area layout
    HBox buttons; //These buttons are generally for testing and should be replaced with something better.
    Button stepButton = new Button("step");
    Button randomizeButton = new Button("randomize");
    Button animatorButton = new Button("Go!");
    Button loadButton = new Button("load");
    //Top area layout

    @Override
    public void start(Stage stage) {
        //Board initialization
        gameBoard = new GameBoard();
        bottomArea = new BorderPane();
        gameInfoBar = new GameInfoBar(gameBoard.getWidth());
        gameRoot.setSpacing(0);
        gameRoot.setStyle("-fx-background-color: #D1D1D1");
        gameRoot.setAlignment(Pos.CENTER);
        resetGameRootChildren();
        /* Bottom area setup */
        buttons = new HBox(stepButton, randomizeButton, animatorButton, loadButton); //Represent some actions the player can take
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
        stage.setResizable(false);
        stage.show();

        //Animator
        AnimationTimer animationTimer = new AnimationTimer() {
            private long lastTime = 0;

            @Override
            public void handle(long l) {
                long timeSinceLastUpdate = l - lastTime;
                if (timeSinceLastUpdate > 330000000L) {
                    lastTime = l;
                    gameBoard.step();
                }
            }
        };

        /* Event handlers */
        //Button stuff
        stepButton.setOnAction(event -> gameBoard.step()); //Step button
        randomizeButton.setOnAction(event -> gameBoard.randomize()); //Randomize the board
        animatorButton.setOnAction(event -> {
            if (ref.autoMode) { //Turning off the auto runner
                animatorButton.setText("Go!");
                ref.autoMode = false;
                animationTimer.stop();
                gameBoard.setClickPlacementMode(GameBoard.CLICK_PLACEMENT_MODE.UNRESTRICTED);
            } else {
                animatorButton.setText("Stop!");
                ref.autoMode = true;
                animationTimer.start();
                gameBoard.setClickPlacementMode(GameBoard.CLICK_PLACEMENT_MODE.DISABLE);
            }
        });
        loadButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File("src/main/resources/FileResources"));
            File file = fileChooser.showOpenDialog(new Stage());

            if(file == null) return; //If the user clicks cancel or the file has incorrect permissions nothing happens.

            gameBoard = new GameBoard(file);
            gameInfoBar = new GameInfoBar(gameBoard.getWidth());
            resetGameRootChildren();
            stage.sizeToScene();
        });

        //TODO: Delete this, it's just a test of the number system
        gameRoot.setOnScroll(scrollEvent -> {
            gameInfoBar.setTimeLeft((int) scrollEvent.getDeltaY() + gameInfoBar.getTimeLeft());
            gameInfoBar.setTargetCellsLeft((int) scrollEvent.getDeltaY() + gameInfoBar.getTargetCellsLeft());
        });
    }

    //Required after creating a new instance of one of the contained views (i.e. after loading a new board)
    public void resetGameRootChildren(){
        gameRoot.getChildren().clear();
        gameRoot.getChildren().addAll(gameInfoBar, gameBoard, bottomArea);
    }

    public static void main(String[] args) {
        launch();
    }
}