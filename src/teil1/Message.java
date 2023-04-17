package teil1;

public class Message {
    private static final String SPLIT_SYMBOL = ";";

    public static boolean isClientMessage (String message){
        String[] splitResponse = message.split(SPLIT_SYMBOL, 3);
        boolean answer = false;
        if(Integer.parseInt(splitResponse[1])>=0){
            answer = true;
        }
        return answer;
    }

    private int userId;

    // get()- und set()-Methode
    public static String getSplitSymbol() {
        return SPLIT_SYMBOL;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
