package com.vanityblade.cgol;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
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

    AnimationTimer animationTimer; //Animation timer has to be handled globally because of scope issues
    @Override
    public void start(Stage stage) {
        //Board initialization
        gameBoard = new GameBoard();
        gameInfoBar = new GameInfoBar(gameBoard.getWidth(), gameBoard.maxGenerations, gameBoard.targetNumberCells);
        gameRoot.setSpacing(0);
        gameRoot.setStyle("-fx-background-color: #D1D1D1");
        gameRoot.setAlignment(Pos.CENTER);
        /* Bottom area setup */
        buttonContainer = new CGOLButtonBox(Math.max(256, gameBoard.getWidth()));

        resetGameRootChildren();

        //Scene initialization
        Scene scene = new Scene(gameRoot);
        stage.setTitle("C-GoL: The Competitive Game of Life!");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        //Animator
        animationTimer = new AnimationTimer() {
            private long lastTime = 0;

            @Override
            public void handle(long l) {
                long timeSinceLastUpdate = l - lastTime;
                if (timeSinceLastUpdate > 330000000L) {
                    lastTime = l;
                    handleGameStep();
                }
            }
        };

        /* Event handlers */
        //Button stuff
        buttonContainer.bStep.setOnMouseClicked(event -> {
            if (buttonContainer.bStep.isNotEnabled()) return;
            if(gameBoard.maxGenerations != -1) gameBoard.setClickPlacementMode(GameBoard.CLICK_PLACEMENT_MODE.DISABLE);
            handleGameStep();
        }); //Step button
        buttonContainer.bLoad.setOnMouseClicked(event -> {
            if (buttonContainer.bLoad.isNotEnabled()) return;
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File("src/main/resources/SavedGames"));
            File file = fileChooser.showOpenDialog(new Stage());

            if (file == null) return; //If the user clicks cancel or the file has incorrect permissions nothing happens.

            gameBoard = new GameBoard(file);
            gameInfoBar = new GameInfoBar(gameBoard.getWidth(), gameBoard.maxGenerations, gameBoard.maxGenerations);
            resetGameRootChildren();
            stage.sizeToScene();
            lastLoadedFile = file;
            if (buttonContainer.getStopVisible()) buttonStopHelper(); //Stops the animation from happening on level load.
            buttonContainer.bStep.setEnableState(true);
            buttonContainer.bStart.setEnableState(true);
            gameInfoBar.setTargetCellsLeft(gameBoard.countNumFilledCells() - gameBoard.targetNumberCells);
        });
        buttonContainer.bReset.setOnMouseClicked(event -> {
            if (buttonContainer.bReset.isNotEnabled()) return;
            if (lastLoadedFile == null) {
                gameBoard = new GameBoard(gameBoard.getRows(), gameBoard.getCols(), gameBoard.maxGenerations, gameBoard.targetNumberCells);
            } else {
                gameBoard = new GameBoard(lastLoadedFile);
            }
            gameInfoBar = new GameInfoBar(gameBoard.getWidth(), gameBoard.maxGenerations, gameBoard.targetNumberCells);
            resetGameRootChildren();
            stage.sizeToScene();
            if (buttonContainer.getStopVisible()) buttonStopHelper(); //Stops the animation from happening on level load.
            buttonContainer.bStep.setEnableState(true);
            buttonContainer.bStart.setEnableState(true);
            gameInfoBar.setTargetCellsLeft(gameBoard.countNumFilledCells() - gameBoard.targetNumberCells);
        });
        buttonContainer.bStart.setOnMouseClicked(event -> {
            if (buttonContainer.bStart.isNotEnabled()) return;
            buttonStartHelper();
        });
        buttonContainer.bStop.setOnMouseClicked(event -> {
            if (buttonContainer.bStop.isNotEnabled()) return;
            buttonStopHelper();
        });
        buttonContainer.bSave.setOnMouseClicked(event -> {
            if(buttonContainer.bSave.isNotEnabled()) return;
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File("src/main/resources/SavedGames"));
            File file = fileChooser.showSaveDialog(new Stage());
            if(file == null) return;
            TextInputDialog maxGenDialog = new TextInputDialog("-1");
            TextInputDialog targetCellDialog = new TextInputDialog("-1");
            maxGenDialog.setHeaderText("Number of turns the player has to empty the grid (0 or less for Create Mode)");
            targetCellDialog.setHeaderText("Target number of cells the player must get the board down to (must be non-negative)");
            maxGenDialog.showAndWait();
            try{
                if(Integer.parseInt(maxGenDialog.getResult()) <= 0) {
                    gameBoard.saveToFile(file, -1, -1);
                } else {
                    targetCellDialog.showAndWait();
                    gameBoard.saveToFile(file, Integer.parseInt(maxGenDialog.getResult()), Math.max(0, Integer.parseInt(targetCellDialog.getResult())));
                }
            } catch(NumberFormatException e){
                gameBoard.saveToFile(file, -1, -1);
            }
        });
    }

    private void buttonStopHelper() {
        animationTimer.stop();
        if(gameBoard.maxGenerations == -1) gameBoard.setClickPlacementMode(GameBoard.CLICK_PLACEMENT_MODE.UNRESTRICTED);
        else gameBoard.setClickPlacementMode(GameBoard.CLICK_PLACEMENT_MODE.DISABLE);
        buttonContainer.flipStartStop();
        buttonContainer.bStep.setEnableState(true);
    }

    private void buttonStartHelper() {
        animationTimer.start();
        gameBoard.setClickPlacementMode(GameBoard.CLICK_PLACEMENT_MODE.DISABLE);
        buttonContainer.flipStartStop();
        buttonContainer.bStep.setEnableState(false);
    }

    //Handling a gameBoard step
    private void handleGameStep() {
        if (gameBoard.maxGenerations == -1) { //In this mode, there's no special handling.
            gameBoard.step();
        } else {
            if (gameInfoBar.getTimeLeft() > 0) { //The game is still running
                gameBoard.step();
                gameInfoBar.setTimeLeft(gameInfoBar.getTimeLeft() - 1);
                gameInfoBar.setTargetCellsLeft(gameBoard.countNumFilledCells() - gameBoard.targetNumberCells);
                if(gameInfoBar.getTargetCellsLeft() <= 0){
                    if(buttonContainer.getStopVisible()) buttonStopHelper();
                    buttonContainer.bStep.setEnableState(false);
                    buttonContainer.bStart.setEnableState(false);
                }
            } else {
                if(buttonContainer.getStopVisible()) buttonStopHelper();
                buttonContainer.bStep.setEnableState(false);
                buttonContainer.bStart.setEnableState(false);
            }
        }
    }

    //Required after creating a new instance of one of the contained views (i.e. after loading a new board)
    private void resetGameRootChildren() {
        gameRoot.getChildren().clear();
        gameRoot.getChildren().addAll(gameInfoBar, gameBoard, buttonContainer);
        buttonContainer.relocateButtons(Math.max(256, gameBoard.getWidth()));
    }

    public static void main(String[] args) {
        launch();
    }
}