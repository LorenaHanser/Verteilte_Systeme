package teil1;

import java.io.*;
import java.net.*;

/**
 * Klasse ServerUserThread die zuständig sind die einzelnen
 * Clients zu verwalten und eine Verbindung herzustellen.
 * Man kann sie als eine Art "Schnittstelle" zwischen dem eigentlichen Server sehen.
 */
public class ServerUserThread extends Thread {

    private Socket socket;
    private Server server;
    private final String DISCONNECT = "DISCONNECT";
    private final String SHUTDOWN = "SHUTDOWN";
    private PrintWriter writer;
    private int chatPartnerID;

    private int ownID;
    private FileHandler fileHandler;
    private boolean userSuccessfullyAuthenticated;

    /**
     * Konstruktor
     *
     * @param socket      Verbindungsanschluss zwischen Client und Server
     * @param server      Server Klasse damit man auf die Server Methoden zugreifen kann
     * @param fileHandler FileHandler Klasse damit man auf die FileHandler Methoden zugreifen kann
     */
    public ServerUserThread(Socket socket, Server server, FileHandler fileHandler) {
        this.socket = socket;
        this.server = server;
        this.fileHandler = fileHandler;
        this.ownID = -1;
    }

    /**
     * run-Methode des Threads um die Verbindung zu einem beliebigen Client herzustellen.
     * Authentifizierungsprozess wird durchlaufen {link {@link ServerUserThread}} {@link #userSuccessfullyAuthenticated}.
     * Es werden sowohl die Referenzvariablen auf den passenden Thread dem User-Array gleichgesetzt mit {link {@link Server#setThreadId(String, ServerUserThread)}},
     * als auch die passende UserId des neu angemeldeten Clients übermittelt.
     * Die Datei wird mit {@link FileHandler#readWholeChatFile(int, int)} gelesen und dem User zur Verfügung gestellt.
     * Am Ende geht die Methode in eine Endlosschleife, um Eingaben des Clients zu erfassen und weiterzuleiten.
     */
    @Override
    public void run() {

        String userName = "";

        try {

            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            // Authentifizierungsprozess
            userSuccessfullyAuthenticated = false;
            String password;
            do {
                if (server.mcsHandler.isServerBlocked()) {
                    writer.println(Server.ANSI_RED + "Es tut uns sehr leid, aber leider haben wir ein Technisches Problem.\n Sie können sich nicht anmelden! \n Bitte versuchen Sie es später erneut! Sollte dieses Problem weiterhin bestehen, wenden Sie sich bitte an den Support!" + Server.ANSI_RESET);
                    socket.close();
                } else {
                    writer.println(Server.ANSI_PURPLE + "Bitte Benutzername eingeben:" + Server.ANSI_RESET);
                    userName = getText(reader.readLine());
                    if (server.checkUsernameExists(userName)) {
                        writer.println(Server.ANSI_PURPLE + "Bitte Passwort eingeben:" + Server.ANSI_RESET);
                        password = getText(reader.readLine());
                        if (server.checkPasswordValid(userName, password)) {
                            if (server.getUserIsOnServer(server.askForID(userName)) != 0) {
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
                }
            } while (!userSuccessfullyAuthenticated);

            server.setThreadId(userName, this);
            ownID = server.askForID(userName);
            server.setUserLoggedIn(ownID);

            String serverMessage;

            boolean foundPartner = false;

            //Endlosschleife, bis existierender Chatpartner gefunden
            while (!foundPartner) {
                writer.println(Server.ANSI_PURPLE + "Mit wem möchtest du schreiben?" + Server.ANSI_RESET);
                chatPartnerID = server.askForID(getText(reader.readLine()));
                if (ownID == chatPartnerID) {
                    writer.println(Server.ANSI_RED + "Du kannst nicht mit dir selbst schreiben. Bitte gebe einen anderen Chatpartner an!" + Server.ANSI_RESET);
                } else if (chatPartnerID != -1) {
                    writer.println(Server.ANSI_PURPLE + "Alles klar, du wirst verbunden!" + Server.ANSI_RESET);
                    server.setChatPartner(ownID, chatPartnerID);
                    foundPartner = true;
                } else {
                    writer.println(Server.ANSI_RED + "Der User ist nicht registriert. Bitte versuch es nochmal!" + Server.ANSI_RESET);
                }
            }

            String clientMessage;
            try {
                server.sendMessage(fileHandler.readWholeChatFile(ownID, chatPartnerID), ownID); // bisheriger Chat wird an den Client übergeben
            } catch (Exception e) {
                writer.println(Server.ANSI_RED + "Es tut uns sehr leid, aber leider haben wir ein Technisches Problem.\n Sie werden jetzt abgemeldet! \n Bitte versuchen Sie es später erneut! Sollte dieses Problem weiterhin bestehen, wenden Sie sich bitte an den Support!" + Server.ANSI_RESET);
                socket.close();
            }
            // Endlosschleife
            do {
                serverMessage = reader.readLine();
                if (server.mcsHandler.isServerBlocked()) {
                    writer.println(Server.ANSI_RED + "Es tut uns sehr leid, aber leider haben wir ein Technisches Problem.\n Die Nachricht kann nicht zugestellt werden \n Bitte versuchen Sie es später erneut! Sollte dieses Problem weiterhin bestehen, wenden Sie sich bitte an den Support!" + Server.ANSI_RESET);
                } else {
                    if (serverMessage != null) {
                        server.sendMessageToServer(new MessageClient(serverMessage, ownID, chatPartnerID));
                    } else {
                        System.out.println(Server.ANSI_RED + "ServerMessage ist null" + Server.ANSI_RESET);
                    }
                }

            } while (!getText(serverMessage).equals(DISCONNECT) && !getText(serverMessage).equals((SHUTDOWN)));

            socket.close();
            server.userConnectionReset(ownID, this);

        } catch (IOException ex) {
            System.out.println(Server.ANSI_RED + "Error in UserThread: " + ex.getMessage() + Server.ANSI_RESET);
            server.userConnectionReset(ownID, this);
        }
    }

    /**
     * @param message Die Nachricht wird an den Client übermittelt
     */
    public void sendMessage(String message) {
        writer.println(Server.ANSI_CYAN + message + Server.ANSI_RESET);
    }

    private String getText(String message) {
        String[] messageSplit = message.split(";", 2);
        return messageSplit[messageSplit.length - 1];
    }
}
