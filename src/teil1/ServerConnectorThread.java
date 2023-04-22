package teil1;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServerConnectorThread extends Thread {

    private String hostname;
    private int port;
    private Server server;

    private PrintWriter writer;
    private BufferedReader reader;
    private Socket socket;

    private String response;
    private String fullresponse;
    private boolean answerIsThere;
    private boolean answerIsPicked;
    private boolean isThreadAlreadyConnected;

    public ServerConnectorThread(String hostname, int port, Server server) {
        this.hostname = hostname;
        this.port = port;
        this.server = server;
        this.isThreadAlreadyConnected = false;

    }
    public ServerConnectorThread(Socket socket, PrintWriter writer, BufferedReader reader,Server server) {
        this.socket = socket;
        this.writer = writer;
        this.reader = reader;
        this.server = server;
        this.isThreadAlreadyConnected = true;
//Hier mögliche Fehelrquele durch fehlednes Output
    }

    public void run() {
        while (true) {

            try {
                if(!isThreadAlreadyConnected) {
                    isThreadAlreadyConnected = false;
                    socket = new Socket(hostname, port);
                    OutputStream output = socket.getOutputStream();
                    writer = new PrintWriter(output, true);
                    InputStream input = socket.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(input));
                    System.out.println(Server.ANSI_YELLOW + "Sync Server verbunden" + Server.ANSI_RESET);
                }
                while (socket.isConnected()) {
                    try {
                        response = reader.readLine();
                        System.out.println(response);
                        if(Message.getMessageCategoryFromString(response) == Message.CATEGORY_SERVER_MESSAGE){
                            answerIsPicked = true;
                            System.out.println("Haben eine UserDataSync Nachricht erhalten");
                            server.handleUserStatusSync(response);
                        }
                        answerIsThere = true;
                        while (!answerIsPicked){
                            System.out.println("Nachricht ist da!!!");
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }

                    } catch (IOException ex) {
                        System.out.println(Server.ANSI_PURPLE + "Verbindung getrennt " + ex.getMessage() + Server.ANSI_RESET);
                        break;
                    }
                }
                System.out.println(Server.ANSI_RED + "Verbindung verloren" + Server.ANSI_RESET);

            } catch (UnknownHostException ex) {
            } catch (IOException ex) {
            }
        }
    }
    private MessageClient sendSyncResponseToServer(MessageClient messageClient){
        //if(clientMessage.getType() == Server.)
    return messageClient;//ist kaputt
    }

    protected MessageSync requestSynchronization(MessageSync messageSync){
        MessageSync answer = null;
            System.out.println("=========== Setzte answerIsThere -> false ===========");
            answerIsThere = false;
            writer.println(messageSync.toString());
            System.out.println("====== In der Schliefe drinnen ===========");
            while(!answerIsThere){
                //Wartet, bis eine Antwort eintrifft, hier muss man das Timeout reinbauen
                System.out.println("warte");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            System.out.println("====== Aus der Schliefe draußen ===========");
            answerIsPicked = true;
            answer = MessageSync.toObject(response);
        System.out.println("toObject was gemacht");
        return answer;
    }

    // Senden einer ClientMessage zum anderen Server
    protected void sendMessageToOtherServer(MessageClient messageClient) {
        System.out.println(Server.ANSI_GREEN+"SENDEN: Message wird gesendet"+Server.ANSI_RESET);
        try {
            if (writer != null) {
            writer.println(messageClient.toString());
        }
        }catch (Exception e){
            System.out.println(Server.ANSI_RED+"Gab beim Senden der Message einen ERROR im ServerConnectorThread"+Server.ANSI_RESET);
        }
    }

    protected void sendUserActivity(MessageUserActivity messageUserActivity) {
        System.out.println(Server.ANSI_GREEN+"SENDEN: Useraktivität wird gesendet"+Server.ANSI_RESET);
        try {
            if (writer != null) {
                writer.println(messageUserActivity.toString());
            }
        } catch (Exception e) {
            System.out.println(Server.ANSI_RED+"Gab beim Senden der UserActivity einen ERROR im ServerConnectorThread"+Server.ANSI_RESET);
        }
    }
    protected void askForUserStatus(){
        System.out.println(Server.ANSI_GREEN+ "SENDEN: Haben UserDaten Angefragt"+Server.ANSI_RESET);
        MessageUserActivity syncUserDataRequest = new MessageUserActivity(2);
        writer.println(syncUserDataRequest.toString());
        System.out.println(Server.ANSI_GREEN+ "SENDEN:Fertig mit der Anfrage"+Server.ANSI_RESET);

    }
}

