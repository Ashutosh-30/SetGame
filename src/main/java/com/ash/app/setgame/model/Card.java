package com.ash.app.setgame.model;

public class Card {

    private Color color;
    private Symbol symbol;
    private Shading shading;
    private int number;

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    public Shading getShading() {
        return shading;
    }

    public void setShading(Shading shading) {
        this.shading = shading;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
