package teil1;

public class LoadDistribution {
    private static int clientCounter = 0;
    private static int clientCounterServer1 = 0;
    private static int clientCounterServer2 = 0;
    private static final int PORT_SERVER1 = 8989;
    private static final int PORT_SERVER2 = 8990;
    private static boolean runningServer1 = false;
    private static boolean runningServer2 = false;

    public static synchronized void increaseClientCounter(int port) {
        clientCounter++;
        if (port == PORT_SERVER1) {                       // Server 1
            clientCounterServer1++;
        } else if (port == PORT_SERVER2) {                // Server 2
            clientCounterServer2++;
        } else {
            System.out.println("Port " + port + " unbekannt.");
        }
        System.out.println("clientCounterServer1: " + clientCounterServer1 + "clientCounterServer2: " + clientCounterServer2);
    }

    public static synchronized void decreaseClientCounter(int port) {
        clientCounter--;
        if (port == PORT_SERVER1) {                       // Server 1
            clientCounterServer1--;
        } else if (port == PORT_SERVER2) {                // Server 2
            clientCounterServer2--;
        } else {
            System.out.println("Port " + port + " unbekannt.");
        }
        System.out.println("clientCounterServer1: " + clientCounterServer1 + "clientCounterServer2: " + clientCounterServer2);
    }

    public static synchronized String getServerStatus() {
        return "--- Server 1: " + runningServer1 + " - Server 2: " + runningServer2 + " ---";
    }

    public static synchronized void setServerStatus(int port, boolean isRunning) {
        if (port == PORT_SERVER1) {
            runningServer1 = isRunning;
        } else if (port == PORT_SERVER2) {
            runningServer2 = isRunning;
        }
        System.out.println(getServerStatus());
    }

    public static synchronized int getPort() {
        int port = 0;

        if (clientCounterServer1 < clientCounterServer2) {
            port = PORT_SERVER1;
        } else if (clientCounterServer1 > clientCounterServer2) {
            port = PORT_SERVER2;
        } else if (clientCounterServer1 == clientCounterServer2) {
            int randomNumber = (int) (Math.random() * 2);
            port = PORT_SERVER1 + randomNumber;
        }

        return port;
    }

    public static synchronized boolean getStatusFromPort(int port) {
        boolean returnBoolean = false;
        switch (port) {
            case PORT_SERVER1 -> {
                returnBoolean = runningServer1;
                break;
            }
            case PORT_SERVER2 -> {
                returnBoolean = runningServer2;
                break;
            }
        }
        return returnBoolean;
    }

}
