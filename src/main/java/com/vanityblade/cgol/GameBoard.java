package com.vanityblade.cgol;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class GameBoard extends Canvas {
    private final int rows;
    private final int cols;
    private GameCell[][] board;

    public GameBoard() {
        this(16, 16);
    }

    public GameBoard(int rows, int cols) {
        super();
        this.rows = Math.max(1, rows);
        this.cols = Math.max(1, cols);
        super.setHeight(cols * 16 + 8);
        super.setWidth(rows * 16 + 8);
        board = new GameCell[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                board[r][c] = new GameCell();
            }
        }
        //Fill so that the outer edges are black
        GraphicsContext g = getGraphicsContext2D();
        g.setFill(Color.BLACK);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        //Draw an initially empty board
        render();
    }

    //Render the current board
    private void render() {
        //No need to clear the canvas because the entire canvas will be drawn over.
        GraphicsContext g = getGraphicsContext2D();
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream("src/main/resources/ImageAssets/CellStateSprites.png");
        } catch (FileNotFoundException e) {
            fileInputStream = null;
        }
        assert fileInputStream != null;
        Image cellSprites = new Image(fileInputStream);
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                g.drawImage(cellSprites, 16 * getCell(x, y).getState().ordinal(), 0, 16, 16, x * 16 + 4, y * 16 + 4, 16, 16);
            }
        }
    }

    //Step forward in time, using the next board.
    public void step() {
        board = updateBoard();
        show();
    }

    //Show the current board's state as well as how it will look in the next step.
    // Specifically, this does not step forward like step().
    public void show() {
        board = calcNextBoardVisuals();
        render();
    }

    //Calculate the next board
    private GameCell[][] updateBoard() {
        GameCell[][] newBoard = new GameCell[rows][cols];
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                GameCell cell = new GameCell(getCell(x, y));
                //Set the next state of this cell
                STATES state = switch (cell.getState()) {
                    case UNFILLED, SOON_UNFILLED -> STATES.UNFILLED;
                    case FILLED, PLACED, SOON_FILLED -> STATES.FILLED;
                    /* NO_GO has to be handled separately because its state is not updated by the visual update,
                     and can be either filled or not depending on neighbor count. */
                    case NO_GO -> countNeighbors(x, y) == 3 ? STATES.FILLED : STATES.UNFILLED;
                };
                cell.setState(state);
                newBoard[x][y] = cell;
            }
        }
        return newBoard;
    }

    private GameCell[][] calcNextBoardVisuals() {
        GameCell[][] newBoard = new GameCell[rows][cols];
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                newBoard[x][y] = calcNextCellVisual(x, y);
            }
        }
        return newBoard;
    }

    //Calculate a cell with the next state for a given cell on the board
    //This returns a reference to a new cell, not an existing one on the board.
    private GameCell calcNextCellVisual(int x, int y) {
        GameCell gameCell = new GameCell(getCell(x, y));
        int neighbors = countNeighbors(x, y);
        switch (gameCell.getState()) {
            case FILLED -> { //Cell is currently filled and shows that it will not be unfilled soon
                if (neighbors > 3 || neighbors < 2) gameCell.setState(STATES.SOON_UNFILLED);
            }
            case UNFILLED -> { //Cell is currently empty and shows that it will not be filled soon
                if (neighbors == 3) gameCell.setState(STATES.SOON_FILLED);
            }
            case SOON_FILLED -> { //Cell is currently empty but shows that it is about to be filled
                if (neighbors != 3) gameCell.setState(STATES.UNFILLED);
            }
            case SOON_UNFILLED -> { //Cell is currently filled but shows that it is about to be unfilled
                if (neighbors <= 3 && neighbors >= 2) gameCell.setState(STATES.FILLED);
            }
            default -> {
            } //State is either PLACED or NO_GO, which have special functionality not handled here
        }
        return gameCell;
    }

    //Count the number of neighbors for a given cell
    private int countNeighbors(int x, int y) {
        int count = 0;
        for (int neiX = x - 1; neiX <= x + 1; neiX++) {
            for (int neiY = y - 1; neiY <= y + 1; neiY++) {
                if (neiX == x && neiY == y) continue; //Don't count a cell as its own neighbor
                STATES state = getCell(neiX, neiY).getState();
                if (state == STATES.FILLED || state == STATES.SOON_UNFILLED || state == STATES.PLACED) {
                    count++;
                }
            }
        }
        return count;
    }


    /**
     * Returns a reference to the cell at the given position
     * (0, _) is a leftmost cell, (_, 0) is a topmost cell
     * Negative values and overlarge values get wrapped (e.g. (-1, _) == (rows - 1, _))
     */
    public GameCell getCell(int x, int y) {
        return board[(x % rows + rows) % rows][(y % cols + cols) % cols];
    }


    public void randomize() {
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                int rand = (int) (Math.random() * 2);
                if (rand == 0) {
                    getCell(x, y).setState(STATES.UNFILLED);
                } else {
                    getCell(x, y).setState(STATES.FILLED);
                }
            }
        }
        show();
    }
}