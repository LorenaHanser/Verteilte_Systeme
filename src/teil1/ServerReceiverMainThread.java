package teil1;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Ist für die Verwaltung der eingehenden Syncverbindungen der anderen Server zuständig
 * Wenn Verbindung zu diesem Server aufgebaut wird, weist ServerReceiverMainThread der Verbindung einen Thread zu.
 * Es sind max. 2 Verbindungen gleichzeitig möglich.
 */
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

    /**
     * Methode startet Server und nimmt die eingehenden Verbindungen entgegen und weißt diese einem Thread zu.
     * Es kann max. 2 Aktive Verbindungen geben.
     */
    @Override
    public void run() {
        try {
            ServerSocket syncServerSocket = new ServerSocket(port);
            while (true) {
                socket = syncServerSocket.accept();
                System.out.println("Sync Server versucht sich zu verbinden");
                if(thread1 == null){
                    thread1 = new ServerReceiverThread(socket, server, this, 1);//Server ist der Hauptserver
                    thread1.start();
                    System.out.println("Sync Server1 verbunden");
                } else if (thread2 == null) {
                    thread2 = new ServerReceiverThread(socket, server, this, 2);//Server ist der Hauptserver
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

    /**
     * ruft Thread auf, wenn sich der ServerConnector Thread des anderen Servers disconnectet wird.
     * Thread setzt seine Referenzvariable auf null. Damit wird der Platz für eine neue Verbindung frei.
     * @param threadNumber wird zum zuordnen des Threads gebraucht.
     *
     */
    protected void resetThread(int threadNumber){
        if(threadNumber == 1){
            thread1 = null;
        } else if (threadNumber == 2) {
            thread2 = null;
        }else {
            System.out.println("ERROR bei Resetten von Thread");
        }
    }
}
