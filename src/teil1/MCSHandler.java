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

    public boolean isServerBlocked()
    {
        boolean answer = true;
        if(isServer1Online | isServer2Online){
            answer = false;
        }
        return answer;
    }
}
