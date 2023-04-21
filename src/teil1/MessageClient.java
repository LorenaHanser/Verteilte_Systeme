package teil1;

import java.sql.Timestamp;

public class MessageClient extends Message {

    // int category durch Vererbung
    // int userId durch Vererbung
    private int receiverId;
    private Timestamp timestamp;
    private String content;

    // Konstruktor
    public MessageClient(int userId, int receiverId, Timestamp timestamp, String content) {
        this.setCategory(Message.CATEGORY_CLIENT_MESSAGE);
        this.setUserId(userId);
        this.setReceiverId(receiverId);
        this.setTimestamp(timestamp);
        this.setContent(content);
    }

    public MessageClient(int userId, int receiverId, String content) {
        this.setCategory(Message.CATEGORY_CLIENT_MESSAGE);
        this.setUserId(userId);
        this.setReceiverId(receiverId);
        this.setContent(content);
    }

    public MessageClient(String clientResponse, int userId, int receiverId) {
        this.setCategory(Message.CATEGORY_CLIENT_MESSAGE);
        String[] clientResponseSplit = clientResponse.split(SPLIT_SYMBOL, 2);
        this.setUserId(userId);
        this.setReceiverId(receiverId);
        this.setTimestamp(Timestamp.valueOf(clientResponseSplit[0]));
        this.setContent(clientResponseSplit[1]);
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        String filterdContent = content;
        if(content != null){
            filterdContent = content.replace("*", "");
        }
        this.content = filterdContent;
    }

    public String getUserName() {
        return Server.USER_NAME_REGISTER[this.getUserId()];
    }

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

    // Methoden
    @Override
    public String toString() {
        return this.getCategory() + SPLIT_SYMBOL + this.getUserId() + SPLIT_SYMBOL + this.getReceiverId() + SPLIT_SYMBOL + this.getTimestamp() + SPLIT_SYMBOL + this.getContent() + "\n*";
    }

    public static MessageClient toObject(String string) {
        string = string.replace("*", "");
        String[] attributes = string.split(Message.SPLIT_SYMBOL, 5);

        int userId = Integer.parseInt(attributes[0]);
        int receiverId = Integer.parseInt(attributes[1]);
        Timestamp timestamp = Timestamp.valueOf(attributes[2]);
        int type = Integer.parseInt(attributes[3]);
        String content = attributes[4];

        return new MessageClient(userId, receiverId, timestamp, content);
    }

}
