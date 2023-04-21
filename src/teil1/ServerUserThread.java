package teil1;

import java.io.*;
import java.net.*;
import java.sql.Timestamp;

public class ServerUserThread extends Thread {

    private Socket socket;
    private Server server;
    private final String DISCONNECT = "DISCONNECT";
    private final String SHUTDOWN = "SHUTDOWN";
    private PrintWriter writer;

    private int chatPartnerID;

    private int ownID; //Die Id des Users, der auf dem Thread läuft
    private FileHandler fileHandler;

    // Konstruktor

    public ServerUserThread(Socket socket, Server server, int serverNummer, FileHandler fileHandler) {
        this.socket = socket;
        this.server = server;
        this.fileHandler = fileHandler;
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
                writer.println(Server.ANSI_PURPLE + "Please enter your name:" + Server.ANSI_RESET);
                userName = getText(reader.readLine());
                if (server.checkUsernameExists(userName)) {
                    writer.println(Server.ANSI_PURPLE + "Please insert Password:" + Server.ANSI_RESET);
                    password = getText(reader.readLine());
                    if (server.checkPasswordValid(userName, password)) {
                        if (server.getUserIsOnServer(server.askForID(userName)) != 0) { // User war noch nicht online
                            writer.println(Server.ANSI_RED + "Der User ist schon angemeldet. Melden Sie sich bitte mit der anderen Verbindung ab." + Server.ANSI_RESET);
                        } else {
                            userSuccessfullyAuthenticated = true;
                        }
                    } else {
                        writer.println(Server.ANSI_RED + "Passwort ist falsch! Bitte versuch es erneut" + Server.ANSI_RESET);
                    }
                } else {
                    writer.println(Server.ANSI_RED + "Benutzername ist nicht registriert! Bitte versuch es erneut" + Server.ANSI_RESET);
                }
            } while (!userSuccessfullyAuthenticated);

            server.setThreadId(userName, this); //die Referenzvariable des Threads wird mit dem User verknüpft
            ownID = server.askForID(userName); // eigene ID wird gespeichert
            server.setUserLoggedIn(ownID);

            String serverMessage = Server.ANSI_PURPLE + "New user connected: " + userName + Server.ANSI_RESET;

            boolean foundPartner = false;
            while (!foundPartner) { //Endlosschleife, bis existierender Chatpartner gefunden
                writer.println(Server.ANSI_PURPLE + "Mit wem möchtest du schreiben?" + Server.ANSI_RESET);
                chatPartnerID = server.askForID(getText(reader.readLine()));
                if (ownID == chatPartnerID) {
                    writer.println(Server.ANSI_RED + "Du kannst nicht mit dir selbst schreiben. Bitte gebe einen anderen Chatpartner an." + Server.ANSI_RESET);
                } else if (chatPartnerID != -1) { //geprüft ob ChatPartnerId gültig ist
                    writer.println(Server.ANSI_PURPLE + "Alles klar, du wirst verbunden" + Server.ANSI_RESET);
                    server.setChatPartner(ownID, chatPartnerID); //User geht in Chatraum
                    foundPartner = true;
                } else {
                    writer.println(Server.ANSI_RED + "Der User ist nicht registriert. Bitte versuch es nochmal" + Server.ANSI_RESET);
                }
            }// ab hier weiß der User die ID seines Chatpartners

            // todo: "kleiner Sonderfall"
            // server.sendMessageToServer(new MessageClient(ownID, chatPartnerID, new Timestamp(System.currentTimeMillis()), 0, serverMessage));       //Nachricht an den Partner

            String clientMessage;
            //todo: hier ist die Ausgabe der File -> muss aktualisiert werden
            server.sendMessage(fileHandler.readWholeChatFile(ownID, chatPartnerID), ownID); // bisheriger Chat wird an den Client übergeben

            // Endlosschleife

            do {
                serverMessage = reader.readLine();
                if (serverMessage != null) {
                    server.sendMessageToServer(new MessageClient(serverMessage, ownID, chatPartnerID));
                } else {
                    System.out.println(Server.ANSI_RED + "Servermessage ist null" + Server.ANSI_RESET);
                }

            } while (!getText(serverMessage).equals(DISCONNECT) && !getText(serverMessage).equals((SHUTDOWN)));

            serverMessage = Server.ANSI_PURPLE + "Client: " + userName + " hat die Verbindung getrennt!" + Server.ANSI_RESET;
            // todo: "kleiner Sonderfall"
            // server.sendMessageToServer(new MessageClient(ownID, chatPartnerID, new Timestamp(System.currentTimeMillis()), 0, serverMessage));
            socket.close();
            server.userConnectionReset(ownID, this);

        } catch (IOException ex) {
            System.out.println(Server.ANSI_RED + "Error in UserThread: " + ex.getMessage() + Server.ANSI_RESET);
            String serverMessage = Server.ANSI_YELLOW + "Client: " + userName + " hat die Verbindung getrennt!" + Server.ANSI_RESET;
            // todo: "kleiner Sonderfall"
            // server.sendMessageToServer(new MessageClient(ownID, chatPartnerID, new Timestamp(System.currentTimeMillis()), 0, serverMessage));
            server.userConnectionReset(ownID, this);
        }
    }

    void sendMessage(String message) {
        writer.println(Server.ANSI_CYAN + message + Server.ANSI_RESET);
    }

    private String getText(String message) {
        String[] messageSplit = message.split(";", 2);
        return messageSplit[messageSplit.length - 1]; //gibt Message von User in jedem Fall zurück
    }

}
