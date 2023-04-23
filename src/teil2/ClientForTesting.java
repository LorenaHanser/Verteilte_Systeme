package teil2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Diese Klasse haben wir für Entwicklungszwecke verwendet.
 * Sie kann alternativ zu {@link Client} ausgeführt werden, um den Port des Servers nicht zufällig zugewiesen zu bekommen, sondern ihn selbst eingeben zu können.
 * <p>
 * Danach erstellt sie ein neues Objekt der Klasse {@link Client} und führt die {@link Client#execute()}-Methode aus.
 */
public class ClientForTesting {

    public static void main(String[] args) {

        try {
            BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
            String hostname = "localhost";
            boolean portFound = false;
            int port = 8988; //Default Port from Server 1
            while (!portFound) {
                System.out.println("Bitte Port eingeben: \n Server 1: 8988 \n Server 2: 8989 \n Server 3: 8990");
                String answer = userIn.readLine();
                port = Integer.parseInt(answer);
                if (port <= 8990 && port >= 8988) {
                    portFound = true;
                } else {
                    System.out.println("Leider ist die Eingabe nicht korrekt");
                }
            }
            Client client = new Client(hostname, port);
            client.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}