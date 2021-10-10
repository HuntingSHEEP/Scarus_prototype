import javax.swing.*;

public class OdswiezanieEkranu extends Thread{

    private JPanel panel;

    public OdswiezanieEkranu(){}

    public void setPanel(JPanel aPanel){
        this.panel = aPanel;
    }

    public void run(){
        System.out.println("Uruchomiono odświeżanie");
        while(true){
            this.panel.repaint();
            waitSomeTime();
        }
    }

    private void waitSomeTime() {
        final long INTERVAL = (int) (1000000);
        long start = System.nanoTime();
        long end;
        do{
            end = System.nanoTime();
        }while(start + INTERVAL >= end);
    }
}
