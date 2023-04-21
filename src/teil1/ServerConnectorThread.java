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
    private MCSHandler mcsHandler;
    private int threadNumber;

    private String response;
    private String fullresponse;
    private boolean answerIsThere;
    private boolean answerIsPicked;
    private boolean isThreadAlreadyConnected;


    public ServerConnectorThread(String hostname, int port, Server server, MCSHandler mcsHandler, int threadNumber) {
        this.hostname = hostname;
        this.port = port;
        this.server = server;
        this.mcsHandler = mcsHandler;
        this.threadNumber = threadNumber;
        this.isThreadAlreadyConnected = false;
    }

    public ServerConnectorThread(Socket socket, PrintWriter writer, BufferedReader reader, Server server, MCSHandler mcsHandler, int threadNumber) {
        this.socket = socket;
        this.writer = writer;
        this.reader = reader;
        this.server = server;
        this.mcsHandler = mcsHandler;
        this.threadNumber = threadNumber;
        this.isThreadAlreadyConnected = true;
//Hier mögliche Fehelrquele durch fehlednes Output
    }

    public void run() {
        while (true) {

            try {
                if (!isThreadAlreadyConnected) {
                    isThreadAlreadyConnected = false;
                    socket = new Socket(hostname, port);
                    OutputStream output = socket.getOutputStream();
                    writer = new PrintWriter(output, true);
                    InputStream input = socket.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(input));
                    System.out.println(Server.ANSI_YELLOW + "Sync Server verbunden" + Server.ANSI_RESET);
                }
                mcsHandler.setServerOnline(threadNumber);
                while (socket.isConnected()) {
                    try {
                        answerIsPicked = false;
                        //System.out.println("=================================================");
                        String response = reader.readLine();
                        fullresponse = response;
                        System.out.println(response);
                        while (response != null & !response.contains("*")) {
                            //System.out.println("Nachricht noch nicht am Ende");

                            response = reader.readLine();
                            if (response != null & !response.contains("*")) {
                                fullresponse += '\n';
                                fullresponse += response;
                                System.out.println(response);
                            }
                        }
                        //System.out.println("=================================================");
                        //System.out.println("Bin im Receiver " + fullresponse);;
                        answerIsThere = true;
                        System.out.println(Server.ANSI_RED + "answerIsThere = true" + Server.ANSI_RESET);
                        while (!answerIsPicked) {

                        }
                        System.out.println("Fertig ist fertig");
                    } catch (IOException ex) {
                        System.out.println(Server.ANSI_PURPLE + "Verbindung getrennt " + ex.getMessage() + Server.ANSI_RESET);
                        break;
                    }
                }
                System.out.println(Server.ANSI_RED + "Verbindung verloren" + Server.ANSI_RESET);
                mcsHandler.setServerOffline(threadNumber);

            } catch (UnknownHostException ex) {
            } catch (IOException ex) {
            }
            mcsHandler.setServerOffline(threadNumber);
        }
    }

    protected ClientMessage requestSynchronization(ClientMessage clientMessage) {
        ClientMessage answer = null;
        System.out.println("=========== Setzte answerIsThere -> false ===========");
        answerIsThere = false;
        writer.println(clientMessage.toString());
        System.out.println("====== In der Schliefe drinnen ===========");
        while (!answerIsThere) {
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
        } catch (Exception e) {
            System.out.println(Server.ANSI_RED + "Gab beim Senden der Message einen ERROR im ServerConnectorThread" + Server.ANSI_RESET);
        }
    }

    protected void sendUserActivity(ServerMessage serverMessage) {
        try {
            if (writer != null) {
                writer.println(serverMessage.toString());
            }
        } catch (Exception e) {
            System.out.println(Server.ANSI_RED + "Gab beim Senden der UserActivity einen ERROR im ServerConnectorThread" + Server.ANSI_RESET);
        }
    }
}

