package teil1;

import java.io.*;
import java.net.*;

/**
 * This thread is responsible for reading user's input and send it
 * to the server.
 * It runs in an infinite loop until the user types 'bye' to quit.
 *
 * @author www.codejava.net
 */
public class ClientWriteThread extends Thread {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    private PrintWriter writer;
    private Socket socket;
    private Client client;

    private final String DISCONNECT = "DISCONNECT";
    private final String SHUTDOWN = "SHUTDOWN";

    // Konstruktor

    public ClientWriteThread(Socket socket, Client client) {
        this.socket = socket;
        this.client = client;

        try {
            // Schreiben der Chatnachricht
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
        } catch (IOException ex) {
            System.out.println(ANSI_RED + "Error getting output stream: " + ex.getMessage() + ANSI_RESET);
            ex.printStackTrace();
        }
    }

    public void run() {

        boolean reconnect = false;

        BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));

        String userName = null;
        try {
            userName = userIn.readLine();
        } catch (IOException e) {
            System.out.println(ANSI_RED + "!!!!!!!!!!!!!!!!!! Fehler 1 in WriteThread!!!!!!!!!!!!!!!!!!!!!!!" + ANSI_RESET);
            throw new RuntimeException(e);

        }
        client.setUserName(userName);
        writer.println(userName);

        String text;

        // Endlosschleife
        do {
            // switch/case
            try {
                text = userIn.readLine();
                //System.out.println("-- wir sind in ClientWriteThread run()");
            } catch (IOException e) {
                System.out.println(ANSI_RED + "!!!!!!!!!!!!!!!!!! Fehler 2 in WriteThread!!!!!!!!!!!!!!!!!!!!!!!" + ANSI_RESET);
                throw new RuntimeException(e);
            }
            if (text.equals(DISCONNECT)) {
                reconnect = true; // Flag setzen
                break; // Schleife beenden, und Socket noch nicht schließen
            } else if (text.equals(SHUTDOWN)) {
                break; // Schleife beenden, und Socket schließen
            } else {
                if (text.trim().isEmpty()) {
                    System.out.println("Leere Texteingaben sind nicht erlaubt!");
                } else{
                    writer.println(text); // Nachricht senden
                }
            }

        } while (true);

        if (reconnect) {
            // Client fragen, ob er sich neu verbinden möchte
            reconnect = false;
            System.out.println(ANSI_PURPLE + "Möchten Sie weiter chatten? (y/n)" + ANSI_RESET);
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                String input = userIn.readLine();
                if (input.equalsIgnoreCase("y")) {
                    client.execute(); // Neue Verbindung aufbauen
                } else if (input.equalsIgnoreCase("n")) {
                    System.exit(0);
                } else {
                    System.out.println(ANSI_RED + "Fehlerhafte Eingabe!" + ANSI_RESET);
                    System.exit(0);
                }
            } catch (IOException e) {
                System.out.println(ANSI_RED + "Fehler beim Lesen von Benutzereingabe: " + e.getMessage() + ANSI_RESET);
            }
        } else {
            try {
                socket.close();
                System.exit(0);
            } catch (IOException ex) {
                System.out.println(ANSI_RED + "Error writing to server: " + ex.getMessage() + ANSI_RESET);
            }
        }
    }
}
