package org.example.logics;

public class Field {
    private final int height;
    private final int width;
    private final int countFood;
    public final int sizeBlock;



    public Field(int height, int width, int countFood, int sizeBlock){
        this.sizeBlock = sizeBlock;
        this.height = height;
        this.width  = width;
        this.countFood = countFood;

    }
    public int getCountFood() {
        return countFood;
    }
    public int getHeight(){return height;}
    public int getWidth(){return width;}
}
