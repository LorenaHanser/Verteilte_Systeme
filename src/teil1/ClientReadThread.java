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
            System.out.println("Error getting input stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void run() {
        // Endlosschleife
        while (socket.isBound()) {
            try {
                String response = reader.readLine();
                System.out.println(response);

            } catch (IOException ex) {
                System.out.println("Die Verbindung zum Server wurde getrennt: " + ex.getMessage() + "\n" + "Möchten sie weiterchatten? (y/n)");
                break;
            }
        }
    }
}

