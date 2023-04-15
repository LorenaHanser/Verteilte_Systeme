package teil1;

import java.io.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MyFile {

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
    private int serverNumber;

    public MyFile(String serverNumber) {
        this.serverNumber = Integer.parseInt(serverNumber);
        this.serverDirectoryName = DIRECTORY_NAME + serverNumber;
        this.path = this.getPath(Integer.parseInt(serverNumber));
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
        this.synchronize();
    }

    // Methode, um eine Chatdatei zu lesen und in der Konsole anzeigen zu lassen
    // zum Aufrufen von außerhalb der Klasse
    public String readWholeChatFile(int ownID, int chatPartnerID) {
        return ANSI_PURPLE + "Bisheriger Chat:\n" + ANSI_RESET + this.readWholeChatFile(path, this.getFilename(ownID, chatPartnerID));
    }

    // zum Aufrufen innerhalb der Klasse File, damit die Methode synchronize() richtig ausgeführt werden kann
    public String readWholeChatFile(String path, String filename) {
        StringBuilder chat = new StringBuilder();
        String currentLine;

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path + filename + ENDING));
            while ((currentLine = bufferedReader.readLine()) != null) {
   //             chat.append(ANSI_BLUE).append(currentLine).append(ANSI_RESET).append("\n");
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

    // Methode, um alle Textnachrichten der Datei in die andere Datei zu übertragen
    public void write(String message, String filename, String path ) {
        try {
            System.out.println(message);
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

    public void synchronize() {
        // todo: Methode dynamisch machen
        for(int i = 0; i < FILENAMES.length; i++){
            String path1 = this.getPath(1);
            String path2 = this.getPath(2);

            String content1 = "";
            String content2 = "";

            content1 = this.readWholeChatFile(path1, FILENAMES[i]);
            content2 = this.readWholeChatFile(path2, FILENAMES[i]);

            File[] myFiles1 = new File[3];
            File[] myFiles2 = new File[3];
            String genauerPfad = path1 + FILENAMES[i] + ENDING;
            String genauerPfad2 = path2 + FILENAMES[i] + ENDING;

            File myFileObj1 = new File(genauerPfad);
            File myFileObj2 = new File(genauerPfad2);

            myFiles1[i] = myFileObj1;
            myFiles2[i] = myFileObj2;

            if (content1.equals(content2)) {
                System.out.println("Die beiden Dateien " + FILENAMES[i] + " sind identisch.");
            } else {
                System.out.println("Die beiden Dateien " + FILENAMES[i] + " sind unterschiedlich.");
                if (myFiles1[i].lastModified() > myFiles2[i].lastModified()){
                    System.out.println("Kontent 1 :" + content1);
                    System.out.println("Kontent 2 :" + content2);
                    System.out.println("File: " + path1 + FILENAMES[i] + " ist neuer als " + path2 + FILENAMES[i] );
                    myFiles2[i].delete();
                    System.out.println(myFiles2[i] + " wurde gelöscht");
                    write(content1, FILENAMES[i], path2);
                } else if(myFiles2[i].lastModified() > myFiles1[i].lastModified()){
                    System.out.println("Kontent 1 :" + content1);
                    System.out.println("Kontent 2 :" + content2);
                    System.out.println("File: " + path2 + FILENAMES[i] + " ist neuer als "  + path1 + FILENAMES[i] );
                    myFiles1[i].delete();
                    System.out.println(myFiles1[i] + " wurde gelöscht");
                    write(content2, FILENAMES[i], path1);
                } else {
                    System.out.println("Beide Dateien haben das gleiche Änderungsdatum");
                }
            }
        }
    }
}