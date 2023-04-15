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

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

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

            System.out.println(ANSI_PURPLE + "Connected to the chat server (port: " + port + ")" + ANSI_RESET);

            new ClientReadThread(socket, this).start();
            new ClientWriteThread(socket, this).start(); //hier starten wir die Threads

        } catch (UnknownHostException ex) {
            System.out.println(ANSI_RED + "Die Konfiguration stimmt nicht: " + ex.getMessage() + ANSI_RESET);
        } catch (IOException ex) {
            System.out.println(ANSI_RED + "Der Server " + port + " ist nicht online: " + ex.getMessage() + ".\nProbiere erneut." + ANSI_RESET);
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
