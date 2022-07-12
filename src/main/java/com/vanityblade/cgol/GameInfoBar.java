package com.vanityblade.cgol;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class GameInfoBar extends Canvas {
    private int num;

    public GameInfoBar(double width) {
        setHeight(32);
        setWidth(width); //TODO: Change to Math.max(width, MINIMUM_WIDTH) to prevent text overlap
        render();
    }

    //Renders the current state of the info bar
    public void render(){
        drawBG();
        drawNumber(20, 8, 123456);
    }

    //This renders a background image and tiles it such that a continuous image is created.
    private void drawBG(){
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
        for(int xpos = 16; xpos < getWidth() - 16; xpos += 16){
            g.drawImage(background, 16, 0, 16, 32, xpos, 0, 16, 32); //Center bar
        }
        g.drawImage(background, 32, 0, 16, 32, getWidth() - 16, 0, 16, 32); //Rightmost piece
    }

    private void drawNumber(double posX, double posY, int num){
        GraphicsContext g = getGraphicsContext2D();
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream("src/main/resources/ImageAssets/cgol_numberfont.png");
        } catch (FileNotFoundException e) {
            fileInputStream = null;
        }
        assert  fileInputStream != null;
        Image numbers = new Image(fileInputStream);
        for(int i = 0; i < countDigits(num); i++){
            int toDraw = digitAtPos(i, num);
            g.drawImage(numbers, 8 * toDraw, 0, 8, 16, posX + 10 * i, posY, 8, 16);
        }
    }

    //Returns the number of digits in an integer.
    private int countDigits(int num){
        int res = Math.abs(num);
        if (res < 10) return 1;
        else return 1 + countDigits(res / 10);
    }

    //Returns the value of the digit at the nth position from the left in a number.
    //Example: The 0th position of the number 456 is 4, and the 2nd position is 6.
    //Returns 0 if position is too large, fails for negative positions.
    private int digitAtPos(int position, int num){
        assert position >= 0;
        int result = num;
        int digits = countDigits(num) - position - 1;
        for(int i = 0; i < digits; i++){
            result /= 10;
        }
        return result % 10;
    }
}
