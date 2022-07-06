package com.vanityblade.cgol;

import javafx.scene.canvas.Canvas;

public class GameBoard extends Canvas {
    private int rows;
    private int cols;
    private GameCell[][] board;

    public GameBoard() {
        this(80, 60);
    }

    public GameBoard(int rows, int cols) {
        super();
        this.rows = Math.max(1, rows);
        this.cols = Math.max(1, cols);
        super.setHeight(cols * 13);
        super.setWidth(rows * 13);
        board = new GameCell[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                board[r][c] = new GameCell();
            }
        }
    }

    //Render the current board
    private void render() {
        //TODO: render the current board
        //  This means that for each cell in the board, an image from CellStateSprites.png is rendered
        //   depending on the state of the cell and its neighbors.
    }

    //Step forward in time, using the next board.
    public void step() {
        board = calcNextBoardVisuals(); //updateBoard() requires calcNextBoardVisuals().
        board = updateBoard();
        render();
    }

    //Show the current board's state as well as how it will look in the next step.
    // Specifically, this does not step forward like step().
    public void show(){
        board = calcNextBoardVisuals();
        render();
    }

    //Calculate the next board
    private GameCell[][] updateBoard() {
        GameCell[][] newBoard = new GameCell[rows][cols];
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                GameCell cell = new GameCell(getCell(x,y));
                //Set the next state of this cell
                STATES state = switch (cell.getState()) {
                    case UNFILLED, NO_GO, SOON_UNFILLED -> STATES.UNFILLED;
                    case FILLED, PLACED, SOON_FILLED -> STATES.FILLED;
                };
                //4 cases for neighbors
                NEIGHBORS neighbors;
                STATES rightState = board[x+1][y].getState();
                STATES downState = board[x][y+1].getState();
                if(rightState == STATES.FILLED || rightState == STATES.SOON_UNFILLED || rightState == STATES.PLACED){
                    neighbors = NEIGHBORS.RIGHT;
                } else {
                    neighbors = NEIGHBORS.NONE;
                }
                if(downState == STATES.FILLED || rightState == STATES.SOON_UNFILLED || rightState == STATES.PLACED){
                    if(neighbors == NEIGHBORS.RIGHT) {
                        neighbors = NEIGHBORS.BOTH;
                    } else {
                        neighbors = NEIGHBORS.DOWN;
                    }
                }
                cell.setState(state);
                cell.setNeighbors(neighbors);
                newBoard[x][y] = cell;
            }
        }
        return newBoard;
    }

    private GameCell[][] calcNextBoardVisuals() {
        //TODO: This function will calculate the next "step" of the board's VISUAL CUES.
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {

            }
        }
        return null;
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
        return board[(x % rows + rows) % rows][(y % cols + cols) & cols];
    }


}