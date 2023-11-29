package org.example.server;

import com.google.protobuf.InvalidProtocolBufferException;
import org.example.Arab;
import org.example.Message;
import org.example.SnakesProto;
import org.example.graphic.Graphic;
import org.example.logics.*;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

public class Server {
    private final int port;
    //private final ServerSocketChannel serverSocketChannel;
    private final int time;
    private final Game game;
    //private ArrayList<SocketChannel> socketChannels = new ArrayList<>();
    private final Message message;
    private HashMap<Integer, WriteRead> players = new HashMap<>();
    private long read;
    private ArrayList<Integer> id = new ArrayList<>();
    private int idLast = 0;
    private final int myId;
    private SocketChannel dep;
    private Selector selector;
    private long seq = 0;
    private DatagramSocket datagramSocket;
    public Server(int port, int time, Game game, Message message, int id) {
        try {
            selector = Selector.open();
            datagramSocket = new DatagramSocket(port);
            new Multicast().findCopies(game, datagramSocket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.message = message;
        this.game = game;
        this.time = time;
        this.port = port;
        myId = id;
        StateMsg().start();
        PingMsg().start();
        checkDied().start();
        if(players.size()>1)
            DEPUTY();
        someoneHere().start();
        readMessage().start();
        new Arab(new Graphic(game, myId));
    }
    private Thread StateMsg(){
        return new Thread(() ->{
            long pastTime = System.nanoTime();
            long nowTime;
            while (true){
                nowTime = System.nanoTime();
                if(nowTime-pastTime>time* 1000000L) {
                    pastTime = System.nanoTime();
                    for (int i = 0; i < id.size(); i++) {
                        send(players.get(id.get(i)).myPort,players.get(id.get(i)).ip, message.StateMsg());
                        seq++;
                        players.get(id.get(i)).write=System.nanoTime();
                    }
                }
            }
        });
    }
    private void DEPUTY(){
        int idDep = new Random().nextInt(players.size());
        game.getSnake().get(id.get(idDep)).nodeRole = SnakesProto.NodeRole.DEPUTY;
        players.get(id.get(idDep)).dep=true;
        players.get(id.get(idDep)).read = System.nanoTime();
    }
    private Thread PingMsg(){
        return new Thread(() ->{
            while (true){
                for (int i = 0; i < id.size(); i++) {
                    if(System.nanoTime()-players.get(id.get(i)).write>time* 100000L) {
                        send(players.get(id.get(i)).myPort,players.get(id.get(i)).ip, message.PingMsg());
                        seq++;
                        players.get(id.get(i)).write=System.nanoTime();
                    }
                }
            }
        });
    }
    private Thread someoneHere(){
        return new Thread(() ->{
            while (true) {
                for (int i = 0; i < id.size(); i++) {
                    if (players.get(id.get(i)).dep && System.nanoTime() - players.get(id.get(i)).read > time * 1000000L * 0.8) {
                        DEPUTY();
                    }
                }
            }
        });
    }
    private Thread checkDied(){
        return new Thread(() ->{
            while (true){
                for (int i = 0; i < id.size(); i++) {
                    if(!game.getSnake().get(id.get(i)).ImAlive) {
                        send(players.get(id.get(i)).myPort, players.get(id.get(i)).ip, message.RoleChangeMsg(SnakesProto.NodeRole.MASTER,
                                SnakesProto.NodeRole.VIEWER, myId, id.get(i)));
                        seq++;
                        players.get(id.get(i)).write=System.nanoTime();
                    }
                }
            }
        });
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
            case ACK, PING -> {
                int idPlayer = (datagramPacket.getAddress().toString()
                        + datagramPacket.getPort()).hashCode();
                players.get(idPlayer).read = System.nanoTime();
            }
            case JOIN -> {
                int idPlayer = (datagramPacket.getAddress().toString()
                        + datagramPacket.getPort()).hashCode();
                players.put(idPlayer, new WriteRead(datagramPacket.getPort(), datagramPacket.getAddress()));

                send(datagramPacket.getPort(), datagramPacket.getAddress(), message.AckMsg(seq, myId, idPlayer));
                SnakesProto.GameMessage.JoinMsg joinMsg = gam.getJoin();
                game.addSnake(joinMsg.getPlayerName(), idPlayer, datagramPacket.getPort(), datagramPacket.getAddress().getHostAddress(),
                        joinMsg.getRequestedRole(), joinMsg.getPlayerType());
                id.add(idPlayer);
                seq++;
            }
            case STATE -> {}
            case STEER -> {
                int idPlayer = (datagramPacket.getAddress().toString()
                        + datagramPacket.getPort()).hashCode();
                send(datagramPacket.getPort(), datagramPacket.getAddress(), message.AckMsg(seq, myId, idPlayer));
                game.getSnake().get(idPlayer).changeDirection(Direction.valueOf(gam.getSteer().getDirection().toString()));
            }
            case DISCOVER -> send(datagramPacket.getPort(), datagramPacket.getAddress(), message.AnnouncementMsg());
            case ROLE_CHANGE -> {
                int idPlayer = (datagramPacket.getAddress().toString()
                        + datagramPacket.getPort()).hashCode();
                send(datagramPacket.getPort(), datagramPacket.getAddress(), message.AckMsg(seq, myId, idPlayer));
                game.getSnake().get(idPlayer).nodeRole = SnakesProto.NodeRole.VIEWER;
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
}
