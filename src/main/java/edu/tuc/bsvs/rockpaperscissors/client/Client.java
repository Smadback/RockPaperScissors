package edu.tuc.bsvs.rockpaperscissors.client;

import edu.tuc.bsvs.rockpaperscissors.helper.Identifier;
import edu.tuc.bsvs.rockpaperscissors.server.MySocket;
import edu.tuc.bsvs.rockpaperscissors.server.Packet;

import java.io.EOFException;
import java.net.SocketException;
import java.util.Scanner;

public class Client extends Thread {

    private boolean isRunning = true;
    private int id;

    @Override
    public void run() {
        Scanner in = new Scanner(System.in);
        MySocket socket = null;

        boolean again;
        do {
            again = false;
            System.out.print("[INPUT] The server ip address: ");
            String ip = in.nextLine();
            System.out.print("[INPUT] The server port: ");
            int port = Integer.valueOf(in.nextLine());

            // connect to the server
            try {
                socket = new MySocket(ip, port);
            } catch (Exception e) {
                System.out.println("[INFO] The connection to the server " + ip + ":" + port +  " couldn't be established, please try again.");
                again = true;
            }
        } while (again);

        System.out.println("[INFO] Connection to the server established.");


        while (isRunning) {
            Packet input;
            String i;

            try {
                input = socket.read();

                if (input != null) {
                    synchronized (socket) {
                        switch (input.getIdentifier()) {
                            case INIT:
                                id = (int) input.getValue();
                                break;
                            case CHOICE:
                                System.out.print(input.getValue());
                                i = in.nextLine();
                                socket.write(new Packet(Identifier.CHOICE, i.toUpperCase(), id));
                                break;
                            case AGAIN:
                                System.out.print(input.getValue());
                                i = in.nextLine();
                                socket.write(new Packet(Identifier.AGAIN, i.toUpperCase(), id));
                                break;
                            case INFO:
                                System.out.println(input.getValue());
                                break;
                            case ERROR:
                            case GAMEOVER:
                                System.out.println(input.getValue());
                                finish();
                                break;
                            default:
                                break;
                        }
                    }
                }

            } catch (EOFException | SocketException e) {
                System.out.println("[INFO] Connection to the Server lost.");
                finish();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("[INFO] Something bad happened...");
                finish();
            }
        }
    }


    public void finish() {
        isRunning = false;
    }
}

