package teil1;

public class Server2 {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static void main(String[] args) {
        System.out.println(ANSI_YELLOW + "Server 2 wird gestartet" + ANSI_RESET);
        int port = 8989;//Server 2 läuft immer auf Port 8990 Server 1 auf 8989 Server 3 auf 89919
        int partner1ServerPort = 8991;
            int partner2ServerPort = 8993;
        int serverReceiverPort = 8992;
        int serverNummer = 2;

            Server server = new Server(port, partner1ServerPort, partner2ServerPort, serverReceiverPort, serverNummer);
            server.execute();
        }
}
