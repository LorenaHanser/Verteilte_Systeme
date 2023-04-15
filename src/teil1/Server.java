package teil1;

import java.io.*;
import java.net.*;
import java.nio.channels.AsynchronousByteChannel;
import java.util.*;

/**
 * This is the chat server program.
 * Press Ctrl + C to terminate the program.
 *
 * @author www.codejava.net
 */
public class Server {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    private int port;
    private File file = new File();

    private String[] userNameRegister = {"Daniel", "David", "Lorena"}; //Speichert die Usernamen der Index wird als Id für den User genutzt

    private String[] userPassword = {"hallo", "geheim", "test"};


    //hier sind die Attribute für die Synchronisation

    //Variablen für den anderen Server
    private int partnerServerPort; //Port des Partnerservers (Port für Serverkommunikation)
    private String partnerServerAdress = "localhost"; //hier die Adresse des anderen Server eintragen.
    private ServerConnectorThread SyncThread;

    //Variablen für den eigenen Server
    private int serverReciverPort;
    private ServerReceiverThread reciverSyncThread;

    private int[] userChattetWith = new int[3]; //Speichert, wer sich aktuell mit wem im Chat befindet (damit man nicht mit einer Person chatten kann, die gerade mit wem anders chattet)
    private ServerUserThread[] userThreadRegister = new ServerUserThread[3];//Speichert die Referenzvariable des Threads auf dem der User (wenn er online ist) läuft. Der Index für das Feld ist, dabei die ID des Users

    private Set<ServerUserThread> userThreads = new HashSet<>(); //hier werden die Referenzvariabeln gespeichert (kann man das überarbeiten?) Vorsicht vor Garbagecollector

    // Konstruktor
    public Server(int port, int partnerServerPort, int serverReceiverPort) {
        System.out.println(ANSI_YELLOW + "Server 1 wird gestartet" + ANSI_RESET);
        this.port = port;
        this.partnerServerPort = partnerServerPort;
        this.serverReciverPort = serverReceiverPort;
    }

    public void execute() {
        reciverSyncThread = new ServerReceiverThread(this, serverReciverPort);
        reciverSyncThread.start();
        System.out.println(ANSI_YELLOW + "Sync ServerThread gestartet" + ANSI_RESET);
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println(ANSI_YELLOW + "Chat Server is listening on port " + port + ANSI_YELLOW);

            file.create();

            SyncThread = new ServerConnectorThread(partnerServerAdress, partnerServerPort, this);
            SyncThread.start();

            // Endlosschleife

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println(ANSI_YELLOW + "New user connected" + ANSI_YELLOW);
                ServerUserThread newUser = new ServerUserThread(socket, this);
                userThreads.add(newUser);
                newUser.start(); //Thread startet mit User → Name unbekannt deswegen noch kein Eintrag in das userThreadRegister Array

            }

        } catch (IOException ex) {
            System.out.println(ANSI_RED + "Error in the server: " + ex.getMessage() + ANSI_RESET);
        }
    }

    public static void main(String[] args) {
        int port = 8989;//Integer.parseInt(args[0]);
        int partnerServerPort = 8991;
        int serverReceiverPort = 8992;
        Server server = new Server(port, partnerServerPort, serverReceiverPort);
        server.execute();
    }

    /**
     * Delivers a message from one user to another
     */
    void sendMessage(String message, int sendUserId, int receiverUserId) {
        try {
            SyncThread.sendMessageToOtherServer(message, sendUserId, receiverUserId);
        } catch (Exception e) {
            System.out.println(ANSI_RED + "Sync Server nicht gefunden" + ANSI_RESET);
        }

        // Timestamp prüfen(?)
        file.write(message, sendUserId, receiverUserId);

        if (userThreadRegister[receiverUserId] != null) { //es wird geschaut, ob der User online ist (zum Vermeiden von Exception)
            System.out.println(ANSI_YELLOW + "Diese Nachricht wurde erhalten: " + message + ANSI_RESET);
            if (userChattetWith[receiverUserId] == sendUserId) { //Es wird geschaut, ob der User sich im gleichen Chatraum (mit dem sendenUser) befindet
                userThreadRegister[receiverUserId].sendMessage(message); //nachricht wird an den User gesendet
            } else {
                System.out.println(ANSI_YELLOW + "Der User ist gerade beschäftigt. Die Nachricht: " + ANSI_CYAN + message + ANSI_YELLOW + " wird gespeichert" + ANSI_RESET);
            }
        } else {
            System.out.println(ANSI_YELLOW + "Der User ist nicht online, die Nachricht: " + ANSI_CYAN + message + ANSI_YELLOW + " wird aber für ihn gespeichert..." + ANSI_RESET);
        }
    }

    void sendMessageFromServer(String message, int sendUserId, int receiverUserId) {

        // Timestamp prüfen(?)
        file.write(message, sendUserId, receiverUserId);

        if (userThreadRegister[receiverUserId] != null) { //es wird geschaut, ob der User online ist (zum Vermeiden von Exception)
            System.out.println(ANSI_YELLOW + "Diese Nachricht wurde erhalten: " + ANSI_CYAN + message + ANSI_RESET);
            if (userChattetWith[receiverUserId] == sendUserId) { //Es wird geschaut, ob der User sich im gleichen Chatraum (mit dem sendenUser) befindet
                userThreadRegister[receiverUserId].sendMessage(message); //nachricht wird an den User gesendet
            } else {
                System.out.println(ANSI_YELLOW + "Der User ist gerade beschäftigt. Die Nachricht: " + ANSI_CYAN + message + ANSI_YELLOW + " wird gespeichert!");
            }
        } else {
            System.out.println(ANSI_YELLOW + "Der User ist nicht online, die Nachricht: " + ANSI_CYAN + message + ANSI_YELLOW + " wird aber für ihn gespeichert...");
        }
    }

    void sendMessage(String message, int ownID) {       // receiverUser war vorher excludeUser
        userThreadRegister[ownID].sendMessage(message); //nachricht wird an den User gesendet
    }

    boolean checkUsernameExists(String userName) { //überprüft, ob der User existiert
        boolean usernameValid = false;
        for (int i = 0; i < userNameRegister.length; i++) {
            if (userNameRegister[i].equals(userName)) {
                usernameValid = true;
                break;
            }
        }
        return usernameValid;
    }

    boolean checkPasswordValid(String userName, String password) { //Überprüft, ob das Password das richtige ist
        boolean passwordValid = false;
        for (int i = 0; i < userNameRegister.length; i++) {
            if (userNameRegister[i].equals(userName)) {
                if (userPassword[i].equals(password)) {
                    passwordValid = true;
                }
                break;
            }
        }
        return passwordValid;
    }

    void setThreadId(String userName, ServerUserThread Thread) { //nachdem der User sich registriert hat, wird Referenz von Thread an den Platz vom User gespeichert → ab jetzt ist Thread erreichbar
        for (int i = 0; i < userNameRegister.length; i++) {
            if (userNameRegister[i].equals(userName)) {
                userThreadRegister[i] = Thread;
                break;
            }
        }
    }

    /**
     * When a client is disconnected, removes the UserThread
     */
    void removeUser(String userName, ServerUserThread aUser) { //noch von Tutorial
        userThreads.remove(aUser);
        System.out.println(ANSI_YELLOW + "The user " + userName + " quit." + ANSI_RESET);
    }

    int askForID(String username) { //Es wird geschaut, welche Id der User hat (Index von userNameRegister)
        int answer = -1;
        for (int i = 0; i < userNameRegister.length; i++) {
            if (username.equals(userNameRegister[i])) {
                answer = i;
                break;
            }
        }
        return answer;
    }

    void setChatPartner(int user, int chatPartner) { //der ChatPartner bzw. der Chatraum wird für den User gesetzt (ab jetzt kann er Nachrichten empfangen, aber nur von dem Partner)
        userChattetWith[user] = chatPartner;

    }

}