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
    private File file = new File();

    private String[] userNameRegister = {"Daniel","David","Lorena"}; //Speichert die Usernamen der Index wird als Id für den User genutzt

    private String[] userPassword = {"hallo", "geheim", "test"};


    //hier sind die Attribute für die Synccronisation

    //Variablen für den anderen Server
    private int partnerServerPort; //Port des Partnerservers (Port für Servercommunication)
    private String partnerServerAdress = "localhost"; //hier die Adresse des anderen Server eintragen.
    private ServerConnectorThread SyncThread;

    //Variablen für den eigenen Server
    private int serverReciverPort;
    private ServerReciverThread reciverSyncThread;


    private int[] userChattetWith = new int[3]; //Speichert, wer sich aktuell mit wem im Chat befindet (damit man nicht mit einer Person chatten kann, die gerade mit wem anders chattet)
    private ServerUserThread[] userThreadRegister = new ServerUserThread[3];//Speichert die Referenzvariable des Threads auf dem der User (wenn er online ist) läuft. Der Index für das Feld ist, dabei die ID des Users

    private Set<ServerUserThread> userThreads = new HashSet<>(); //hier werden die Referenzvariabeln gespeichert (kann man das überarbeiten?) Vorsicht vor Garbagecollecktor

    // Konstruktor

    public Server(int port, int partnerServerPort, int serverReciverPort) {
        System.out.println("Server 1 wird gestartet");
        this.port = port;
        this.partnerServerPort = partnerServerPort;
        this.serverReciverPort = serverReciverPort;
    }


    public void execute() {
        reciverSyncThread = new ServerReciverThread(this, serverReciverPort);
        reciverSyncThread.start();
        System.out.println("Sync ServerThread gestartet");
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Chat Server is listening on port " + port);

            file.create();

            SyncThread = new ServerConnectorThread(partnerServerAdress,partnerServerPort, this);
            SyncThread.start();

            // Endlosschleife

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New user connected");
                ServerUserThread newUser = new ServerUserThread(socket, this);
                userThreads.add(newUser);
                newUser.start(); //Thread startet mit User -> Name unbekannt desswegen noch kein Eintrag in das userThreadRegister Array

            }

        } catch (IOException ex) {
            System.out.println("Error in the server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int port = 8989;//Integer.parseInt(args[0]);
        int partnerServerPort = 8991;
        int serverReciverPort = 8992;
        Server server = new Server(port, partnerServerPort, serverReciverPort);
        server.execute();
    }

    /**
     * Delivers a message from one user to others (broadcasting)
     */
    void sendMessage(String message, int sendUserId, int receiverUserId) {

        SyncThread.sendMessageToOtherServer(message, sendUserId, receiverUserId);

        file.write(message, sendUserId, receiverUserId);

        if(userThreadRegister[receiverUserId] != null) { //es wird geschaut, ob der User online ist (zum Vermeiden von Exeption)        //todo: fix pls
            System.out.println("Diese Nachricht wurde erhalten: " + message);
            if(userChattetWith[receiverUserId] == sendUserId) { //Es wird geschaut, ob der User sich im gleichen Chatraum (mit dem sendenUser) befindet
                userThreadRegister[receiverUserId].sendMessage(message); //nachricht wird an den User gesendet
            }else{
                System.out.println("Der User ist gerade beschäftigt. Die Nachricht wird gespeichert!");
            }
        }else {
            System.out.println("Der User ist nicht online, die Nachricht wird aber für ihn gespeichert..."); //todo: Lustige Kommentare schreiben
        }
    }

    void sendMessageFromServer(String message, int sendUserId, int receiverUserId) {


        file.write(message, sendUserId, receiverUserId);

        if(userThreadRegister[receiverUserId] != null) { //es wird geschaut, ob der User online ist (zum Vermeiden von Exeption)        //todo: fix pls
            System.out.println("Diese Nachricht wurde erhalten: " + message);
            if(userChattetWith[receiverUserId] == sendUserId) { //Es wird geschaut, ob der User sich im gleichen Chatraum (mit dem sendenUser) befindet
                userThreadRegister[receiverUserId].sendMessage(message); //nachricht wird an den User gesendet
            }else{
                System.out.println("Der User ist gerade beschäftigt. Die Nachricht wird gespeichert!");
            }
        }else {
            System.out.println("Der User ist nicht online, die Nachricht wird aber für ihn gespeichert..."); //todo: Lustige Kommentare schreiben
        }
    }

    void sendMessage(String message,int ownID) {       // receiverUser war vorher excludeUser
        userThreadRegister[ownID].sendMessage(message); //nachricht wird an den User gesendet
    }


    boolean checkUsernameExists(String userName){ //überprüft, ob der User existiert
        boolean usernameValid = false;
        for (int i = 0; i < userNameRegister.length; i++) {
            if(userNameRegister[i].equals(userName)) {
                usernameValid = true;
                break;
            }
        }
        return usernameValid;
    }

    boolean checkPasswordValid(String userName, String password){ //Überprüft, ob das Password das richtige ist
        boolean passwordValid = false;
        for (int i = 0; i < userNameRegister.length; i++) {
            if(userNameRegister[i].equals(userName)) {
                if(userPassword[i].equals(password)) {
                    passwordValid = true;
                }
                break;
            }
        }
        return passwordValid;
    }

    void setThreadId(String userName, ServerUserThread Thread) { //nachdem der User sich regestriert hat, wird Referenz von Thread an den Platz vom User gespeichert -> ab jetzt ist Thread erreichbar
        for (int i = 0; i < userNameRegister.length; i++) {
            if(userNameRegister[i].equals(userName)) {
                userThreadRegister[i] = Thread;
                break;
            }
        }
    }

    /**
     * When a client is disconneted, removes the UserThread
     */
    void removeUser(String userName, ServerUserThread aUser) { //noch von Tutorial
            userThreads.remove(aUser);
            System.out.println("The user " + userName + " quitted");
    }



    int askForID(String username) //Es wird geschaut, welche Id der User hat (Index von userNameRegister)
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

    void setChatPartner(int user, int chatPartner){ //der ChatPartner bzw. der Chatraum wird für den User gesetzt (ab jetzt kann er Nachrichten empfangen, aber nur von dem Partner)
        userChattetWith[user] = chatPartner;

    }



}