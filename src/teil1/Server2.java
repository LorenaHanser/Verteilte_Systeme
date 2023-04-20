package teil1;

public class Server2 {

    public static void main(String[] args) {
        System.out.println(Server.ANSI_YELLOW + "Server 2 wird gestartet" + Server.ANSI_RESET);
        int port = 8989;//Server 2 läuft immer auf Port 8990 Server 1 auf 8989 Server 3 auf 89919
        int partner1ServerPort = 8991;
            int partner2ServerPort = 8993;
        int serverReceiverPort = 8992;
        int serverNummer = 2;

            Server server = new Server(port, partner1ServerPort, partner2ServerPort, serverReceiverPort, serverNummer);
            server.execute();
        }
}
