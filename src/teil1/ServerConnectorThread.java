package teil1;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServerConnectorThread extends Thread {

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
        while(true){

                try {
                    Socket socket = new Socket(hostname, port);
                    OutputStream output = socket.getOutputStream();
                    writer = new PrintWriter(output, true);
                    System.out.println("Sync Server verbunden");
                    while(socket.isConnected()){

                    }
                    System.out.println("Verbindung verlohren");

                } catch (UnknownHostException ex) {
                } catch (IOException ex) {
                }
        }
    }

    protected void sendMessageToOtherServer(String rawMessage, int sendUserId, int receiverUserId) {
        String message = sendUserId + ";" + receiverUserId + ";" + rawMessage;
        writer.println(message);

    }
}

