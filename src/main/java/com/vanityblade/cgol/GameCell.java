package com.vanityblade.cgol;

public class GameCell {
    private STATES state;
    private NEIGHBORS neighbors;
    public GameCell() {
        setState(STATES.UNFILLED);
        setNeighbors(NEIGHBORS.NONE);
    }

    public GameCell(GameCell toCopy){
        this.state = toCopy.getState();
        this.neighbors = toCopy.getNeighbors();
    }

    //State getters/setters
    public STATES getState() {
        return state;
    }
    public void setState(STATES state) {
        this.state = state;
    }
    public NEIGHBORS getNeighbors() {
        return neighbors;
    }
    public void setNeighbors(NEIGHBORS neighbors) {
        this.neighbors = neighbors;
    }
}