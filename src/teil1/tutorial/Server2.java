package teil1.tutorial;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class Server2 {

    /**
     * This is the chat server program.
     * Press Ctrl + C to terminate the program.
     *
     * @author www.codejava.net
     */

        private int port;
        private File file = new File();

        private String[] userNameRegister = {"Daniel","David","Lorena"}; //Speichert die Usernamen der Index wird als Id für den User genutzt

        private String[] userPassword = {"hallo", "geheim", "test"};

        private int[] userChattetWith = new int[3]; //Speichert, wer sich aktuell mit wem im Chat befindet (damit man nicht mit einer Person chatten kann, die gerade mit wem anders chattet)
        private ServerUserThread2[] userThreadRegister = new ServerUserThread2[3];//Speichert die Referenzvariable des Threads auf dem der User (wenn er online ist) läuft. Der Index für das Feld ist, dabei die ID des Users

        private Set<ServerUserThread2> userThreads = new HashSet<>(); //hier werden die Referenzvariabeln gespeichert (kann man das überarbeiten?) Vorsicht vor Garbagecollecktor

        // Konstruktor

        public Server2(int port) {
            this.port = port;
        }


        public void execute() {
            try (ServerSocket serverSocket = new ServerSocket(port)) {

                System.out.println("Chat Server is listening on port " + port);

                file.create();

                // Endlosschleife

                while (true) {
                    Socket socket = serverSocket.accept();
                    System.out.println("New user connected");
                    ServerUserThread2 newUser = new ServerUserThread2(socket, this);
                    userThreads.add(newUser);
                    newUser.start(); //Thread startet mit User -> Name unbekannt desswegen noch kein Eintrag in das userThreadRegister Array

                }

            } catch (IOException ex) {
                System.out.println("Error in the server: " + ex.getMessage());
                ex.printStackTrace();
            }
        }

        public static void main(String[] args) {
            int port = 8990;//Server 2 läuft immer auf Port 8990 Server 1 auf 8989

            teil1.tutorial.Server server = new teil1.tutorial.Server(port);
            server.execute();
        }

        /**
         * Delivers a message from one user to others (broadcasting)
         */
        void sendMessage(String message, int sendUserId, int receiverUserId) {       // receiverUser war vorher excludeUser

            // todo: Methodenaufruf von WriteInFile

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
            //    }
            // }
        }

        void sendMessage(String message,int ownID) {       // receiverUser war vorher excludeUser
            // todo: Methodenaufruf von WriteInFile
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

        void setThreadId(String userName, ServerUserThread2 Thread) { //nachdem der User sich regestriert hat, wird Referenz von Thread an den Platz vom User gespeichert -> ab jetzt ist Thread erreichbar
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
        void removeUser(String userName, ServerUserThread2 aUser) { //noch von Tutorial
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
