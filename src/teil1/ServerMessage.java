package teil1;

public class ServerMessage extends Message {
    // int userId durch Vererbung
    private int serverId;
    private int type;
    private int status;
    private int[] userIsOnServer;

    // Konstruktor
    public ServerMessage(int userId, int serverId,  int status) {
        this.setUserId(userId);
        this.setServerId(serverId);
        this.setStatus(status);
    }

    public ServerMessage(int userId, int serverId, int[] userIsOnServer ) {
        this.setUserId(userId);
        this.setServerId(serverId);
        this.setType(1);
        this.setuserIsOnServer(userIsOnServer);
    }

    public ServerMessage(int userId, int serverId, int type, int status) {
        this.setUserId(userId);
        this.setServerId(serverId);
        this.setType(type);
        this.setStatus(status);
    }

    // get()- und set()-Methoden
    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = -serverId;
    }
    public void setType(int type){
        this.type = type;
    }
    public void setuserIsOnServer(int[] userIsOnServer){
        this.userIsOnServer = userIsOnServer;
    }
    public int[] getUserIsOnServer(){
        return this.userIsOnServer;
    }
    public String getUserIsOnServerWithSplitsymbols(){
        String answer = String.valueOf(0);
        answer += getSplitSymbol();
        answer += String.valueOf(userIsOnServer[0]);
        for (int i = 1; i < 3; i++) {
            answer += getSplitSymbol();
            answer += i;
            answer += getSplitSymbol();
            answer += String.valueOf(userIsOnServer[i]);
        }
        return answer;
    }
    public int getType(){
        return type;
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
        if(getType() == 0) {
            return this.getUserId() + getSplitSymbol() + this.getServerId() + getSplitSymbol() + getType() + getSplitSymbol() + this.getStatus() + "*";
        }else {
            return this.getUserId() + getSplitSymbol() + this.getServerId() + getSplitSymbol() + getType() + getSplitSymbol() + getUserIsOnServerWithSplitsymbols() + "*";
        }
    }

    public static ServerMessage toObject(String string) {
        try{
            string = string.replace("*", "");
            string = string.replace("\n", "");
            String[] attributes = string.split(Message.getSplitSymbol(), 4);


            int userId = Integer.parseInt(attributes[0]);
            int serverId = Integer.parseInt(attributes[1]);
            int type = Integer.parseInt(attributes[2]);
            if(type == 0) {
                int status = Integer.parseInt(attributes[3]);
                return new ServerMessage(userId, serverId, status);
            } else {
                String[] data = attributes[3].split(getSplitSymbol(), 6);
                int[] userDataArray = new int[6];
                for (int i = 0; i < userDataArray.length; i++) {
                    userDataArray[i] = Integer.parseInt(data[i]);
                }
                return new ServerMessage(userId, serverId, userDataArray);
            }
        } catch(Exception e){
            System.out.println("Fehler bei ServerMessage.toObject(): " + e.getMessage());
            return new ServerMessage(-100, -100, -100);
        }
    }

}
