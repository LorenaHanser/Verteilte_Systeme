package teil1;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Klasse ServerConnectorThread damit die Verbindung zwischen den Servern besteht, um
 * Messages an den anderen Server zu übermitteln. [EINBAHNSTRASSE: SENDEN]
 */
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
    private boolean answerIsThere;
    private boolean answerIsPicked;

    private boolean isServerDown;

    /**
     * Konstruktor
     *
     * @param hostname     hostname ist immer die Adresse des Partnerservers. Nötig um die Verbindung zu Server 2 zu ermöglichen.
     * @param port         port ist immer der Port des Partnerservers. Nötig um die Verbindung zu Server 2 zu ermöglichen.
     * @param server       Attribut um auf Methoden der Server-Klasse ausführen zu können
     * @param mcsHandler
     * @param threadNumber
     */
    public ServerConnectorThread(String hostname, int port, Server server, MCSHandler mcsHandler, int threadNumber) {
        this.hostname = hostname;
        this.port = port;
        this.server = server;
        this.mcsHandler = mcsHandler;
        this.threadNumber = threadNumber;
    }

    /**
     * run-Methode des Threads das bei {{@link ServerConnectorThread #run}} ausgeführt wird.
     * Thread sendet alle Nachrichten nach Kategorie von {{@link Message}} über passende Methoden an den Partnerserver.
     */
    @Override
    public void run() {
        while (true) {

            try {
                socket = new Socket(hostname, port);
                OutputStream output = socket.getOutputStream();
                writer = new PrintWriter(output, true);
                InputStream input = socket.getInputStream();
                reader = new BufferedReader(new InputStreamReader(input));
                System.out.println(Server.ANSI_YELLOW + "Sync Server verbunden" + Server.ANSI_RESET);
                isServerDown = false;
                mcsHandler.setServerOnline(threadNumber);
                while (socket.isConnected()) {
                    try {
                        response = reader.readLine();
                        if (Message.getMessageCategoryFromString(response) == Message.CATEGORY_SERVER_MESSAGE) {
                            answerIsPicked = true;
                            server.handleUserStatusSync(response);
                        }
                        answerIsThere = true;
                        while (!answerIsPicked) {
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
                mcsHandler.setServerOffline(threadNumber);
                server.setUserOffline(threadNumber);

                isServerDown = true;
            } catch (Exception e) {
            }
            mcsHandler.setServerOffline(threadNumber);
        }
    }

    /**
     * Methode zum Anfragen einer Synchronisation der Dateien.
     * Es wurde ein künstliches Delay eingeführt, um die Threads auszubremsen und zu warten bis er seine Antwort erhält.
     * Außerdem wird aus dem erhaltenen MessageSync-Objekt ein String gemacht, damit dieses zu dem ReceiverThread übertragen werden kann mit {link {@link MessageSync#toString()}}.
     * @param messageSync Die Sync-Message mit Aufbau wird an die Methode weitergegeben. {link {@link MessageSync}}
     * @return Die Antwort auf die Anfrage zum Synchronisieren wird hier wieder zurückgegeben. ("OK"; "INHALT DER NEUEREN DATEI")
     */
    protected MessageSync requestSynchronization(MessageSync messageSync) {
        if (isServerDown | writer == null) {
            throw new RuntimeException(Server.ANSI_RED + "Server Verbindung ist verloren gegangen!" + Server.ANSI_RESET);
        } else {
            MessageSync answer;
            answerIsThere = false;
            writer.println(messageSync.toString());
            int i = 0;
            while (!answerIsThere) {
                try {
                    i++;
                    Thread.sleep(1000);
                    if (i == 10) {
                        break;
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            answerIsPicked = true;
            answer = MessageSync.toObject(response);
            return answer;
        }
    }

    /**
     * Methode zum Senden der Chatnachricht an den anderen Server.
     * Außerdem wird aus dem erhaltenen messageClient-Objekt ein String gemacht damit dieses zu dem ReceiverThread übertragen werden kann mit {link {@link MessageClient#toString()}}.
     * @param messageClient Die Client-Message mit Aufbau wird an die Methode weitergegeben. {link {@link MessageClient}}
     */
    protected void sendMessageToOtherServer(MessageClient messageClient) {
        try {
            if (writer != null) {
                writer.println(messageClient.toString());
            }
        } catch (Exception e) {
            System.out.println(Server.ANSI_RED + "Es gab beim Senden der Message einen ERROR im ServerConnectorThread" + Server.ANSI_RESET);
        }
    }

    /**
     * Methode zum Senden einer User-Aktivität an den anderen Server.
     * Außerdem wird aus dem erhaltenen messageUserActivity-Objekt ein String gemacht damit dieses zu dem ReceiverThread übertragen werden kann mit {link {@link MessageUserActivity#toString()}}.
     * @param messageUserActivity Die UserActivity-Message mit Aufbau wird an die Methode weitergegeben. {link {@link MessageUserActivity}}
     */
    protected void sendUserActivity(MessageUserActivity messageUserActivity) {
        try {
            if (writer != null) {
                writer.println(messageUserActivity.toString());
            }
        } catch (Exception e) {
            System.out.println(Server.ANSI_RED + "Es gab beim Senden der UserActivity einen ERROR im ServerConnectorThread" + Server.ANSI_RESET);
        }
    }

    /**
     * Methode fragt den anderen Server, welche Userdaten er besitzt. Damit bringt er in Erfahrung welche User wo angemeldet sind.
     * Außerdem wird aus dem erhaltenen messageUserActivity-Objekt ein String gemacht, damit dieses zu dem ReceiverThread übertragen werden kann mit {link {@link MessageUserActivity#toString()}}
     */
    protected void askForUserStatus() {
        MessageUserActivity syncUserDataRequest = new MessageUserActivity(Message.SERVER_SYNC_REQUEST);
        writer.println(syncUserDataRequest.toString());
    }
}

