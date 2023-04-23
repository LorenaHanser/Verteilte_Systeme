package teil1;

/**
 * Die Klasse startet ein Objekt der Klasse {@link Server}.
 */
public class Server2 {

    /**
     * In der Methode wird ein Objekt der Klasse {@link Server} erstellt und die {@link Server#execute()}-Methode ausgeführt.
     * Dabei läuft dieser {@link Server2} auf einem anderen Port als ein in der Klasse {@link Server} erstelltes Objekt.
     * <p>
     * {@link Server2} kann auch vor {@link Server} gestartet werden.
     * @param args String[]
     */
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
