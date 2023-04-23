package teil2;

/**
 * Die Klasse startet ein Objekt der Klasse {@link Server}.
 */
public class Server3 {

    /**
     * In der Methode wird ein Objekt der Klasse {@link Server} erstellt und die {@link Server#execute()}-Methode ausgeführt.
     * Dabei läuft dieser {@link Server2} auf einem anderen Port als ein in der Klasse {@link Server} erstelltes Objekt.
     * <p>
     * Server 1 auf 8988; Server 2 läuft immer auf Port 8989; Server 3 auf 8990.
     * Die Server können in beliebiger Reihenfolge gestartet werden.
     *
     * @param args String[]
     */
    public static void main(String[] args) {
        System.out.println(Server.ANSI_YELLOW + "Server 3 wird gestartet" + Server.ANSI_RESET);
        int port = 8990;
        int partner1ServerPort = 8991;
        int partner2ServerPort = 8992;
        int serverReceiverPort = 8993;
        int serverNummer = 3;

        Server server = new Server(port, partner1ServerPort, partner2ServerPort, serverReceiverPort, serverNummer);
        server.execute();
    }
}