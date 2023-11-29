package org.example.server;

import java.net.InetAddress;

public class WriteRead {
    public int myPort;
    public InetAddress ip;
    public long write = System.nanoTime();
    public long read = System.nanoTime();
    public boolean dep = false;
    public WriteRead(int myPort, InetAddress ip){
        this.myPort = myPort;
        this.ip = ip;
    }
}
