package teil1;

public class Server3 {
//Das ist der Beweis das das zusammenführen geklappt hat

        public static void main(String[] args) {
            System.out.println("Server 3 wird gestartet");
            int port = 8990;//Server 3 läuft immer auf Port 8990 Server 1 auf 8989 Server 3 auf 8991
            int partner1ServerPort = 8991;
            int partner2ServerPort = 8992;
            int serverReciverPort = 8993;
            int serverNummer = 3;

            Server server = new Server(port, partner1ServerPort, partner2ServerPort, serverReciverPort, serverNummer);
            server.execute();
        }
}