package org.example.logics;

import java.util.ArrayList;
import java.util.Random;

public class Food {
    public boolean ImAlive = true;
    private static Field field;
    private final Random random = new Random();
    public final Coord coord = new Coord();
    public static void setField(Field field){
        Food.field = field;
    }
    public void createFood(ArrayList<Food> foods){
        comparison(foods);
        foods.add(this);
    }
    private void comparison(ArrayList<Food> foods){
        while (true){
            coord.x = random.nextInt(field.getWidth()/field.sizeBlock)*field.sizeBlock;
            coord.y = random.nextInt(field.getHeight()/field.sizeBlock)*field.sizeBlock;
            boolean t = true;
            for (Food f:
                 foods) {
                if(coord.equals(f.coord)){
                    t = false;
                }
            }
            if (t)
                break;
        }
    }
    public Coord getCoord(){
        return coord;
    }
}
