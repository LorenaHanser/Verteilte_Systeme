package teil1;

public class Message {
    private static final String SPLIT_SYMBOL = ";";

    public static boolean isClientMessage(String message) {
        boolean answer = false;
        try{
            String[] splitResponse = message.split(SPLIT_SYMBOL, 3);
            if (Integer.parseInt(splitResponse[1]) >= 0) {
                answer = true;
            }
        } catch (Exception e){
            System.out.println("Fehler bei Message " + e.getMessage());
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
