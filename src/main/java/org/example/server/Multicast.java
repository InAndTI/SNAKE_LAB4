package org.example.server;

import org.example.Message;
import org.example.SnakesProto;
import org.example.logics.Game;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class Multicast {
    private final InetAddress multicastAddress;
    private final int port;
//    private final UUID myUUID;
    private final long sendTime = 1000;
//    private int length = 0;
    private DatagramSocket datagramSocket;


    public Multicast(){
        try {
            this.port = 9192;

            multicastAddress = InetAddress.getByName("239.192.0.4");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void findCopies(Game game, DatagramSocket datagramSocket) {
        Thread discoveryThread = new Thread(() -> {
            this.datagramSocket = datagramSocket;
            while (true) {
                try {
                    Thread.sleep(sendTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                sendUDP(game);
            }
        });
        discoveryThread.start();
    }
    private void sendUDP(Game game) {
        try {
            Message message = new Message(game);
            SnakesProto.GameMessage gam = message.AnnouncementMsg();

            DatagramPacket sendPacket = new DatagramPacket(gam.toByteArray(), gam.toByteArray().length, multicastAddress, port);
            datagramSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
