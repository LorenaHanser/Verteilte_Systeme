package teil1;

public class Server2 {


        public static void main(String[] args) {
            System.out.println("Server 2 wird gestartet");
            int port = 8989;//Server 2 läuft immer auf Port 8990 Server 1 auf 8989 Server 3 auf 89919
            int partner1ServerPort = 8991;
            int partner2ServerPort = 8993;
            int serverReciverPort = 8992;
            String serverNummer = "2";

            Server server = new Server(port, partner1ServerPort, partner2ServerPort, serverReciverPort, serverNummer);
            server.execute();
        }
}
