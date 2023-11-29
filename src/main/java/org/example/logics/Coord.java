package org.example.logics;

public class Coord {
    public int x;
    public int y;

    public boolean equals(Coord coord){
        if (x==coord.x && y==coord.y)
            return true;
        else
            return false;
    }
    public Coord setXY(int x, int y){
        this.x = x;
        this.y = y;
        return this;
    }
    public Coord setXY(Field field, int x, int y){
        if (x > field.getWidth()/field.sizeBlock)
            this.x = x - field.getWidth()/field.sizeBlock;
        else if(x < 0)
            this.x = x + field.getWidth()/field.sizeBlock;
        else
            this.x = x;
        if (y > field.getHeight()/field.sizeBlock)
            this.y = y - field.getHeight()/field.sizeBlock;
        else if(y < 0)
            this.y = y + field.getHeight()/field.sizeBlock;
        else
            this.y = y;
        return this;
    }
    @Override
    public int hashCode() {
        return x*1000+y;
    }
    @Override
    public String toString(){
        return (x*1000+y)+"";
    }

}
