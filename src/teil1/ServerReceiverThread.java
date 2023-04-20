package teil1;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.stream.Collectors;

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
                    //System.out.println("=================================================");
                    String response =  reader.readLine();
                    String fullresponse = response;
                    System.out.println(response);
                    while(response != null & !response.contains("*")){
                        //System.out.println("Nachricht noch nicht am Ende");

                        response = reader.readLine();
                        if(response != null & !response.contains("*")) {
                            fullresponse += '\n';
                            fullresponse += response;
                            //System.out.println(response);
                        }
                    }
                    //System.out.println("=================================================");
                    //System.out.println("Bin im Receiver " + fullresponse);
                    if(fullresponse.contains(";")) {
                        if (Message.isClientMessage(fullresponse)) {

                            sendMessageToServer(ClientMessage.toObject(fullresponse));
                        } else {
                            sendUserActivityToServer(ServerMessage.toObject(fullresponse));
                        }
                    }else{
                        System.out.println("Und wieder gibt es einen ERROR in der Nachricht: "+fullresponse);
                    }
                } while (socket.isConnected());
                System.out.println(Server.ANSI_RED + "Sync Server Verbindung verloren" + Server.ANSI_RESET);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void sendMessageToServer(ClientMessage clientMessage) {
        // todo nur Nachrichten Typ 1 und 2 sollen in sendMessage verarbeitet werden (stand 17.04.) (rest war für Sync gedacht)
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
        if (clientMessage.getType() == Server.SYNC_REQUEST) {
            System.out.println("Datei wird übertragen!");
            writer.println(server.receiveSynchronization(clientMessage).toString());
            System.out.println("Datei wurde erfolgreich übertragen!");
        } else if (clientMessage.getType() == Server.NEW_MESSAGE | clientMessage.getType() == Server.NEW_MESSAGE_WITHOUT_TIMESTAMP) {
            server.sendMessage(clientMessage);
        }
    }

    private void sendUserActivityToServer(ServerMessage serverMessage) {
        server.changeUserActivity(serverMessage);
    }

}
