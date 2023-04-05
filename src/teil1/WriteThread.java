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
public class WriteThread extends Thread {
    private PrintWriter writer;
    private Socket socket;
    private ChatClient client;

    public WriteThread(Socket socket, ChatClient client) {
        this.socket = socket;
        this.client = client;

        try {
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
        } catch (IOException ex) {
            System.out.println("Error getting output stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void run() {

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

        do {
            try {
                text = userIn.readLine();
            } catch (IOException e) {
                System.out.println("!!!!!!!!!!!!!!!!!! Fehler 2 in WriteThread!!!!!!!!!!!!!!!!!!!!!!!");
                throw new RuntimeException(e);
            }
            writer.println(text);

        } while (!text.equals("bye"));

        try {
            socket.close();
        } catch (IOException ex) {

            System.out.println("Error writing to server: " + ex.getMessage());
        }
    }
}
