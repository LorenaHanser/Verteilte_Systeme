package teil1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerReciverThread extends Thread {
    private Socket socket;
    private Server server;

    private int port;
    private BufferedReader reader;

    public ServerReciverThread(Server server, int port) {
        this.server = server;
        this.port = port;

    }

    @Override
    public void run() {
        try {
            ServerSocket syncServerSocket = new ServerSocket(port);

            while (true) {
                socket = syncServerSocket.accept();
                System.out.println("Sync Server verbunden")
                ;
                InputStream input = socket.getInputStream();
                reader = new BufferedReader(new InputStreamReader(input));
                do {
                    try {
                        String response = reader.readLine();
                        System.out.println(response);
                        sendMessageToServer(response);
                    } catch (IOException ex) {
                        System.out.println("Error reading from server: " + ex.getMessage());
                        ex.printStackTrace();
                        break;
                    }
                } while (socket.isConnected());
                System.out.println("Sync Server Verbindung verlohren");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void sendMessageToServer(String rawMessage) {
        String[] rawMessageArray = rawMessage.split(";", 3);//String wird in Array gesplittet
        int senderID = Integer.parseInt(rawMessageArray[0]);
        int reciverID = Integer.parseInt(rawMessageArray[1]);
        String message = rawMessageArray[2];
        server.sendMessageFromServer(message, senderID, reciverID);
    }

}
