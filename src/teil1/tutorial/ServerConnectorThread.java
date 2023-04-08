package teil1.tutorial;

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

    public ServerConnectorThread(String hostname, int port, Server server){
        this.hostname = hostname;
        this.port = port;
        this.server = server;

    }

    public void run(){
        //while(true){
            try {
                System.out.println("Versuch Sync Server zu verbinden");
            Socket socket = new Socket(hostname, port);
                OutputStream output = socket.getOutputStream();
                writer = new PrintWriter(output, true);
                System.out.println("Sync Server verbunden");

        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O Error: " + ex.getMessage());
        }
        //}

    }



    protected void sendMessageToOtherServer(String rawMessage, int sendUserId, int receiverUserId){
        String message = sendUserId + ";" + receiverUserId + ";" + rawMessage;
        System.out.println(message);
        writer.println(message);

    }
}
