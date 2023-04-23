package teil2;

import java.net.*;
import java.io.*;

/**
 * Hier wird der Client gestartet und verbindet sich über Lastverteilung mittels {@link Math#random()} mit einem zufälligen {@link Server}.
 * Außerdem werden die Threads {@link ClientReadThread} und {@link ClientReadThread} gestartet
 */
public class Client {

    public static final String DISCONNECT = "DISCONNECT";
    public static final String SHUTDOWN = "SHUTDOWN";

    private final String hostname;
    private int port;
    private String userName;

    /**
     * Konstruktor
     * @param hostname meistens "localhost", kann aber auch durch die IP-Adresse des Servers im gleichen Netzwerk ersetzt werden
     * @param port Port, auf dem der Server läuft
     */
    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    /**
     * Die Methode erstellt ein neues Objekt der Klasse Socket und baut so die Verbindung zum Server auf.
     * Zusätzlich werden Objekte der Klassen {@link ClientReadThread} und {@link ClientWriteThread} erstellt und gestartet.
     * <p>
     * Wenn auf dem zufällig ausgewählten Port kein Server online ist, wird erneut die main-Methode aufgerufen, damit ein neuer Port zufällig generiert wird.
     */
    public void execute() {

        try {
            Socket socket = new Socket(hostname, port);

            System.out.println(Server.ANSI_PURPLE + "Connected to the chat server (port: " + port + ")" + Server.ANSI_RESET);

            new ClientReadThread(socket, this).start();
            new ClientWriteThread(socket, this).start();

        } catch (UnknownHostException ex) {
            System.out.println(Server.ANSI_RED + "Die Konfiguration stimmt nicht: " + ex.getMessage() + Server.ANSI_RESET);
        } catch (IOException ex) {
            System.out.println(Server.ANSI_RED + "Der Server " + port + " ist nicht online: " + ex.getMessage() + ".\nProbiere erneut." + Server.ANSI_RESET);
            main(new String[1]);
        }

    }

    /**
     * Setzt den Nutzernamen des Clients
     * @param userName vom Nutzer eingegebener Nutzername
     */
    void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Die Methode wird zum Starten eines Clients ausgeführt. Ggf. führt die {@link Client#execute()}-Methode diese Methode erneut aus.
     * <p>
     * Hier wird die Lastverteilung ausgeführt, indem mit {@link Math#random()} eine Zufallszahl generiert wird, die entweder 0 oder 1 ist.
     * Diese wird zum Port des ersten Servers dazu addiert.
     * @param args String[]
     */
    public static void main(String[] args) {

        String hostname = "localhost";
        int port = 8988;
        int randomNumber = (int) (Math.random() * 3);
        port = port + randomNumber;

        Client client = new Client(hostname, port);
        client.execute();

    }

}