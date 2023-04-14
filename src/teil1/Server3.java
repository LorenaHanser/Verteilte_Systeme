package teil1;

public class Server3 {


        public static void main(String[] args) {
            System.out.println("Server 2 wird gestartet");
            int port = 8990;//Server 2 läuft immer auf Port 8990 Server 1 auf 8989 Server 3 auf 8991
            int partnerServerPort = 0000;
            int serverReciverPort = 8993;
            String serverNummer = "3";

            Server server = new Server(port, partnerServerPort, serverReciverPort, serverNummer);
            server.execute();
        }
}
