package com.vanityblade.cgol;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class CGOLButtonBox extends Pane {
    public CGOLButton bStart, bStop, bLoad, bSave, bNew, bStep, bReset;
    private boolean isStartVisible = true;
    public CGOLButtonBox(double width) {
        this.setWidth(width);
        this.setMinWidth(width);
        this.setHeight(112);
        this.setMinHeight(112);
        //Button initialization
        bStart = new CGOLButton(true, 0, 64, 48);
        bStop = new CGOLButton(true, 64, 64, 48);
        bLoad = new CGOLButton(true, 128, 48, 32);
        bSave = new CGOLButton(true, 176, 48, 32);
        bNew = new CGOLButton(true, 224, 48, 32);
        bStep = new CGOLButton(true, 272, 32, 32);
        bReset = new CGOLButton(true, 304, 32, 32);
        //Start handling these buttons with this object
        this.getChildren().addAll(bStart, bStop, bLoad, bSave, bNew, bStep, bReset);
        //Position the buttons in the pane
        relocateButtons(getWidth());
        bStop.setScaleX(0);
    }

    public void relocateButtons(double newWidth) {
        this.setWidth(newWidth);
        this.setMinWidth(newWidth);
        bStart.relocate(this.getWidth() - 80, 16);
        bStop.relocate(this.getWidth() - 80, 16);
        bLoad.relocate(16, 16);
        bSave.relocate(16, 64);
        bNew.relocate(80, 64);
        bStep.relocate(this.getWidth() - 128, 16);
        bReset.relocate(80, 16);
    }

    //Super garbage method for flipping which button is clickable at the moment, but it works perfectly.
    public void flipStartStop(){
        if(isStartVisible){
            bStart.setScaleX(0);
            bStop.setScaleX(1);
        } else {
            bStart.setScaleX(1);
            bStop.setScaleX(0);
        }
        isStartVisible = !isStartVisible;
    }

    public boolean getStopVisible(){
        return !isStartVisible;
    }

    public static class CGOLButton extends Canvas {
        private boolean enableState;
        private final Image icon;
        private final double x;
        private final double w;
        private final double h; //Button positions

        public CGOLButton(boolean enableState, double x, double w, double h) {
            this.enableState = enableState;
            this.x = x;
            this.w = w;
            this.h = h;
            this.setWidth(w);
            this.setHeight(h);
            render();
            FileInputStream fileInputStream;
            try {
                fileInputStream = new FileInputStream("src/main/resources/ImageAssets/cgol_buttonSpriteSheet.png");
            } catch (FileNotFoundException e) {
                fileInputStream = null;
            }
            assert fileInputStream != null;
            icon = new Image(fileInputStream);
            render();
        }

        public void setEnableState(boolean state) {
            enableState = state;
            render();
        }

        public boolean isNotEnabled() {
            return !enableState;
        }

        public boolean flipEnableState() {
            enableState = !enableState;
            render();
            return enableState;
        }

        public void render() {
            GraphicsContext g = getGraphicsContext2D();
            g.drawImage(icon, x, enableState ? 0 : 48, w, h, 0, 0, w, h);
        }
    }
}
