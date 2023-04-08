package teil1.tutorial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerReciverThread extends Thread{
    private Socket socket;
    private Server2 server;
    private BufferedReader reader;
    public ServerReciverThread(Socket socket, Server2 server){
        this.socket = socket;
        this.server = server;
        try{
            InputStream input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
        } catch (IOException e) {
            System.out.println("Error getting input stream: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(true){
            try {
                String response = reader.readLine();
                System.out.println(response);

            } catch (IOException ex) {
                System.out.println("Error reading from server: " + ex.getMessage());
                ex.printStackTrace();
                break;
            }
        }
    }
}
