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

    public ServerConnectorThread(String hostname, int port, Server server) {
        this.hostname = hostname;
        this.port = port;
        this.server = server;

    }

    public void run() {
        while (true) {

            try {
                socket = new Socket(hostname, port);
                OutputStream output = socket.getOutputStream();
                writer = new PrintWriter(output, true);
                InputStream input = socket.getInputStream();
                reader = new BufferedReader(new InputStreamReader(input));
                System.out.println(Server.ANSI_YELLOW + "Sync Server verbunden" + Server.ANSI_RESET);
                while (socket.isConnected()) {
                    try {
                        answerIsPicked = false;
                        //System.out.println("=================================================");
                        String response =  reader.readLine();
                        fullresponse = response;
                        System.out.println(response);
                        while(response != null & !response.contains("*")){
                            //System.out.println("Nachricht noch nicht am Ende");

                            response = reader.readLine();
                            if(response != null & !response.contains("*")) {
                                fullresponse += '\n';
                                fullresponse += response;
                                System.out.println(response);
                            }
                        }
                        //System.out.println("=================================================");
                        //System.out.println("Bin im Receiver " + fullresponse);;
                        answerIsThere = true;
                        System.out.println(Server.ANSI_RED+"answerIsThere = true"+Server.ANSI_RESET);
                        while(!answerIsPicked){

                        }
                        System.out.println("Fertig ist fertig");
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
    private ClientMessage sendSyncResponseToServer(ClientMessage clientMessage){
        //if(clientMessage.getType() == Server.)
    return clientMessage;//ist kaputt
    }

    protected ClientMessage requestSynchronization(ClientMessage clientMessage){
        ClientMessage answer = null;
            System.out.println("=========== Setzte answerIsThere -> false ===========");
            answerIsThere = false;
            writer.println(clientMessage.toString());
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
            answer = ClientMessage.toObject(fullresponse);
        return answer;
    }

    // Senden einer ClientMessage zum anderen Server
    protected void sendMessageToOtherServer(ClientMessage clientMessage) {
        try {
            if (writer != null) {
            writer.println(clientMessage.toString());
        }
        }catch (Exception e){
            System.out.println(Server.ANSI_RED+"Gab beim Senden der Message einen ERROR im ServerConnectorThread"+Server.ANSI_RESET);
        }
    }

    protected void sendUserActivity(ServerMessage serverMessage) {
        try {
            if (writer != null) {
                writer.println(serverMessage.toString());
            }
        } catch (Exception e) {
            System.out.println(Server.ANSI_RED+"Gab beim Senden der UserActivity einen ERROR im ServerConnectorThread"+Server.ANSI_RESET);
        }
    }
}

