package teil1;

import java.io.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class File {

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
    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    private String serverDirectoryName;

    public File(int serverNummer) {
        this.serverDirectoryName = DIRECTORY_NAME + Integer.toString(serverNummer);
        this.getPath();
    }

    // Methode zur Erstellung von Messages-Ordner und Chatfiles
    public void create() {
        try {
            //Ordner erstellen
            if (!new java.io.File(path).exists()) {
                boolean createdDirectory = new java.io.File(path).mkdir();
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
                if (!new java.io.File(path, filename + ENDING).exists()) {
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
    public String read(int ownID, int chatPartnerID) {
        StringBuilder chat = new StringBuilder(ANSI_PURPLE + "Bisheriger Chat:\n" + ANSI_RESET);
        String currentLine;

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path + getFilename(ownID, chatPartnerID) + ENDING));
            while ((currentLine = bufferedReader.readLine()) != null) {
                chat.append(ANSI_BLUE).append(currentLine).append(ANSI_RESET).append("\n");
            }
            bufferedReader.close();
        } catch (IOException e) {
            System.out.println(ANSI_RED + "Fehler beim Lesen der Datei: " + e.getMessage() + ANSI_RESET);
        }
        return chat.toString();
    }

    // Methode, um eine neue Chatnachricht in der .txt Datei zu speichern
    // [06.04.2023 17:01:12] [Daniel]: Nachricht
    public void write(String message, int ownID, int chatPartnerID) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println(message);

        String[] notAllowedColors = {ANSI_BLACK, ANSI_RED, ANSI_GREEN, ANSI_YELLOW, ANSI_BLUE, ANSI_PURPLE, ANSI_CYAN, ANSI_WHITE, "]: null"};
        boolean writingAllowed = true;
        for (String notAllowedString : notAllowedColors) {
            if (message.contains(notAllowedString)) {
                writingAllowed = false;
                break;
            }
        }

        try {
            if (!new java.io.File(path).exists()) {
                this.create();
            }

            if (writingAllowed) {
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path + getFilename(ownID, chatPartnerID) + ENDING, true));
                bufferedWriter.write("[" + TIMESTAMP_FORMAT.format(timestamp) + "] " + message);
                bufferedWriter.newLine();
                bufferedWriter.close();
            }
        } catch (IOException e) {
            System.out.println(ANSI_RED + "Fehler beim Speichern in der Datei: " + e.getMessage() + ANSI_RESET);
        }
    }

    // Methode ermittelt den Pfad zum Speichern der Chatdateien abhängig von Betriebssystem und Nutzername
    public void getPath() {
        String systemUserHome = System.getProperty("user.home");
        String systemOS = System.getProperty("os.name").toLowerCase();
        String desktop = "Desktop";
        String systemSign;

        if (systemOS.contains("windows")) {
            systemSign = "\\";
            path = systemUserHome + systemSign + desktop + systemSign + serverDirectoryName + systemSign;
        } else if (systemOS.contains("mac")) {
            systemSign = "/";
            path = systemUserHome + systemSign + desktop + systemSign + serverDirectoryName + systemSign;
        } else {
            System.out.println(ANSI_RED + "Das Betriebssystem wird leider nicht unterstützt :(\n" + ANSI_PURPLE + "Der Dateipfad zum Home-Verzeichnis kann manuell eingegeben werden." + ANSI_RESET);
        }
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

}