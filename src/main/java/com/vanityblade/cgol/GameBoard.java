package com.vanityblade.cgol;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.*;

public class GameBoard extends Canvas {
    private final int rows;
    private final int cols;
    private GameCell[][] board;
    private double mouseX = 0;
    private double mouseY = 0;
    public final int maxGenerations;
    public final int targetNumberCells;
    private CLICK_PLACEMENT_MODE clickPlacementMode = CLICK_PLACEMENT_MODE.UNRESTRICTED;

    public enum CLICK_PLACEMENT_MODE {UNRESTRICTED, RESTRICTED, DISABLE}

    public GameBoard() {
        this(16, 16, -1, -1);
    }

    public GameBoard(int rows, int cols, int maxGenerations, int targetNumberCells) {
        super();
        this.rows = Math.max(3, rows);
        this.cols = Math.max(3, cols);
        this.maxGenerations = maxGenerations;
        this.targetNumberCells = targetNumberCells;
        super.setHeight(cols * 16 + 2);
        super.setWidth(rows * 16 + 2);
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

        //Event handling initialization
        setOnMouseMoved(event -> {
            mouseX = event.getX();
            mouseY = event.getY();
        });
        setOnMouseClicked(event -> handleClick());

        //Draw an initially empty board
        render();
    }

    /**
     * Create a new GameBoard using information from a file
     * Details for how a file is set up can be found in src/main/resources/FileResources
     *
     * @param file The file to be read; this does not perform checks for file correctness.
     */
    public GameBoard(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            rows = Integer.parseInt(reader.readLine());
            cols = Integer.parseInt(reader.readLine());
            maxGenerations = Integer.parseInt(reader.readLine());
            targetNumberCells = Integer.parseInt(reader.readLine());
            board = new GameCell[rows][cols];
            for (int y = 0; y < cols; y++) {
                String currentRow = reader.readLine();
                for (int x = 0; x < rows; x++) {
                    STATES newCellState = switch (Integer.parseInt(currentRow.substring(x, x + 1))) {
                        case 0 -> STATES.UNFILLED;
                        case 1 -> STATES.FILLED;
                        default -> STATES.NO_GO;
                    };
                    board[x][y] = new GameCell(newCellState);
                }
            }
            clickPlacementMode = maxGenerations == -1? CLICK_PLACEMENT_MODE.UNRESTRICTED : CLICK_PLACEMENT_MODE.RESTRICTED;

            super.setHeight(cols * 16 + 2);
            super.setWidth(rows * 16 + 2);

            //Drawing this board
            GraphicsContext g = getGraphicsContext2D();
            g.setFill(Color.BLACK);
            g.fillRect(0, 0, this.getWidth(), this.getHeight()); //Outer rectangle

            //Event handling initialization
            setOnMouseMoved(event -> {
                mouseX = event.getX();
                mouseY = event.getY();
            });
            setOnMouseClicked(event -> handleClick());
            reader.close();

            //Draw the initial state of this board
            show();
        } catch (IOException e) {
            System.out.println("Something went wrong.");
            throw new RuntimeException(e);
        }
    }

    //Render the current board
    private void render() {
        //No need to clear the canvas because the entire canvas will be drawn over.
        GraphicsContext g = getGraphicsContext2D();
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream("src/main/resources/ImageAssets/cgol_cellSprites.png");
        } catch (FileNotFoundException e) {
            fileInputStream = null;
        }
        assert fileInputStream != null;
        Image cellSprites = new Image(fileInputStream);
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                g.drawImage(cellSprites, 16 * getCell(x, y).getState().ordinal(), 0, 16, 16, x * 16 + 1, y * 16 + 1, 16, 16);
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
                    case FILLED, SOON_FILLED -> STATES.FILLED;
                    //No-go and placed have to be updated separately because their states are ambiguous.
                    case PLACED ->
                            countNeighbors(x, y) == 2 || countNeighbors(x, y) == 3 ? STATES.FILLED : STATES.UNFILLED;
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

    //Returns the number of cells in the board that are filled.
    public int countNumFilledCells() {
        int count = 0;
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                STATES temp = getCell(x, y).getState();
                if (temp == STATES.FILLED || temp == STATES.SOON_UNFILLED || temp == STATES.PLACED) count++;
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

    //On-click handler; this messes with the state of the blocks on the canvas
    public void handleClick() {
        if (clickPlacementMode == CLICK_PLACEMENT_MODE.DISABLE) return; //If user interaction is disabled, do nothing
        int cellX = (int) (mouseX - 1) / 16;
        int cellY = (int) (mouseY - 1) / 16;
        GameCell target = getCell(cellX, cellY); //The target is the cell that is about to be modified
        /*
         * In unrestricted placement mode, the user can fill no-go cells and empty a filled cell.
         * In restricted placement mode, the user cannot alter filled or no-go cells.
         */
        target.setState(switch (target.getState()) {
            case UNFILLED, SOON_FILLED -> STATES.PLACED; //Empty squares are replaced with filled squares
            case PLACED -> //Placed squares can always be unfilled
                    clickPlacementMode == CLICK_PLACEMENT_MODE.UNRESTRICTED ? STATES.NO_GO : STATES.UNFILLED;
            case NO_GO -> //These squares block players from placing a cell (while in restricted placement)
                    clickPlacementMode == CLICK_PLACEMENT_MODE.UNRESTRICTED ? STATES.UNFILLED : STATES.NO_GO;
            case FILLED, SOON_UNFILLED -> //These squares are considered "filled" and follow placement rules
                    clickPlacementMode == CLICK_PLACEMENT_MODE.UNRESTRICTED ? STATES.UNFILLED : STATES.FILLED;
        });
        show();
    }

    public void saveToFile(File f, int maxGens, int targetCells) {
        FileWriter fw;
        try {
            fw = new FileWriter(f);
            fw.write(String.format("%d\n%d\n%d\n%d\n", rows, cols, maxGens, targetCells));
            for (int y = 0; y < cols; y++) {
                for (int x = 0; x < rows; x++) {
                    switch (getCell(x, y).getState()) {
                        case UNFILLED, SOON_FILLED -> fw.write('0');
                        case FILLED, PLACED, SOON_UNFILLED -> fw.write('1');
                        case NO_GO -> fw.write('2');
                    }
                }
                fw.write('\n');
            }
            fw.flush();
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public CLICK_PLACEMENT_MODE getClickPlacementMode() {
        return clickPlacementMode;
    }

    public void setClickPlacementMode(CLICK_PLACEMENT_MODE c) {
        clickPlacementMode = c;
    }
}