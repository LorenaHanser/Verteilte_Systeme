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

    private int ownID;

    // Konstruktor

    public ServerUserThread(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    public ServerUserThread(Server server, int ThreadId){
        this.server = server;
        this.ThreadId = ThreadId;
    }

    public void setUser(Socket socket)
    {
        this.socket = socket;
        System.out.println("Neuer User wurde gesetzt");
    }

    public void run() {

            try {
                // Nachricht vom client empfangen
                System.out.println("Ein Neuer USer ist im Thread: "+ThreadId+" angekommen");
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                OutputStream output = socket.getOutputStream();
                writer = new PrintWriter(output, true);

                printUsers();
                writer.println("Please enter your name:");

                String userName = reader.readLine();
                server.setThreadId(userName, this);
                ownID = server.askForID(userName);

                String serverMessage = "New user connected: " + userName;

                boolean foundPartner = false;
                while(foundPartner == false){
                    writer.println("Mit wem möchstest du schreiben?");
                    chatPartnerId = server.askForID(reader.readLine());
                    if(chatPartnerId != -1){
                        writer.println("Alles klar, du wirst verbunden");
                        server.setChatPartner(ownID, chatPartnerId);
                        foundPartner = true;
                                            }
                }// ab hier weiß der User die ID seines Chatpartners
                //selectChatRoom();
                server.sendMessage(serverMessage, ownID, chatPartnerId);        //todo: this muss ausgetauscht werden zum jeweiligen Chatpartner

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
     * Sends a list of online users to the newly connected user.
     */
    void printUsers() {
        if (server.hasUsers()) {
            writer.println("Connected users: " + server.getUserNames());
        } else {
            writer.println("No other users connected");
        }
    }

    /**
     * Sends a message to the client.
     */
    void sendMessage(String message) {
        writer.println(message);
    }

    void selectChatRoom()
    {
        boolean foundPartner = false;
        while(foundPartner == false){
            writer.println("Mit wem möchstest du schreiben?");

        }
    }
}
