package edu.tuc.bsvs.rockpaperscissors;

import edu.tuc.bsvs.rockpaperscissors.client.Client;
import edu.tuc.bsvs.rockpaperscissors.server.Server;

import java.io.IOException;
import java.util.Scanner;

/**
 * Created by Smadback on 13.05.2016.
 */
public class RockPaperScissors {

    public static void main(String args[]) {

        Scanner scanner = new Scanner(System.in);

        System.out.print("What do you want to do, host a Server(1) or join a Game(2)?  To exit press(3). ");
        String input = scanner.nextLine();

        try {
            int integer = Integer.valueOf(input);

            switch (integer) {
                case 1:
                    try {
                        Server.startServer(args);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    Client.startClient();
                    break;
                case 3:
                    break;
                default:
                    throw new Exception();
            }

        } catch(Exception e) {
            System.out.println("Wrong input. Try again.");
        }

    }

}
