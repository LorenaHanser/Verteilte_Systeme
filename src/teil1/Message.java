package teil1;

public class Message {
    private static final String SPLIT_SYMBOL = ";";

    private int userId;

    // get()- und set()-Methode
    public String getSplitSymbol() {
        return SPLIT_SYMBOL;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
