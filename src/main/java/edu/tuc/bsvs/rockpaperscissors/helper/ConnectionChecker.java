package edu.tuc.bsvs.rockpaperscissors.helper;

import edu.tuc.bsvs.rockpaperscissors.server.Game;
import edu.tuc.bsvs.rockpaperscissors.server.MySocket;
import edu.tuc.bsvs.rockpaperscissors.server.Packet;

import java.io.IOException;

/**
 * Created by Maik on 25/05/16.
 */
public class ConnectionChecker extends Thread {

    private MySocket client = null;
    private Game game = null;
    private boolean isRunning = true;

    public ConnectionChecker(Game game, MySocket client) {
        this.game = game;
        this.client = client;
    }

    @Override
    public void run() {
        while(isRunning) {
            try {
                sleep(3000);
                client.write(new Packet(Identifier.CONNECTION, "Check connection"));
            } catch (IOException e) {
                synchronized (game) {
                    synchronized (client) {
                        game.connectionLost(client.getSocketId());
                    }
                }
                finish();
            } catch (InterruptedException e) {
                finish();
            }
        }
    }

    public void finish() {
        isRunning = false;
    }
}
