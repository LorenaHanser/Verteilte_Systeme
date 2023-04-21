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
        this.setType(0);
        this.setStatus(status);
    }

    public MessageUserActivity(int userId, int serverId, int[] userIsOnServer ) {
        this.setCategory(Message.CATEGORY_SERVER_MESSAGE);
        this.setUserId(userId);
        this.setServerId(serverId);
        this.setType(1);
        this.setuserIsOnServer(userIsOnServer);
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
            return this.getCategory() + SPLIT_SYMBOL + this.getUserId() + SPLIT_SYMBOL + this.getServerId() + SPLIT_SYMBOL + getType() + SPLIT_SYMBOL + this.getStatus();
        }else {
            return this.getCategory() + SPLIT_SYMBOL + this.getUserId() + SPLIT_SYMBOL + this.getServerId() + SPLIT_SYMBOL + getType() + SPLIT_SYMBOL + getUserIsOnServerWithSplitsymbols();
        }
    }

    public static MessageUserActivity toObject(String string) {
        try{
            string = string.replace("*", "");
            string = string.replace("\n", "");
            String[] attributes = string.split(SPLIT_SYMBOL, 5);


            int Category = Integer.parseInt(attributes[0]);
            int userId = Integer.parseInt(attributes[1]);
            int serverId = Integer.parseInt(attributes[2]);
            int type = Integer.parseInt(attributes[3]);
            if(type == 0) {
                int status = Integer.parseInt(attributes[4]);
                return new MessageUserActivity(userId, serverId, status);
            } else {
                String[] data = attributes[4].split(SPLIT_SYMBOL, 6);
                int[] userDataArray = new int[6];
                for (int i = 0; i < userDataArray.length; i++) {
                    userDataArray[i] = Integer.parseInt(data[i]);
                }
                return new MessageUserActivity(userId, serverId, userDataArray);
            }
        } catch(Exception e){
            System.out.println("Fehler bei MessageUserActivity.toObject(): " + e.getMessage());
            return new MessageUserActivity(-100, -100, -100);
        }
    }

}
