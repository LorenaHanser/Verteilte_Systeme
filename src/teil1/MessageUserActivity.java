package teil1;

public class MessageUserActivity extends Message {

    /// int category durch Vererbung
    // int userId durch Vererbung
    //korrigiert
    private int serverId;
    private int type;
    private int status;
    private int[] userIsOnServer;

    // Konstruktor
    public MessageUserActivity(int userId, int serverId, int status) {
        this.setCategory(Message.CATEGORY_SERVER_MESSAGE);
        this.setUserId(userId);
        this.setServerId(serverId);
        this.setType(Message.SERVER_MESSAGE);
        this.setStatus(status);
    }

    public MessageUserActivity(int[] userIsOnServer ) {
        this.setCategory(Message.CATEGORY_SERVER_MESSAGE);
        this.setType(Message.SERVER_SYNC_RESPONSE);
        this.setuserIsOnServer(userIsOnServer);
    }
    public MessageUserActivity(int type) {
        this.setCategory(Message.CATEGORY_SERVER_MESSAGE);
        this.setType(type);
    }

    public MessageUserActivity(int userId, int serverId, int type, int status) {
        this.setCategory(Message.CATEGORY_SERVER_MESSAGE);
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
        this.serverId = serverId;
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
            return this.getCategory() + SPLIT_SYMBOL + getType() + SPLIT_SYMBOL + this.getUserId() + SPLIT_SYMBOL + this.getServerId() + SPLIT_SYMBOL +  this.getStatus();
        }else if(getType() == 1){
            return this.getCategory() + SPLIT_SYMBOL + getType() + SPLIT_SYMBOL + getUserIsOnServerWithSplitsymbols();
        }else {
            return this.getCategory() + SPLIT_SYMBOL + getType();
        }
    }

    public static MessageUserActivity toObject(String string) {
        try{
            string = string.replace("*", "");
            string = string.replace("\n", "");
            String[] header = string.split(SPLIT_SYMBOL, 3);
            System.out.println("Im Message User Activity: "+string);

            int Category = Integer.parseInt(header[0]);
            int type = Integer.parseInt(header[1]);
            if(type == 2) {
                return new MessageUserActivity(type);
            }else if(type == Message.SERVER_SYNC_RESPONSE){
                String[] data = header[2].split(SPLIT_SYMBOL, 6);
                int[] userDataArray = new int[6];
                for (int i = 0; i < userDataArray.length; i++) {
                    userDataArray[i] = Integer.parseInt(data[i]);
                }
                return new MessageUserActivity(userDataArray);
            } else {
                String[] attributes = header[2].split(SPLIT_SYMBOL, 3);
                int serverId = Integer.parseInt(attributes[0]);
                int userId = Integer.parseInt(attributes[1]);
                if (type == Message.SERVER_MESSAGE) {
                    int status = Integer.parseInt(attributes[2]);
                    return new MessageUserActivity(userId, serverId, status);
                } else{
                    System.out.println(Server.ANSI_RED+"MessageUserAcivity konnte nicht ausgewertet werden"+Server.ANSI_RESET);
                    throw new RuntimeException();
                }
            }
        } catch(Exception e){
            System.out.println("Fehler bei MessageUserActivity.toObject(): " + e.getMessage());
            return new MessageUserActivity(-100, -100, -100);
        }
    }

}
