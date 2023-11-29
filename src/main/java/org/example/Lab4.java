package org.example;

import org.example.graphic.Graphic;
import org.example.logics.*;
import org.example.server.Multicast;
import org.example.server.Server;

import javax.swing.*;
import java.awt.*;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Random;

public class Lab4 extends JFrame{
    public static void main(String[] args){
//        Snake.setField(new Field(10, 12, 12));
//        Snake snake = new Snake("витя", 13, 3, NodeRole.MASTER,
//                PlayerType.HUMAN, Snake.getPoint(Direction.DOWN));
        new Lab4();

    }
    private Lab4(){
        //SnakesProto.GameMessage gameMessage = SnakesProto.GameMessage.newBuilder().
        int width = 800;
        int height = 800;
        int countFood = 10;
        int time = 700;
        Game game = new Game(height, width, countFood, 50, time, "game");
        for(int i=0;i<countFood;i++)
            game.addFood();
        game.addSnake("victor", 1, 9191, "localhost", SnakesProto.NodeRole.NORMAL, SnakesProto.PlayerType.HUMAN);
//        game.addSnake("lictor", 2, 2222,"locl", SnakesProto.NodeRole.NORMAL, SnakesProto.PlayerType.HUMAN);
//        game.addSnake("rictor", 3, 2222,"locl", SnakesProto.NodeRole.NORMAL, SnakesProto.PlayerType.HUMAN);
//        game.addSnake("yictor", 4, 2222,"locl", SnakesProto.NodeRole.NORMAL, SnakesProto.PlayerType.HUMAN);
//        game.addSnake("iictor", 5, 2222,"locl", SnakesProto.NodeRole.NORMAL, SnakesProto.PlayerType.HUMAN);

//        Message message = new Message(game);
//        SnakesProto.GameMessage gam = message.AnnouncementMsg();
//        System.out.println(gam.toByteArray().length);
//        gam = message.DiscoverMsg();
//        System.out.println(gam.toByteArray().length);
//        gam = message.RoleChangeMsg(SnakesProto.NodeRole.MASTER, SnakesProto.NodeRole.NORMAL, 0, 1);
//        System.out.println(gam.toByteArray().length);
//        gam = message.SteerMsg(SnakesProto.Direction.RIGHT);
//        System.out.println(gam.toByteArray().length);
//        gam = message.AckMsg(1, 0, 1);
//        System.out.println(gam.toByteArray().length);
//        gam = message.JoinMsg("", "", SnakesProto.NodeRole.NORMAL);
//        System.out.println(gam.toByteArray().length);
//        gam = message.PingMsg();
//        System.out.println(gam.toByteArray().length);
//        gam = message.StateMsg();
//        System.out.println(gam.toByteArray().length);
//        gam = message.Error("");
//        System.out.println(gam.toByteArray().length);
        //Server server = new Server(9191, time, game, new Message(game), 1);

//
       org.example.client.Multicast multicastClient = new org.example.client.Multicast();
        multicastClient.findCopies();





//        switch (gam.getTypeCase()){
//            case ACK -> System.out.println(1);
//            case JOIN -> System.out.println(1);
//            case PING -> System.out.println(1);
//            case STATE -> System.out.println(gam.getState().getState().getSnakes(0).getHeadDirection());
//            case STEER -> System.out.println(1);
//            case DISCOVER -> System.out.println(1);
//            case ANNOUNCEMENT -> System.out.println(2);
//            case ROLE_CHANGE -> System.out.println(1);
//            case TYPE_NOT_SET -> System.out.println(1);
//            case ERROR -> System.out.println(gam.getError().getErrorMessage());
//        }


    }
}
