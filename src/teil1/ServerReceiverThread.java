package teil1;

import java.io.*;
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
    private PrintWriter writer;

    public ServerReceiverThread(Server server, int port) {
        this.server = server;
        this.port = port;

    }

    @Override
    public void run() {
        try {
            ServerSocket syncServerSocket = new ServerSocket(port);

            while (true) {
                socket = syncServerSocket.accept();
                System.out.println(ANSI_YELLOW + "Sync Server verbunden" + ANSI_RESET);
                OutputStream output = socket.getOutputStream();
                writer = new PrintWriter(output, true);
                InputStream input = socket.getInputStream();
                reader = new BufferedReader(new InputStreamReader(input));
                do {
                    try {
                        String response = reader.readLine();
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
                System.out.println(ANSI_RED + "Sync Server Verbindung verloren" + ANSI_RESET);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void sendMessageToServer(ClientMessage clientMessage) {
        // todo nur Nachrichten Typ 1 und 2 sollen in sendMessage verarbeitet werden (stand 17.04.) (rest war für Sync gedacht)
        /*
        // todo: einkommentieren, um einen Delay zwischen den Servern zu simulieren
        try {
            System.out.println("Delay Anfang --- Thread schläft");
            Thread.sleep(10000);
            System.out.println("Delay Ende --- Thread ist aufgewacht");
        } catch (Exception e) {
            System.out.println(ANSI_RED + "Fehler beim Schlafen: " + e.getMessage() + ANSI_RESET);
        }
        */
        if (clientMessage.getType() == Server.SYNC_REQUEST) {
            writer.println(server.receiveSynchronization(clientMessage).toString());
        } else if (clientMessage.getType() == Server.NEW_MESSAGE | clientMessage.getType() == Server.NEW_MESSAGE_WITHOUT_TIMESTAMP) {
            server.sendMessage(clientMessage);
        }
    }

    private void sendUserActivityToServer(ServerMessage serverMessage) {
        server.changeUserActivity(serverMessage);
    }

}
