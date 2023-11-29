package org.example.logics;

public enum Direction {
    UP,     // Вверх (в отрицательном направлении оси y)
    DOWN,   // Вниз (в положительном направлении оси y)
    LEFT,   // Влево (в отрицательном направлении оси x)
    RIGHT ; // Вправо (в положительном направлении оси x)
    public Direction revers(){
        switch (this){
            case DOWN -> {
                return UP;
            }
            case UP -> {
                return DOWN;
            }
            case LEFT -> {
                return RIGHT;
            }
            case RIGHT -> {
                return LEFT;
            }
        }
        return null;
    }
}
