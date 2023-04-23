package teil1;

/**
 * MCSHandler ist dafür zuständig zu überprüfen, ob der Server arbeiten darf, also ein Minimum an Servern erreichbar sind.
 * Die Klasse ist für max. 2 Server ausgelegt
 */
public class MCSHandler {
    private boolean isServer1Online;
    private boolean isServer2Online;


    public MCSHandler() {
    }

    /**
     * hierbei wird die Variable, welche den Status des Servers speichert, auf online gesetzt.
     *
     * @param ThreadNummer wird dafür verwendet um die ConnectorThreads und damit die Server zu unterscheiden.
     */
    public void setServerOnline(int ThreadNummer) {

        if (ThreadNummer == 1) {
            isServer1Online = true;
        } else if (ThreadNummer == 2) {
            isServer2Online = true;
        } else {
            System.out.println(Server.ANSI_RED + "Es gibt in der MCSHanlderklasse ein Problem" + Server.ANSI_RESET);
        }

    }

    /**
     * hierbei wird die Variable, welche den Status des Servers speichert, auf offline gesetzt.
     *
     * @param ThreadNummer wird dafür verwendet um die Connectorthreads und damit die Server zu unterscheiden.
     */
    public void setServerOffline(int ThreadNummer) {
        if (ThreadNummer == 1) {
            isServer1Online = false;

        } else if (ThreadNummer == 2) {
            isServer2Online = false;

        } else {
            System.out.println(Server.ANSI_RED + "Es gibt in der MCSHanlderklasse ein Problem" + Server.ANSI_RESET);
        }
    }

    /**
     * Die Methode prüft, ob der Server arbeiten darf, anhand der Anzahl der verfügbaren Server
     *
     * @return ein boolean true bedeutet, dass Server blockiert ist und auf weitere Server warten muss
     */
    public boolean isServerBlocked() {
        boolean answer = true;
        if (isServer1Online | isServer2Online) {
            answer = false;
        }
        return answer;
    }

    /**
     * Gibt zurück, ob der Server, zudem sich ConnectorThread1 verbinden soll, online ist.
     * Die Servernummer kann dann über den Port, welchen der ConnectorThread mitgegeben bekommen hat identifiziert werden.
     *
     * @return boolean true -> Server von ConnectorThread1 ist online
     */
    public boolean isServer1Online() {
        return isServer1Online;
    }

    /**
     * Gibt zurück, ob der Server, zudem sich ConnectorThread2 verbinden soll, online ist.
     * Die Servernummer kann dann über den Port, welchen der ConnectorThread mitgegeben bekommen hat identifiziert werden.
     *
     * @return boolean true -> Server von ConnectorThread2 ist online
     */
    public boolean isServer2Online() {
        return isServer2Online;
    }

}
