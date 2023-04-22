package teil1;

import java.net.*;
import java.io.*;

/**
 * Der Client:
 *
 * Hier wird der Client gestartet und verbindet sich über Lastverteilung (math.random)
 * mit einem zufälligen Server.
 * Außerdem werden die Threads ClientReadThread & ClientWriteThread gestartet
 */
public class Client {

    private final String hostname;
    private int port;
    private String userName;

    // Konstruktor
    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void execute() {
        try {
            Socket socket = new Socket(hostname, port);           // Verbindung mit einem Server aufbauen

            System.out.println(Server.ANSI_PURPLE + "Connected to the chat server (port: " + port + ")" + Server.ANSI_RESET);

            new ClientReadThread(socket, this).start();     // hier starten wir den ClientReadThread
            new ClientWriteThread(socket, this).start();    // hier starten wir den ClientWriteThread

        } catch (UnknownHostException ex) {
            System.out.println(Server.ANSI_RED + "Die Konfiguration stimmt nicht: " + ex.getMessage() + Server.ANSI_RESET);
        } catch (IOException ex) {
            System.out.println(Server.ANSI_RED + "Der Server " + port + " ist nicht online: " + ex.getMessage() + ".\nProbiere erneut." + Server.ANSI_RESET);   // Falls Server mit gewähltem Port nicht online,
            main(new String[1]);                                                                                                                                // versuche erneut
        }
    }

    void setUserName(String userName) {
        this.userName = userName;
    }

    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 8989;                                    // Server 1 hat immer Port 8989 Server 2 8990
        int randomNumber = (int) (Math.random() * 2);       // Lastverteilung mit Math.random()
        port = port + randomNumber;                         // 8989 + 0 oder 1

        Client client = new Client(hostname, port);
        client.execute();
    }
}
