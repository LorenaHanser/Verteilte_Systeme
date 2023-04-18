package teil1;

public class MCSHandler {
    boolean isServer1Online;
    boolean isServer2Online;


    public MCSHandler(){
    }

    public void setServer1Online() {
        isServer1Online = true;
    }
    public void setServer1Offline() {
        isServer1Online = false;
    }
    public void setServer2Online() {
        isServer2Online = true;
    }
    public void setServer2Offline() {
        isServer2Online = false;
    }

    public void setServerOnline(int ThreadNummer) {

        if(ThreadNummer == 1){
            isServer1Online = true;
            System.out.println("Server 1 online");
        } else if (ThreadNummer == 2) {
            isServer2Online = true;
            System.out.println("Server 2 online");
        }else {
            System.out.println("Es gibt in der MCSHanlderklasse ein Problem");
        }

    }
    public void setServerOffline(int ThreadNummer) {
        if(ThreadNummer == 1){
            isServer1Online = false;
            System.out.println("Server 1 offline");
        } else if (ThreadNummer == 2) {
            isServer2Online = false;
            System.out.println("Server 2 offline");
        }else {
            System.out.println("Es gibt in der MCSHanlderklasse ein Problem");
        }
    }

    public boolean isServerBlocked()
    {
        boolean answer = true;
        if(isServer1Online | isServer2Online){
            answer = false;
        }
        return answer;
    }
}
