package teil1;

public class MCS {
    private int uhrzeit;

    public MCS(){
        this.uhrzeit = 0;
        //diese Version macht das lediglich mit einer Datei
    }

    public int getUhrzeit() {
        return uhrzeit;
    }

    public void updateUhrzeit(int empfangeneZeit){
        if(empfangeneZeit > uhrzeit)
        {
            uhrzeit = empfangeneZeit;
        }
    }

    public boolean uhrzeitValid(int emfangeneZeit){
        boolean response = false;
        if(emfangeneZeit > uhrzeit){
            response = true;
        }
        return response;
    }

    public String checkAndSetUhr(int empfangeneZeit){
        String response = "ERROR";
        if(uhrzeitValid(empfangeneZeit)){
            updateUhrzeit(empfangeneZeit);
            response = "ok";
        }else {
            response = "Uhrzeit ist alt, Datei ist nicht aktuell";
        }
        return  response;
    }

    public String readFile(){
        System.out.println("Nachricht soll gelesen werden");
        return "test";
    }

    public void addMessage(){
        System.out.println("Nachricht soll geschrieben werden");
    }

}
