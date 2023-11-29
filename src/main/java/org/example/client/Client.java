package org.example.client;

import com.google.protobuf.InvalidProtocolBufferException;
import org.example.Arab;
import org.example.Message;
import org.example.SnakesProto;
import org.example.logics.*;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class Client {
    private final int port;
    private final int time;
    //private final Game game;
    private final Message message;
    private long write;
    private long read;
    private ArrayList<Integer> id = new ArrayList<>();
    private int idLast = 0;
    private int myId;
    private long seq = 0;
    private DatagramSocket datagramSocket;
    private int serverPort;
    private InetAddress serverIp;
    private int serverId;
    private ClientGraphic clientGraphic;
    public Client(int port, int time) {
        try {
            datagramSocket = new DatagramSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.message = new Message(null);
       // this.game = game;
        this.time = time;
        this.port = port;

    }
    public void joinMsg(DatagramPacket datagramPacket, String name, SnakesProto.NodeRole nodeRole) throws InvalidProtocolBufferException {
        int height = 800;
        int width = 800;
        serverPort = datagramPacket.getPort();
        serverIp = datagramPacket.getAddress();
        System.out.println("++++++++"+serverPort);
        send(serverPort, serverIp, message.JoinMsg(name, SnakesProto.GameMessage.parseFrom
                (datagramPacket.getData()).getAnnouncement().getGames(0).getGameName(), nodeRole));
        write = System.nanoTime();
        read = System.nanoTime();
        SnakesProto.GameConfig gm = SnakesProto.GameMessage.parseFrom(
                datagramPacket.getData()).getAnnouncement().getGames(0).getConfig();

        if(height/gm.getHeight()<width/gm.getWidth()) {
            clientGraphic = new ClientGraphic(new Field(height, gm.getWidth() * height / gm.getHeight(),
                    gm.getFoodStatic(), height / gm.getHeight()), new FieldKeyListener());
            System.out.println(height / gm.getHeight());
        }
        else {
            clientGraphic = new ClientGraphic(new Field(gm.getHeight() * width / gm.getWidth(), width,
                    gm.getFoodStatic(), width / gm.getWidth()), new FieldKeyListener());
            System.out.println(gm.getHeight() * width / gm.getWidth());
            System.out.println(width / gm.getWidth());
        }

        new Arab(clientGraphic);
        PingMsg().start();
        //sayIDied();
        //someoneHere().start();
        readMessage().start();
    }
    private Thread PingMsg(){
        return new Thread(() ->{
            while (true){
                if(System.nanoTime()-write > time * 100000L) {
                    send(serverPort, serverIp, message.PingMsg());
                    seq++;
                    write = System.nanoTime();
                }
            }
        });
    }
    private void SteerMsg(SnakesProto.Direction direction){
        send(serverPort, serverIp, message.SteerMsg(direction));
        write = System.nanoTime();
        seq++;
    }
    private void DiscoverMsg(){
        send(serverPort, serverIp, message.DiscoverMsg());
        write = System.nanoTime();
        seq++;
    }
//    private Thread someoneHere(){abstract
//        return new Thread(() ->{
//            while (true) {
//                for (int i = 0; i < id.size(); i++) {
//                    if (System.nanoTime() - read > time * 1000000L * 0.8) {
//                        DEPUTY();
//                    }
//                }
//            }
//        });
//    }
    private void sayIDied(){
        send(serverPort, serverIp, message.RoleChangeMsg(SnakesProto.NodeRole.VIEWER,
                SnakesProto.NodeRole.MASTER, myId, serverId));
        seq++;
    }
    private Thread readMessage(){
        return new Thread(() ->{
            byte[] bt = new byte[1100];
            DatagramPacket datagramPacket = new DatagramPacket(bt, bt.length);
            while (true){
                try {
                    datagramSocket.receive(datagramPacket);
                    react(cut(datagramPacket));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    private DatagramPacket cut(DatagramPacket receivePacket){
        int actualLength = receivePacket.getLength();
        // Создаем новый буфер с фактическим размером данных
        byte[] actualData = new byte[actualLength];
        System.arraycopy(receivePacket.getData(), 0, actualData, 0, actualLength);
        return new DatagramPacket(actualData, actualData.length, receivePacket.getAddress(), receivePacket.getPort());
    }
    private void react(DatagramPacket datagramPacket) throws InvalidProtocolBufferException {
        SnakesProto.GameMessage gam = SnakesProto.GameMessage.parseFrom(datagramPacket.getData());
        switch (gam.getTypeCase()){
            case ACK ->{
                myId = gam.getReceiverId();
                serverId =  gam.getSenderId();
            }
            case PING -> {
                read = System.nanoTime();
            }
            case JOIN -> {}
            case STATE -> {
                SnakesProto.GameState gs = SnakesProto.GameMessage.parseFrom
                        (datagramPacket.getData()).getState().getState();
                send(serverPort, serverIp, message.AckMsg(seq, myId, serverId));
                ArrayList<SnakesProto.GameState.Coord> foods = new ArrayList<>();
                for (int i = 0; i < gs.getFoodsList().size(); i++) {
                    foods.add(gs.getFoods(i));
                }
                ArrayList<ArrayList<SnakesProto.GameState.Coord>> snake = new ArrayList<>();
                for (int i = 0; i < gs.getSnakesList().size(); i++) {
                    snake.add(new ArrayList<>());
                    for (int j = 0; j < gs.getSnakes(i).getPointsCount(); j++) {
                        snake.get(i).add(gs.getSnakes(i).getPoints(j));
                    }
                }
                //System.out.println(snake.get(0).size());
                clientGraphic.graphic(foods, snake);
            }
            case STEER -> {
            }
            case DISCOVER -> send(datagramPacket.getPort(), datagramPacket.getAddress(), message.AnnouncementMsg());
            case ROLE_CHANGE -> {
//                int idPlayer = (datagramPacket.getAddress().toString()
//                        + datagramPacket.getPort()).hashCode();
//                send(datagramPacket.getPort(), datagramPacket.getAddress(), message.AckMsg(seq, myId, idPlayer));
//                game.getSnake().get(idPlayer).nodeRole = SnakesProto.NodeRole.VIEWER;
            }
            case TYPE_NOT_SET -> System.out.println(1);
            case ERROR -> System.out.println(gam.getError().getErrorMessage());
        }
    }
    private void send(int port, InetAddress ip, SnakesProto.GameMessage gameMessage){
        byte[] bytes = gameMessage.toByteArray();
        DatagramPacket datagramPacket = new DatagramPacket(bytes,bytes.length, ip, port);
        try {
            datagramSocket.send(datagramPacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    class FieldKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            super.keyPressed(e);
            int key = e.getKeyCode();
            if(key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT){
                SteerMsg(SnakesProto.Direction.LEFT);
            }
            if(key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT){
                SteerMsg(SnakesProto.Direction.RIGHT);
            }
            if(key == KeyEvent.VK_W || key == KeyEvent.VK_UP){
                SteerMsg(SnakesProto.Direction.UP);
            }
            if(key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN){
                SteerMsg(SnakesProto.Direction.DOWN);
            }
            if(key == KeyEvent.VK_ESCAPE){
                System.exit(1);
            }
        }
    }
}
