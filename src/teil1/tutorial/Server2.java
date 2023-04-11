package teil1.tutorial;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class Server2 {


        public static void main(String[] args) {
            System.out.println("Server 2 wird gestartet");
            int port = 8990;//Server 2 läuft immer auf Port 8990 Server 1 auf 8989
            int partnerServerPort = 8992;
            int serverReciverPort = 8991;

            Server server = new Server(port, partnerServerPort, serverReciverPort);
            server.execute();
        }
}
