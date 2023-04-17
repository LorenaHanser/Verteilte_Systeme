package teil1;

import java.sql.Timestamp;

public class ClientMessage extends Message {

    // int userId durch Vererbung
    private int receiverId;
    private Timestamp timestamp;
    private int type;
    private String content;

    // Konstruktor
    public ClientMessage(int userId, int receiverId, Timestamp timestamp, int type, String content) {
        this.setUserId(userId);
        this.setReceiverId(receiverId);
        this.setTimestamp(timestamp);
        this.setType(type);
        this.setContent(content);
    }

    public ClientMessage(String clientResponse, int userId, int receiverId) {
        String[] clientResponseSplit = clientResponse.split(";",2);
        this.setUserId(userId);
        this.setReceiverId(receiverId);
        this.setTimestamp(Timestamp.valueOf(clientResponseSplit[0]));
        this.setType(Server.NEW_MESSAGE);
        this.setContent(clientResponseSplit[1]);
    }
    public ClientMessage(int userId, int receiverId,  String content) {
        this.setUserId(userId);
        this.setReceiverId(receiverId);
        this.setType(Server.NEW_MESSAGE_WITHOUT_TIMESTAMP);
        this.setContent(content);
    }

    // get()- und set()-Methoden
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // Methoden
    @Override
    public String toString() {
        return this.getUserId() + this.getSplitSymbol() + this.getReceiverId() + this.getSplitSymbol() + this.getTimestamp() + this.getSplitSymbol() + this.getType() + this.getSplitSymbol() + this.getContent();
    }

    public static ClientMessage toObject(String string) {
        String[] attributes = string.split(Message.getSplitSymbol(), 5);
        System.out.println("Das steht in der Nachricht, die als ClientMessage ausgegeben werden");
        for (int i = 0; i < attributes.length; i++) {
            System.out.println(attributes[i]);
        }
        int userId = Integer.parseInt(attributes[0]);
        int receiverId = Integer.parseInt(attributes[1]);
        Timestamp timestamp = Timestamp.valueOf(attributes[2]);
        int type = Integer.parseInt(attributes[3]);
        String content = attributes[4];

        return new ClientMessage(userId, receiverId, timestamp, type, content);
    }

    public String getUserName() {
        return Server.USER_NAME_REGISTER[this.getUserId()];
    }

    public String getMessage() {
        return "[" + this.getUserName() + "]: " + this.getContent();
    }
}
