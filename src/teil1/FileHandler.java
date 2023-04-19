package teil1;

import java.io.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class FileHandler {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    private String path;
    private final String[] FILENAMES = {"DanielDavid", "DanielLorena", "DavidLorena"};
    private final String ENDING = ".txt"; //Dateiendung der Textnachrichten
    private final String DIRECTORY_NAME = "Messages";
    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");

    private String serverDirectoryName;
    private int serverNumber;

    public FileHandler(int serverNumber) {
        this.serverNumber = serverNumber;
        this.serverDirectoryName = DIRECTORY_NAME + serverNumber;
        this.path = this.getPath(serverNumber);
    }

    // Methode zur Erstellung von Messages-Ordner und Chatfiles
    public void create() {
        try {
            //Ordner erstellen
            if (!new File(path).exists()) {
                boolean createdDirectory = new File(path).mkdir();
                if (createdDirectory) {
                    System.out.println(ANSI_WHITE + "Ordner " + serverDirectoryName + " wurde neu erstellt." + ANSI_RESET);
                } else {
                    System.out.println(ANSI_WHITE + "Ordner " + serverDirectoryName + " konnte nicht erstellt werden." + ANSI_RESET);
                }
            } else {
                System.out.println(ANSI_WHITE + "Ordner " + serverDirectoryName + " existiert bereits." + ANSI_RESET);
            }

            //Dateien erstellen
            for (String filename : FILENAMES) {
                if (!new File(path, filename + ENDING).exists()) {
                    PrintWriter myWriter = new PrintWriter(new FileWriter(path + filename + ENDING));
                    System.out.println(ANSI_WHITE + "Datei " + filename + ENDING + " wurde neu erstellt." + ANSI_RESET);
                    myWriter.close();
                } else {
                    System.out.println(ANSI_WHITE + "Datei " + filename + ENDING + " existiert bereits." + ANSI_RESET);
                }
            }
        } catch (IOException e) {
            System.out.println(ANSI_RED + "Fehler bei der Erstellung der Dateien: " + e.getMessage() + ANSI_RESET);
        }
    }

    // Methode, um eine Chatdatei zu lesen und in der Konsole anzeigen zu lassen
    // zum Aufrufen von außerhalb der Klasse
    public String readWholeChatFile(int ownID, int chatPartnerID) {
        this.synchronize();
        this.sortChatMessages(this.path + this.getFilename(ownID, chatPartnerID) + ENDING);
        return ANSI_PURPLE + "Bisheriger Chat:\n" + ANSI_BLUE + this.readWholeChatFile(path, this.getFilename(ownID, chatPartnerID)) + ANSI_RESET;
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
            System.out.println(ANSI_RED + "Fehler beim Lesen der Datei: " + e.getMessage() + ANSI_RESET);
        }
        return chat.toString();
    }

    // Methode, um eine neue Chatnachricht in der .txt Datei zu speichern
    // [06.04.2023 17:01:12] [Daniel]: Nachricht
    public void writeOneNewMessage(ClientMessage clientMessage) {
        // todo: die Abfrage brauchen wir eigentlich mit dem neuen Protokoll nicht...
        String[] notAllowedColors = {ANSI_BLACK, ANSI_RED, ANSI_GREEN, ANSI_YELLOW, ANSI_BLUE, ANSI_PURPLE, ANSI_CYAN, ANSI_WHITE, "SHUTDOWN", "DISCONNECT"};
        boolean writingAllowed = true;
        for (String notAllowedString : notAllowedColors) {
            if (clientMessage.getContent().contains(notAllowedString)) {
                writingAllowed = false;
                break;
            }
        }

        try {
            if (!new File(path).exists()) {
                this.create();
            }

            if (writingAllowed) {
                String pathToFile = path + getFilename(clientMessage.getUserId(), clientMessage.getReceiverId()) + ENDING;
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(pathToFile, true));
                bufferedWriter.write("[" + TIMESTAMP_FORMAT.format(clientMessage.getTimestamp()) + "] [" + clientMessage.getUserName() + "]: " + clientMessage.getContent());
                bufferedWriter.newLine();
                bufferedWriter.close();

                this.sortChatMessages(pathToFile);
            }
        } catch (IOException e) {
            System.out.println(ANSI_RED + "Fehler beim Speichern in der Datei: " + e.getMessage() + ANSI_RESET);
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
            System.out.println(ANSI_RED + "Fehler beim Speichern in der Datei: " + e.getMessage() + ANSI_RESET);
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
            System.out.println(ANSI_RED + "Das Betriebssystem wird leider nicht unterstützt :(\n" + ANSI_PURPLE + "Der Dateipfad zum Home-Verzeichnis kann manuell eingegeben werden." + ANSI_RESET);
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
                System.out.println(ANSI_WHITE + "Die beiden Dateien " + filename + " sind identisch." + ANSI_RESET);
            } else {
                System.out.println(ANSI_WHITE + "Die beiden Dateien " + filename + " sind unterschiedlich." + ANSI_RESET);
                if (currentFileServer1.lastModified() > currentFileServer2.lastModified()) {
                    System.out.println(ANSI_WHITE + "File: " + path1 + filename + " ist neuer als " + path2 + filename + ANSI_RESET);
                    System.out.println(ANSI_WHITE + "Die ältere Datei: " + filename + " wurde gelöscht: " + currentFileServer2.delete() + ANSI_RESET);
                    writeWholeChatfile(contentServer1, filename, path2);
                } else if (currentFileServer2.lastModified() > currentFileServer1.lastModified()) {
                    System.out.println(ANSI_WHITE + "File: " + path2 + filename + " ist neuer als " + path1 + filename + ANSI_RESET);
                    System.out.println(ANSI_WHITE + "Die ältere Datei: " + filename + " wurde gelöscht: " + currentFileServer1.delete() + ANSI_RESET);
                    writeWholeChatfile(contentServer2, filename, path1);
                } else {
                    System.out.println(ANSI_WHITE + "Beide Dateien haben das gleiche Änderungsdatum!" + ANSI_RESET);
                }
            }
        }
    }

    // Methode, um zwei Dateien wirklich verteilt zu synchronisieren
    public void synchronize(ClientMessage clientMessage) {


        // clientMessage wird erzeugt mit eigen änderungsdatum
        // sendClientMessage

        // also basically synchronize quatsch
        /*

       String message = clientMessage.getContent();

       message.lastModified() <-- Änderungsdatum
       message.contentWholeChatFile <-- kompletter inhalt der datei

        if (clientMessage.contentServer1.equals(contentServer2)) {
                System.out.println(ANSI_WHITE + "Die beiden Dateien " + filename + " sind identisch." + ANSI_RESET);
            } else {
                System.out.println(ANSI_WHITE + "Die beiden Dateien " + filename + " sind unterschiedlich." + ANSI_RESET);
                if (CLIENTMESSAGE.currentFileServer1.lastModified() > currentFileServer2.lastModified()) {
                    System.out.println(ANSI_WHITE + "File: " + path1 + filename + " ist neuer als " + path2 + filename + ANSI_RESET);
                    System.out.println(ANSI_WHITE + "Die ältere Datei: " + filename + " wurde gelöscht: " + currentFileServer2.delete() + ANSI_RESET);
                    writeWholeChatfile(contentServer1, filename, path2);                                               <-- Sende message mit neuerem Content zurück an Server1 und sag ihm das er veraltet ist
                } else if (currentFileServer2.lastModified() > CLIENTMESSAGE.currentFileServer1.lastModified()) {
                    System.out.println(ANSI_WHITE + "File: " + path2 + filename + " ist neuer als " + path1 + filename + ANSI_RESET);
                    System.out.println(ANSI_WHITE + "Die ältere Datei: " + filename + " wurde gelöscht: " + currentFileServer1.delete() + ANSI_RESET);
                    writeWholeChatfile(contentServer2, filename, path1);                                               <--  geschickten content in eigene datei übertragen WRITEWHOLECHATFILE von server2 machen
                } else {
                    System.out.println(ANSI_WHITE + "Beide Dateien haben das gleiche Änderungsdatum!" + ANSI_RESET);
                }
         */

    }

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
                        System.out.println(ANSI_RED + "Fehler beim Sortieren der Nachrichten: " + e.getMessage() + ANSI_RESET);
                    }
                    return returnInt;
                }
            });

            // Zurückschreiben der sortierten Daten in die Textdatei
            BufferedWriter writer = new BufferedWriter(new FileWriter(pathToFile));
            for (String sortedLine : lines) {
                writer.write(sortedLine);
                writer.newLine();
            }
            writer.close();

            System.out.println(ANSI_WHITE + "Die Textdatei wurde erfolgreich nach dem Timestamp sortiert." + ANSI_RESET);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}