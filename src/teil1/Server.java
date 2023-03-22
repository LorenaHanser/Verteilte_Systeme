package teil1;

import java.io.*;
import java.net.*;

public class Server {

    public static final int PORT = 7777;

    public static void main(String[] args) {


        try{

            while(true) {

                //My Server Socket bindet sich an einen Port des Betriebssystems
                ServerSocket connection = new ServerSocket(PORT);

                //Server Socket wartet bis sich ein Client anmeldet
                Socket serverSocket = connection.accept();

                //Nachricht vom Client aufnehmen
                InputStreamReader serverInput = new InputStreamReader(serverSocket.getInputStream());

                //Server puffert die aufgenommenen Nachrichten des Clients
                BufferedReader serverPuffer = new BufferedReader(serverInput);

                //Server liest die aufgenommenen Nachrichten des Clients und speichert sie in im String nachricht
                String nachricht = serverPuffer.readLine();
                System.out.println("Anforderung des Clients: " + nachricht);

                //Die Anforderung des Clients wird auseinandergenommen und verarbeitet
                String antwort;
                MyFile myFile = new MyFile();
                //Verwendung der split()-Methode zur Zerlegung der Nachricht, um die Key-Wörter ausführen zu können (switch-case)
                String[] stringArray = nachricht.split(" ");
                //Je nach Key-Wort wird eine andere Methode aufgerufen
                if(stringArray.length > 1) {
                    switch (stringArray[0]) {
                        case "SAVE":
                            antwort = myFile.save(stringArray);
                            break;
                        case "GET":
                            antwort = myFile.get(stringArray);
                            break;
                        default:
                            antwort = "Ungültige Eingabe\n";
                            System.out.println(antwort);
                    }
                } else {
                    antwort = "Ungültige Eingabe\n";
                    System.out.println(antwort);
                }

                //Antwort absenden
                OutputStreamWriter serverOut = new OutputStreamWriter(serverSocket.getOutputStream());
                PrintWriter printerWriter = new PrintWriter(serverOut);

                //Die Antwort wird abhängig von der Anforderung des Clients dem PrintWriter übergeben
                printerWriter.print(antwort +  "\n");

                //Mit flush() wird die Antwort über den Ausgabestrom an den Client weitergeleitet
                printerWriter.flush();

                //Die Verbindung wird nach jeder beendeten Anfrage geschlossen, damit der Server Non-Persistent implementiert ist.
                connection.close();

            }

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}