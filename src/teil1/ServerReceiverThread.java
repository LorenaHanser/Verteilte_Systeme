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
                        int messageCategory = Message.getMessageCategoryFromString(response);
                        if (messageCategory == Message.CATEGORY_CLIENT_MESSAGE) {
                            this.sendMessageToServer(MessageClient.toObject(response));
                        } else if (messageCategory == Message.CATEGORY_SERVER_MESSAGE) {
                            this.sendUserActivityToServer(MessageUserActivity.toObject(response));
                        } else if (messageCategory == Message.CATEGORY_SYNC_MESSAGE) {
                            this.sendSyncMessageToServer(MessageSync.toObject(response));
                        }

                    } catch (IOException ex) {
                        System.out.println(Server.ANSI_RED + "Error reading from server: " + ex.getMessage() + Server.ANSI_RESET);
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

        // todo: auskommentieren, um den simulierten Delay wieder herauszunehmen
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            System.out.println(Server.ANSI_RED + "Fehler beim Schlafen: " + e.getMessage() + Server.ANSI_RESET);
        }
        server.sendMessage(messageClient);
    }

    /**
     * Wenn in der {@link ServerReceiverThread#run()}-Methode eine Nachricht der Kategorie {@link Message#CATEGORY_SERVER_MESSAGE} empfangen wurde, wird diese Methode aufgerufen.
     * <p>
     * Sie verarbeitet die Nutzeraktivität.
     */
    private void sendUserActivityToServer(MessageUserActivity messageUserActivity) {
        if (messageUserActivity.getType() == 0) {
            server.changeUserActivity(messageUserActivity);
        } else if (messageUserActivity.getType() == 2 && server.isServerReadyToShareUserData()) {
            writer.println(server.getUserIsOnServerArrayAsServerMessage());
        }

    }

    /**
     * Wenn in der {@link ServerReceiverThread#run()}-Methode eine Nachricht der Kategorie {@link Message#CATEGORY_SYNC_MESSAGE} empfangen wurde, wird diese Methode aufgerufen.
     * <p>
     * Die Methode gibt die Syncanfrage an {@link Server#receiveSynchronization(MessageSync)} und verarbeitet die Antwort.
     */
    private void sendSyncMessageToServer(MessageSync messageSync) {
        String answer = server.receiveSynchronization(messageSync).toString();
        writer.println(answer);
    }

}