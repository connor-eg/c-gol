package com.vanityblade.cgol;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class GameInfoBar extends Canvas {
    private int timeLeft = 0;
    private int targetCellsLeft = 0;

    public GameInfoBar(double width) {
        setHeight(32);
        setWidth(Math.max(width, 256));
        render();
    }

    //Renders the current state of the info bar
    public void render() {
        drawBG();
        drawIcons();
        drawNumber(32, timeLeft);
        drawNumber(-32, targetCellsLeft);
    }

    //This renders a background image and tiles it such that a continuous image is created.
    private void drawBG() {
        GraphicsContext g = getGraphicsContext2D();
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream("src/main/resources/ImageAssets/cgol_topBG.png");
        } catch (FileNotFoundException e) {
            fileInputStream = null;
        }
        assert fileInputStream != null;
        Image background = new Image(fileInputStream);
        g.drawImage(background, 0, 0, 16, 32, 0, 0, 16, 32); //Leftmost piece
        for (int i = 16; i < getWidth() - 16; i += 16) {
            g.drawImage(background, 16, 0, 16, 32, i, 0, 16, 32); //Center bar
        }
        g.drawImage(background, 32, 0, 16, 32, getWidth() - 16, 0, 16, 32); //Rightmost piece
    }

    private void drawIcons() {
        GraphicsContext g = getGraphicsContext2D();
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream("src/main/resources/ImageAssets/cgol_gameIcons.png");
        } catch (FileNotFoundException e) {
            fileInputStream = null;
        }
        assert fileInputStream != null;
        Image icons = new Image(fileInputStream);
        //Left icon is a timer for the number of remaining generations
        g.drawImage(icons, 0, 0, 32, 32, 0, 0, 32, 32);
        //Right icon is a target for the number of remaining cells to be cleared
        g.drawImage(icons, 32, 0, 32, 32, getWidth() - 33, 0, 32, 32);
    }

    /**
     * This function draws a number in the top bar.
     *
     * @param posX This is the position of the number. If this is positive,
     *             this value represents how many pixels away from the left side
     *             of the bar the leftmost pixel of the leftmost digit will be placed.
     *             If this is negative, this value is right-aligned instead.
     * @param num  This is the number to display.
     */
    private void drawNumber(double posX, int num) {
        if (posX < 0) {
            posX = getWidth() + posX + 1 - (10 * countDigits(num));
            if(num < 0) posX -= 10;
        }
        GraphicsContext g = getGraphicsContext2D();
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream("src/main/resources/ImageAssets/cgol_numberFont.png");
        } catch (FileNotFoundException e) {
            fileInputStream = null;
        }
        assert fileInputStream != null;
        Image numbers = new Image(fileInputStream);
        //Drawing a minus sign, if applicable
        if(num < 0){
            if(posX < 0){
                g.drawImage(numbers, 80, 0, 8, 16, posX - 10, 8, 8, 16);
            } else {
                g.drawImage(numbers, 80, 0, 8, 16, posX, 8, 8, 16);
                posX += 10;
            }
        }
        //Drawing the numerical portion of the number
        for (int i = 0; i < countDigits(num); i++) {
            int toDraw = digitAtPos(i, num);
            g.drawImage(numbers, 8 * toDraw, 0, 8, 16, posX + 10 * i, 8, 8, 16);
        }
    }

    //Returns the number of digits in an integer.
    private int countDigits(int num) {
        if (num < 0) return countDigits(-num);
        if (num < 10) return 1;
        else return 1 + countDigits(num / 10);
    }

    //Returns the value of the digit at the nth position from the left in a number.
    //Example: The 0th position of the number 456 is 4, and the 2nd position is 6.
    //Returns 0 if position is too large, fails for negative positions.
    private int digitAtPos(int position, int num) {
        assert position >= 0;
        if (num < 0) return digitAtPos(position, -num);
        int result = num;
        int digits = countDigits(num) - position - 1;
        for (int i = 0; i < digits; i++) {
            result /= 10;
        }
        return result % 10;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
        render();
    }

    public int getTargetCellsLeft() {
        return targetCellsLeft;
    }

    public void setTargetCellsLeft(int targetCellsLeft) {
        this.targetCellsLeft = targetCellsLeft;
        render();
    }
}
