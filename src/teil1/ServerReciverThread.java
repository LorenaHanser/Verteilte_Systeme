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
    private BufferedReader reader;


    public ServerReciverThread(Socket socket, Server server){
        this.socket = socket;
        this.server = server;

    }

    @Override
    public void run() {
        try {
            while (true) {

                System.out.println("SyncThread gestartet");
                InputStream input = socket.getInputStream();
                reader = new BufferedReader(new InputStreamReader(input));
                do {
                    try {
                        System.out.println("Fange an zu lauschen");
                        String response = reader.readLine();
                        System.out.println("Nachricht erhalten");
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
        System.out.println("Nachricht erhalten");
        String[] rawMessageArray = rawMessage.split(";", 3);//String wird in Array gesplittet
        int senderID = Integer.parseInt(rawMessageArray[0]);
        int reciverID = Integer.parseInt(rawMessageArray[1]);
        String message = rawMessageArray[2];
        server.sendMessageFromServer(message, senderID, reciverID);

    }

}
