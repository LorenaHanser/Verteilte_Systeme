package teil1;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    public final static String HOST = "localhost";
    public static final int PORT = 7777;

    public static void main(String[] args) {

        try{

            while(true) {

                //Client Socket um den Server zu finden und mit diesem kommunizieren
                Socket clientSocket = new Socket(HOST, PORT);

                //Nachricht von client welcher nach erfolgreicher Verbindung
                BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
                String nachricht = userIn.readLine() + "\n";

                //OutputStream um nachricht an Server zu senden
                OutputStreamWriter clientOut = new OutputStreamWriter(clientSocket.getOutputStream());

                //PrinterWriter um eine Nachricht einzufangen, und entsprechend später zu übermitteln
                PrintWriter printerWriter = new PrintWriter(clientOut);

                printerWriter.print(nachricht);

                //Übermittlung der Nachricht
                printerWriter.flush();

                //Antwort des Servers entgegennehmen
                InputStreamReader clientReader = new InputStreamReader(clientSocket.getInputStream());

                //Der BufferedReader liest die Antwort des Servers
                BufferedReader clientPuffer = new BufferedReader(clientReader);
                String antwort = clientPuffer.readLine();
                System.out.println(antwort + "\n");
            }

        }catch(Exception e){
            System.out.println(e);
        }
    }
}