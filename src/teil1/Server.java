package teil1;

import java.io.*;
import java.net.*;
import java.rmi.server.ServerCloneException;
import java.util.*;


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

    //Für das Protokoll
    //public static final int USER_ACTIVITY = 0;
    //public static final int MESSAGE = 1;
    public static final int LOGGED_OUT = 0;
    public static final int LOGGED_IN = 1;

    public static final int NEW_MESSAGE = 0;
    public static final int NEW_MESSAGE_WITHOUT_TIMESTAMP = 1;
    public static final int SYNC_REQUEST = 2;
    public static final int SYNC_RESPONSE = 3;

    public static final String OK = "OK";

    private int port;
    private FileHandler fileHandler;

    private int serverNumber;

    public static final String[] USER_NAME_REGISTER = {"Daniel", "David", "Lorena"}; //Speichert die Usernamen der Index wird als Id für den User genutzt

    private final String[] USER_PASSWORD = {"hallo", "geheim", "test"};


    //hier sind die Attribute für die Synchronisation

    //Variablen für den anderen Server
    private int partner1ServerPort; //Port des Partnerservers (Port für Serverkommunikation)
    private int partner2ServerPort;
    private String partnerServerAdress = "localhost"; //hier die Adresse des anderen Server eintragen.
    private ServerConnectorThread syncThread1;
    private ServerConnectorThread syncThread2;
    private ServerConnectorThread syncThreadArray[] = new ServerConnectorThread[2];

    protected MCSHandler mcsHandler;

    //Variablen für den eigenen Server
    private int serverReceiverPort;
    private ServerReceiverMainThread receiverSyncThread;

    private boolean syncThreadActive = false;
    private int[] userIsOnServer = new int[3]; //evtl muss auf protected oder public
    private int[] userChattetWith = new int[3]; //Speichert, wer sich aktuell mit wem im Chat befindet (damit man nicht mit einer Person chatten kann, die gerade mit wem anders chattet)
    private ServerUserThread[] userThreadRegister = new ServerUserThread[3];//Speichert die Referenzvariable des Threads auf dem der User (wenn er online ist) läuft. Der Index für das Feld ist, dabei die ID des Users

    private Set<ServerUserThread> userThreads = new HashSet<>(); //hier werden die Referenzvariablen gespeichert (kann man das überarbeiten?) Vorsicht vor Garbage Collector

    // Konstruktor
    public Server(int port, int partner1ServerPort, int partner2ServerPort, int serverReceiverPort, int serverNumber) {
        System.out.println(ANSI_YELLOW + "Server 1 wird gestartet" + ANSI_RESET);
        this.serverNumber = serverNumber;
        this.port = port;
        this.partner1ServerPort = partner1ServerPort;
        this.partner2ServerPort = partner2ServerPort;
        this.serverReceiverPort = serverReceiverPort;
        this.mcsHandler = new MCSHandler();
    }

    public void execute() {
        System.out.println(ANSI_YELLOW + "Server wird gebootet" + ANSI_RESET);
        fileHandler = new FileHandler(this, serverNumber);
        System.out.println(ANSI_YELLOW + "Sync ServerThread gestartet" + ANSI_RESET);
        try{
            this.getUserStatusFromOtherServer1(partnerServerAdress, partner1ServerPort);
            syncThread2 = new ServerConnectorThread(partnerServerAdress, partner2ServerPort, this,mcsHandler,2);
            syncThread2.start();
        } catch (Exception e) {
            try {
                System.out.println(ANSI_RED+"Leider konnte Version 1 nicht verbunden werden"+ANSI_RESET);
                this.getUserStatusFromOtherServer2(partnerServerAdress, partner2ServerPort);
                syncThread1 = new ServerConnectorThread(partnerServerAdress, partner2ServerPort, this,mcsHandler,2);
                syncThread1.start();
            } catch (Exception ex) {
                System.out.println(ANSI_RED+"Leider konnte Version 2 nicht verbunden werden. starte normal"+ANSI_RESET);
                syncThread1 = new ServerConnectorThread(partnerServerAdress, partner1ServerPort, this,mcsHandler,1); // hier noch 2. Port anmelden
                syncThread2 = new ServerConnectorThread(partnerServerAdress, partner2ServerPort, this,mcsHandler,2); // hier noch 2. Port anmelden
                syncThread1.start();
                syncThread2.start();
            }
        }


        receiverSyncThread = new ServerReceiverMainThread(this, serverReceiverPort);
        receiverSyncThread.start();
        System.out.println(ANSI_YELLOW + "Chat Server ist hochgefahren und bereit für Clienten " + port + ANSI_YELLOW);
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println(ANSI_YELLOW + "Chat Server is listening on port " + port + ANSI_YELLOW);

            fileHandler.create();

            // Endlosschleife

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println(ANSI_YELLOW + "New user connected" + ANSI_YELLOW);
                ServerUserThread newUser = new ServerUserThread(socket, this, serverNumber, fileHandler);
                userThreads.add(newUser);
                newUser.start(); //Thread startet mit User → Name unbekannt deswegen noch kein Eintrag in das userThreadRegister Array

            }

        } catch (IOException ex) {
            System.out.println(ANSI_RED + "Error in the server: " + ex.getMessage() + ANSI_RESET);
        }
    }

    public static void main(String[] args) {
        int port = 8988;//Server 2 läuft immer auf Port 8990 Server 1 auf 8989 Server 3 auf 8991
        int partner1ServerPort = 8992;
        int partner2ServerPort = 8993;
        int serverReceiverPort = 8991;
        int serverNummer = 1;
        Server server = new Server(port, partner1ServerPort, partner2ServerPort, serverReceiverPort, serverNummer);
        server.execute();
    }

    void sendMessageToServer(ClientMessage clientMessage) {
        if(!mcsHandler.isServerBlocked())
        {

        try {
            syncThread1.sendMessageToOtherServer(clientMessage);
            syncThread2.sendMessageToOtherServer(clientMessage);
        } catch (Exception e) {
            System.out.println(ANSI_RED + "Sync Server nicht gefunden" + ANSI_RESET);
        }
        sendMessage(clientMessage);

        }else {
            System.out.println(ANSI_RED+"Server ist Blockiert Nachricht kann nicht zugestellt werden!!!"+ANSI_RESET);
        }
    }

    void sendMessage(ClientMessage clientMessage) {

        fileHandler.writeOneNewMessage(clientMessage);
        // todo nur Nachrichten Typ 1 und 2 Sollen verarbeitet werden (stand 17.04.)
        if (userThreadRegister[clientMessage.getReceiverId()] != null) { //es wird geschaut, ob der User online ist (zum Vermeiden von Exception)
            System.out.println(ANSI_YELLOW + "Diese Nachricht wurde erhalten: " + ANSI_CYAN + clientMessage.toString() + ANSI_RESET);
            if (userChattetWith[clientMessage.getReceiverId()] == clientMessage.getUserId()) { //Es wird geschaut, ob der User sich im gleichen Chatraum (mit dem sendenUser) befindet
                userThreadRegister[clientMessage.getReceiverId()].sendMessage(clientMessage.getMessage()); //nachricht wird an den User gesendet
            } else {
                System.out.println(ANSI_YELLOW + "Der User ist gerade beschäftigt. Die Nachricht: " + ANSI_CYAN + clientMessage.getContent() + ANSI_YELLOW + " wird gespeichert!");
            }
        } else if (userIsOnServer[clientMessage.getReceiverId()] < 1) {
            System.out.println(ANSI_YELLOW + "Der User ist nicht online, die Nachricht: " + ANSI_CYAN + clientMessage.getContent() + ANSI_YELLOW + " wird aber für ihn gespeichert...");
        }
    }

    void sendMessage(String message, int ownID) {       // receiverUser war vorher excludeUser
        userThreadRegister[ownID].sendMessage(message); //nachricht wird an den User gesendet
    }

    ClientMessage receiveSynchronization(ClientMessage receivedClientMessage){
        //System.out.println("Bin jetzt in Server bei receiveSynchronization " + receivedClientMessage);
        return fileHandler.synchronize(receivedClientMessage);
    }

    ClientMessage requestSynchronization(ClientMessage sendClientMessage){
        ClientMessage message = syncThread1.requestSynchronization(sendClientMessage);
        System.out.println("============================= Antwort ist da ======================");
        System.out.println(message.toString());
       return message;
    }

    boolean checkUsernameExists(String userName) { //überprüft, ob der User existiert
        boolean usernameValid = false;
        for (int i = 0; i < USER_NAME_REGISTER.length; i++) {
            if (USER_NAME_REGISTER[i].equals(userName)) {
                usernameValid = true;
                break;
            }
        }
        return usernameValid;
    }

    boolean checkPasswordValid(String userName, String password) { //Überprüft, ob das Password das richtige ist
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

    void setThreadId(String userName, ServerUserThread Thread) { //nachdem der User sich registriert hat, wird Referenz von Thread an den Platz vom User gespeichert → ab jetzt ist Thread erreichbar
        for (int i = 0; i < USER_NAME_REGISTER.length; i++) {
            if (USER_NAME_REGISTER[i].equals(userName)) {
                userThreadRegister[i] = Thread;
                userIsOnServer[i] = serverNumber;
                break;
            }
        }
    }

    int askForID(String username) { //Es wird geschaut, welche Id der User hat (Index von userNameRegister)
        int answer = -1;
        for (int i = 0; i < USER_NAME_REGISTER.length; i++) {
            if (username.equals(USER_NAME_REGISTER[i])) {
                answer = i;
                break;
            }
        }
        return answer;
    }

    void setChatPartner(int user, int chatPartner) { //der ChatPartner bzw. der Chatraum wird für den User gesetzt (ab jetzt kann er Nachrichten empfangen, aber nur von dem Partner)
        userChattetWith[user] = chatPartner;

    }
    void removeChatPartner(int user) { //der ChatPartner bzw. der Chatraum wird für den User gesetzt (ab jetzt kann er Nachrichten empfangen, aber nur von dem Partner)
        userChattetWith[user] = -1;

    }
    // When a client is disconnected, removes the UserThread
    void removeUser(String userName, ServerUserThread aUser) { //noch von Tutorial
        userThreads.remove(aUser);
        System.out.println(ANSI_YELLOW + "The user " + userName + " quit." + ANSI_RESET);
    }
    void removeUser( ServerUserThread aUser) { //noch von Tutorial
        userThreads.remove(aUser);
    }
    void removeUserThread(int userID, ServerUserThread serverUserThread){
        removeUser(serverUserThread);
        userThreadRegister[userID] = null;

    }

    int getServerNumber() {
        return serverNumber;
    }

    void setUserLoggedIn(int userID) {
        ServerMessage newActivity = new ServerMessage(userID, getServerNumber(), LOGGED_IN);
        syncThread1.sendUserActivity(newActivity);
        syncThread2.sendUserActivity(newActivity);
        changeUserActivity(newActivity);
    }

    void setUserLoggedOut(int userID) {
        ServerMessage newActivity = new ServerMessage(userID, getServerNumber(), LOGGED_OUT);
        syncThread1.sendUserActivity(newActivity);
        syncThread2.sendUserActivity(newActivity);
        changeUserActivity(newActivity);
    }

    void changeUserActivity(ServerMessage serverMessage) {
        if (serverMessage.getStatus() == LOGGED_IN) {
            userIsOnServer[serverMessage.getUserId()] = serverMessage.getServerId();
        } else if (serverMessage.getStatus() == LOGGED_OUT) {
            userIsOnServer[serverMessage.getUserId()] = 0;
        }
    }

    public int getUserIsOnServer(int index) {
        return userIsOnServer[index];
    }

    void userConnectionReset(int userID, ServerUserThread serverUserThread){
        setUserLoggedOut(userID);
        removeChatPartner(userID);
        removeUserThread(userID, serverUserThread);
        System.out.println(ANSI_YELLOW + "User wurde Erfolgreich abgemeldet!" + ANSI_RESET);
    }

    void getUserStatusFromOtherServer1(String hostname, int port) throws IOException {
        Socket socket = new Socket(hostname, port);
        OutputStream output = socket.getOutputStream();
        PrintWriter writer = new PrintWriter(output, true);
        InputStream input = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        System.out.println(ANSI_YELLOW + "Sync Server verbunden" + ANSI_RESET);
        ServerMessage syncUserDataRequest = new ServerMessage(7,1,0);
        writer.println(syncUserDataRequest.toString());
        String response = reader.readLine();
        System.out.println("Die Antwort gab es:"+ response);
        handleUserStatusSync(response);
        syncThread1 = new ServerConnectorThread(socket, writer, reader,this, mcsHandler, 1);
        syncThread1.start();
        //jetzt muss der Connector Thread, mit einem anderen Konstrucktor gebaut werden
    }
    void getUserStatusFromOtherServer2(String hostname, int port) throws IOException {
        Socket socket = new Socket(hostname, port);
        OutputStream output = socket.getOutputStream();
        PrintWriter writer = new PrintWriter(output, true);
        InputStream input = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        System.out.println(ANSI_YELLOW + "Sync Server verbunden" + ANSI_RESET);
        ServerMessage syncUserDataRequest = new ServerMessage(7,1,0);
        writer.println(syncUserDataRequest.toString());
        String response = reader.readLine();
        System.out.println("Die Antwort gab es:"+ response);
        handleUserStatusSync(response);
        syncThread2 = new ServerConnectorThread(socket, writer, reader,this, mcsHandler, 1);
        syncThread2.start();
        //jetzt muss der Connector Thread, mit einem anderen Konstrucktor gebaut werden
    }
    void handleUserStatusSync(String response){
        ServerMessage reponseAsObject = ServerMessage.toObject(response);
        // hier sollen dann der String dann ausgwertet werden!!!
        System.out.println("===========================Antwort erhalten==================");
        System.out.println(reponseAsObject.toString());
        int[] responseArray = reponseAsObject.getUserIsOnServer();
        for (int i = 0; i < 3; i++) {
            userIsOnServer[i] = responseArray[2*i+1];
        }
        //System.out.println("Das ist das Ergebnis: "+ userIsOnServer);
    }
    public ServerMessage getUserIsOnServerArrayAsServerMessage(){
        ServerMessage answer = new ServerMessage(7,1,userIsOnServer);
        return answer;
    }



}