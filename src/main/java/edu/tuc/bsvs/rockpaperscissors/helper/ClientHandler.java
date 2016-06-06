package edu.tuc.bsvs.rockpaperscissors.helper;

import edu.tuc.bsvs.rockpaperscissors.server.Game;
import edu.tuc.bsvs.rockpaperscissors.server.MySocket;
import edu.tuc.bsvs.rockpaperscissors.server.Packet;

import java.io.IOException;

/**
 * Created by Smadback on 23.05.2016.
 */
public class ClientHandler extends Thread {

    private boolean isRunning = true;
    private MySocket socket = null;
    private Game game = null;

    public ClientHandler(MySocket socket, Game game) {
        this.socket = socket;
        this.game = game;
    }

    @Override
    public void run() {
        while(isRunning) {
            Packet input;
            try {
                input = socket.read();
                if (input != null) {
                    synchronized (game) {
                        synchronized (socket) {
                            game.handleInput(input);
                        }
                    }
                }
            } catch (IOException e) {
                synchronized (game) {
                    synchronized (socket) {
                        game.connectionLost(socket.getSocketId());
                    }
                }
                finish();
                break;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public void finish() {
        isRunning = false;
    }
}