package teil1;

import java.io.*;
import java.net.*;

/**
 * This thread handles connection for each connected client, so the server
 * can handle multiple clients at the same time.
 *
 * @author www.codejava.net
 */
public class ServerUserThread extends Thread {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    private Socket socket;
    private Server server;
    private final String DISCONNECT = "DISCONNECT";
    private final String SHUTDOWN = "SHUTDOWN";
    private PrintWriter writer;

    private int chatPartnerID;

    private int ownID; //Die Id des Users, der auf dem Thread läuft
    private MyFile myFile;

    // Konstruktor

    public ServerUserThread(Socket socket, Server server, String serverNummer) {
        this.socket = socket;
        this.server = server;
        this.myFile = new MyFile(serverNummer);
    }

    public void run() {

        String userName = "";

        try {

            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            writer.println(ANSI_PURPLE + "Please enter your name:" + ANSI_RESET);
            boolean userSuccessfullAuthenticated = false;
            userName = reader.readLine();
            String password;
            if (server.checkUsernameExists(userName)) {
                writer.println(ANSI_PURPLE + "Please insert Password:" + ANSI_RESET);
                password = reader.readLine();
                if (server.checkPasswordValid(userName, password)) {
                    userSuccessfullAuthenticated = true;
                }
            }
            while (!userSuccessfullAuthenticated) {
                writer.println(ANSI_RED + "Password oder User falsch! Bitte versuch es nochmal" + ANSI_RESET);
                writer.println(ANSI_PURPLE + "Please enter your name:" + ANSI_RESET);
                userName = reader.readLine();
                if (server.checkUsernameExists(userName)) {
                    writer.println(ANSI_PURPLE + "Please insert Password:" + ANSI_RESET);
                    password = reader.readLine();
                    if (server.checkPasswordValid(userName, password)) {
                        userSuccessfullAuthenticated = true;
                    }
                }
            }
            server.setThreadId(userName, this); //die Referenzvariable des Threads wird mit dem User verknüpft
            ownID = server.askForID(userName); // eigene ID wird gespeichert

            String serverMessage = ANSI_PURPLE + "New user connected: " + userName + ANSI_RESET;

            boolean foundPartner = false;
            while (!foundPartner) { //Endlosschleife, bis existierender Chatpartner gefunden
                writer.println(ANSI_PURPLE + "Mit wem möchtest du schreiben?" + ANSI_RESET);
                chatPartnerID = server.askForID(reader.readLine());
                if (chatPartnerID != -1) { //geprüft ob ChatPartnerId gültig ist
                    writer.println(ANSI_PURPLE + "Alles klar, du wirst verbunden" + ANSI_RESET);
                    server.setChatPartner(ownID, chatPartnerID); //User geht in Chatraum
                    foundPartner = true;
                }
            }// ab hier weiß der User die ID seines Chatpartners

            server.sendMessage(serverMessage, ownID, chatPartnerID);        //Nachricht an den Partner

            String clientMessage;
            server.sendMessage(myFile.readWholeChatFile(ownID, chatPartnerID), ownID); // bisheriger Chat wird an den Client übergeben

            // Endlosschleife

            do {
                clientMessage = reader.readLine();
                serverMessage = "[" + userName + "]: " + clientMessage;
                server.sendMessage(serverMessage, ownID, chatPartnerID);

            } while (!clientMessage.equals(DISCONNECT) && !clientMessage.equals((SHUTDOWN)));

            server.removeUser(userName, this);
            serverMessage = ANSI_PURPLE + "Client: " + userName + " hat die Verbindung getrennt!" + ANSI_RESET;
            server.sendMessage(serverMessage, ownID, chatPartnerID);
            socket.close();

        } catch (IOException ex) {
            System.out.println(ANSI_RED + "Error in UserThread: " + ex.getMessage() + ANSI_RESET);
            String serverMessage = ANSI_YELLOW + "Client: " + userName + " hat die Verbindung getrennt!" + ANSI_RESET;
            server.sendMessage(serverMessage, ownID, chatPartnerID);
        }
    }

    /**
     * Sends a message to the client.
     */
    void sendMessage(String message) {
        writer.println(ANSI_CYAN + message + ANSI_RESET);
    }

}
