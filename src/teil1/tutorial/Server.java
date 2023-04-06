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

    private String[] userNameRegister = {"David","Daniel","Lorena"};

    private int[] userChattetWith = new int[3];
    private ServerUserThread[] userThreadRegister = new ServerUserThread[3];

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
        int port = 8989;//Integer.parseInt(args[0]);

        Server server = new Server(port);
        server.execute();
    }

    /**
     * Delivers a message from one user to others (broadcasting)
     */
    void sendMessage(String message, int sendUserId, int receiverUserId) {       // receiverUser war vorher excludeUser

        // todo: Methodenaufruf von WriteInFile
        //userThreadRegister[receiverUser].sendMessage(message);
        // UserThread wird ausglesen aus dme Namen des Users mit dem kommuniziert werden will, und wird als "receiverUser" mitgegeben
        // Dem "receiverUser" wird dann diese Nachricht gesendet
        //
        // for (UserThread aUser : userThreads) {
        //  if (aUser != receiverUser) {
        if(userThreadRegister[receiverUserId] != null) {
            System.out.println("Diese Nachricht wurde erhalten: " + message);
            if(userChattetWith[receiverUserId] == sendUserId) {
                userThreadRegister[receiverUserId].sendMessage(message);
            }else{
                System.out.println("Der User ist gerade beschäftigt. Die Nachricht wird gespeichert!");
            }
        }else {
            System.out.println("Der User ist nicht online, die Nachricht wird aber für ihn gespeichert...");
        }
        //    }
        // }
    }

    /**
     * Stores username of the newly connected client.
     */

    void setThreadId(String userName, ServerUserThread Thread) {
        for (int i = 0; i < userNameRegister.length; i++) {
            if(userNameRegister[i].equals(userName)) {
                userThreadRegister[i] = Thread;
                break;
            }
        }


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

    int askForID(String username)
    {
        System.out.println("Folgender Name soll überprüft werden: '"+username+"'");
        int answer = -1;
        for (int i = 0; i < userNameRegister.length; i++) {
            if(username.equals( userNameRegister[i])){
                System.out.println("Es wurde eine ID gefunden");
                answer = i;
                break;
            }
        }
        return answer;
    }

    void setChatPartner(int user, int chatPartner){
        userChattetWith[user] = chatPartner;

    }



}