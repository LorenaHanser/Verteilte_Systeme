package teil1;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Um einen Server zu starten, muss diese Klasse ausgeführt werden.
 * Sie stellt allgemeine Variablen bereit, startet die ServerThreads und beinhaltet zentrale Methoden.
 */
public class Server {

    /**
     * Die Farben für die Konsolenausgabe werden hier einmalig definiert, sodass von allen Klassen aus auf diese Farben zugegriffen werden kann.
     */

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    // public static final int USER_ACTIVITY = 0;
    // public static final int MESSAGE = 1;
    public static final int LOGGED_OUT = 0;
    public static final int LOGGED_IN = 1;

    public static final int NEW_MESSAGE = 0;
    public static final int NEW_MESSAGE_WITHOUT_TIMESTAMP = 1;
    public static final int SYNC_REQUEST = 2;
    public static final int SYNC_RESPONSE = 3;

    public static final String[] OK = {"OK"};

    /**
     * Der Index der Nutzernamen wird als ID des Nutzers verwendet
     */
    public static final String[] USER_NAME_REGISTER = {"Daniel", "David", "Lorena"};


    /**
     * Attribute für Objekte der Klasse Server (auch für Synchronisation)
     */

    private int port;
    private FileHandler fileHandler;

    private int serverNumber;
    protected boolean needUserStateSync;

    private final String[] USER_PASSWORD = {"hallo", "geheim", "test"};


    /**
     * Variablen für den anderen Server
     */

    private int partnerServerPort; //Port des Partnerservers (Port für Serverkommunikation)
    private String partnerServerAddress = "localhost"; //hier die Adresse des anderen Server eintragen.
    private ServerConnectorThread syncThread;

    /**
     * Variablen für den eigenen Server
     */

    private int serverReceiverPort;
    private ServerReceiverThread receiverSyncThread;
    private int[] userIsOnServer = new int[3];
    private boolean syncThreadActive = false;
    private int[] userChattetWith = new int[3]; //Speichert, wer sich aktuell mit wem im Chat befindet (damit man nicht mit einer Person chatten kann, die gerade mit wem anders chattet)
    private ServerUserThread[] userThreadRegister = new ServerUserThread[3];//Speichert die Referenzvariable des Threads auf dem der User (wenn er online ist) läuft. Der Index für das Feld ist, dabei die ID des Users

    private Set<ServerUserThread> userThreads = new HashSet<>(); //hier werden die Referenzvariablen gespeichert (kann man das überarbeiten?) Vorsicht vor Garbage Collector

    /**
     * Konstruktor
     *
     * @param port               Port des Servers
     * @param partnerServerPort  Port für Synchronisierung, gehört zum anderen Server
     * @param serverReceiverPort Port für Synchronisation, gehört zu eigenem Server
     * @param serverNumber       Nummer des Servers
     */
    public Server(int port, int partnerServerPort, int serverReceiverPort, int serverNumber) {
        System.out.println(ANSI_YELLOW + "Server 1 wird gestartet" + ANSI_RESET);
        this.serverNumber = serverNumber;
        this.port = port;
        this.partnerServerPort = partnerServerPort;
        this.serverReceiverPort = serverReceiverPort;
        this.needUserStateSync = true;
    }

    /**
     * Methode zum Hochfahren des Servers: Starten der Threads {@link ServerConnectorThread} und {@link ServerReceiverThread}.
     * Wenn sich ein neuer Nutzer verbindet, wird einer neuer {@link ServerUserThread} gestartet.
     */
    public void execute() {
        System.out.println(ANSI_YELLOW + "Server wird gebootet" + ANSI_RESET);
        fileHandler = new FileHandler(this, serverNumber);
        System.out.println(ANSI_YELLOW + "Sync ServerThread gestartet" + ANSI_RESET);
        syncThread = new ServerConnectorThread(partnerServerAddress, partnerServerPort, this);
        syncThread.start();
        try {
            Thread.sleep(1000);
            syncThread.askForUserStatus();
        } catch (Exception e) {
            needUserStateSync = false;
        }
        receiverSyncThread = new ServerReceiverThread(this, serverReceiverPort);
        receiverSyncThread.start();
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println(ANSI_YELLOW + "Server läuft auf Port " + port + ANSI_RESET);

            fileHandler.create();

            System.out.println(ANSI_YELLOW + "Server ist online" + ANSI_RESET);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println(ANSI_YELLOW + "Neuer Nutzer verbunden" + ANSI_RESET);
                needUserStateSync = false;
                ServerUserThread newUser = new ServerUserThread(socket, this, fileHandler);
                userThreads.add(newUser);
                newUser.start();
            }

        } catch (IOException ex) {
            System.out.println(ANSI_RED + "Error in the server: " + ex.getMessage() + ANSI_RESET);
        }
    }

    /**
     * Ein neues Objekt der Klasse {@link Server} wird erstellt und ausgeführt.
     *
     * @param args String[]
     */
    public static void main(String[] args) {
        int port = 8989;
        int partnerServerPort = 8991;
        int serverReceiverPort = 8992;
        int serverNummer = 1;
        Server server = new Server(port, partnerServerPort, serverReceiverPort, serverNummer);
        server.execute();
    }

    /**
     * Die Methode empfängt die mitgegebene {@link MessageClient} und ruft damit die Methode {@link Server#sendMessage(MessageClient)} auf.
     *
     * @param messageClient Nachricht, die an den Server geschickt werden soll
     */
    public void sendMessageToServer(MessageClient messageClient) {
        try {
            syncThread.sendMessageToOtherServer(messageClient);
        } catch (Exception e) {
            System.out.println(ANSI_RED + "Sync Server nicht gefunden" + ANSI_RESET);
        }
        sendMessage(messageClient);
    }

    /**
     * Die Methode nimmt eine {@link MessageClient} entgegen und schickt diese an den entsprechenden User, wenn dieser online ist.
     * In jedem Fall wird die Nachricht in der .txt-Datei gespeichert.
     *
     * @param messageClient Nachricht eines Clients
     */
    public void sendMessage(MessageClient messageClient) {

        fileHandler.writeOneNewMessage(messageClient);
        if (userThreadRegister[messageClient.getReceiverId()] != null) {
            System.out.println(ANSI_YELLOW + "Diese Nachricht wurde erhalten: " + ANSI_CYAN + messageClient.toString() + ANSI_RESET);
            if (userChattetWith[messageClient.getReceiverId()] == messageClient.getUserId()) {
                userThreadRegister[messageClient.getReceiverId()].sendMessage(messageClient.getMessage());
            } else {
                System.out.println(ANSI_YELLOW + "Der User ist gerade beschäftigt. Die Nachricht: " + ANSI_CYAN + messageClient.getContent() + ANSI_YELLOW + " wird gespeichert!");
            }
        } else if (userIsOnServer[messageClient.getReceiverId()] < 1) {
            System.out.println(ANSI_YELLOW + "Der User ist nicht online, die Nachricht: " + ANSI_CYAN + messageClient.getContent() + ANSI_YELLOW + " wird aber für ihn gespeichert...");
        }
    }

    /**
     * Die Methode schickt eine Nachricht an den entsprechenden User.
     *
     * @param message Nachricht
     * @param ownID   eigene ID
     */
    public void sendMessage(String message, int ownID) {
        userThreadRegister[ownID].sendMessage(message);
    }

    /**
     * Die Methode empfängt eine Synchronisationsanfrage eines anderen Servers und gibt die Antwort der Methode {@link FileHandler#synchronize(MessageSync)} zurück.
     *
     * @param receivedSyncMessage Empfangene Synchronisationsanfrage
     * @return Gibt das Ergebnis von {@link FileHandler} zurück
     */
    public MessageSync receiveSynchronization(MessageSync receivedSyncMessage) {
        return fileHandler.synchronize(receivedSyncMessage);
    }

    /**
     * Die Methode wird vom {@link FileHandler} aufgerufen und gibt die Anfrage eines Servers zur Weiterverarbeitung weiter.
     *
     * @param sendMessageSync Synchronisierungsanfrage vom {@link FileHandler}
     * @return Rückgabe der {@link MessageSync} an den {@link FileHandler}
     */
    public MessageSync requestSynchronization(MessageSync sendMessageSync) {
        MessageSync message = syncThread.requestSynchronization(sendMessageSync);
        return message;
    }

    /**
     * Methode überprüft, ob ein Nutzername im Array USER_NAME_REGISTER existiert.
     *
     * @param userName Nutzername
     * @return Gibt mit boolean zurück, ob der Nutzername existiert
     */
    public boolean checkUsernameExists(String userName) {
        boolean usernameValid = false;
        for (int i = 0; i < USER_NAME_REGISTER.length; i++) {
            if (USER_NAME_REGISTER[i].equals(userName)) {
                usernameValid = true;
                break;
            }
        }
        return usernameValid;
    }

    /**
     * Methode überprüft, ob das Passwort zum angegebenen Nutzernamen passt.
     *
     * @param userName Nutzername
     * @param password Passwort
     * @return Gibt mit boolean zurück, ob das Passwort stimmt
     */
    public boolean checkPasswordValid(String userName, String password) {
        boolean passwordValid = false;
        for (int i = 0; i < USER_NAME_REGISTER.length; i++) {
            if (USER_NAME_REGISTER[i].equals(userName)) {
                if (USER_PASSWORD[i].equals(password)) {
                    passwordValid = true;
                }
                break;
            }
        }
        return passwordValid;
    }

    /**
     * Nachdem sich ein Nutzer angemeldet hat, wird Referenz des {@link ServerUserThread} an den Platz vom User gespeichert, sodass ab jetzt der Thread erreichbar ist.
     *
     * @param userName         Nutzername
     * @param serverUserThread passender ServerUserThread
     */
    public void setThreadId(String userName, ServerUserThread serverUserThread) {
        for (int i = 0; i < USER_NAME_REGISTER.length; i++) {
            if (USER_NAME_REGISTER[i].equals(userName)) {
                userThreadRegister[i] = serverUserThread;
                userIsOnServer[i] = serverNumber;
                break;
            }
        }
    }

    /**
     * Die Methode schaut über den Index des userNameRegister, welche Id der User hat.
     *
     * @param username Nutzername
     * @return UserId
     */
    public int askForID(String username) {
        int answer = -1;
        for (int i = 0; i < USER_NAME_REGISTER.length; i++) {
            if (username.equals(USER_NAME_REGISTER[i])) {
                answer = i;
                break;
            }
        }
        return answer;
    }

    /**
     * Die Methode setzt den ChatPartner bzw. der Chatraum für den User (ab jetzt kann er Nachrichten empfangen, aber nur von dem Partner).
     *
     * @param userId
     * @param chatPartnerId
     */
    public void setChatPartner(int userId, int chatPartnerId) {
        userChattetWith[userId] = chatPartnerId;
    }

    /**
     * Die Methode setzt den ChatPartner bzw. den Chatraum des Users zurück
     *
     * @param userId eigene Id
     */
    public void removeChatPartner(int userId) {
        userChattetWith[userId] = -1;
    }

    /**
     * Die Methode setzt den ChatPartner bzw. den Chatraum des Users zurück
     *
     * @param serverUserThread ServerUserThread des Nutzers
     */
    public void removeUser(ServerUserThread serverUserThread) {
        userThreads.remove(serverUserThread);
    }

    /**
     * Die Methode setzt den {@link ServerUserThread} des Nutzers im {@link Server#userThreadRegister} auf null.
     *
     * @param userID           eigene ID
     * @param serverUserThread ServerUserThread des Nutzers
     */
    public void removeUserThread(int userID, ServerUserThread serverUserThread) {
        removeUser(serverUserThread);
        userThreadRegister[userID] = null;
    }

    public int getServerNumber() {
        return serverNumber;
    }

    public void setUserLoggedIn(int userID) {
        MessageUserActivity newActivity = new MessageUserActivity(userID, getServerNumber(), LOGGED_IN);
        syncThread.sendUserActivity(newActivity);
        changeUserActivity(newActivity);
    }

    public void setUserLoggedOut(int userID) {
        MessageUserActivity newActivity = new MessageUserActivity(userID, getServerNumber(), LOGGED_OUT);
        syncThread.sendUserActivity(newActivity);
        changeUserActivity(newActivity);
    }

    /**
     * Die Methode ruft je nach Nutzeraktivität andere Methoden auf.
     *
     * @param messageUserActivity Nutzeraktivität
     */
    public void changeUserActivity(MessageUserActivity messageUserActivity) {
        needUserStateSync = false;
        if (messageUserActivity.getStatus() == LOGGED_IN) {
            userIsOnServer[messageUserActivity.getUserId()] = messageUserActivity.getServerId();
        } else if (messageUserActivity.getStatus() == LOGGED_OUT) {
            userIsOnServer[messageUserActivity.getUserId()] = 0;
        }
    }

    public void setUserOffline() {
        System.out.println(ANSI_YELLOW + "Leider müssen wir alle User des Servers: " + getOtherServerNumber() + " offline nehmen" + ANSI_RESET);
        for (int i = 0; i < userIsOnServer.length; i++) {
            if (userIsOnServer[i] == getOtherServerNumber()) {
                userIsOnServer[i] = 0;
            }
        }
    }

    public int getOtherServerNumber() {
        int otherServerNumber = 1;
        if (getServerNumber() == 1) {
            otherServerNumber = 2;
        }
        return otherServerNumber;
    }

    public int getUserIsOnServer(int index) {
        return userIsOnServer[index];
    }

    /**
     * Die Methode meldet einen Nutzer ab.
     *
     * @param userID           eigene ID
     * @param serverUserThread ServerUserThread des Nutzers
     */
    public void userConnectionReset(int userID, ServerUserThread serverUserThread) {
        if (userID >= 0) {
            setUserLoggedOut(userID);
            removeChatPartner(userID);
            removeUserThread(userID, serverUserThread);
            System.out.println(ANSI_YELLOW + "Nutzer wurde erfolgreich abgemeldet!" + ANSI_RESET);
        }
    }

    public boolean isServerReadyToShareUserData() {
        boolean answer = false;
        if (!needUserStateSync) {
            answer = true;
        }
        return answer;
    }

    /**
     * Die Methode verwaltet die Antwort des Status vom Nutzer zum Sync und gibt diese Antwort aus.
     *
     * @param response Antwort
     */
    public void handleUserStatusSync(String response) {
        if (needUserStateSync) {
            needUserStateSync = false;
            MessageUserActivity responseAsObject = MessageUserActivity.toObject(response);
            int[] responseArray = responseAsObject.getUserIsOnServer();
            for (int i = 0; i < 3; i++) {
                userIsOnServer[i] = responseArray[2 * i + 1];
            }
        }
    }

    /**
     * Die Methode verpackt das int[]-Array {@link Server#userIsOnServer} als Objekt der Klasse {@link MessageUserActivity} und gibt es zurück.
     *
     * @return Array {@link Server#userIsOnServer} als Objekt der Klasse {@link MessageUserActivity}
     */
    public MessageUserActivity getUserIsOnServerArrayAsServerMessage() {
        MessageUserActivity answer = new MessageUserActivity(userIsOnServer);
        return answer;
    }

}