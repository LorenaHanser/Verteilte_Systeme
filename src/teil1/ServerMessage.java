package teil1;

public class ServerMessage extends Message {
    // int userId durch Vererbung
    private int serverId;
    private int status;

    // Konstruktor
    public ServerMessage(int userId, int serverId, int status) {
        this.setUserId(userId);
        this.setServerId(serverId);
        this.setStatus(status);
    }

    // get()- und set()-Methoden
    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = -serverId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    // Methoden
    @Override
    public String toString() {
        return this.getUserId() + getSplitSymbol() + this.getServerId() + getSplitSymbol() + this.getStatus();
    }

    public static ServerMessage toObject(String string) {
        String[] attributes = string.split(Message.getSplitSymbol(), 3);

        int userId = Integer.parseInt(attributes[0]);
        int serverId = Integer.parseInt(attributes[1]);
        int status = Integer.parseInt(attributes[2]);

        return new ServerMessage(userId, serverId, status);
    }
}
