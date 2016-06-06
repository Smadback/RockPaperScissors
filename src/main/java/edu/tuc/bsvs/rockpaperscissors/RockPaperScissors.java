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
        boolean again;
        String input = "";

        System.out.print("[INPUT] What do you want to do, host a Server(1) or join a Game(2)?  To exit press(3). ");

        do {
            input = scanner.nextLine();
            again = false;

            try {
                int integer = Integer.valueOf(input);

                switch (integer) {
                    case 1: // Server
                        try {
                            new Server().start(args);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2: // Client
                        new Client().start();
                        break;
                    case 3: // Exit
                        break;
                    default:
                        System.out.print("[ERROR] Your input was invalid, please try again: ");
                        again = true;
                        break;
                }

            } catch (Exception e) {
                System.out.print("[ERROR] Your input was invalid, please try again: ");
                again = true;
            }
        } while (again);

    }

}
