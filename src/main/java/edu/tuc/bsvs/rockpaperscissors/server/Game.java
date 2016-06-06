package edu.tuc.bsvs.rockpaperscissors.server;

import edu.tuc.bsvs.rockpaperscissors.helper.ClientHandler;
import edu.tuc.bsvs.rockpaperscissors.helper.ConnectionChecker;
import edu.tuc.bsvs.rockpaperscissors.helper.Identifier;

import java.io.IOException;

/**
 * Created by Maik on 12/05/16.
 */
public class Game extends Thread {

    private MySocket client_1 = null;
    private MySocket client_2 = null;
    private int clients = 0;
    private boolean isRunning = true;


    public Game(MySocket client_1) {
        this.client_1 = client_1;
        clients = 1;
    }

    @Override
    public int hashCode() {
        int hash = 5;

        hash = 73 * hash + client_1.getSocket().hashCode();
        hash = 73 * hash + client_2.getSocket().hashCode();

        return hash;
    }

    @Override
    public void run() {
        System.out.println("[" + hashCode() + "] Game with the clients has started.");

        new ConnectionChecker(this, client_1).start();
        new ConnectionChecker(this, client_2).start();

        try {
            client_1.write(new Packet(Identifier.INIT, client_1.getSocketId()));
            client_2.write(new Packet(Identifier.INIT, client_2.getSocketId()));
            broadcast(new Packet(Identifier.CHOICE, "[INPUT] Game starts now. Do you chose [R]ock, [P]aper or [S]cissors?: "));
        } catch (IOException e) {
            e.printStackTrace();
        }

        new ClientHandler(client_1, this).start();
        new ClientHandler(client_2, this).start();

    }

    private void broadcast(Packet p) throws IOException {
        client_1.write(p);
        client_2.write(p);
    }

    private char choiceClient_1 = 'x';
    private char choiceClient_2 = 'x';

    public void handleInput(Packet p) {
        try {
            // je nachdem wer gesendet hat wird das input gesetzt
            if (p.getSenderId() == client_1.getSocketId()) {
                choiceClient_1 = ((String) p.getValue()).charAt(0);
                if (choiceClient_2 == 'x')
                    client_1.write(new Packet(Identifier.INFO, "[INFO] Please wait for the other player to make a choice."));
            } else if (p.getSenderId() == client_2.getSocketId()) {
                choiceClient_2 = ((String) p.getValue()).charAt(0);
                if (choiceClient_1 == 'x')
                    client_2.write(new Packet(Identifier.INFO, "[INFO] Please wait for the other player to make a choice."));
            }

            // prüfen was gesendet wurde und dementsprechend verfahren
            switch (p.getIdentifier()) {
                case CHOICE: // what the players chose
                    if (choiceClient_1 != 'x' && choiceClient_2 != 'x')
                        determineTheWinner();
                    break;
                case AGAIN: // what the players chose
                    if (choiceClient_1 == 'Y' && choiceClient_2 == 'Y') {
                        resetInputs();
                        broadcast(new Packet(Identifier.CHOICE, "[INPUT] Game starts now. Do you chose [R]ock, [P]aper or [S]cissors?: "));
                    } else if (choiceClient_1 != 'x' && choiceClient_2 != 'x') {
                        broadcast(new Packet(Identifier.GAMEOVER, "[GAME OVER] Alright boys, at least one of you is not addicted to this game. See you later..."));
                    }
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // TODO: 24.05.2016 anfrage neuer eingabe wenn ein spieler etwas falsches aussucht
    private void determineTheWinner() {
        System.out.println("Player One chose " + choiceClient_1 + " and Player Two chose " + choiceClient_2);
        int result = choiceClient_1 - choiceClient_2;

        try {
            broadcast(new Packet(Identifier.INFO, "#####################################################################"));
            switch (result) {
                case 0: //draw
                    broadcast(new Packet(Identifier.INFO, "[DRAW] Oh snap, both players chose the same and it is a DRAW!"));
                    break;
                case -1: // client one wins
                case -2:
                case 3:
                    client_1.write(new Packet(Identifier.INFO, "[WINNER] Yeah dude, good job, real nice, you did it"));
                    client_2.write(new Packet(Identifier.INFO, "[LOSER] What a disgrace you are..."));
                    break;
                case -3: // player two wins
                case 2:
                case 1:
                    client_1.write(new Packet(Identifier.INFO, "[LOSER] HAHAHAHA u fkng scrub m8"));
                    client_2.write(new Packet(Identifier.INFO, "[WINNER] WAOW WHAT A PLAYER"));
                    break;
                default:
                    broadcast(new Packet((Identifier.ERROR), "Whoopsy daisy, something went wrong I guess..."));
                    break;
            }
            resetInputs();
            broadcast(new Packet(Identifier.INFO, "#####################################################################"));
            broadcast(new Packet(Identifier.AGAIN, "[INPUT] Do you want to play again? [Y/N]: "));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void resetInputs() {
        choiceClient_1 = 'x';
        choiceClient_2 = 'x';
    }

    /**
     * @param client
     * @return -1 = error; 0 = 1 client; 1 = 2 clients
     */
    public int enterClient(MySocket client) throws IOException {
        switch (clients) {
            case 0:
                client_1 = client;
                client_1.write(new Packet(Identifier.INFO, "Please wait for a second player to connect to your game."));
                clients++;
                return 0;
            case 1:
                client_2 = client;
                clients++;
                return 1;
            case 2:
            default:
                return -1;
        }
    }

    public void connectionLost(int socketId) {
        try {
            if (socketId == client_1.getSocketId()) {
                client_2.write(new Packet(Identifier.ERROR, "[INFO] Your opponent left the game. You won."));
            } else if (socketId == client_2.getSocketId()) {
                client_1.write(new Packet(Identifier.ERROR, "[INFO] Your opponent left the game. You won."));
            }
        } catch (IOException e) {
            System.out.println("One client did a nice rage quit");
            finish();
        }
    }

    public void finish() {
        isRunning = true;
    }
}