package edu.tuc.bsvs.rockpaperscissors.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {

    /**
     * @param args Port des Servers
     * @throws IOException
     */
    public static void startServer(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        int port = 0;

        try {
            port = Integer.valueOf(args[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.print("You have to enter the desired port for the server. Please do so now: ");
            port = Integer.valueOf(scanner.nextLine());
        }
        // the user has to set the port between 10000 and 20000
        while (port < 10000 || port > 20000) {
            System.out.print("The port has to be between 10000 and 20000, please enter a new one: ");
            port = Integer.valueOf(scanner.nextLine());
        }

        ServerSocket server = new ServerSocket(port);
        InetAddress ip = InetAddress.getLocalHost();
        Socket client;
        Socket lastClient = null;
        int counter = 0;

        System.out.println("Server successfully started on " + ip + ":" + server.getLocalPort() + ". Waiting for players to connect.");

        while (true) {
            client = server.accept();
            counter++;

            // we want to start a game when 2 clients connected, otherwise we tell the first client he has to wait
            if (counter % 2 == 0) {
                Game game = new Game(lastClient, client);
                game.start();
            } else {
                lastClient = client;
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                out.println("Waiting for one more player to connect...");
            }
        }
    }
}