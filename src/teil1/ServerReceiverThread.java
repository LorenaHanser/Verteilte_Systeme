package teil1;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerReceiverThread extends Thread {

    private Socket socket;
    private Server server;

    private int port;
    private BufferedReader reader;
    private PrintWriter writer;

    public ServerReceiverThread(Server server, int port) {
        this.server = server;
        this.port = port;

    }

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
                        System.out.println(Server.ANSI_GREEN+"EMPFANGEN: Es gab eine Nachricht"+Server.ANSI_RESET);
                            if (Message.getMessageCategoryFromString(response) == Message.CATEGORY_CLIENT_MESSAGE) {
                                sendMessageToServer(MessageClient.toObject(response));
                            } else if(Message.getMessageCategoryFromString(response) == Message.CATEGORY_SERVER_MESSAGE){
                                System.out.println(Server.ANSI_GREEN+"EMPFANGEN: Es gab eine Useraktivität"+Server.ANSI_RESET);
                                sendUserActivityToServer(MessageUserActivity.toObject(response));
                            } else if(Message.getMessageCategoryFromString(response) == Message.CATEGORY_SYNC_MESSAGE){
                                System.out.println("Hier wird noch gebaut!");
                                sendUserActivityToServer(MessageSync.toObject(response));
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

    private void sendUserActivityToServer(MessageUserActivity messageUserActivity) {
        if(messageUserActivity.getType() == 0){
            System.out.println(Server.ANSI_GREEN+"EMPFANGEN: Wir haben Useraktivitäten erhalten!!"+Server.ANSI_RESET);
            server.changeUserActivity(messageUserActivity);
        } else if (messageUserActivity.getType() == 1) {
            System.out.println(Server.ANSI_GREEN+"EMPFANGEN: Wir sollen glaube ich UserDaten übermitteln!!"+Server.ANSI_RESET);
            System.out.println("Das sind unsere Antworten: "+ server.getUserIsOnServerArrayAsServerMessage().toString());
            writer.println(server.getUserIsOnServerArrayAsServerMessage());
        }

    }
    private void sendUserActivityToServer(MessageSync messageSync) {
        System.out.println(Server.ANSI_GREEN+"EMPFANGEN: Syncanfrage erhalten:"+Server.ANSI_RESET);
        System.out.println(messageSync.toString());
        String answer = server.receiveSynchronization(messageSync).toString();
        System.out.println(Server.ANSI_GREEN+"ANTWORTEN: Das ist unsere Antwort: "+Server.ANSI_RESET);
        System.out.println(answer);
        writer.println(answer);
    }

}
