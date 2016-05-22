package edu.tuc.bsvs.rockpaperscissors.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Maik on 12/05/16.
 */
public class Game extends Thread {

    public static final String WAIT_MESSAGE = "Waiting for Input";
    public static final String END_MESSAGE = "Game is over";

    // the clients
    private final Socket CLIENT_1;
    private final Socket CLIENT_2;

    // the input scanners
    private Scanner inClient1;
    private Scanner inClient2;

    // the output writers
    private PrintWriter outClient1;
    private PrintWriter outClient2;

    public Game(Socket client1, Socket client2) {
        this.CLIENT_1 = client1;
        this.CLIENT_2 = client2;

        try {
            inClient1 = new Scanner(this.CLIENT_1.getInputStream());
            inClient2 = new Scanner(this.CLIENT_2.getInputStream());
            outClient1 = new PrintWriter(this.CLIENT_1.getOutputStream(), true);
            outClient2 = new PrintWriter(this.CLIENT_2.getOutputStream(), true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + CLIENT_1.hashCode();
        hash = 73 * hash + CLIENT_2.hashCode();
        return hash;
    }

    @Override
    public void run() {
        int inputClient1;
        int inputClient2;
        boolean again;

        // print game info
        System.out.println("Game " + hashCode() + " with the clients " + getCLIENT_1().getInetAddress() + " and "
                + getCLIENT_2().getInetAddress() + " has started.");

        do {
            again = false;
            broadcast("The game begins! What do you chose? Rock(1), Paper(2), Scissors(3)");

            // send out messages to both clients that they should stop listening for messages
            broadcast(WAIT_MESSAGE);

            // wait for user input
            inputClient1 = inClient1.nextInt();
            inputClient2 = inClient2.nextInt();



            boolean playerOneWins = false;
            boolean playerTwoWins = false;
            String choice1 = "";
            String choice2 = "";

            // decide who wins
            switch (inputClient1) {
                case 1:
                    switch (inputClient2) {
                        case 1:
                            choice2 = "rock";
                            break;
                        case 2:
                            playerTwoWins = true;
                            choice2 = "paper";
                            break;
                        case 3:
                            playerOneWins = true;
                            choice2 = "scissors";
                            break;
                        default:
                            break;
                    }
                    choice1 = "rock";
                    break;
                case 2:
                    switch (inputClient2) {
                        case 1:
                            playerOneWins = true;
                            choice2 = "rock";
                            break;
                        case 2:
                            choice2 = "paper";
                            break;
                        case 3:
                            playerTwoWins = true;
                            choice2 = "scissors";
                            break;
                        default:
                            break;
                    }
                    choice1 = "paper";
                    break;
                case 3:
                    switch (inputClient2) {
                        case 1:
                            playerTwoWins = true;
                            choice2 = "rock";
                            break;
                        case 2:
                            playerOneWins = true;
                            choice2 = "paper";
                            break;
                        case 3:
                            choice2 = "scissors";
                            break;
                        default:
                            break;
                    }
                    choice1 = "scissors";
                    break;
                default:
                    break;
            }

            // print player choice info
            System.out.println("[" + hashCode() + "] Player One chose " + choice1 + " and Player Two chose " + choice2);

            // send out the winner/loser message
            if (playerOneWins) {
                System.out.println("[" + hashCode() + "] Player One wins.");
                outClient1.println("Congratulations! You won with " + choice1 + " against " + choice2 + ". Do you want to play again? YES (1) or NO (2)");
                outClient2.println("Damn, you lost with " + choice2 + " against " + choice1 + ". Do you want a rematch? YES (1) or NO (2)");
            } else if (playerTwoWins) {
                System.out.println("[" + hashCode() + "] Player Two wins.");
                outClient2.println("Congratulations! You won with " + choice2 + " against " + choice1 + ". Do you want to play again? YES (1) or NO (2)");
                outClient1.println("Damn, you lost with " + choice1 + " against " + choice2 + ". Do you want a rematch? YES (1) or NO (2)");
            } else {
                System.out.println("[" + hashCode() + "] Draw.");
                broadcast("WOW a draw. Both of you picked " + choice1 + ". Do you want to play again? YES (1) or NO (2)");
            }

            broadcast(WAIT_MESSAGE);
            inputClient1 = inClient1.nextInt();
            inputClient2 = inClient2.nextInt();

            // evaluate if the players want to play again
            switch (inputClient1) {
                case 1:
                    switch (inputClient2) {
                        case 1:
                            again = true;
                            broadcast("Both players want to play a new game.");
                            break;
                        case 2:
                            outClient1.println("The other player does not want to play again. The session will be closed.");
                            outClient2.println("The session will be closed now.");
                            break;
                    }
                    break;
                case 2:
                    switch (inputClient2) {
                        case 1:
                            outClient1.println("The session will be closed now.");
                            outClient2.println("The other player does not want to play again. The session will be closed.");
                            break;
                        case 2:
                            broadcast("The session will be closed now.");
                            break;
                    }
                    break;
                default:
                    break;
            }

        } while(again);



        try {
            broadcast(END_MESSAGE);
            CLIENT_1.close();
            CLIENT_2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Game " + hashCode() + " is over.");


    }

    private void broadcast(String message) {
        outClient1.println(message);
        outClient2.println(message);
    }

    /*
        GETTER
     */

    public Socket getCLIENT_1() {
        return CLIENT_1;
    }

    public Socket getCLIENT_2() {
        return CLIENT_2;
    }




}
