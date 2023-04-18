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
    private MCSHandler mcsHandler;
    private int threadNumber;

    public ServerConnectorThread(String hostname, int port, Server server, MCSHandler mcsHandler, int threadNumber) {
        this.hostname = hostname;
        this.port = port;
        this.server = server;
        this.mcsHandler = mcsHandler;
        this.threadNumber = threadNumber;

    }

    public void run() {
        while (true) {

            try {
                Socket socket = new Socket(hostname, port);
                OutputStream output = socket.getOutputStream();
                writer = new PrintWriter(output, true);
                InputStream input = socket.getInputStream();
                reader = new BufferedReader(new InputStreamReader(input));
                System.out.println(ANSI_YELLOW + "Sync Server verbunden" + ANSI_RESET);
                mcsHandler.setServerOnline(threadNumber);

                while (socket.isConnected()) {
                    try {
                        String response = reader.readLine();
                        System.out.println(response);

                    } catch (IOException ex) {
                        System.out.println(ANSI_PURPLE + "Verbindung getrennt " + ex.getMessage() + ANSI_RESET);
                        break;
                    }
                }
                System.out.println(ANSI_RED + "Verbindung verloren" + ANSI_RESET);
                mcsHandler.setServerOffline(threadNumber);

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

