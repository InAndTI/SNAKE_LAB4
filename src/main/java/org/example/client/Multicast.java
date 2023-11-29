package org.example.client;

import com.google.protobuf.InvalidProtocolBufferException;
import org.example.SnakesProto;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class Multicast {
    private final InetAddress multicastAddress;
    private final MulticastSocket socket ;
    private byte[] receiveData = new byte[1180];
    private int port = 9192;;
    private int length = 0;

    private boolean p = true;
    public Multicast() {
        try {
            multicastAddress = InetAddress.getByName("239.192.0.4");
            socket = new MulticastSocket(port);
            socket.joinGroup(new InetSocketAddress(multicastAddress, port), NetworkInterface.getByName("net0 (Microsoft 6to4 Adapter)"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void findCopies() {
        Thread discoveryThread = new Thread(() -> {
            int i = 0;
            while (true) {
                    SnakesProto.GameMessage gameMessage = receiveUDP();
                    //System.out.println(gameMessage.getAnnouncement().getGames(0).getGameName());
            }
        });
        discoveryThread.start();
    }
    private SnakesProto.GameMessage receiveUDP() {
        //System.out.println(1);
        DatagramPacket receivePacket = new DatagramPacket(receiveData, 1180);
        try {
            socket.receive(receivePacket);
            //System.out.println("+++++++++"+socket.getInetAddress().getHostAddress()+"+++++++++++++++++++++");
            int actualLength = receivePacket.getLength();
            // Создаем новый буфер с фактическим размером данных
            byte[] actualData = new byte[actualLength];
            System.arraycopy(receivePacket.getData(), 0, actualData, 0, actualLength);
            SnakesProto.GameMessage gameMessage = SnakesProto.GameMessage.parseFrom(actualData);
            if(p&& receivePacket.getLength()>0) {
                System.out.println(p);
                Client client = new Client(9911, gameMessage.getAnnouncement().getGames(0).getConfig().getStateDelayMs());
                client.joinMsg(new DatagramPacket(actualData, actualData.length, receivePacket.getAddress(), receivePacket.getPort())
                        , "араб", SnakesProto.NodeRole.NORMAL);
                p  = false;
            }

            return gameMessage;
        } catch (IOException exception) {
            return null;
        }
    }
}
