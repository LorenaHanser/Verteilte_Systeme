package teil1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerReceiverThread extends Thread {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    private Socket socket;
    private Server server;
    private BufferedReader reader;


    public ServerReceiverThread(Socket socket, Server server){
        this.socket = socket;
        this.server = server;

    }

    @Override
    public void run() {
        try {
            while (true) {

                System.out.println(ANSI_YELLOW + "Sync Server verbunden" + ANSI_RESET);
                InputStream input = socket.getInputStream();
                reader = new BufferedReader(new InputStreamReader(input));
                do {
                    try {
                        String response = reader.readLine();
                        System.out.println(response);
                        if(Message.isClientMessage(response)){
                            sendMessageToServer(ClientMessage.toObject(response));
                        }else{
                            sendUserActivityToServer(ServerMessage.toObject(response));
                        }
                    } catch (IOException ex) {
                        System.out.println(ANSI_RED + "Error reading from server: " + ex.getMessage() + ANSI_RESET);
                        ex.printStackTrace();
                        break;
                    }
                } while (socket.isConnected());
                System.out.println(ANSI_RED + "Sync Server Verbindung verloren" + ANSI_RESET);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessageToServer(ClientMessage clientMessage) {
        System.out.println("Nachricht erhalten");
        // todo nur Nachrichten Typ 1 und 2 sollen in sendMessage verarbeitet werden (stand 17.04.) (rest war für Sync gedacht)
        server.sendMessage(clientMessage);
    }

    private void sendUserActivityToServer(ServerMessage serverMessage) {
        System.out.println("User Activity erhalten");
        server.changeUserActivity(serverMessage);
    }

    }
