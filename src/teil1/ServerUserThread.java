package teil1;

import java.io.*;
import java.net.*;
import java.sql.Timestamp;

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
    private FileHandler fileHandler;

    // Konstruktor

    public ServerUserThread(Socket socket, Server server, int serverNummer) {
        this.socket = socket;
        this.server = server;
        this.fileHandler = new FileHandler(serverNummer);
    }

    public void run() {

        String userName = "";

        try {

            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            // Authentifizierungsprozess
            boolean userSuccessfullyAuthenticated = false;
            String password;
            do {
                writer.println(ANSI_PURPLE + "Please enter your name:" + ANSI_RESET);
                userName = getText(reader.readLine());
                if (server.checkUsernameExists(userName)) {
                    writer.println(ANSI_PURPLE + "Please insert Password:" + ANSI_RESET);
                    password = getText(reader.readLine());
                    if (server.checkPasswordValid(userName, password)) {
                        if (server.getUserIsOnServer(server.askForID(userName)) != 0) { // User war noch nicht online
                            writer.println(ANSI_RED + "Der User ist schon angemeldet. Melden Sie sich bitte mit der anderen Verbindung ab." + ANSI_RESET);
                        } else {
                            userSuccessfullyAuthenticated = true;
                        }
                    } else {
                        writer.println(ANSI_RED + "Passwort ist falsch! Bitte versuch es erneut" + ANSI_RESET);
                    }
                } else {
                    writer.println(ANSI_RED + "Benutzername ist nicht registriert! Bitte versuch es erneut" + ANSI_RESET);
                }
            } while (!userSuccessfullyAuthenticated);

            server.setThreadId(userName, this); //die Referenzvariable des Threads wird mit dem User verknüpft
            ownID = server.askForID(userName); // eigene ID wird gespeichert
            server.setUserLoggedIn(ownID);

            String serverMessage = ANSI_PURPLE + "New user connected: " + userName + ANSI_RESET;

            boolean foundPartner = false;
            while (!foundPartner) { //Endlosschleife, bis existierender Chatpartner gefunden
                writer.println(ANSI_PURPLE + "Mit wem möchtest du schreiben?" + ANSI_RESET);
                chatPartnerID = server.askForID(getText(reader.readLine()));
                if (chatPartnerID != -1) { //geprüft ob ChatPartnerId gültig ist
                    writer.println(ANSI_PURPLE + "Alles klar, du wirst verbunden" + ANSI_RESET);
                    server.setChatPartner(ownID, chatPartnerID); //User geht in Chatraum
                    foundPartner = true;
                } else {
                    writer.println(ANSI_RED + "Der User ist nicht registriert. Bitte versuch es nochmal" + ANSI_RESET);
                }
            }// ab hier weiß der User die ID seines Chatpartners

            server.sendMessageToServer(new ClientMessage(ownID, chatPartnerID, new Timestamp(System.currentTimeMillis()), 0, serverMessage));       //Nachricht an den Partner

            String clientMessage;
            //todo: hier ist die Ausgabe der File -> muss aktualisiert werden
            server.sendMessage(fileHandler.readWholeChatFile(ownID, chatPartnerID), ownID); // bisheriger Chat wird an den Client übergeben

            // Endlosschleife

            do {
                serverMessage = reader.readLine();
                if (serverMessage != null) {
                    server.sendMessageToServer(new ClientMessage(serverMessage, ownID, chatPartnerID));
                } else {
                    System.out.println(ANSI_RED + "Servermessage ist null" + ANSI_RESET);
                }

            } while (!getText(serverMessage).equals(DISCONNECT) && !getText(serverMessage).equals((SHUTDOWN)));

            server.removeUser(userName, this);
            serverMessage = ANSI_PURPLE + "Client: " + userName + " hat die Verbindung getrennt!" + ANSI_RESET;
            server.setUserLoggedOut(ownID);
            server.sendMessageToServer(new ClientMessage(ownID, chatPartnerID, new Timestamp(System.currentTimeMillis()), 0, serverMessage));
            socket.close();

        } catch (IOException ex) {
            System.out.println(ANSI_RED + "Error in UserThread: " + ex.getMessage() + ANSI_RESET);
            String serverMessage = ANSI_YELLOW + "Client: " + userName + " hat die Verbindung getrennt!" + ANSI_RESET;
            server.sendMessageToServer(new ClientMessage(ownID, chatPartnerID, new Timestamp(System.currentTimeMillis()), 0, serverMessage));
        }
    }

    void sendMessage(String message) {
        writer.println(ANSI_CYAN + message + ANSI_RESET);
    }

    private String getText(String message) {
        String[] messageSplit = message.split(";", 2);
        return messageSplit[messageSplit.length - 1]; //gibt Message von User in jedem Fall zurück
    }

}
