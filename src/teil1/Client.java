package teil1;

import java.net.*;
import java.io.*;

/**
 * This is the chat client program.
 * Type 'bye' to terminte the program.
 *
 * @author www.codejava.net
 */
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

            System.out.println("Connected to the chat server (port: " + port + ")");

            new ClientReadThread(socket, this).start();
            new ClientWriteThread(socket, this).start(); //hier starten wir die Threads

        } catch (UnknownHostException ex) {
            System.out.println("Die Konfiguration stimmt nicht: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("Der Server " + port + " ist nicht online: " + ex.getMessage() + ". Probiere erneut");
            main(new String[1]);
        }

    }

    void setUserName(String userName) {
        this.userName = userName;
    }

    String getUserName() {
        return this.userName;
    }

    public static void main(String[] args) {

        String hostname = "localhost";//args[0];
        int port = 8989;                                    // Server 1 hat immer Port 8989 Server 2 8990
        int randomNumber = (int) (Math.random() * 2);       // Lastverteilung mit Math.random()
        port = port + randomNumber;

        Client client = new Client(hostname, port);
        client.execute();
    }
}
