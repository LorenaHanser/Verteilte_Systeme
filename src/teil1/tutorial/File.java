package teil1.tutorial;

import java.io.*;
import java.nio.Buffer;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class File {
    private final String path = "C:\\Users\\I551726\\Desktop\\Messages\\"; //todo: hier Username anpassen
    private final String ending = ".txt"; //Dateiendung der Textnachrichten
    private static final SimpleDateFormat timestampformat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    // Methode zur Erstellung von statischen Chatfiles
    public void create() {
        String[] filenames = {"DanielLorena", "DavidDaniel", "DavidLorena"};

        try {
            for (String filename : filenames) {
                if(! new java.io.File(path, filename + ending).exists()) {
                    PrintWriter myWriter = new PrintWriter(new FileWriter(path + filename + ending));
                    System.out.println("Datei " + filename + ending + " wurde erstellt.");
                    myWriter.close();
                } else {
                    System.out.println("Datei " + filename + ending + " existiert bereits.");
                }
            }
        } catch (IOException e) {
            System.out.println("Fehler bei der Erstellung der Dateien: " + e.getMessage());
        }
    }

    // Methode, um eine Chatdatei zu lesen und in der Konsole anzeigen zu lassen
    public String read(int ownID, int chatPartnerID){
        String chat = "Bisheriger Chat:\n";
        String currentLine;

        try{
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path + "DanielLorena" + ending)); // todo: filename dynamisch
            while((currentLine = bufferedReader.readLine()) != null){
                chat = chat + currentLine + "\n";
            }
            bufferedReader.close();
        } catch(IOException e){
            System.out.println("Fehler beim Lesen der Datei: " + e.getMessage());
        }
        return chat;
    }

    // Methode, um eine neue Chatnachricht in der .txt Datei zu speichern
    // [06.04.2023 17:01:12] [Daniel]: Nachricht
    public void write(String message, int ownID, int chatPartnerID) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println(message);

        try {
            if(! message.equals("New user connected: " ) && ! message.equals("Bisheriger Chat:\n")){
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path + "DanielLorena" + ending, true)); // todo: filename dynamisch
                bufferedWriter.write("[" + timestampformat.format(timestamp) + "] " + message);
                bufferedWriter.newLine();
                bufferedWriter.close();
            }
        } catch(IOException e){
            System.out.println("Fehler beim Speichern in der Datei: " + e. getMessage());
        }
    }
}