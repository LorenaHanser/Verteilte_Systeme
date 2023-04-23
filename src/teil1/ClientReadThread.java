package teil1;

import java.io.*;
import java.net.*;

public class ClientReadThread extends Thread {

    private BufferedReader reader;
    private Socket socket;
    private Client client;

    // Konstruktor

    public ClientReadThread(Socket socket, Client client) {
        this.socket = socket;
        this.client = client;

        try {
            // Lesen der Chatnachricht
            InputStream input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
        } catch (IOException ex) {
            System.out.println(Server.ANSI_RED + "Error getting input stream: " + ex.getMessage() + Server.ANSI_RESET);
            ex.printStackTrace();
        }
    }

    public void run() {
        // Endlosschleife
        while (socket.isBound()) {
            try {
                String response = reader.readLine();
                if(response == null){
                    throw new RuntimeException("Verbindung getrennt");
                }
                if (!response.contains("SHUTDOWN") && !response.contains("DISCONNECT")) {
                    System.out.println(response);
                }
            } catch (IOException ex) {
                System.out.println(Server.ANSI_PURPLE + "Die Verbindung zum Server wurde getrennt: " + ex.getMessage() + Server.ANSI_RESET);
                break;
            }
        }
    }
}
