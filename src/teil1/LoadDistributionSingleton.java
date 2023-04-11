package teil1;

public class LoadDistributionSingleton {
    private static LoadDistributionSingleton instance;
    private boolean server1Running = false;

    private LoadDistributionSingleton(){
        // smart comment
    }

    public static synchronized LoadDistributionSingleton getInstance(){
        if(instance == null){
            instance = new LoadDistributionSingleton();
        }
        System.out.println(instance);
        return instance;
    }

    public boolean isServer1Running() {
        return server1Running;
    }

    public void setServer1Running(boolean value) {
        server1Running = value;
    }
}
