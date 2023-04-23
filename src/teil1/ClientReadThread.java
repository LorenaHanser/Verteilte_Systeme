package teil1;

import java.io.*;
import java.net.*;

/**
 * Liest Nachrichten, die von Server über Netzwerk kommen und gibt die in der Konsole des Clients aus
 */
public class ClientReadThread extends Thread {

    private BufferedReader reader;
    private Socket socket;
    private Client client;

    /**
     * Konstruktor zum "Hochfahren"
     * @param socket
     * @param client Objekt der Klasse {@link Client}, welches diesen Thread startet
     */
    public ClientReadThread(Socket socket, Client client) {
        this.socket = socket;
        this.client = client;

        try {
            InputStream input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
        } catch (IOException ex) {
            System.out.println(Server.ANSI_RED + "Error getting input stream: " + ex.getMessage() + Server.ANSI_RESET);
        }
    }

    /**
     * Es wird so lange alles in der Konsole des Clients ausgegeben, bis er in eine Exception reinläuft, weil die Verbindung zum Server getrennt wurde.
     */
    @Override
    public void run() {

        // Endlosschleife
        while (socket.isBound()) {
            try {
                String response = reader.readLine();
                if (!response.contains(Client.SHUTDOWN) && !response.contains(Client.DISCONNECT)) {
                    System.out.println(response);
                }
            } catch (IOException ex) {
                System.out.println(Server.ANSI_PURPLE + "Die Verbindung zum Server wurde getrennt: " + ex.getMessage() + Server.ANSI_RESET);
                break;
            }
        }

    }

}