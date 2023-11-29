package org.example.logics;

import org.example.SnakesProto;

import java.util.*;

public class Game {
    public final String name;
    public final int time;
    public boolean can_join = true;
    private HashMap<Integer, Snake> snakes = new HashMap<>();
    private ArrayList<Food> foods = new ArrayList<>();
    private ArrayList<Integer> ids = new ArrayList<>();
    public Field field;
    private int countPoint = 0;
    public Game(int height, int width, int countFood, int sizeBlock, int time, String name){
        field = new Field(height, width, countFood, sizeBlock);
        Snake.setField(field);
        Food.setField(field);
        this.time = time;
        this.name = name;
    }
    public HashMap<PieceSnake, ArrayList<Coord>> go(HashMap<Integer, Direction> directions){
        HashMap<PieceSnake, ArrayList<Coord>> coords = new HashMap<>();
        for (Integer i:
             directions.keySet()) {
            snakes.get(i).changeDirection(directions.get(i));
        }
        for (Snake s:
             snakes.values()) {
            s.live();
        }

        return coords;
    }
    public void addSnake(String name, int id, int port, String ip, SnakesProto.NodeRole nodeRole, SnakesProto.PlayerType playerType){
        snakes.put(id, new Snake(name, id, port, ip, nodeRole, playerType, foundPoint(), Direction.RIGHT));
        ids.add(id);
        addFood();
    }
    private Snake.Point foundPoint(){
        HashSet<Integer> coords1 = new HashSet<>();
        for (Integer i:ids) {
            for (Snake.Point p:snakes.get(i).getSnakePoints()) {
                coords1.add(p.getXY().hashCode());
            }
        }


        for (int i = 0; i < field.getHeight()/field.sizeBlock; i++) {
            for (int j = 0; j < field.getWidth()/field.sizeBlock; j++) {
                int t = coords1.size();
                HashSet<Integer> coords = new HashSet<>(coords1);
                int h = 0;
                boolean b = true;
                for (int k = i - 2; k < i + 2; k++) {
                    for (int l = j - 2; l < j + 2; l++) {
                        Coord c = new Coord().setXY(field, l, k);
                        coords.add(new Coord().setXY(c.x*field.sizeBlock,c.y*field.sizeBlock).hashCode());
                        h++;
                        if (coords.size() != t+h) {
                            b = false;
                            break;
                        }
                        if(!b)
                            break;
                    }
                }
                if (b)
                    return new Snake.Point(PieceSnake.HEAD, new Coord().setXY(j*field.sizeBlock, i*field.sizeBlock));
            }
        }
        System.out.println("бляяяяяяяяяяяяяяяяяяя");
        return null;
    }
    public void addFood(){
        new Food().createFood(foods);
    }
    public void checkDied(){
        HashSet<Integer> coords = new HashSet<>();
        for (Integer i:ids) {
            for (Snake.Point p:snakes.get(i).getSnakePoints()) {
                if(p.getPieceSnake()== PieceSnake.BODY)
                    coords.add(p.getXY().hashCode());
            }
        }
        countPoint = coords.size();
        for (Integer i:ids) {
            coords.add(snakes.get(i).getSnakePoints().get(0).getXY().hashCode());
            if (countPoint == coords.size())
                snakes.get(i).ImAlive = false;
            countPoint = coords.size();
        }
        coords.clear();
        int k = -1;
        for (int i = 0; i< ids.size(); i++) {
            if(!snakes.get(ids.get(i)).ImAlive) {
                transformation(snakes.get(ids.get(i)));
                snakes.remove(ids.get(i));
                k = i;
            }
            if (k!=-1){
                ids.remove(k);
                k=-1;
            }
        }
    }

    private void transformation(Snake snake){
        for (Snake.Point coord:
             snake.getSnakePoints()) {
            if (new Random().nextBoolean()) {
                Food food = new Food();
                food.coord.x = coord.getXY().x;
                food.coord.y = coord.getXY().y;
                System.out.println(2);
                foods.add(food);
            }
            //System.out.println(1);
        }
    }
    public void checkFood(){
        HashSet<Integer> coords = new HashSet<>();
        int size = 0;
        for (Food food:foods) {
            coords.add(food.getCoord().hashCode());
            if (size == coords.size())
                food.ImAlive=false;
            else
                size++;
        }
        countPoint = coords.size();
        for (Integer i:ids) {
            coords.add(snakes.get(i).getSnakePoints().get(0).getXY().hashCode());
            if (countPoint == coords.size()) {
                //System.out.println(1);
                snakes.get(i).ImEat();//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
                for (Food food:foods) {
                    if(snakes.get(i).getSnakePoints().get(0).getXY().equals(food.getCoord())){
                        food.ImAlive = false;
                    }
                }
            }
            countPoint = coords.size();
           // System.out.println(countPoint);
        }
        coords.clear();
        int i = 0;
        while (i < foods.size()) {
            if(!foods.get(i).ImAlive) {
                foods.remove(i);
                addFood();
               // System.out.println(1);
            }else
                i++;
        }
    }
    public ArrayList<Coord> getFood(){
        ArrayList<Coord> coords = new ArrayList<>();
        for (Food f:
             foods) {
            coords.add(f.getCoord());
        }
        return coords;
    }
    public HashMap<Integer, Snake> getSnake(){
        return snakes;
    }
}





















