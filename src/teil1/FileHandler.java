package teil1;

import java.io.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class FileHandler {

    private String path;
    private final String[] FILENAMES = {"DanielDavid", "DanielLorena", "DavidLorena"};
    private final String ENDING = ".txt"; //Dateiendung der Textnachrichten
    private final String DIRECTORY_NAME = "Messages";
    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");

    private String serverDirectoryName;
    private int serverNumber;
    private Server server;

    public FileHandler(Server server, int serverNumber) {
        this.serverNumber = serverNumber;
        this.serverDirectoryName = DIRECTORY_NAME + serverNumber;
        this.path = this.getPath(serverNumber);
        this.server = server;
    }

    // Methode zur Erstellung von Messages-Ordner und Chatfiles
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
                    myBufferedWriter.write("[" + TIMESTAMP_FORMAT.format((System.currentTimeMillis())) + "] [" + " FILEHANDLER " + "]: " + "Chatfile zwischen " + filename);
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

    // Methode, um eine Chatdatei zu lesen und in der Konsole anzeigen zu lassen
    // zum Aufrufen von außerhalb der Klasse
    public String readWholeChatFile(int ownID, int chatPartnerID) {
        this.askForSynchronization(ownID, chatPartnerID);
        // this.synchronize();
        this.sortChatMessages(this.path + this.getFilename(ownID, chatPartnerID) + ENDING);
        return Server.ANSI_PURPLE + "Bisheriger Chat:\n" + Server.ANSI_BLUE + this.readWholeChatFile(path, this.getFilename(ownID, chatPartnerID)) + Server.ANSI_RESET;
    }

    // zum Aufrufen innerhalb der Klasse File, damit die Methode synchronize() richtig ausgeführt werden kann
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

    // Methode, um eine neue Chatnachricht in der .txt Datei zu speichern
    // [06.04.2023 17:01:12] [Daniel]: Nachricht
    public void writeOneNewMessage(MessageClient messageClient) {
        // todo: die Abfrage brauchen wir eigentlich mit dem neuen Protokoll nicht...
        String[] notAllowedColors = {Server.ANSI_BLACK, Server.ANSI_RED, Server.ANSI_GREEN, Server.ANSI_YELLOW, Server.ANSI_BLUE, Server.ANSI_PURPLE, Server.ANSI_CYAN, Server.ANSI_WHITE, "SHUTDOWN", "DISCONNECT"};
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

    // Methode, um alle Textnachrichten der Datei in die andere Datei zu übertragen
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

    // Methode ermittelt den Pfad zum Speichern der Chatdateien abhängig von Betriebssystem und Nutzername
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

    // Methode gibt aus zwei UserIDs den richtigen Dateinamen zurück
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

    // Methode, um zwei Dateien statisch auf BS-Ebene zu synchronisieren
    public void synchronize() {

        // todo: Methode dynamisch machen
        for (String filename : FILENAMES) {
            String path1 = this.getPath(1);
            String path2 = this.getPath(2);

            String contentServer1 = this.readWholeChatFile(path1, filename);
            String contentServer2 = this.readWholeChatFile(path2, filename);

            File currentFileServer1 = new File(path1 + filename + ENDING);
            File currentFileServer2 = new File(path2 + filename + ENDING);

            if (contentServer1.equals(contentServer2)) {
                System.out.println(Server.ANSI_WHITE + "Die beiden Dateien " + filename + " sind identisch." + Server.ANSI_RESET);
            } else {
                System.out.println(Server.ANSI_WHITE + "Die beiden Dateien " + filename + " sind unterschiedlich." + Server.ANSI_RESET);
                if (currentFileServer1.lastModified() > currentFileServer2.lastModified()) {
                    System.out.println(Server.ANSI_WHITE + "File: " + path1 + filename + " ist neuer als " + path2 + filename + Server.ANSI_RESET);
                    System.out.println(Server.ANSI_WHITE + "Die ältere Datei: " + filename + " wurde gelöscht: " + currentFileServer2.delete() + Server.ANSI_RESET);
                    this.writeWholeChatfile(contentServer1, filename, path2);
                } else if (currentFileServer2.lastModified() > currentFileServer1.lastModified()) {
                    System.out.println(Server.ANSI_WHITE + "File: " + path2 + filename + " ist neuer als " + path1 + filename + Server.ANSI_RESET);
                    System.out.println(Server.ANSI_WHITE + "Die ältere Datei: " + filename + " wurde gelöscht: " + currentFileServer1.delete() + Server.ANSI_RESET);
                    this.writeWholeChatfile(contentServer2, filename, path1);
                } else {
                    System.out.println(Server.ANSI_WHITE + "Beide Dateien haben das gleiche Änderungsdatum!" + Server.ANSI_RESET);
                }
            }
        }
    }

    // Methode, um zwei Dateien wirklich verteilt zu synchronisieren
    public MessageSync synchronize(MessageSync otherServerFile) {
        System.out.println("=======================Bin im Sync vom Server der Angefragt wurde==========================");
        System.out.println(otherServerFile.toString());
        System.out.println("=====================================Ende==================================================");
        String[] otherContentArray = otherServerFile.getContent();                                                    // ganzer Inhalt der Datei
        String otherContent = "";
        for (int i = 0; i < otherContentArray.length ; i++) {
            otherContent += "\n" + otherContentArray[i];
        }
        Timestamp otherTimestamp = otherServerFile.getTimestamp();                                             // Änderungsdatum der Datei
        long otherLastModified = otherTimestamp.getTime();

        String ownFilename = this.getFilename(otherServerFile.getUserId(), otherServerFile.getReceiverId());
        String ownPath = this.path;
        File ownServerFile = new File(ownPath + ownFilename + ENDING);
        long ownLastModified = ownServerFile.lastModified();
        String ownContent = this.readWholeChatFile(ownPath, ownFilename);

        MessageSync syncResponse = new MessageSync(otherServerFile.getUserId(), MessageSync.SYNC_RESPONSE, otherServerFile.getReceiverId());

        if (ownContent.equals(otherContent)) {
            System.out.println(Server.ANSI_WHITE + "Die beiden Dateien " + ownFilename + " sind identisch." + Server.ANSI_RESET);
            syncResponse.setType(Server.SYNC_RESPONSE);
            return syncResponse;
        } else {
            if (ownLastModified == otherLastModified) {
                System.out.println("Beide Dateien sind gleich neu.");
                syncResponse.setContent(Server.OK);
                syncResponse.setType(Server.SYNC_RESPONSE);
                System.out.println("Das ist die syncRespones " + syncResponse.getType() + Arrays.toString(syncResponse.getContent()));
                return syncResponse;
            } else if (ownLastModified < otherLastModified) {
                System.out.println("Die eigene Datei ist neuer.");
                syncResponse.setContent(this.readWholeChatFile(ownPath, ownFilename).split("\n"));
                syncResponse.setType(Server.SYNC_RESPONSE);
                System.out.println("Die eigene Datei wurde an Partner gesendet!");

            } else if (otherLastModified < ownLastModified) {
                System.out.println("Die andere Datei ist neuer.");
                System.out.println(ownServerFile.delete());
                this.writeWholeChatfile(otherContent, ownFilename, ownPath);
                System.out.println("Die eigene Datei wurde ordentlich beschrieben!");
                syncResponse.setContent(Server.OK);
                syncResponse.setType(Server.SYNC_RESPONSE);
                System.out.println("Das ist die syncRespones " + syncResponse.getType() + Arrays.toString(syncResponse.getContent()));
                return syncResponse;
            }
        }
        System.out.println("Das ist die syncRespones " + syncResponse.getType() + Arrays.toString(syncResponse.getContent()));
        return syncResponse;
    }
    /* Hier ist fusch von Daniel S.
    public MessageSync synchronize(MessageSync otherServerFile) {
        System.out.println("=======================Bin im Sync vom Server der Angefragt wurde==========================");
        System.out.println(otherServerFile.toString());
        System.out.println("=====================================Ende==================================================");
        String[] otherContentArray = otherServerFile.getContent();                                                    // ganzer Inhalt der Datei
        String otherContent = "";
        for (int i = 0; i < otherContentArray.length ; i++) {
            otherContent += "\n" + otherContentArray[i];
        }
        Timestamp otherTimestamp = otherServerFile.getTimestamp();                                             // Änderungsdatum der Datei
        long otherLastModified = otherTimestamp.getTime();

        String ownFilename = this.getFilename(otherServerFile.getUserId(), otherServerFile.getReceiverId());
        String ownPath = this.path;
        File ownServerFile = new File(ownPath + ownFilename + ENDING);
        long ownLastModified = ownServerFile.lastModified();
        String ownContent = this.readWholeChatFile(ownPath, ownFilename);

        MessageSync syncResponse = new MessageSync(otherServerFile.getUserId(), MessageSync.SYNC_RESPONSE, otherServerFile.getReceiverId());

        if (ownContent.equals(otherContent)) {
            System.out.println(Server.ANSI_WHITE + "Die beiden Dateien " + ownFilename + " sind identisch." + Server.ANSI_RESET);
            syncResponse.setType(Server.SYNC_RESPONSE);
            return syncResponse;
        } else {
            if (ownLastModified == otherLastModified) {
                System.out.println("Beide Dateien sind gleich neu.");
                syncResponse= new MessageSync(otherServerFile.getUserId(), MessageSync.SYNC_RESPONSE, otherServerFile.getReceiverId(), new Timestamp(System.currentTimeMillis()), readWholeChatFile(ownPath, ownFilename).split("\n"));
                System.out.println("Das ist die syncRespones " + syncResponse.getType() + Arrays.toString(syncResponse.getContent()));
                return syncResponse;
            } else if (ownLastModified < otherLastModified) {
                System.out.println("Die eigene Datei ist neuer.");
                syncResponse= new MessageSync(otherServerFile.getUserId(), MessageSync.SYNC_RESPONSE, otherServerFile.getReceiverId(), new Timestamp(System.currentTimeMillis()), readWholeChatFile(ownPath, ownFilename).split("\n"));
                System.out.println("Die eigene Datei wurde an Partner gesendet!");

            } else {//if (otherLastModified < ownLastModified) {
                System.out.println("Die andere Datei ist neuer.");
                System.out.println(ownServerFile.delete());
                this.writeWholeChatfile(otherContent, ownFilename, ownPath);
                System.out.println("Die eigene Datei wurde ordentlich beschrieben!");
                syncResponse= new MessageSync(otherServerFile.getUserId(), MessageSync.SYNC_RESPONSE, otherServerFile.getReceiverId(), new Timestamp(System.currentTimeMillis()), readWholeChatFile(ownPath, ownFilename).split("\n"));
                System.out.println("Das ist die syncRespones " + syncResponse.getType() + Arrays.toString(syncResponse.getContent()));
                return syncResponse;
            }
        }
        System.out.println("Das ist die syncRespones " + syncResponse.getType() + Arrays.toString(syncResponse.getContent()));
        return syncResponse;
    }*/

    public void sortChatMessages(String pathToFile) {
        try {
            // Liste, um Daten aus Textdatei zu speichern
            List<String> lines = new ArrayList<>();

            // Lesen der Daten aus der Textdatei und Speichern in der Liste
            BufferedReader reader = new BufferedReader(new FileReader(pathToFile));
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            reader.close();

            // Sortieren der Liste nach dem Timestamp
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

            // Zurückschreiben der sortierten Daten in die Textdatei
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

    private void askForSynchronization(int ownID, int chatPartnerID) {

        String ownPath = this.path;
        String ownFilename = this.getFilename(ownID, chatPartnerID);

        String contentServer1 = readWholeChatFile(ownPath, ownFilename);

        File ownServerFile = new File(ownPath + ownFilename + ENDING);

        long ownLastModified = ownServerFile.lastModified();
        Timestamp ownTimestamp = new Timestamp(ownLastModified);

        // trigger receiver to synchronize with synchronize
        try {
            MessageSync triggerSync = new MessageSync(ownID, MessageSync.SYNC_REQUEST, chatPartnerID, new Timestamp(System.currentTimeMillis()), contentServer1.split("\n"));

            // todo @Daniel: server.requestSynchronization zu return MessageSync umbauen
            MessageSync response = server.requestSynchronization(triggerSync); //hier bekommt man die antwort des anderen Servers
            System.out.println("=======================Bin im Sync der den anderen Server anfragt==========================");
            System.out.println(response.toString());
            System.out.println("=====================================Ende==================================================");
            System.out.println("Wir sind jetzt im Filehandler: " + response.toString());
            // Auswerten der Antwort
            if (response.getType() == MessageSync.SYNC_RESPONSE) {
                System.out.println("Sync war nicht nötig! Alles ist gut gelaufen.");
            } else {
                String synchronizedFileContentAsString = response.getContentAsString();
                System.out.println(ownServerFile.delete());
                this.writeWholeChatfile(synchronizedFileContentAsString, this.getFilename(response.getUserId(), response.getReceiverId()), this.path);
                System.out.println("Sync war nötig! Datei wurde neu beschrieben.");
            }
        } catch (Exception e) {
            System.out.println(Server.ANSI_RED + "Anderer Server ist nicht online" + Server.ANSI_RESET);
            e.printStackTrace();
        }
    }
}