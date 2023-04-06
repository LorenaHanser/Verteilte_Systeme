package teil1.tutorial;

import java.io.*;
import java.net.*;

/**
 * This thread handles connection for each connected client, so the server
 * can handle multiple clients at the same time.
 *
 * @author www.codejava.net
 */
public class ServerUserThread extends Thread {
    private Socket socket;
    private Server server;
    private PrintWriter writer;
    private int ThreadId;

    private int chatPartnerId;

    private int ownID; //Die Id des Users, der auf dem Thread läuft

    // Konstruktor

    public ServerUserThread(Socket socket, Server server) {
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

                String userName = reader.readLine();
                server.setThreadId(userName, this); //die Referenzvariable des Threads wird mit dem User verknüpft
                ownID = server.askForID(userName); // eigene ID wird gespeichert

                String serverMessage = "New user connected: " + userName;

                boolean foundPartner = false;
                while(foundPartner == false){ //Endlosschleife, bis existierender Chatpartner gefunden
                    writer.println("Mit wem möchstest du schreiben?");
                    chatPartnerId = server.askForID(reader.readLine());
                    if(chatPartnerId != -1){ //geprüft ob ChatPartnerId gültig ist
                        writer.println("Alles klar, du wirst verbunden");
                        server.setChatPartner(ownID, chatPartnerId); //User geht in Chatraum
                        foundPartner = true;
                                            }
                }// ab hier weiß der User die ID seines Chatpartners
                //selectChatRoom();
                server.sendMessage(serverMessage, ownID, chatPartnerId);        //Nachricht an den Partner

                String clientMessage;

                // Endlosschleife

                do {
                    clientMessage = reader.readLine();
                    serverMessage = "[" + userName + "]: " + clientMessage;
                    server.sendMessage(serverMessage, ownID, chatPartnerId);  //todo: this muss ausgetauscht werden zum jeweiligen Chatpartner

                } while (!clientMessage.equals("bye"));     // todo: Globale String Variable mit dem Namen CLOSECONNECTION = "CLOSE"

                server.removeUser(userName, this);
                socket.close();

                serverMessage = userName + " has quitted.";
                server.sendMessage(serverMessage, ownID, chatPartnerId);  //todo: this muss ausgetauscht werden zum jeweiligen Chatpartner

            } catch (IOException ex) {
                System.out.println("Error in UserThread: " + ex.getMessage());
                ex.printStackTrace();
            }


    }

    /**
     * Sends a message to the client.
     */
    void sendMessage(String message) {
        writer.println(message);
    }

}
