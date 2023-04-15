package teil1;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerReceiverMainThread extends Thread {

    private Socket socket;
    private Socket socket1;
    private Socket socket2;
    private Server server;

    private int port;
    private BufferedReader reader1;
    private BufferedReader reader2;

    private ServerReceiverThread thread1;
    private ServerReceiverThread thread2;

    public ServerReceiverMainThread(Server server, int port) {
        this.server = server;
        this.port = port;

    }

    @Override
    public void run() {
        try {
            ServerSocket syncServerSocket = new ServerSocket(port);
            while (true) {
                socket = syncServerSocket.accept();
                System.out.println("Sync Server versucht sich zu verbinden");
                if(thread1 == null){
                    thread1 = new ServerReceiverThread(socket, server);//Server ist der Hauptserver
                    thread1.start();
                    System.out.println("Sync Server1 verbunden");
                } else if (thread2 == null) {
                    thread2 = new ServerReceiverThread(socket, server);//Server ist der Hauptserver
                    thread2.start();
                    System.out.println("Sync Server2 verbunden");
                }else{
                    System.out.println("alle Sockets belegt!");
                    socket.close();
                }
            } //hier Endlosschliefe (für die anderen Server)
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
