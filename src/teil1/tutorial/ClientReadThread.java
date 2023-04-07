package teil1.tutorial;

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
        while (true) {
            try {
                String response = reader.readLine();
                System.out.println("\n" + response);

            } catch (IOException ex) {
                System.out.println("Error reading from server: " + ex.getMessage());
                ex.printStackTrace();
                break;
            }
        }
    }
}

