package teil2;

import java.io.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class File {
    private String path;
    private final String[] FILENAMES = {"DanielDavid", "DanielLorena", "DavidLorena"};
    private final String ENDING = ".txt"; //Dateiendung der Textnachrichten
    private final String DIRECTORY_NAME = "Messages";
    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    private String serverDirectoryName;

    public File(String serverNummer) {
        this.serverDirectoryName = DIRECTORY_NAME + serverNummer;
        this.getPath();
    }

    // Methode zur Erstellung von Messages-Ordner und Chatfiles
    public void create() {
        try {
            //Ordner erstellen
            if (!new java.io.File(path).exists()) {
                boolean createdDirectory = new java.io.File(path).mkdir();
                if (createdDirectory) {
                    System.out.println("Ordner " + serverDirectoryName + " wurde neu erstellt.");
                } else {
                    System.out.println("Ordner " + serverDirectoryName + " konnte nicht erstellt werden.");
                }
            } else {
                System.out.println("Ordner " + serverDirectoryName + " existiert bereits.");
            }

            //Dateien erstellen
            for (String filename : FILENAMES) {
                if (!new java.io.File(path, filename + ENDING).exists()) {
                    PrintWriter myWriter = new PrintWriter(new FileWriter(path + filename + ENDING));
                    System.out.println("Datei " + filename + ENDING + " wurde neu erstellt.");
                    myWriter.close();
                } else {
                    System.out.println("Datei " + filename + ENDING + " existiert bereits.");
                }
            }
        } catch (IOException e) {
            System.out.println("Fehler bei der Erstellung der Dateien: " + e.getMessage());
        }
    }

    // Methode, um eine Chatdatei zu lesen und in der Konsole anzeigen zu lassen
    public String read(int ownID, int chatPartnerID) {
        StringBuilder chat = new StringBuilder("Bisheriger Chat:\n");
        String currentLine;

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path + getFilename(ownID, chatPartnerID) + ENDING));
            while ((currentLine = bufferedReader.readLine()) != null) {
                chat.append(currentLine).append("\n");
            }
            bufferedReader.close();
        } catch (IOException e) {
            System.out.println("Fehler beim Lesen der Datei: " + e.getMessage());
        }
        return chat.toString();
    }

    // Methode, um eine neue Chatnachricht in der .txt Datei zu speichern
    // [06.04.2023 17:01:12] [Daniel]: Nachricht
    public void write(String message, int ownID, int chatPartnerID) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println(message);

        String[] notAllowedStrings = {"New user connected: ", " has quit.", "Bisheriger Chat:\n", "]: null"};
        boolean writingAllowed = true;
        for (String notAllowedString : notAllowedStrings) {
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
            System.out.println("Fehler beim Speichern in der Datei: " + e.getMessage());
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
            System.out.println("Das Betriebssystem wird leider nicht unterstützt :(");
        }
    }

    // Methode gibt aus zwei UserIDs den richtigen Dateinamen zurück
    public String getFilename(int ownID, int chatPartnerID) {
        System.out.println("Die ChatPartnerID lautet " + chatPartnerID);
        String filename;
        int sum = ownID + chatPartnerID;

        filename = switch (sum) {
            case 1 -> FILENAMES[0];
            case 2 -> FILENAMES[1];
            case 3 -> FILENAMES[2];
            default -> "error";
        };

        return filename;
    }

}