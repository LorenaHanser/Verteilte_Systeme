package teil1;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServerConnectorThread extends Thread {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    private String hostname;
    private int port;
    private Server server;

    private PrintWriter writer;
    private BufferedReader reader;
    private Socket socket;

    private String response;
    private boolean answerIsThere;

    public ServerConnectorThread(String hostname, int port, Server server) {
        this.hostname = hostname;
        this.port = port;
        this.server = server;

    }

    public void run() {
        while (true) {

            try {
                socket = new Socket(hostname, port);
                OutputStream output = socket.getOutputStream();
                writer = new PrintWriter(output, true);
                InputStream input = socket.getInputStream();
                reader = new BufferedReader(new InputStreamReader(input));
                System.out.println(ANSI_YELLOW + "Sync Server verbunden" + ANSI_RESET);
                while (socket.isConnected()) {
                    try {
                        response = reader.readLine();
                        answerIsThere = true;
                    } catch (IOException ex) {
                        System.out.println(ANSI_PURPLE + "Verbindung getrennt " + ex.getMessage() + ANSI_RESET);
                        break;
                    }
                }
                System.out.println(ANSI_RED + "Verbindung verloren" + ANSI_RESET);

            } catch (UnknownHostException ex) {
            } catch (IOException ex) {
            }
        }
    }
    private ClientMessage sendSyncResponseToServer(ClientMessage clientMessage){
        //if(clientMessage.getType() == Server.)
    return clientMessage;//ist kaputt
    }

    protected ClientMessage requestSynchronization(ClientMessage clientMessage){
        ClientMessage answer = null;
        try {
            answerIsThere = false;
            writer.println(clientMessage.toString());
            while(!answerIsThere){
                //Wartet, bis eine Antwort eintrifft, hier muss man das Timeout reinbauen
            }
            answer = ClientMessage.toObject(response);
        }catch (Exception e){
            e.printStackTrace();
        }
        return answer;
    }

    // Senden einer ClientMessage zum anderen Server
    protected void sendMessageToOtherServer(ClientMessage clientMessage) {
        try {
            writer.println(clientMessage.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void sendUserActivity(ServerMessage serverMessage) {
        if (writer != null) {
            writer.println(serverMessage.toString());
        }
    }
}

