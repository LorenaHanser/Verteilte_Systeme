package teil1;

public class Message {
    public static final String SPLIT_SYMBOL = ";";
    public static final int UNKNOWN_CATEGORY = -1;
    public static final int CATEGORY_CLIENT_MESSAGE = 0;
    public static final int CATEGORY_SERVER_MESSAGE = 1;
    public static final int CATEGORY_SYNC_MESSAGE = 2;

    public static int getMessageCategoryFromString(String message) {
        try {
            String[] splitResponse = message.split(SPLIT_SYMBOL, 2);
            int category = Integer.parseInt(splitResponse[0]);

            if (category == CATEGORY_CLIENT_MESSAGE) {
                return CATEGORY_CLIENT_MESSAGE;
            } else if (category == CATEGORY_SERVER_MESSAGE) {
                return CATEGORY_SERVER_MESSAGE;
            } else if (category == CATEGORY_SYNC_MESSAGE) {
                return CATEGORY_SYNC_MESSAGE;
            } else {
                return UNKNOWN_CATEGORY;
            }
        } catch (Exception e) {
            return UNKNOWN_CATEGORY;
        }
    }

    private int userId;
    private int category;

    // get()- und set()-Methode

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }
}
