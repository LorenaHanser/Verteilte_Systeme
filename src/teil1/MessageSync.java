package teil1;

import java.sql.Timestamp;

/**
 * Die Klasse erbt von {@link Message} und beinhaltet Methoden zur Verarbeitung von Nachrichten zur Synchronisation von zwei Servern.
 */
public class MessageSync extends Message {

    public static final int SYNC_REQUEST = 0;
    public static final int SYNC_RESPONSE = 1;
    public static final int SYNC_CONFIRMATION = 2;

    // int category durch Vererbung
    // int userId durch Vererbung
    private int type;
    private int receiverId;
    private Timestamp timestamp;
    private int length;
    private String[] content;

    /**
     * Konstruktor
     * @param userId eigene ID
     * @param type Typ der Nachricht
     * @param receiverId ID des Chatpartners
     * @param timestamp Zeitstempel des Änderungsdatums der Datei
     * @param content Inhalt der Datei
     */
    public MessageSync(int userId, int type, int receiverId, Timestamp timestamp, String[] content) {
        this.setCategory(Message.CATEGORY_SYNC_MESSAGE);
        this.setUserId(userId);
        this.setType(type);
        this.setReceiverId(receiverId);
        this.setTimestamp(timestamp);
        this.setLength(content.length);
        this.setContent(content);
    }

    /**
     * Konstruktor
     * @param userId eigene ID
     * @param type Typ der Nachricht
     * @param receiverId ID des Chatpartners
     * @param content Inhalt der Datei
     */
    public MessageSync(int userId, int type, int receiverId, String[] content) {
        this.setCategory(Message.CATEGORY_SYNC_MESSAGE);
        this.setUserId(userId);
        this.setType(type);
        this.setReceiverId(receiverId);
        this.setLength(content.length);
        this.setContent(content);
    }

    /**
     * Konstruktor
     * @param userId eigene ID
     * @param type Typ der Nachricht
     * @param receiverId ID des Chatpartners
     */
    public MessageSync(int userId, int type, int receiverId) {
        this.setCategory(Message.CATEGORY_SYNC_MESSAGE);
        this.setUserId(userId);
        this.setType(type);
        this.setReceiverId(receiverId);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String[] getContent() {
        return content;
    }

    public void setContent(String[] content) {
        this.content = content;
    }

    /**
     * Der Inhalt der .txt-Datei wird als String-Array gespeichert.
     * Um diesen als String zurückzugeben, werden die Stellen des Arrays nacheinander, mit Semikolon voneinander getrennt, aneinander gehängt.
     * @return Inhalt als String und nicht als String-Array
     */
    public String getContentAsString() {
        String contentArrayToString = this.getContent()[0];
        for (int i = 1; i < this.getContent().length; i++) {
            contentArrayToString += SPLIT_SYMBOL + this.getContent()[i];
        }
        return contentArrayToString;
    }

    /**
     * Die Methode wird ausgeführt, bevor eine Nachricht über das Netzwerk verschickt werden soll.
     * Die Attribute der Klasse {@link MessageSync} beinhalten alle Informationen zur Nachricht.
     * Wenn diese Nachricht verschickt werden soll, werden alle Attribute in einer festen Reihenfolge mit ";" getrennt aneinander gesetzt.
     * @return Gibt die Attribute des Objekts, von dem es aufgerufen wird, als String zusammengesetzt zurück
     */
    @Override
    public String toString() {
        return this.getCategory() + SPLIT_SYMBOL + this.getUserId() + SPLIT_SYMBOL + this.getType() + SPLIT_SYMBOL + this.getReceiverId() + SPLIT_SYMBOL + this.getTimestamp() + SPLIT_SYMBOL + this.getLength() + SPLIT_SYMBOL + this.getContentAsString();
    }

    /**
     * Die Methode wird ausgeführt, wenn eine Nachricht von einem anderen Server empfangen wurde.
     * Da vor dem Versenden der Nachricht über das Netzwerk die {@link MessageSync#toString()}-Methode ausgeführt wurde, sind die Attribute in einer festen Reihenfolge.
     * Sie können nun wieder voneinander getrennt und als Attribute eines neuen Objekts der Klasse gespeichert werden.
     * @param string Über das Netzwerk empfangene Nachricht
     * @return Gibt ein Objekt der Klasse {@link MessageSync} zurück
     */
    public static MessageSync toObject(String string) {
        String[] attributes = string.split(SPLIT_SYMBOL, 7); // Anzahl der Attribute
        int userId = Integer.parseInt(attributes[1]);
        int type = Integer.parseInt(attributes[2]);
        int receiverId = Integer.parseInt(attributes[3]);
        if (type == SYNC_REQUEST) {
            Timestamp timestamp = Timestamp.valueOf(attributes[4]);
            String contentAsString = attributes[6];
            String[] content = contentAsString.split(SPLIT_SYMBOL);
            return new MessageSync(userId, type, receiverId, timestamp, content);
        } else {
            String contentAsString = attributes[6];
            String[] content = contentAsString.split(SPLIT_SYMBOL);
            return new MessageSync(userId, type, receiverId, content);
        }
    }

}