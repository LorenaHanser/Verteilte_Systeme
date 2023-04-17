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
                System.out.println(ANSI_YELLOW + "Sync Server verbunden" + ANSI_RESET)
                ;
                InputStream input = socket.getInputStream();
                reader = new BufferedReader(new InputStreamReader(input));
                do {
                    try {
                        String response = reader.readLine();
                        System.out.println(response);
                        sendMessageToServer(response);
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

    private void sendMessageToServer(String rawMessage) {
        System.out.println("Nachricht erhalten");
        String[] rawMessageHeader = rawMessage.split(";", 2);
        int header = Integer.parseInt(rawMessageHeader[0]);
        if(header == server.USER_AKTIVITY){
            System.out.println("Es gab eine Useraktivität");
            String[] rawMessageArray = rawMessageHeader[1].split(";", 3);
            int userID = Integer.parseInt(rawMessageArray[0]);
            int serverID = Integer.parseInt(rawMessageArray[1]);
            int userStatus = Integer.parseInt(rawMessageArray[2]);
            if(userStatus == server.LOGGED_IN){
                server.setUserLoggedInLocal(userID, serverID);
            } else if (userStatus == server.LOGGED_OUT) {
                server.setUserLoggedOutLocal(userID);
            }
        } else if (header == server.MESSAGE) {
            String[] rawMessageArray = rawMessageHeader[1].split(";", 3);//String wird in Array gesplittet
            int senderID = Integer.parseInt(rawMessageArray[0]);
            int receiverID = Integer.parseInt(rawMessageArray[1]);
            String message = rawMessageArray[2];
            server.sendMessageFromServer(message, senderID, receiverID);
        }

    }

}
