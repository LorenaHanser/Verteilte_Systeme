package teil2;

import java.io.*;
import java.net.*;
import java.sql.Timestamp;

/**
 * Die Klasse liest Konsoleneingaben des Clients aus und leitet diese zur Weiterverarbeitung an den Server weiter.
 */
public class ClientWriteThread extends Thread {

    private PrintWriter writer;
    private Socket socket;
    private Client client;

    /**
     * Konstruktor
     * @param socket Socket
     * @param client Client des Threads
     */
    public ClientWriteThread(Socket socket, Client client) {
        this.socket = socket;
        this.client = client;

        try {
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
        } catch (IOException ex) {
            System.out.println(Server.ANSI_RED + "Error getting output stream: " + ex.getMessage() + Server.ANSI_RESET);
        }
    }

    /**
     * Die Methode liest zu Beginn den Nutzernamen des Clients aus.
     * Danach werden in einer Schleife so lange die neuen Konsoleneingaben des Clients gelesen, bis eines der Schlüsselwörter "DISCONNECT" oder "SHUTDOWN" gelesen wird.
     * <p>
     * Es folgt ein Dialog mit "Möchten Sie weiter chatten? (y/n)", ob der Chatraum gewechselt werden oder der Client vollständig getrennt werden soll
     */
    @Override
    public void run() {

        boolean reconnect = false;

        BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));

        String userName = "";
        try {
            userName = userIn.readLine();
        } catch (IOException e) {
            System.out.println(Server.ANSI_RED + "Fehler 1 in WriteThread!" + Server.ANSI_RESET);
        }
        client.setUserName(userName);
        writer.println(userName);

        String text = "";
        Timestamp timestamp;

        do {
            try {
                text = userIn.readLine();
                timestamp = new Timestamp(System.currentTimeMillis());
                if (text.trim().isEmpty()) {
                    System.out.println(Server.ANSI_RED + "Leere Texteingaben sind nicht erlaubt!" + Server.ANSI_RESET);
                } else {
                    String message = timestamp + ";" + text;
                    writer.println(message); // Nachricht senden
                }
            } catch (IOException e) {
                System.out.println(Server.ANSI_RED + "Fehler 2 in WriteThread!" + Server.ANSI_RESET);
            }

            if (text.equals(Client.DISCONNECT)) {
                reconnect = true;   // Flag setzen
                break;              // Schleife beenden, und Socket noch nicht schließen
            } else if (text.equals(Client.SHUTDOWN)) {
                break;              // Schleife beenden, und Socket schließen
            }
        } while (true);

        if (reconnect) {
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
                System.out.println(Server.ANSI_RED + "Error: " + ex.getMessage() + Server.ANSI_RESET);
            }
        }
    }

}
