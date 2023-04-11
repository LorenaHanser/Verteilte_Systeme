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
    private PrintWriter writer;
    private Socket socket;
    private Client client;

    private final String disconnect = "DISCONNECT";
    private final String shutdown = "SHUTDOWN";

    // Konstruktor

    public ClientWriteThread(Socket socket, Client client) {
        this.socket = socket;
        this.client = client;

        try {
            // Schreiben der Chatnachricht
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
        } catch (IOException ex) {
            System.out.println("Error getting output stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void run() {

        boolean reconnect = false;

        //Console console = System.console();
        //überarbeitet von Daniel S.
        BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));

        String userName = null;
        try {
            userName = userIn.readLine();
        } catch (IOException e) {
            System.out.println("!!!!!!!!!!!!!!!!!! Fehler 1 in WriteThread!!!!!!!!!!!!!!!!!!!!!!!");
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
            } catch (IOException e) {
                System.out.println("!!!!!!!!!!!!!!!!!! Fehler 2 in WriteThread!!!!!!!!!!!!!!!!!!!!!!!");
                throw new RuntimeException(e);
            }
            if (text.equals(disconnect)) {
                reconnect = true; // Flag setzen
                break; // Schleife beenden, und Socket noch nicht schließen
            } else if (text.equals(shutdown)) {
                break; // Schleife beenden, und Socket schließen
            } else {
                writer.println(text); // Nachricht senden
            }

        } while (true);

        if (reconnect) {
            // Client fragen, ob er sich neu verbinden möchte
            System.out.println("Sie wurden abgemeldet. Möchten Sie weiterchatten? (y/n)");

            try {
                String input = userIn.readLine();
                if (input.equalsIgnoreCase("y")) {
                    client.execute(); // Neue Verbindung aufbauen
                } else if (input.equalsIgnoreCase("n")) {
                    try {
                        socket.close();
                        System.exit(0);
                    } catch (IOException ex) {
                        System.out.println("Error writing to server: " + ex.getMessage());
                    }
                } else {
                    System.out.println("Fehlerhafte Eingabe!");
                    socket.close();
                    System.exit(0);
                }
            } catch (IOException e) {
                System.out.println("Fehler beim Lesen von Benutzereingabe: " + e.getMessage());
            }
        } else {
            try {
                socket.close();
                System.exit(0);
            } catch (IOException ex) {
                System.out.println("Error writing to server: " + ex.getMessage());
            }
        }
    }
}
