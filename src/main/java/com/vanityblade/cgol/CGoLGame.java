package com.vanityblade.cgol;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class CGoLGame extends Application {
    //Layout
    VBox gameRoot = new VBox(); //The game window is arranged in this
    //Big picture layout
    GameBoard gameBoard;
    GameInfoBar gameInfoBar;
    //Bottom area layout
    CGOLButtonBox buttonContainer;
    File lastLoadedFile = null;

    //Top area layout

    @Override
    public void start(Stage stage) {
        //Board initialization
        gameBoard = new GameBoard();
        gameInfoBar = new GameInfoBar(gameBoard.getWidth());
        gameRoot.setSpacing(0);
        gameRoot.setStyle("-fx-background-color: #D1D1D1");
        gameRoot.setAlignment(Pos.CENTER);
        /* Bottom area setup */
        buttonContainer = new CGOLButtonBox(Math.max(256, gameBoard.getWidth()));
        System.out.println(gameBoard.getWidth());

        resetGameRootChildren();

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
        buttonContainer.bStep.setOnMouseClicked(e -> {
            if (buttonContainer.bStep.isNotEnabled()) return;
            gameBoard.step();
        }); //Step button
        buttonContainer.bLoad.setOnMouseClicked(e -> {
            if (buttonContainer.bLoad.isNotEnabled()) return;
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File("src/main/resources/FileResources"));
            File file = fileChooser.showOpenDialog(new Stage());

            if (file == null) return; //If the user clicks cancel or the file has incorrect permissions nothing happens.

            gameBoard = new GameBoard(file);
            gameInfoBar = new GameInfoBar(gameBoard.getWidth());
            resetGameRootChildren();
            stage.sizeToScene();
            lastLoadedFile = file;
        });
        buttonContainer.bReset.setOnMouseClicked(e -> {
            if (buttonContainer.bReset.isNotEnabled()) return;
            if(lastLoadedFile == null){
                gameBoard = new GameBoard(gameBoard.getRows(), gameBoard.getCols(), gameBoard.maxGenerations, gameBoard.targetNumberCells);
            } else {
                gameBoard = new GameBoard(lastLoadedFile);
            }
            gameInfoBar = new GameInfoBar(gameBoard.getWidth());
            resetGameRootChildren();
            stage.sizeToScene();
        });
        buttonContainer.bStart.setOnMouseClicked(e -> {
            if (buttonContainer.bStart.isNotEnabled()) return;
            animationTimer.start();
            gameBoard.setClickPlacementMode(GameBoard.CLICK_PLACEMENT_MODE.DISABLE);
            buttonContainer.flipStartStop();
        });
        buttonContainer.bStop.setOnMouseClicked(e -> {
            if (buttonContainer.bStop.isNotEnabled()) return;
            animationTimer.stop();
            gameBoard.setClickPlacementMode(GameBoard.CLICK_PLACEMENT_MODE.UNRESTRICTED);
            buttonContainer.flipStartStop();
        });
    }

    //Required after creating a new instance of one of the contained views (i.e. after loading a new board)
    public void resetGameRootChildren() {
        gameRoot.getChildren().clear();
        gameRoot.getChildren().addAll(gameInfoBar, gameBoard, buttonContainer);
        buttonContainer.relocateButtons(Math.max(256, gameBoard.getWidth()));


    }

    public static void main(String[] args) {
        launch();
    }
}