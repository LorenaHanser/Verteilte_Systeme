package teil1;

import java.sql.Timestamp;

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

    public MessageSync(int userId, int type, int receiverId, Timestamp timestamp, String[] content) {
        this.setCategory(Message.CATEGORY_SYNC_MESSAGE);
        this.setUserId(userId);
        this.setType(type);
        this.setReceiverId(receiverId);
        this.setTimestamp(timestamp);
        this.setLength(content.length);
        this.setContent(content);
    }

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

    public String getContentAsString() {
        String contentArrayToString = this.getContent()[0];
        for (int i = 1; i < this.getLength(); i++) {
            contentArrayToString += SPLIT_SYMBOL + this.getContent()[i];
        }
        return contentArrayToString;
    }

    @Override
    public String toString() {
        return this.getCategory() + SPLIT_SYMBOL + this.getUserId() + SPLIT_SYMBOL + this.getType() + SPLIT_SYMBOL + this.getReceiverId() + SPLIT_SYMBOL + this.getTimestamp() + SPLIT_SYMBOL + this.getLength() + SPLIT_SYMBOL + this.getContentAsString();
    }

    public static MessageSync toObject(String string) {
        String[] attributes = string.split(SPLIT_SYMBOL, 7); // Anzahl der Attribute
        int category = Integer.parseInt(attributes[0]);
        int userId = Integer.parseInt(attributes[1]);
        int type = Integer.parseInt(attributes[2]);
        int receiverId = Integer.parseInt(attributes[3]);
        Timestamp timestamp = Timestamp.valueOf(attributes[4]);
        int length = Integer.parseInt(attributes[5]);
        String contentAsString = attributes[6];
        String[] content = contentAsString.split(SPLIT_SYMBOL);

        return new MessageSync(userId, type, receiverId, timestamp, content);
    }

}
