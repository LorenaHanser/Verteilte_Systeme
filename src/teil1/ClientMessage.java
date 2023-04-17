package teil1;

import java.sql.Timestamp;

public class ClientMessage extends Message {
    private Timestamp timestamp;
    // int userId durch Vererbung
    private int receiverId;
    private int type;
    private String content;

    // Konstruktor
    public ClientMessage(Timestamp timestamp, int userId, int receiverId, int type, String content) {
        this.setTimestamp(timestamp);
        this.setUserId(userId);
        this.setReceiverId(receiverId);
        this.setType(type);
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
        return this.getTimestamp() + this.getSplitSymbol() + this.getUserId() + this.getSplitSymbol() + this.getReceiverId() + this.getSplitSymbol() + this.getType() + this.getSplitSymbol() + this.getContent();
    }

    public ClientMessage toObject(String string) {
        String[] attributes = string.split(this.getSplitSymbol(), 5);

        Timestamp timestamp = Timestamp.valueOf(attributes[0]);
        int userId = Integer.parseInt(attributes[1]);
        int receiverId = Integer.parseInt(attributes[2]);
        int type = Integer.parseInt(attributes[3]);
        String content = attributes[4];

        return new ClientMessage(timestamp, userId, receiverId, type, content);
    }
}
