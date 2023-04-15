package teil1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * This is the chat client program.
 * Type 'bye' to terminte the program.
 *
 * @author www.codejava.net
 */
public class ClientForTesting {
    private String hostname;
    private int port;
    private String userName;

    // Konstruktor

    public ClientForTesting(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void execute() {
        try {
            Socket socket = new Socket(hostname, port);

            System.out.println("Connected to the chat server");

            new ClientReadThread(socket, this).start();
            new ClientWriteThread(socket, this).start(); //hier starten wir die Threads

        } catch (UnknownHostException ex) {
            System.out.println("Die Konfiguration stimmt nicht: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("Der Server " + port + " ist nicht online: " + ex.getMessage() + ". Probiere erneut");
            main(new String[1]);
        }

    }

    void setUserName(String userName) {
        this.userName = userName;
    }

    String getUserName() {
        return this.userName;
    }


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
                }else{
                    System.out.println("Leider ist die Eingabe nicht korrekt");
                }
            }
            ClientForTesting client = new ClientForTesting(hostname, port);
            client.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
