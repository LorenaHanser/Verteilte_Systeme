package teil1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerReceiverThread extends Thread {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    private Socket socket;
    private Server server;

    private int port;
    private BufferedReader reader;
    private ServerReceiverMainThread serverReceiverMainThread;
    private int threadNumber;


    public ServerReceiverThread(Socket socket, Server server, ServerReceiverMainThread serverReceiverMainThread, int threadNumber){
        this.socket = socket;
        this.server = server;
        this.serverReceiverMainThread = serverReceiverMainThread;
        this.threadNumber = threadNumber;

    }

    @Override
    public void run() {
        try {
            while (true) {

                System.out.println(ANSI_YELLOW + "Sync Server hat sich verbunden" + ANSI_RESET);
                InputStream input = socket.getInputStream();
                reader = new BufferedReader(new InputStreamReader(input));
                do {
                    try {
                        String response = reader.readLine();
                        System.out.println(response);
                        if (Message.isClientMessage(response)) {
                            sendMessageToServer(ClientMessage.toObject(response));
                        } else {
                            sendUserActivityToServer(ServerMessage.toObject(response));
                        }
                    } catch (IOException ex) {
                        System.out.println(ANSI_RED + "Error reading from server: " + ex.getMessage() + ANSI_RESET);
                        ex.printStackTrace();
                        break;
                    }
                } while (socket.isConnected());
                System.out.println(ANSI_RED + "Sync Server Verbindung zu Connector verloren" + ANSI_RESET);
                serverReceiverMainThread.resetThread(threadNumber);
                break;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void sendMessageToServer(ClientMessage clientMessage) {
        // todo nur Nachrichten Typ 1 und 2 sollen in sendMessage verarbeitet werden (stand 17.04.) (rest war für Sync gedacht)

        // todo: einkommentieren, um einen Delay zwischen den Servern zu simulieren
       /* try {
            System.out.println("Delay Anfang --- Thread schläft");
            Thread.sleep(10000);
            System.out.println("Delay Ende --- Thread ist aufgewacht");
        } catch (Exception e) {
            System.out.println(ANSI_RED + "Fehler beim Schlafen: " + e.getMessage() + ANSI_RESET);
        }
        */

        server.sendMessage(clientMessage);
    }

    private void sendUserActivityToServer(ServerMessage serverMessage) {
        server.changeUserActivity(serverMessage);
    }

}
