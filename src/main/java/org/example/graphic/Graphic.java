package org.example.graphic;

import org.example.SnakesProto;
import org.example.logics.*;
import org.example.server.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Graphic extends JPanel implements ActionListener {
    private final Timer  timer;
    public Game game;
    private int myId;
    public Graphic(Game game, int myId){
        this.myId = myId;
        setBackground(Color.CYAN);
        this.game = game;
        timer = new Timer(game.time,this);
        timer.start();
        addKeyListener(new FieldKeyListener());
        setFocusable(true);
    }
    private void paintField(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        int k;
        for(int i = 0; i < game.field.getWidth(); i+=game.field.sizeBlock) {
            if(i%100==0)
                k=0;
            else
                k = game.field.sizeBlock;
            for(int j = k; j < game.field.getHeight(); j+=2*game.field.sizeBlock) {
                Rectangle2D rec = new Rectangle2D.Double(i, j, game.field.sizeBlock , game.field.sizeBlock);
                g2.setColor(Color.pink);
                g2.fill(rec);
                g2.draw(rec);
            }
        }
    }
    private void paintFoods(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        ArrayList<Coord> coords = game.getFood();
        //System.out.println("-----------"+coords);
        for (Coord c:coords){
            Ellipse2D el = new Ellipse2D.Double(c.x+game.field.sizeBlock/4, c.y+game.field.sizeBlock/4, game.field.sizeBlock/2, game.field.sizeBlock/2);
            g2.setColor(Color.RED);
            g2.fill(el);
            g2.draw(el);
        }
    }
    private void paintSnakes(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        HashMap<Integer, Snake> snakes = game.getSnake();
        for (Snake s:
             snakes.values()) {
            for (Snake.Point p:
                 s.getSnakePoints()) {
                switch (p.getPieceSnake()){
                    case BODY -> {
                        Rectangle2D rec = new Rectangle2D.Double(p.getXY().x+game.field.sizeBlock/4, p.getXY().y+game.field.sizeBlock/4, game.field.sizeBlock/2 , game.field.sizeBlock/2);
                        g2.setColor(Color.GREEN);
                        g2.fill(rec);
                        g2.draw(rec);
                    }
                    case HEAD -> {
                    //    System.out.println(p.getXY());
                        Ellipse2D el = new Ellipse2D.Double(p.getXY().x, p.getXY().y, game.field.sizeBlock, game.field.sizeBlock);
                        g2.setColor(Color.GREEN);
                        g2.fill(el);
                        g2.draw(el);
                    }
                    case FOOD -> {
                        System.out.println(1);
                    }
                }
            }
        }
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintField(g);
        paintFoods(g);
        paintSnakes(g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
//        if (new Random().nextInt(2)==0){
//            HashMap<Integer, Direction> f=new HashMap<>();
//            switch (new Random().nextInt(4)){
//                case (0)->f.put(1, Direction.UP);
//                case (1)->f.put(1, Direction.DOWN);
//                case (2)->f.put(1, Direction.RIGHT);
//                case (3)->f.put(1, Direction.LEFT);
//            }
//
//            game.go(f);
//        }else
            game.go(new HashMap<>());
        game.checkDied();
        game.checkFood();
        repaint();
    }
    public class FieldKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            super.keyPressed(e);
            int key = e.getKeyCode();
            if(key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT){
                game.getSnake().get(myId).changeDirection(Direction.valueOf(SnakesProto.Direction.LEFT.toString()));
            }
            if(key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT){
                game.getSnake().get(myId).changeDirection(Direction.valueOf(SnakesProto.Direction.RIGHT.toString()));
            }
            if(key == KeyEvent.VK_W || key == KeyEvent.VK_UP){
                game.getSnake().get(myId).changeDirection(Direction.valueOf(SnakesProto.Direction.UP.toString()));
            }
            if(key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN){
                game.getSnake().get(myId).changeDirection(Direction.valueOf(SnakesProto.Direction.DOWN.toString()));
            }
            if(key == KeyEvent.VK_ESCAPE){
                System.exit(1);
            }
        }
    }
}
