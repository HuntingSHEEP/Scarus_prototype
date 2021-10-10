import java.awt.*;

public class SilnikFizyki extends Thread {
    private PhysicRectangle obiekt, platforma;
    private double deltaTime;
    final long INTERVAL = (int) (1000000);


    public SilnikFizyki(){
        deltaTime = 0.01;
    }

    public void run(){

        System.out.println("Uruchomiono silnik fizyki");
        while(true){
            if(obiekt.collision(platforma))
                obiekt.collisionResponse(platforma);

            obiekt.calculateDynamics(deltaTime);
            //System.out.println(String.format("XY [%d,%d]   V [%.2f, %.2f]   A [%.2f, %.2f]", obiekt.x, obiekt.y, obiekt.v.x, obiekt.v.y, obiekt.a.x, obiekt.a.y));
            waitSomeTime();

        }
    }

    private void waitSomeTime() {
        //final long INTERVAL = (int) (100000); //10^7 * 2 daje 50fps
        long start = System.nanoTime();
        long end;
        do{
            end = System.nanoTime();
        }while(start + INTERVAL >= end);
    }



    public void add(PhysicRectangle aObiekt, PhysicRectangle aPlatforma){
        this.obiekt = aObiekt;
        this.platforma = aPlatforma;
    }
}
