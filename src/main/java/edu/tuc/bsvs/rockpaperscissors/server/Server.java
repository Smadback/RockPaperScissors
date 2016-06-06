package edu.tuc.bsvs.rockpaperscissors.server;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {

    private boolean isRunning = true;
    private ArrayList<MySocket> clients = null;
    private ArrayList<Game> games = null;

    public Server() {
        clients = new ArrayList<>();
    }

    /**
     * @param args Port des Servers
     * @throws IOException
     */
    public void start(String[] args) throws IOException {
        MySocket client;
        games = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        int port = 0;
        boolean again;
        ServerSocket server = null;
        int clientId = 1;


        System.out.print("[INPUT] Please enter the port for your server. It has to be between 10,000 and 20,000: ");
        port = Integer.valueOf(scanner.nextLine());

        do {
            again = false;

            try {
                // the user has to set the port between 10000 and 20000
                while (port < 10000 || port > 20000) {
                    System.out.print("[INPUT] The port has to be between 10000 and 20000, please enter a new one: ");
                    port = Integer.valueOf(scanner.nextLine());
                }

                server = new ServerSocket(port);
            } catch (BindException e) {
                System.out.print("[INPUT] The port you requested is already in use, please chose a different one: ");
                port = Integer.valueOf(scanner.nextLine());
                again = true;
            }
        } while (again);

        InetAddress ip = InetAddress.getLocalHost();
        System.out.println("[SERVER STARTED] " + ip + ":" + server.getLocalPort() + ". Waiting for players to connect.");

        // wait for clients to connect
        while (isRunning) {
            client = new MySocket(server.accept(), clientId++);
            clients.add(client);
            System.out.println("[CONNECTED] " + client.getSocket().getInetAddress() + ": " + client.getSocket().getPort());


            // matchmaking
            int entered;
            try {
                Game game = games.get(games.size() - 1);
                entered = game.enterClient(client);
                switch(entered) {
                    case -1: //game was already full
                        Game g = new Game(client);
                        games.add(g);
                        break;
                    case 1: //2 clients are in the game
                        game.start();
                        break;
                    default:
                        break;
                }
            } catch(ArrayIndexOutOfBoundsException e) {
                Game g = new Game(client);
                games.add(g);
            }
        }
    }

    public void finish() {
        isRunning = false;
    }
}