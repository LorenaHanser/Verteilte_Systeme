package teil1;

import java.net.*;
import java.io.*;

public class Client {
    private String hostname;
    private int port;
    private String userName;

    // Konstruktor
    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void execute() {
        try {
            Socket socket = new Socket(hostname, port);

            System.out.println("Connected to the chat server");

            new ClientReadThread(socket, this).start();
            new ClientWriteThread(socket, this).start(); //hier starten wir die Threads

        } catch (UnknownHostException ex) {
            System.out.println("Die Konfiguration stimmt nicht: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("Der Server ist nicht online: " + ex.getMessage());
        }

    }

    void setUserName(String userName) {
        this.userName = userName;
    }

    String getUserName() {
        return this.userName;
    }


    public static void main(String[] args) {
        //if (args.length < 2) return;

        String hostname = "localhost";//args[0];
        int port = 8989;//Server 1 hat immer Port 8989 Server 2 8990
        // Random funktion für "Lastverteilung"
        int randomNumber = (int) (Math.random()*2);
        port = port+randomNumber;


        Client client = new Client(hostname, port);
        client.execute();
    }
}