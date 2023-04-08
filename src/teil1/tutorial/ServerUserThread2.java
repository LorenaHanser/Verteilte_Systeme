package teil1.tutorial;

import java.io.*;
import java.net.Socket;

public class ServerUserThread2 extends Thread{
    private Socket socket;
    private Server2 server;
    private String userName;
    private PrintWriter writer;
    private int ThreadId;

    private int chatPartnerID;

    private int ownID; //Die Id des Users, der auf dem Thread läuft
    private File file = new File();

    // Konstruktor

    public ServerUserThread2(Socket socket, Server2 server) {
        this.socket = socket;
        this.server = server;
    }



    public void setUser(Socket socket)
    {
        this.socket = socket;
    }

    public void run() {

        try {

            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            writer.println("Please enter your name:");
            boolean userSuccessfullAuthenticated = false;
            String userName = reader.readLine();
            String password;
            if(server.checkUsernameExists(userName))
            {
                writer.println("Please insert Password:");
                password = reader.readLine();
                if(server.checkPasswordValid(userName, password))
                {
                    userSuccessfullAuthenticated = true;
                }
            }
            while(userSuccessfullAuthenticated == false)
            {
                writer.println("Password oder User falsch! Bitte versuch es nochmal");
                writer.println("Please enter your name:");
                userName = reader.readLine();
                if(server.checkUsernameExists(userName))
                {
                    writer.println("Please insert Password:");
                    password = reader.readLine();
                    if(server.checkPasswordValid(userName, password))
                    {
                        userSuccessfullAuthenticated = true;
                    }
                }
            }
            server.setThreadId(userName, this); //die Referenzvariable des Threads wird mit dem User verknüpft
            ownID = server.askForID(userName); // eigene ID wird gespeichert

            String serverMessage = "New user connected: " + userName;

            boolean foundPartner = false;
            while(foundPartner == false){ //Endlosschleife, bis existierender Chatpartner gefunden
                writer.println("Mit wem möchstest du schreiben?");
                chatPartnerID = server.askForID(reader.readLine());
                if(chatPartnerID != -1){ //geprüft ob ChatPartnerId gültig ist
                    writer.println("Alles klar, du wirst verbunden");
                    server.setChatPartner(ownID, chatPartnerID); //User geht in Chatraum
                    foundPartner = true;
                }
            }// ab hier weiß der User die ID seines Chatpartners
            //selectChatRoom();
            server.sendMessage(serverMessage, ownID, chatPartnerID);        //Nachricht an den Partner

            String clientMessage;
            server.sendMessage(file.read(ownID, chatPartnerID), ownID); // bisheriger Chat wird an den Client übergeben

            // Endlosschleife

            do {
                clientMessage = reader.readLine();
                serverMessage = "[" + userName + "]: " + clientMessage;
                server.sendMessage(serverMessage, ownID, chatPartnerID);  //todo: this muss ausgetauscht werden zum jeweiligen Chatpartner

            } while (!clientMessage.equals("bye"));     // todo: Globale String Variable mit dem Namen CLOSECONNECTION = "CLOSE"

            server.removeUser(userName, this);
            socket.close();

            serverMessage = userName + " has quitted.";
            server.sendMessage(serverMessage, ownID, chatPartnerID);  //todo: this muss ausgetauscht werden zum jeweiligen Chatpartner

        } catch (IOException ex) {
            System.out.println("Error in UserThread: " + ex.getMessage());
            ex.printStackTrace();
        }


    }

    /**
     * Sends a message to the client.
     */
    void sendMessage(String message) {
        file.write(message, ownID, chatPartnerID);
        writer.println(message);
    }
}