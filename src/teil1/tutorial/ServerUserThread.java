package teil1.tutorial;

import java.io.*;
import java.net.*;

/**
 * This thread handles connection for each connected client, so the server
 * can handle multiple clients at the same time.
 *
 * @author www.codejava.net
 */
public class ServerUserThread extends Thread {
    private Socket socket;
    private Server server;
    private PrintWriter writer;

    // Konstruktor

    public ServerUserThread(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        try {
            // Nachricht vom client empfangen
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            printUsers();
            writer.println("Please enter your name:");

            String userName = reader.readLine();
            server.addUserName(userName);

            String serverMessage = "New user connected: " + userName;
            server.sendMessage(serverMessage, this);        //todo: this muss ausgetauscht werden zum jeweiligen Chatpartner

            String clientMessage;

            // Endlosschleife

            do {
                clientMessage = reader.readLine();
                serverMessage = "[" + userName + "]: " + clientMessage;
                server.sendMessage(serverMessage, this);  //todo: this muss ausgetauscht werden zum jeweiligen Chatpartner

            } while (!clientMessage.equals("bye"));

            server.removeUser(userName, this);
            socket.close();

            serverMessage = userName + " has quitted.";
            server.sendMessage(serverMessage, this);  //todo: this muss ausgetauscht werden zum jeweiligen Chatpartner

        } catch (IOException ex) {
            System.out.println("Error in UserThread: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Sends a list of online users to the newly connected user.
     */
    void printUsers() {
        if (server.hasUsers()) {
            writer.println("Connected users: " + server.getUserNames());
        } else {
            writer.println("No other users connected");
        }
    }

    /**
     * Sends a message to the client.
     */
    void sendMessage(String message) {
        writer.println(message);
    }
}
