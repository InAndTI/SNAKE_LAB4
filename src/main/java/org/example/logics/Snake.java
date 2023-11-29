package org.example.logics;

import org.example.SnakesProto;

import java.util.ArrayList;

public class Snake {
    public final String name;
    public final int id;
    public int size;
    public SnakesProto.NodeRole nodeRole;
    private static Field field;
    private SnakesProto.PlayerType playerType;
    public SnakesProto.GameState.Snake.SnakeState snakeState = SnakesProto.GameState.Snake.SnakeState.ALIVE;
    public Direction direction;
    public Direction directionPast;
    public boolean ImAlive = true;
    public final String ip_address; // IPv4 или IPv6 адрес игрока в виде строки. Отсутствует в описании игрока-отправителя сообщения

    public int port;
    private ArrayList<Point> snakePoints = new ArrayList<>();
    public Snake(String name, int id, int port, String ip_address, SnakesProto.NodeRole nodeRole, SnakesProto.PlayerType playerType, Point point, Direction direction){
        this.name = name;
        this.id = id;
        this.ip_address = ip_address;
        this.port = port;
        this.size = 2;
        this.nodeRole = nodeRole;
        this.playerType = playerType;
        this.direction = direction;
        setSnakePoints(point);
        for (int i = 1; i < size; i++) {
            ImEat();
            for (int j = i-1; j >= 0; j--) {
                snakePoints.get(j).setdXY(this.direction);
            }

        }
    }
//    public void addPoint(Direction direction){
//        Point point = new Point(snakePoints.get(snakePoints.size()-1).directionPast.revers(), PieceSnake.BODY, snakePoints.get(snakePoints.size()-1).getXY());
//        point.setdXY(snakePoints.get(snakePoints.size()-1).directionPast.revers());
//        //point.directionPast = snakePoints.get(snakePoints.size()-1).directionPast;
//        //point.wherePast();
//        snakePoints.add(point);
//    }
    public static void setField(Field field){
        Snake.field = field;
    }
//    public static Point getPoint(Direction direction){
//        return new Point(direction, PieceSnake.HEAD);
//    }
    public ArrayList<Point> getSnakePoints(){
        return snakePoints;
    }
    public void setSnakePoints(Point point){
        snakePoints.add(point);
    }
    public void changeDirection(Direction direction){
        if (this.directionPast.revers()!=direction)
                this.direction = direction;
    }
    public void live(){
        if(ImAlive){

            for (int i = size-1; i>0; i--) {
                snakePoints.get(i).coord.x=snakePoints.get(i-1).coord.x;
                snakePoints.get(i).coord.y=snakePoints.get(i-1).coord.y;
            }
            snakePoints.get(0).setdXY(direction);
            directionPast = direction;
            if(size<snakePoints.size()){
                addSize();
            }
            //snakePoints.get(snakePoints.size()-1).wherePast();
        }else {

        }
    }
    public void addSize(){
        size++;
    }
    public void ImEat(){
        //System.out.println(size);
        Point point = new Point(PieceSnake.BODY, snakePoints.get(snakePoints.size()-1).getXY());
        //point.setdXY(snakePoints.get(snakePoints.size()-1).directionPast);
//        point.directionPast = snakePoints.get(snakePoints.size()-1).directionPast;
//        point.wherePast();
        //snakePoints.get(snakePoints.size()-1).wherePast();
        snakePoints.add(point);
        //point.setdXY(snakePoints.get(snakePoints.size()-1).directionPast);
    }


    public static class Point{
        private PieceSnake pieceSnake;
        private Coord coord = new Coord();
        private static final int height = field.getHeight();
        private static final int width = field.getWidth();
//        private Direction directionPast;
//        private Direction directionNow;
        //private Direction direct;
        public Point(PieceSnake pieceSnake, Coord coord){
            this.pieceSnake = pieceSnake;
 //           this.direct = direction;
//            directionPast = direction;
//            directionNow = direction;
            this.coord.x = coord.x;
            this.coord.y = coord.y;
        }
//        public Direction wherePast(){
//            direct = directionPast;
//            directionPast = directionNow;
//            return direct;
//        }
        public void setdXY(Direction direction){
            //directionNow = direction;
            switch (direction){
                case RIGHT -> coord.x+=field.sizeBlock;
                case LEFT -> coord.x-=field.sizeBlock;
                case DOWN -> coord.y+=field.sizeBlock;
                case UP -> coord.y-=field.sizeBlock;
            }
            if(coord.x>=width)
                this.coord.x -= width;
            else if(coord.x<0)
                this.coord.x += width;
            if(coord.y>=height)
                this.coord.y -= height;
            else if(coord.y<0)
                this.coord.y += height;
        }
        public Coord getXY(){return coord;}
        public PieceSnake getPieceSnake(){
            return pieceSnake;
        }
    }

}














