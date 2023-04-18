package teil1;

import java.io.*;
import java.net.*;

public class ClientReadThread extends Thread {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

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
            System.out.println(ANSI_RED + "Error getting input stream: " + ex.getMessage() + ANSI_RESET);
            ex.printStackTrace();
        }
    }

    public void run() {
        // Endlosschleife
        while (socket.isBound()) {
            try {
                String response = reader.readLine();
                //System.out.println("-- wir sind in ClientReadThread run()");
                if (!response.contains("SHUTDOWN") && !response.contains("DISCONNECT")) {
                    System.out.println(response);
                }

            } catch (IOException ex) {
                System.out.println(ANSI_PURPLE + "Die Verbindung zum Server wurde getrennt: " + ex.getMessage() + ANSI_RESET);
                break;
            }
        }
    }
}

