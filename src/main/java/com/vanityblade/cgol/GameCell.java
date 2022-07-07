package com.vanityblade.cgol;

public class GameCell {
    private STATES state;
    public GameCell() {
        setState(STATES.UNFILLED);
    }

    public GameCell(GameCell toCopy){
        this.state = toCopy.getState();
    }

    //State getters/setters
    public STATES getState() {
        return state;
    }
    public void setState(STATES state) {
        this.state = state;
    }
}