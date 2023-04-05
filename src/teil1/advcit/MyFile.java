package teil1.advcit;

import java.io.*;

//Hilfsklasse, um die Zugriffe auf die bzw. Interaktionen mit den Dateien zu realisieren
public class MyFile {

    String path = "C:\\Users\\Lorena\\Desktop\\Messages\\"; //todo: hier Username anpassen
    String ending = ".txt"; //Dateiendung der Textnachrichten

    //Die save()-Methode erstellt eine neue .txt-Datei mit vom Client eingegebenen Inhalt und gibt den Dateinamen (key) zurück
    public String save(String[] stringArray) throws IOException {
        //Generierung eines eindeutigen Schlüssels mithilfe eines Zufallszahlengenerators
        String key = null;
        while(new File(path, key + ending).exists() || key == null) {
            key = String.valueOf((int) (Math.random()*10000));
        }

        //Hier wird das gesplittete stringArray wieder zu einem String mit Leerzeichen zusammengesetzt, damit der Inhalt ohne "SAVE" gespeichert wird
        String userInput = "";
        for(int i = 1; i < stringArray.length-1; i++) {
            userInput = userInput + stringArray[i] + " ";
        }
        userInput = userInput + stringArray[stringArray.length-1];

        //Hier wird der Filter-Stream PrintWriter erstellt, um mit der write()-Methode auf die Datei zu schreiben
        PrintWriter myWriter = new PrintWriter(new FileWriter(path + key + ending));
        myWriter.println(userInput);
        //myWriter.flush(); --> bei close() wird automatisch flush() gemacht
        myWriter.close();

        System.out.println("SAVE: Die Datei " + key + ending + " wurde erfolgreich gespeichert.\n");

        return "KEY " + key;
    }

    //Die get()-Methode prüft, ob die gesuchte .txt-Datei vorhanden ist und gibt bei erfolgreicher Prüfung den Inhalt zurück, ansonsten wird "FAILED" zurückgegeben
    public String get(String[] stringArray) throws IOException {
        //stringArray[1] entspricht dem Dateinamen
        if(new File(path, stringArray[1] + ending).exists()) {
            //Hier wird der Filter-Stream BufferedReader erstellt, um den Dateiinhalt zu lesen
            String inhalt = new BufferedReader(new FileReader(path + stringArray[1] + ending)).readLine();
            System.out.println("GET: Der Inhalt der Datei " + stringArray[1] + ending + " wird ausgelesen.\n");
            return "OK "+ inhalt;
        } else {
            System.out.println("GET: Die Datei " + stringArray[1] + ending + " wurde nicht gefunden.\n");
            return "FAILED";
        }
    }

}