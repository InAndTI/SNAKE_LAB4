package org.example.client;

import org.example.SnakesProto;
import org.example.logics.*;

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
import java.util.function.Function;

public class ClientGraphic extends JPanel {
    private ArrayList<ArrayList<SnakesProto.GameState.Coord>> snakes = new ArrayList<>();
    private ArrayList<SnakesProto.GameState.Coord> coords = new ArrayList<>();
    //ArrayList<>
    //private final Timer  timer;
    public Field field;
    public ClientGraphic(Field field, Client.FieldKeyListener key){
        setBackground(Color.CYAN);
        this.field = field;
        addKeyListener(key);
        setFocusable(true);
    }
    private void paintField(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        int k;
        for(int i = 0; i < field.getWidth()*field.sizeBlock; i+=field.sizeBlock) {
            if(i%(2*field.sizeBlock)==0)
                k=0;
            else
                k = field.sizeBlock;
            for(int j = k; j < field.getHeight()*field.sizeBlock; j+=2*field.sizeBlock) {
                Rectangle2D rec = new Rectangle2D.Double(i, j, field.sizeBlock , field.sizeBlock);
                g2.setColor(Color.pink);
                g2.fill(rec);
                g2.draw(rec);
            }
        }
    }
    private void paintFoods(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        //System.out.println("-----------"+coords);
        for (SnakesProto.GameState.Coord c:coords){
            Ellipse2D el = new Ellipse2D.Double(c.getX()*field.sizeBlock+field.sizeBlock/4, c.getY()*field.sizeBlock+field.sizeBlock/4,
                    field.sizeBlock/2, field.sizeBlock/2);
            g2.setColor(Color.RED);
            g2.fill(el);
            g2.draw(el);
        }
    }
    private void paintSnakes(Graphics g, ArrayList<SnakesProto.GameState.Coord> s){
        Graphics2D g2 = (Graphics2D) g;
        Ellipse2D el = new Ellipse2D.Double(s.get(0).getX()*field.sizeBlock, s.get(0).getY()*field.sizeBlock, field.sizeBlock, field.sizeBlock);
        g2.setColor(Color.GREEN);
        g2.fill(el);
        g2.draw(el);
        for (int i = 1; i<s.size();i++){
            Rectangle2D rec = new Rectangle2D.Double(s.get(i).getX()*field.sizeBlock+field.sizeBlock/4,
                    s.get(i).getY()*field.sizeBlock+field.sizeBlock/4, field.sizeBlock/2 , field.sizeBlock/2);
            g2.setColor(Color.GREEN);
            g2.fill(rec);
            g2.draw(rec);
        }
    }
    public void graphic(ArrayList<SnakesProto.GameState.Coord> foodCoords,
                        ArrayList<ArrayList<SnakesProto.GameState.Coord>> snakes){
        this.coords = foodCoords;
        this.snakes.clear();
        for (int i = 0; i < snakes.size(); i++) {
            this.snakes.add(snakes.get(i));
        }
        //this.snakes = snakes;
        repaint();
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintField(g);
        paintFoods(g);
        for (ArrayList<SnakesProto.GameState.Coord> s:
             snakes) {
            paintSnakes(g, s);
        }
    }

}


