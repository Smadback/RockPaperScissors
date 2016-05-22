package edu.tuc.bsvs.rockpaperscissors.client;

import edu.tuc.bsvs.rockpaperscissors.server.Game;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    public static void startClient() {
        Socket server = null;
        Scanner in = new Scanner(System.in);
        String message = "";

        try {
            boolean again;

            System.out.print("The server ip address: ");
            String ip = in.nextLine();
            System.out.print("The server port: ");
            int port = Integer.valueOf(in.nextLine());

            // connect to the server
            server = new Socket(ip, port);
            Scanner input = new Scanner(server.getInputStream());
            PrintWriter output = new PrintWriter(server.getOutputStream(), true);

            System.out.println("Connection to the server established.");


            while (true) {
                while (!message.equals(Game.WAIT_MESSAGE) && !message.equals(Game.END_MESSAGE)) {
                    if (input.hasNext()) {
                        message = input.nextLine();
                        if (!message.equals(Game.WAIT_MESSAGE) && !message.equals(Game.END_MESSAGE))
                            System.out.println(message);
                    }
                }

                if (!message.equals(Game.END_MESSAGE)) {
                    output.println(in.nextLine());
                    message = "";
                } else
                    break;
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (server != null)
                try {
                    server.close();
                } catch (IOException e) {
                }
        }
    }
}

