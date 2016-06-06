package edu.tuc.bsvs.rockpaperscissors.server;

import java.io.*;
import java.net.Socket;

/**
 * Created by Smadback on 22.05.2016.
 */
public class MySocket {

    private Socket socket = null;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;
    private int socketId = 0;

    public MySocket(String address, int port) throws IOException {
        super();
        this.socket = new Socket(address, port);
        this.out = new ObjectOutputStream(new BufferedOutputStream(this.socket.getOutputStream()));
        out.flush();
        this.in = new ObjectInputStream(new BufferedInputStream(this.socket.getInputStream()));
    }

    public MySocket(Socket socket, int socketId) throws IOException {
        super();
        this.socket = socket;
        this.out = new ObjectOutputStream(new BufferedOutputStream(this.socket.getOutputStream()));
        out.flush();
        this.in=new ObjectInputStream(new BufferedInputStream(this.socket.getInputStream()));
        this.socketId = socketId;
    }

    public void write(Packet output) throws IOException {
        out.writeObject(output);
        out.flush();
    }

    public Packet read() throws IOException, ClassNotFoundException {
        return (Packet) in.readObject();
    }

    public Socket getSocket() {
        return socket;
    }

    public int getSocketId() { return socketId; }

}
