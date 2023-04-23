package teil1;

/**
 * Die Klasse erbt von {@link Message} und beinhaltet Methoden zur Möglichkeit für die Server, Benutzeraktivitäten über das Netzwerk zu verschicken.
 */
public class MessageUserActivity extends Message {

    /// int category durch Vererbung
    // int userId durch Vererbung
    private int serverId;
    private int type;
    private int status;
    private int[] userIsOnServer;

    /**
     * Konstruktor
     * @param userId eigene ID
     * @param serverId ID des Servers
     * @param status Status der Nachricht
     */
    public MessageUserActivity(int userId, int serverId, int status) {
        this.setCategory(Message.CATEGORY_SERVER_MESSAGE);
        this.setUserId(userId);
        this.setServerId(serverId);
        this.setType(Message.SERVER_MESSAGE);
        this.setStatus(status);
    }

    /**
     * Konstruktor
     * @param userIsOnServer Array, welcher Client mit welchem Server verbunden ist
     */
    public MessageUserActivity(int[] userIsOnServer) {
        this.setCategory(Message.CATEGORY_SERVER_MESSAGE);
        this.setType(Message.SERVER_SYNC_RESPONSE);
        this.setUserIsOnServer(userIsOnServer);
    }

    /**
     * Konstruktor
     * @param type Art der Benutzeraktivität
     */
    public MessageUserActivity(int type) {
        this.setCategory(Message.CATEGORY_SERVER_MESSAGE);
        this.setType(type);
    }

    /**
     * Konstruktor
     * @param userId eigene ID
     * @param serverId ID des Servers
     * @param type  Art der Benutzeraktivität
     * @param status Status der Nachricht
     */
    public MessageUserActivity(int userId, int serverId, int type, int status) {
        this.setCategory(Message.CATEGORY_SERVER_MESSAGE);
        this.setUserId(userId);
        this.setServerId(serverId);
        this.setType(type);
        this.setStatus(status);
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setUserIsOnServer(int[] userIsOnServer) {
        this.userIsOnServer = userIsOnServer;
    }

    public int[] getUserIsOnServer() {
        return this.userIsOnServer;
    }

    /**
     * Die Methode nimmt das int[]-Array userIsOnServer und setzt die einzelnen Werte nacheinander, mit Semikolon voneinander getrennt, als String zusammen.
     * @return int[]-Array als String
     */
    public String getUserIsOnServerWithSplitSymbols() {
        String answer = String.valueOf(0);
        answer += SPLIT_SYMBOL;
        answer += String.valueOf(userIsOnServer[0]);
        for (int i = 1; i < 3; i++) {
            answer += SPLIT_SYMBOL;
            answer += i;
            answer += SPLIT_SYMBOL;
            answer += String.valueOf(userIsOnServer[i]);
        }
        return answer;
    }

    public int getType() {
        return type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Die Methode wird ausgeführt, bevor eine Nachricht über das Netzwerk verschickt werden soll.
     * Die Attribute der Klasse {@link MessageUserActivity} beinhalten alle Informationen zur Nachricht.
     * Wenn diese Nachricht verschickt werden soll, werden alle Attribute in einer festen Reihenfolge mit ";" getrennt aneinander gesetzt.
     * @return Gibt die Attribute des Objekts, von dem es aufgerufen wird, als String zusammengesetzt zurück
     */
    @Override
    public String toString() {
        if (getType() == 0) {
            return this.getCategory() + SPLIT_SYMBOL + getType() + SPLIT_SYMBOL + this.getUserId() + SPLIT_SYMBOL + this.getServerId() + SPLIT_SYMBOL + this.getStatus();
        } else if (getType() == 1) {
            return this.getCategory() + SPLIT_SYMBOL + getType() + SPLIT_SYMBOL + getUserIsOnServerWithSplitSymbols();
        } else {
            return this.getCategory() + SPLIT_SYMBOL + getType();
        }
    }

    /**
     * Die Methode wird ausgeführt, wenn eine Nachricht von einem anderen Server empfangen wurde.
     * Da vor dem Versenden der Nachricht über das Netzwerk die {@link MessageUserActivity#toString()}-Methode ausgeführt wurde, sind die Attribute in einer festen Reihenfolge.
     * Sie können nun wieder voneinander getrennt und als Attribute eines neuen Objekts der Klasse gespeichert werden.
     * @param string Über das Netzwerk empfangene Nachricht
     * @return Gibt ein Objekt der Klasse {@link MessageUserActivity} zurück
     */
    public static MessageUserActivity toObject(String string) {
        try {
            string = string.replace("*", "");
            string = string.replace("\n", "");
            String[] header = string.split(SPLIT_SYMBOL, 3);

            int Category = Integer.parseInt(header[0]);
            int type = Integer.parseInt(header[1]);
            if (type == 2) {
                return new MessageUserActivity(type);
            } else if (type == Message.SERVER_SYNC_RESPONSE) {
                String[] data = header[2].split(SPLIT_SYMBOL, 6);
                int[] userDataArray = new int[6];
                for (int i = 0; i < userDataArray.length; i++) {
                    userDataArray[i] = Integer.parseInt(data[i]);
                }
                return new MessageUserActivity(userDataArray);
            } else {
                String[] attributes = header[2].split(SPLIT_SYMBOL, 3);
                int userId = Integer.parseInt(attributes[0]);
                int serverId = Integer.parseInt(attributes[1]);
                if (type == Message.SERVER_MESSAGE) {
                    int status = Integer.parseInt(attributes[2]);
                    return new MessageUserActivity(userId, serverId, status);
                } else {
                    System.out.println(Server.ANSI_RED + "MessageUserAcivity konnte nicht ausgewertet werden" + Server.ANSI_RESET);
                    throw new RuntimeException();
                }
            }
        } catch (Exception e) {
            System.out.println(Server.ANSI_RED + "Fehler bei MessageUserActivity.toObject(): " + e.getMessage() + Server.ANSI_RESET);
            return new MessageUserActivity(-100, -100, -100);
        }
    }
}
