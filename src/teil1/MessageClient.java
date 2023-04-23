package teil1;

import java.sql.Timestamp;

/**
 * Die Klasse erbt von {@link Message} und beinhaltet Methoden zur Verarbeitung von Nachrichten des Clients.
 */
public class MessageClient extends Message {

    // int category durch Vererbung
    // int userId durch Vererbung
    private int receiverId;
    private Timestamp timestamp;
    private String content;

    /**
     * Konstruktor für neue Nachrichten
     * @param userId eigene ID
     * @param receiverId ID des Chatpartners
     * @param timestamp Zeitstempel
     * @param content Nachricht des Clients von der Konsole ausgelesen
     */
    public MessageClient(int userId, int receiverId, Timestamp timestamp, String content) {
        this.setCategory(Message.CATEGORY_CLIENT_MESSAGE);
        this.setUserId(userId);
        this.setReceiverId(receiverId);
        this.setTimestamp(timestamp);
        this.setContent(content);
    }

    /**
     * Konstruktor für Antworten von Nachrichten des Clients
     * @param clientResponse Antwort des Clients
     * @param userId eigene ID
     * @param receiverId ID des Chatpartners
     */
    public MessageClient(String clientResponse, int userId, int receiverId) {
        this.setCategory(Message.CATEGORY_CLIENT_MESSAGE);
        String[] clientResponseSplit = clientResponse.split(SPLIT_SYMBOL, 2);
        this.setUserId(userId);
        this.setReceiverId(receiverId);
        this.setTimestamp(Timestamp.valueOf(clientResponseSplit[0]));
        this.setContent(clientResponseSplit[1]);
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        String filteredContent = content;
        if(content != null){
            filteredContent = content.replace("*", "");
        }
        this.content = filteredContent;
    }

    public String getUserName() {
        return Server.USER_NAME_REGISTER[this.getUserId()];
    }

    /**
     * Die Methode gibt Nachrichten in der Form zurück, wie sie in der Konsole angezeigt werden sollen.
     * Wenn die Nachricht in einer der Signalfarben ist, wird sie unverändert zurückgegeben.
     * Wenn die Nachricht nicht farbig ist, kommt sie vom anderen Client und wird in dieser Form zurückgegeben: "[Daniel]: Meine Nachricht"
     * @return Gibt ggf. den zusammengesetzten String zur Ausgabe in der Konsole zurück
     */
    public String getMessage() {
        String[] notAllowedColors = {Server.ANSI_BLACK, Server.ANSI_RED, Server.ANSI_GREEN, Server.ANSI_YELLOW, Server.ANSI_BLUE, Server.ANSI_PURPLE, Server.ANSI_WHITE};
        boolean printWithUserName = true;
        for (String notAllowedString : notAllowedColors) {
            if (this.getContent().contains(notAllowedString)) {
                printWithUserName = false;
                break;
            }
        }

        if (!printWithUserName) {
            return this.getContent();
        } else {
            return "[" + this.getUserName() + "]: " + this.getContent();
        }
    }

    /**
     * Die Methode wird ausgeführt, bevor eine Nachricht über das Netzwerk verschickt werden soll.
     * Die Attribute der Klasse {@link MessageClient} beinhalten alle Informationen zur Nachricht.
     * Wenn diese Nachricht verschickt werden soll, werden alle Attribute in einer festen Reihenfolge mit ";" getrennt aneinander gesetzt.
     * @return Gibt die Attribute des Objekts, von dem es aufgerufen wird, als String zusammengesetzt zurück
     */
    @Override
    public String toString() {
        return this.getCategory() + SPLIT_SYMBOL + this.getUserId() + SPLIT_SYMBOL + this.getReceiverId() + SPLIT_SYMBOL + this.getTimestamp() + SPLIT_SYMBOL + this.getContent() + "\n*";
    }

    /**
     * Die Methode wird ausgeführt, wenn eine Nachricht von einem anderen Server empfangen wurde.
     * Da vor dem Versenden der Nachricht über das Netzwerk die {@link MessageClient#toString()}-Methode ausgeführt wurde, sind die Attribute in einer festen Reihenfolge.
     * Sie können nun wieder voneinander getrennt und als Attribute eines neuen Objekts der Klasse gespeichert werden.
     * @param string Über das Netzwerk empfangene Nachricht
     * @return Gibt ein Objekt der Klasse {@link MessageClient} zurück
     */
    public static MessageClient toObject(String string) {
        string = string.replace("*", "");
        String[] attributes = string.split(Message.SPLIT_SYMBOL, 5);

        int userId = Integer.parseInt(attributes[1]);
        int receiverId = Integer.parseInt(attributes[2]);
        Timestamp timestamp = Timestamp.valueOf(attributes[3]);
        String content = attributes[4];

        return new MessageClient(userId, receiverId, timestamp, content);
    }

}
