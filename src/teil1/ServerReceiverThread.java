package teil1;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Die Klasse empfängt und verarbeitet Anfragen und Hinweise eines anderen Servers, die dieser über seinen {@link ServerConnectorThread} verschickt.
 */
public class ServerReceiverThread extends Thread {

    private Socket socket;
    private Server server;

    private int port;
    private BufferedReader reader;
    private PrintWriter writer;

    /**
     * Konstruktor
     * @param server Entsprechendes Objekt der Klasse {@link Server}
     * @param port   Port des Servers
     */
    public ServerReceiverThread(Server server, int port) {
        this.server = server;
        this.port = port;

    }

    /**
     * In der Methode verbindet sich der Thread mit seinem SyncServer.
     * Je nachdem, welche Nachricht vom anderen Server empfangen wird, wird diese unterschiedlich verarbeitet, indem eine der nachfolgenden Methoden aufgerufen wird.
     */
    @Override
    public void run() {
        try {
            ServerSocket syncServerSocket = new ServerSocket(port);

            while (true) {
                socket = syncServerSocket.accept();
                System.out.println(Server.ANSI_YELLOW + "Sync Server verbunden" + Server.ANSI_RESET);
                OutputStream output = socket.getOutputStream();
                writer = new PrintWriter(output, true);
                InputStream input = socket.getInputStream();
                reader = new BufferedReader(new InputStreamReader(input));
                do {
                    try {
                        String response = reader.readLine();
                        System.out.println(Server.ANSI_GREEN + "EMPFANGEN: Es gab eine Nachricht" + Server.ANSI_RESET);
                        int messageCategory = Message.getMessageCategoryFromString(response);
                        if (messageCategory == Message.CATEGORY_CLIENT_MESSAGE) {
                            this.sendMessageToServer(MessageClient.toObject(response));
                        } else if (messageCategory == Message.CATEGORY_SERVER_MESSAGE) {
                            System.out.println(Server.ANSI_GREEN + "EMPFANGEN: Es gab eine Nutzeraktivität" + Server.ANSI_RESET);
                            this.sendUserActivityToServer(MessageUserActivity.toObject(response));
                        } else if (messageCategory == Message.CATEGORY_SYNC_MESSAGE) {
                            System.out.println("Hier wird noch gebaut!");
                            this.sendSyncMessageToServer(MessageSync.toObject(response));
                        }

                    } catch (IOException ex) {
                        System.out.println(Server.ANSI_RED + "Error reading from server: " + ex.getMessage() + Server.ANSI_RESET);
                        ex.printStackTrace();
                        break;
                    }
                } while (socket.isConnected());
                System.out.println(Server.ANSI_RED + "Sync Server Verbindung verloren" + Server.ANSI_RESET);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Wenn in der {@link ServerReceiverThread#run()}-Methode eine Nachricht der Kategorie {@link Message#CATEGORY_CLIENT_MESSAGE} empfangen wurde, wird diese Methode aufgerufen.
     * <p>
     * Die Nachricht wird an den {@link Server} weitergegeben
     * @param messageClient empfangene MessageClient
     */
    private void sendMessageToServer(MessageClient messageClient) {
        /*
        // todo: einkommentieren, um einen Delay zwischen den Servern zu simulieren
        try {
            System.out.println("Delay Anfang --- Thread schläft");
            Thread.sleep(10000);
            System.out.println("Delay Ende --- Thread ist aufgewacht");
        } catch (Exception e) {
            System.out.println(ANSI_RED + "Fehler beim Schlafen: " + e.getMessage() + ANSI_RESET);
        }
        */
        server.sendMessage(messageClient);
    }

    /**
     * Wenn in der {@link ServerReceiverThread#run()}-Methode eine Nachricht der Kategorie {@link Message#CATEGORY_SERVER_MESSAGE} empfangen wurde, wird diese Methode aufgerufen.
     * <p>
     * Sie verarbeitet die Nutzeraktivität.
     */
    private void sendUserActivityToServer(MessageUserActivity messageUserActivity) {
        if (messageUserActivity.getType() == 0) {
            System.out.println(Server.ANSI_GREEN + "EMPFANGEN: Wir haben Nutzeraktivitäten erhalten!!" + Server.ANSI_RESET);
            server.changeUserActivity(messageUserActivity);
        } else if (messageUserActivity.getType() == 2 && server.isServerReadyToShareUserData()) {
            System.out.println(Server.ANSI_GREEN + "EMPFANGEN: Wir sollen glaube ich UserDaten übermitteln!!" + Server.ANSI_RESET);
            System.out.println("Das sind unsere Antworten: " + server.getUserIsOnServerArrayAsServerMessage().toString());
            writer.println(server.getUserIsOnServerArrayAsServerMessage());
        }

    }

    /**
     * Wenn in der {@link ServerReceiverThread#run()}-Methode eine Nachricht der Kategorie {@link Message#CATEGORY_SYNC_MESSAGE} empfangen wurde, wird diese Methode aufgerufen.
     * <p>
     * Die Methode gibt die Syncanfrage an {@link Server#receiveSynchronization(MessageSync)} und verarbeitet die Antwort.
     */
    private void sendSyncMessageToServer(MessageSync messageSync) {
        System.out.println(Server.ANSI_GREEN + "EMPFANGEN: Syncanfrage erhalten:" + Server.ANSI_RESET);
        System.out.println(messageSync.toString());
        String answer = server.receiveSynchronization(messageSync).toString();
        System.out.println(Server.ANSI_GREEN + "ANTWORTEN(1): Das ist unsere Antwort: " + Server.ANSI_RESET);
        System.out.println(answer);
        writer.println(answer);
    }

}