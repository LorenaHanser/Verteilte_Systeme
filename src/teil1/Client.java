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

            System.out.println(Server.ANSI_PURPLE + "Connected to the chat server (port: " + port + ")" + Server.ANSI_RESET);

            new ClientReadThread(socket, this).start();
            new ClientWriteThread(socket, this).start(); //hier starten wir die Threads

        } catch (UnknownHostException ex) {
            System.out.println(Server.ANSI_RED + "Die Konfiguration stimmt nicht: " + ex.getMessage() + Server.ANSI_RESET);
        } catch (IOException ex) {
            System.out.println(Server.ANSI_RED + "Der Server " + port + " ist nicht online: " + ex.getMessage() + ".\nProbiere erneut." + Server.ANSI_RESET);
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
        int port = 8988;                                    // Server 1 hat immer Port 8988 Server 2 8989 und Server 3 8990
        int randomNumber = (int) (Math.random() * 3);       // Lastverteilung mit Math.random()
        port = port + randomNumber;

        Client client = new Client(hostname, port);
        client.execute();
    }
}
