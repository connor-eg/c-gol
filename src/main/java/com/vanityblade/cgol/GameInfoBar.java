package com.vanityblade.cgol;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class GameInfoBar extends Canvas {
    public GameInfoBar(double width) {
        setHeight(32);
        setWidth(width);
        drawBG();
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
}
