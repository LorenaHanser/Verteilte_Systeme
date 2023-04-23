package teil2;

import java.io.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Die Klasse kümmert sich um alles, was mit dem Lesen, Schreiben und Synchronisieren des Chatverlaufs in die .txt-Datei zu tun hat.
 */
public class FileHandler {

    private final String[] FILENAMES = {"DanielDavid", "DanielLorena", "DavidLorena"};
    private final String ENDING = ".txt"; //Dateiendung der Textnachrichten
    private final String DIRECTORY_NAME = "Messages";
    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");

    private String path;
    private String serverDirectoryName;
    private int serverNumber;
    private Server server;

    /**
     * Konstruktor
     * <p>
     * path wird abhängig vom Betriebssystem automatisch festgelegt
     *
     * @param server       Server, zu dem das neu erstellte Objekt gehört
     * @param serverNumber Nummer des Servers
     */
    public FileHandler(Server server, int serverNumber) {
        this.serverNumber = serverNumber;
        this.serverDirectoryName = DIRECTORY_NAME + serverNumber;
        this.path = this.getPath(serverNumber);
        this.server = server;
    }

    /**
     * Die Methode wird zu Beginn vom {@link Server} ausgeführt, um die Ordner und .txt-Dateien auf dem Rechner zu erstellen.
     * Zuerst wird der Ordner des Servers, z.B. "Messages1" erstellt.
     * In diesem Ordner werden nachfolgend die drei Dateien "DanielDavid.txt", "DanielLorena.txt", "DavidLorena.txt" zum Speichern der entsprechenden Chatverläufe erstellt.
     */
    public void create() {
        try {
            //Ordner erstellen
            if (!new File(path).exists()) {
                boolean createdDirectory = new File(path).mkdir();
                if (createdDirectory) {
                    System.out.println(Server.ANSI_WHITE + "Ordner " + serverDirectoryName + " wurde neu erstellt." + Server.ANSI_RESET);
                } else {
                    System.out.println(Server.ANSI_WHITE + "Ordner " + serverDirectoryName + " konnte nicht erstellt werden." + Server.ANSI_RESET);
                }
            } else {
                System.out.println(Server.ANSI_WHITE + "Ordner " + serverDirectoryName + " existiert bereits." + Server.ANSI_RESET);
            }

            //Dateien erstellen
            for (String filename : FILENAMES) {
                if (!new File(path, filename + ENDING).exists()) {
                    PrintWriter myWriter = new PrintWriter(new FileWriter(path + filename + ENDING));
                    System.out.println(Server.ANSI_WHITE + "Datei " + filename + ENDING + " wurde neu erstellt." + Server.ANSI_RESET);
                    BufferedWriter myBufferedWriter = new BufferedWriter(new FileWriter(path + filename + ENDING, true));
                    myBufferedWriter.write("[" + TIMESTAMP_FORMAT.format((System.currentTimeMillis())) + "] [" + " FILE_HANDLER " + "]: " + "Chatfile zwischen " + filename);
                    myBufferedWriter.newLine();
                    myBufferedWriter.close();
                    myWriter.close();
                } else {
                    System.out.println(Server.ANSI_WHITE + "Datei " + filename + ENDING + " existiert bereits." + Server.ANSI_RESET);
                }
            }
        } catch (IOException e) {
            System.out.println(Server.ANSI_RED + "Fehler bei der Erstellung der Dateien: " + e.getMessage() + Server.ANSI_RESET);
        }
    }

    /**
     * Die Methode dient dazu, eine Chatdatei zu lesen und diese als String zurückzugeben.
     * <p>
     * Sie wird von außerhalb der Klasse aufgerufen.
     * Erst wird der gespeicherte Chatverlauf synchronisiert und sortiert, dann ruft sie die "interne" Methode {@link FileHandler#readWholeChatFile(String, String)} auf.
     *
     * @param ownID         ID des eigenen Clients
     * @param chatPartnerID ID des Chatpartners, mit dem der Client sich in einem Chatraum befindet
     * @return Chatverlauf wird als String zurückgegeben mit dem Zusatz "Bisheriger Chat:\n"
     */
    public String readWholeChatFile(int ownID, int chatPartnerID) {
        this.askForSynchronization(ownID, chatPartnerID);
        this.sortChatMessages(this.path + this.getFilename(ownID, chatPartnerID) + ENDING);
        return Server.ANSI_PURPLE + "Bisheriger Chat:\n" + Server.ANSI_BLUE + this.readWholeChatFile(path, this.getFilename(ownID, chatPartnerID)) + Server.ANSI_RESET;
    }

    /**
     * Die Methode ist zum Lesen einer Datei und wird nur von innerhalb der Klasse aufgerufen.
     *
     * @param path     Pfad zur zu lesenden Datei
     * @param filename Dateiname der zu lesenden Datei
     * @return Chatverlauf wird als String zurückgegeben
     */
    public String readWholeChatFile(String path, String filename) {
        StringBuilder chat = new StringBuilder();
        String currentLine;

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path + filename + ENDING));
            while ((currentLine = bufferedReader.readLine()) != null) {
                chat.append(currentLine).append("\n");
            }
            bufferedReader.close();
        } catch (IOException e) {
            System.out.println(Server.ANSI_RED + "Fehler beim Lesen der Datei: " + e.getMessage() + Server.ANSI_RESET);
        }
        return chat.toString().trim();
    }

    /**
     * Die Methode speichert eine neue Nachricht, welche als Objekt der Klasse {@link MessageClient} mitgegeben wird, in der entsprechenden .txt-Datei.
     * <p>
     * Die Idee ist, dass nur solche Nachrichten gespeichert werden, die keine Farbe haben und nicht die Schlüsselwörter "SHUTDOWN" oder "DISCONNECT" beinhalten.
     * <p>
     * Neue Nachrichten werden im folgenden Format gespeichert: [06.04.2023 17:01:12] [Daniel]: Das ist meine Nachricht
     *
     * @param messageClient Objekt, das alle Informationen zur neuen Datei als Attribute beinhaltet
     */
    public void writeOneNewMessage(MessageClient messageClient) {
        String[] notAllowedColors = {Server.ANSI_BLACK, Server.ANSI_RED, Server.ANSI_GREEN, Server.ANSI_YELLOW, Server.ANSI_BLUE, Server.ANSI_PURPLE, Server.ANSI_CYAN, Server.ANSI_WHITE, Client.SHUTDOWN, Client.DISCONNECT};
        boolean writingAllowed = true;
        for (String notAllowedString : notAllowedColors) {
            if (messageClient.getContent().contains(notAllowedString)) {
                writingAllowed = false;
                break;
            }
        }

        try {
            if (!new File(path).exists()) {
                this.create();
            }

            if (writingAllowed & !messageClient.getContent().isEmpty()) {
                String pathToFile = path + getFilename(messageClient.getUserId(), messageClient.getReceiverId()) + ENDING;
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(pathToFile, true));
                bufferedWriter.write("[" + TIMESTAMP_FORMAT.format(messageClient.getTimestamp()) + "] [" + messageClient.getUserName() + "]: " + messageClient.getContent());
                bufferedWriter.newLine();
                bufferedWriter.close();

                this.sortChatMessages(pathToFile);
            }
        } catch (IOException e) {
            System.out.println(Server.ANSI_RED + "Fehler beim Speichern in der Datei: " + e.getMessage() + Server.ANSI_RESET);
        }
    }

    /**
     * Methode, um alle Textnachrichten der Datei in die andere Datei zu übertragen
     *
     * @param message  Inhalt, der in die Datei geschrieben werden soll
     * @param path     Pfad zur Datei, in die geschrieben werden soll
     * @param filename Dateiname zur Datei, in die geschrieben werden soll
     */
    public void writeWholeChatfile(String message, String filename, String path) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path + filename + ENDING, true));
            bufferedWriter.write(message.trim());
            bufferedWriter.newLine();
            bufferedWriter.close();

        } catch (IOException e) {
            System.out.println(Server.ANSI_RED + "Fehler beim Speichern in der Datei: " + e.getMessage() + Server.ANSI_RESET);
        }
    }

    /**
     * Methode ermittelt den Pfad zum Speichern der Chatdateien abhängig von Betriebssystem und Nutzername
     *
     * @param serverNumber Nummer des Servers
     * @return Gibt den Pfad als String zurück
     */
    public String getPath(int serverNumber) {
        String systemUserHome = System.getProperty("user.home");
        String systemOS = System.getProperty("os.name").toLowerCase();
        String desktop = "Desktop";
        String systemSign;
        String path = "";

        if (systemOS.contains("windows")) {
            systemSign = "\\";
            path = systemUserHome + systemSign + desktop + systemSign + DIRECTORY_NAME + serverNumber + systemSign;
        } else if (systemOS.contains("mac")) {
            systemSign = "/";
            path = systemUserHome + systemSign + desktop + systemSign + DIRECTORY_NAME + serverNumber + systemSign;
        } else {
            System.out.println(Server.ANSI_RED + "Das Betriebssystem wird leider nicht unterstützt :(\n" + Server.ANSI_PURPLE + "Der Dateipfad zum Home-Verzeichnis kann manuell eingegeben werden." + Server.ANSI_RESET);
        }
        return path;
    }

    /**
     * Methode gibt aus zwei UserIDs den richtigen Dateinamen zurück.
     * <p>
     * Achtung: Die Methode funktioniert in dieser Form nur für maximal 3 Clients, da über eine einfache Addition der userIDs der Dateiname errechnet wird.
     * Sollte das System in Zukunft mehr als 3 Clients bedienen können, müsste eine andere Rechnung genutzt werden.
     *
     * @param ownID         eigene ID
     * @param chatPartnerID ID des Chatpartners
     * @return gibt den Dateinamen zurück
     */
    public String getFilename(int ownID, int chatPartnerID) {
        String filename;
        int sum = ownID + chatPartnerID;

        filename = switch (sum) {
            case 1 -> FILENAMES[0];
            case 2 -> FILENAMES[1];
            case 3 -> FILENAMES[2];
            default -> "Error";
        };

        return filename;
    }

    /**
     * Methode, um zwei Dateien über das Netzwerk des verteilten Systems zu synchronisieren.
     * Dabei werden die Dateien und deren Inhalte verglichen und dementsprechend nötige Schritte ausgeführt.
     *
     * @param otherServerFile Anfrage des anderen Servers als Objekt der Klasse {@link MessageSync}
     * @return Antwort der Synchronisationsanfrage wird zurückgegeben, um über das Netzwerk verschickt zu werden.
     */
    public MessageSync synchronize(MessageSync otherServerFile) {
        String[] otherContentArray = otherServerFile.getContent();                                                    // ganzer Inhalt der Datei
        String otherContent = "";
        for (int i = 0; i < otherContentArray.length; i++) {
            otherContent += "\n" + otherContentArray[i];
        }
        Timestamp otherTimestamp = otherServerFile.getTimestamp();                                             // Änderungsdatum der Datei
        long otherLastModified = otherTimestamp.getTime();

        String ownFilename = this.getFilename(otherServerFile.getUserId(), otherServerFile.getReceiverId());
        String ownPath = this.path;
        File ownServerFile = new File(ownPath + ownFilename + ENDING);
        long ownLastModified = ownServerFile.lastModified();
        String ownContent = this.readWholeChatFile(ownPath, ownFilename);
        String[] contentServerArray = ownContent.split("\n");

        MessageSync syncResponse = new MessageSync(otherServerFile.getUserId(), MessageSync.SYNC_RESPONSE, otherServerFile.getReceiverId());

        if (ownContent.equals(otherContent)) {
            System.out.println(Server.ANSI_WHITE + "Die beiden Dateien " + ownFilename + " sind identisch." + Server.ANSI_RESET);
            syncResponse.setType(Server.SYNC_RESPONSE);
            return syncResponse;
        } else {
            if (ownLastModified == otherLastModified) {
                System.out.println(Server.ANSI_WHITE + "Beide Dateien sind gleich neu." + Server.ANSI_RESET);
                syncResponse.setContent(Server.OK);
                syncResponse.setType(Server.SYNC_RESPONSE);
                return syncResponse;
            } else if (ownLastModified > otherLastModified) {
                System.out.println(Server.ANSI_WHITE + "Die eigene Datei ist neuer." + Server.ANSI_RESET);
                syncResponse.setContent(contentServerArray);
                syncResponse.setType(Server.SYNC_RESPONSE);
                System.out.println(Server.ANSI_WHITE + "Die eigene Datei wurde an Partner gesendet!" + Server.ANSI_RESET);

            } else if (otherLastModified > ownLastModified) {
                System.out.println(Server.ANSI_WHITE + "Die andere Datei ist neuer." + Server.ANSI_RESET);
                System.out.println(Server.ANSI_WHITE + "Die eigene Datei wurde gelöscht: " + ownServerFile.delete()+ Server.ANSI_RESET ) ;
                this.writeWholeChatfile(otherContent, ownFilename, ownPath);
                System.out.println(Server.ANSI_WHITE + "Die eigene Datei wurde ordentlich beschrieben!" + Server.ANSI_RESET);
                syncResponse.setContent(Server.OK);
                syncResponse.setType(Server.SYNC_RESPONSE);
                return syncResponse;
            }
        }

        return syncResponse;
    }

    /**
     * Die Methode sortiert alle Nachrichten einer .txt-Datei nach ihren Zeitstempeln.
     * Dazu werden alle Nachrichten der Datei ausgelesen und ihr Zeitstempel extrahiert.
     * Dementsprechend werden die Nachrichten sortiert und wieder in die Datei zurückgeschrieben.
     *
     * @param pathToFile Pfad zur Datei, die sortiert werden soll
     */
    public void sortChatMessages(String pathToFile) {
        try {
            List<String> lines = new ArrayList<>();

            BufferedReader reader = new BufferedReader(new FileReader(pathToFile));
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            reader.close();

            Collections.sort(lines, new Comparator<String>() {
                public int compare(String line1, String line2) {
                    int returnInt = 22;
                    try {
                        // Annahme: Der Timestamp befindet sich am Anfang jeder Zeile
                        Timestamp timestampObject1 = new Timestamp((TIMESTAMP_FORMAT.parse(line1.substring(1).split("]")[0])).getTime());
                        Timestamp timestampObject2 = new Timestamp((TIMESTAMP_FORMAT.parse(line2.substring(1).split("]")[0])).getTime());
                        return timestampObject1.compareTo(timestampObject2);
                    } catch (ParseException e) {
                        System.out.println(Server.ANSI_RED + "Fehler beim Sortieren der Nachrichten: " + e.getMessage() + Server.ANSI_RESET);
                    }
                    return returnInt;
                }
            });

            BufferedWriter writer = new BufferedWriter(new FileWriter(pathToFile));
            for (String sortedLine : lines) {
                writer.write(sortedLine.trim());
                writer.newLine();
            }
            writer.close();

            System.out.println(Server.ANSI_WHITE + "Die Textdatei wurde erfolgreich nach dem Timestamp sortiert." + Server.ANSI_RESET);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param ownID         eigene ID
     * @param chatPartnerID ID des Chatpartners
     */
    private void askForSynchronization(int ownID, int chatPartnerID) {

        String ownPath = this.path;
        String ownFilename = this.getFilename(ownID, chatPartnerID);

        String contentServer1 = readWholeChatFile(ownPath, ownFilename);
        String[] contentServer1Array = contentServer1.split("\n");

        File ownServerFile = new File(ownPath + ownFilename + ENDING);

        long ownLastModified = ownServerFile.lastModified();
        Timestamp ownTimestamp = new Timestamp(ownLastModified);

        try {
            MessageSync triggerSync = new MessageSync(ownID, MessageSync.SYNC_REQUEST, chatPartnerID, ownTimestamp, contentServer1Array);

            MessageSync response = server.requestSynchronization(triggerSync);

            // Auswerten der Antwort
            if (Arrays.equals(response.getContent(), Server.OK)) {
                System.out.println(Server.ANSI_WHITE + "Synchronization war nicht nötig! Alles ist gut gelaufen." + Server.ANSI_RESET);
            } else {
                String synchronizedFileContentAsString = response.getContentAsString().replaceAll(";", "\n");
                System.out.println(Server.ANSI_WHITE + "Die andere Datei wurde gelöscht: " + ownServerFile.delete() + Server.ANSI_RESET);
                this.writeWholeChatfile(synchronizedFileContentAsString, this.getFilename(response.getUserId(), response.getReceiverId()), this.path);
                System.out.println(Server.ANSI_WHITE + "Synchronization der Dateien war nötig! Datei wurde neu beschrieben." + Server.ANSI_RESET);
            }
        } catch (Exception e) {
            System.out.println(Server.ANSI_RED + "Anderer Server ist nicht online" + Server.ANSI_RESET);
        }
    }

    protected void askForSynchronization(int ownID, int chatPartnerID, ServerConnectorThread syncThread) {

        String ownPath = this.path;
        String ownFilename = this.getFilename(ownID, chatPartnerID);

        String contentServer1 = readWholeChatFile(ownPath, ownFilename);
        String[] contentServer1Array = contentServer1.split("\n");

        File ownServerFile = new File(ownPath + ownFilename + ENDING);

        long ownLastModified = ownServerFile.lastModified();
        Timestamp ownTimestamp = new Timestamp(ownLastModified);

        // trigger receiver to synchronize with synchronize
        try {
            MessageSync triggerSync = new MessageSync(ownID, MessageSync.SYNC_REQUEST, chatPartnerID, ownTimestamp, contentServer1Array);

            MessageSync response = syncThread.requestSynchronization(triggerSync); //hier bekommt man die antwort des anderen Servers

            // Auswerten der Antwort
            if (Arrays.equals(response.getContent(), Server.OK)) {
                System.out.println(Server.ANSI_WHITE + "Sync war nicht nötig! Alles ist gut gelaufen." + Server.ANSI_RESET);
            } else {
                String synchronizedFileContentAsString = response.getContentAsString().replaceAll(";", "\n");
                System.out.println(Server.ANSI_WHITE + "Die andere Datei wurde gelöscht: " + ownServerFile.delete() + Server.ANSI_RESET);
                this.writeWholeChatfile(synchronizedFileContentAsString, this.getFilename(response.getUserId(), response.getReceiverId()), this.path);
                System.out.println(Server.ANSI_WHITE + "Sync war nötig! Datei wurde neu beschrieben." + Server.ANSI_RESET);
            }
        } catch (Exception e) {
            System.out.println(Server.ANSI_RED + "Anderer Server ist nicht online" + Server.ANSI_RESET);
            e.printStackTrace();
        }
    }

}