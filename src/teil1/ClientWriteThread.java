package teil1;

import java.io.*;
import java.net.*;
import java.sql.Timestamp;

public class ClientWriteThread extends Thread {

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
            System.out.println(Server.ANSI_RED + "Error getting output stream: " + ex.getMessage() + Server.ANSI_RESET);
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
            System.out.println(Server.ANSI_RED + "!!!!!!!!!!!!!!!!!! Fehler 1 in WriteThread!!!!!!!!!!!!!!!!!!!!!!!" + Server.ANSI_RESET);
            throw new RuntimeException(e);

        }
        client.setUserName(userName);
        writer.println(userName);

        String text;
        Timestamp timestamp;

        // Endlosschleife
        do {
            // switch/case
            try {
                text = userIn.readLine();
                timestamp = new Timestamp(System.currentTimeMillis());
                //System.out.println("-- wir sind in ClientWriteThread run()");
                if (text.trim().isEmpty()) {
                    System.out.println(Server.ANSI_RED + "Leere Texteingaben sind nicht erlaubt!" + Server.ANSI_RESET);
                } else {
                    String message = timestamp + ";" + text;
                    writer.println(message); // Nachricht senden
                }
            } catch (IOException e) {
                System.out.println(Server.ANSI_RED + "!!!!!!!!!!!!!!!!!! Fehler 2 in WriteThread!!!!!!!!!!!!!!!!!!!!!!!" + Server.ANSI_RESET);
                throw new RuntimeException(e);
            }
            if (text.equals(DISCONNECT)) {
                reconnect = true; // Flag setzen
                break; // Schleife beenden, und Socket noch nicht schließen
            } else if (text.equals(SHUTDOWN)) {
                break; // Schleife beenden, und Socket schließen
            }
        } while (true);

        if (reconnect) {
            // Client fragen, ob er sich neu verbinden möchte
            reconnect = false;
            System.out.println(Server.ANSI_PURPLE + "Möchten Sie weiter chatten? (y/n)" + Server.ANSI_RESET);
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
                    System.out.println(Server.ANSI_RED + "Fehlerhafte Eingabe!" + Server.ANSI_RESET);
                    System.exit(0);
                }
            } catch (IOException e) {
                System.out.println(Server.ANSI_RED + "Fehler beim Lesen von Benutzereingabe: " + e.getMessage() + Server.ANSI_RESET);
            }
        } else {
            try {
                socket.close();
                System.exit(0);
            } catch (IOException ex) {
                System.out.println(Server.ANSI_RED + "Error writing to server: " + ex.getMessage() + Server.ANSI_RESET);
            }
        }
    }
}
