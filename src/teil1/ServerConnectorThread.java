package teil1;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
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

    public ServerConnectorThread(String hostname, int port, Server server) {
        this.hostname = hostname;
        this.port = port;
        this.server = server;

    }

    public void run() {
        while (true) {

            try {
                Socket socket = new Socket(hostname, port);
                OutputStream output = socket.getOutputStream();
                writer = new PrintWriter(output, true);
                System.out.println(ANSI_YELLOW + "Sync Server verbunden" + ANSI_RESET);
                while (socket.isConnected()) {

                }
                System.out.println(ANSI_RED + "Verbindung verloren" + ANSI_RESET);

            } catch (UnknownHostException ex) {
            } catch (IOException ex) {
            }
        }
    }

    // Senden einer ClientMessage zum anderen Server
    protected void sendMessageToOtherServer(ClientMessage clientMessage) {
        System.out.println("Message wird gesendet!");
        writer.println(clientMessage.toString());
    }

    protected void sendUserActivity(ServerMessage serverMessage){
        writer.println(serverMessage.toString());
    }
}

