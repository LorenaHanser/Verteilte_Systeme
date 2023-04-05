package teil1.tutorial;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * This is the chat server program.
 * Press Ctrl + C to terminate the program.
 *
 * @author www.codejava.net
 */
public class Server {
    private int port;
    private Set<String> userNames = new HashSet<>();
    private Set<ServerUserThread> userThreads = new HashSet<>();

    // Konstruktor

    public Server(int port) {
        this.port = port;
    }


    public void execute() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Chat Server is listening on port " + port);

            // Endlosschleife

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New user connected");

                ServerUserThread newUser = new ServerUserThread(socket, this);
                userThreads.add(newUser);
                newUser.start();

            }

        } catch (IOException ex) {
            System.out.println("Error in the server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        /*if (args.length < 1) {
            System.out.println("Syntax: java ChatServer <port-number>");
            System.exit(0);
        }*/

        int port = 8989;//Integer.parseInt(args[0]);

        Server server = new Server(port);
        server.execute();
    }

    /**
     * Delivers a message from one user to others (broadcasting)
     */
    void sendMessage(String message, ServerUserThread receiverUser) {       // receiverUser war vorher excludeUser

        // UserThread wird ausglesen aus dme Namen des Users mit dem kommuniziert werden will, und wird als "receiverUser" mitgegeben
        // Dem "receiverUser" wird dann diese Nachricht gesendet
        //
        // for (UserThread aUser : userThreads) {
        //  if (aUser != receiverUser) {
        receiverUser.sendMessage(message);
        //    }
        // }
    }

    /**
     * Stores username of the newly connected client.
     */
    void addUserName(String userName) {
        userNames.add(userName);
    }

    /**
     * When a client is disconneted, removes the associated username and UserThread
     */
    void removeUser(String userName, ServerUserThread aUser) {
        boolean removed = userNames.remove(userName);
        if (removed) {
            userThreads.remove(aUser);
            System.out.println("The user " + userName + " quitted");
        }
    }

    Set<String> getUserNames() {
        return this.userNames;
    }

    /**
     * Returns true if there are other users connected (not count the currently connected user)
     */
    boolean hasUsers() {
        return !this.userNames.isEmpty();
    }
}