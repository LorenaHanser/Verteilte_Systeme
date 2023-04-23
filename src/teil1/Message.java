package teil1;

/**
 * Die Klasse ist die Superklasse der Klassen {@link MessageClient}, {@link MessageUserActivity} und {@link MessageSync}.
 * Sie stellt Möglichkeiten zur Verwaltung jener Klassen zur Verfügung, die von ihr erben.
 * Zusätzlich vererbt sie die Attribute userId und category, die ihre Subklassen benötigen.
 */
public class Message {

    public static final String SPLIT_SYMBOL = ";";

    public static final int UNKNOWN_CATEGORY = -1;
    public static final int CATEGORY_CLIENT_MESSAGE = 0;
    public static final int CATEGORY_SERVER_MESSAGE = 1;
    public static final int CATEGORY_SYNC_MESSAGE = 2;

    public static final int SERVER_MESSAGE = 0;
    public static final int SERVER_SYNC_RESPONSE = 1;
    public static final int SERVER_SYNC_REQUEST = 2;

    private int userId;
    private int category;

    /**
     * Die Methode nimmt eine über das Netzwerk empfangene Nachricht entgegen.
     * Das erste Symbol aller Nachrichten ist die Kategorie dieser Nachricht, getrennt von einem Semikolon.
     * Dieses wird ausgelesen und mit den jeweiligen Konstanten verglichen.
     * @param message Über das Netzwerk empfangene Nachricht als String
     * @return Gibt die Kategorie der Nachricht zurück
     */
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
