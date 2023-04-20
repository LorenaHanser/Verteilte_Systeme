package teil1;

public class Server2 {

    public static void main(String[] args) {
        System.out.println(Server.ANSI_YELLOW + "Server 2 wird gestartet" + Server.ANSI_RESET);
        int port = 8990;//Server 2 läuft immer auf Port 8990 Server 1 auf 8989
        int partnerServerPort = 8992;
        int serverReceiverPort = 8991;
        int serverNummer = 2;

        Server server = new Server(port, partnerServerPort, serverReceiverPort, serverNummer);
        server.execute();
    }
}
