import javax.swing.*;

public class RenderEngine extends Thread{

    private MyPanel panel;
    private Camera camera;
    private World world;

    public RenderEngine(){}

    public void setPanel(MyPanel panel){
        this.panel = panel;
    }

    public void run(){
        System.out.println("Uruchomiono odświeżanie");
        this.panel.setCameraVector(new Vector3D(500, 500));
        while(true){
            /* TODO - optymalizacja
            1)wyznaczyć chunki
            1.b)z chunków wyciągnąć obiekty do renderowania
            2)
             */

            this.panel.setCameraVector(camera.getCameraVector());
            this.panel.repaint = true;
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

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void setWorld(World world) {
        this.world = world;
    }
}
