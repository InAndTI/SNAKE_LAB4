package org.example;

import org.example.client.ClientGraphic;
import org.example.graphic.Graphic;

import javax.swing.*;

public class Arab extends JFrame{
    public Arab(Graphic graphic){
        int width = graphic.game.field.getWidth();
        int height =graphic.game.field.getHeight();
        setTitle("Snake");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(width,height);
        setLocation(0,0);
        add(graphic);
        //setExtendedState(Frame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setVisible(true);
    }
    public Arab(ClientGraphic clientGraphic){
        int width = clientGraphic.field.getWidth();
        int height = clientGraphic.field.getHeight();
        setTitle("Snake");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(width,height);
        setLocation(0,0);
        add(clientGraphic);
        //setExtendedState(Frame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setVisible(true);
    }

}
