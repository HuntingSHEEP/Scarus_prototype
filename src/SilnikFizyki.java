import java.awt.*;

public class SilnikFizyki extends Thread {
    private GameObject obiekt, platforma;
    private double deltaTime;
    final long INTERVAL = (int) (1000000);


    public SilnikFizyki(){
        deltaTime = 0.01;
    }

    public void run(){

        System.out.println("Uruchomiono silnik fizyki");
        while(true){
            calculateDynamics(obiekt, deltaTime);
            calculateDynamics(platforma, deltaTime);

            if(collision((SRectangle) obiekt,(SRectangle) platforma))
                collisionResponse((SRectangle) obiekt,(SRectangle) platforma);





            //TODO: przenieść sekcję uaktualniania skórki do silnika renderowania
            ((SRectangle) obiekt).updateSkin();
            ((SRectangle) platforma).updateSkin();

            System.out.println(String.format("XY [%.2f, %.2f]   V [%.2f, %.2f]   A [%.2f, %.2f]", obiekt.location.position.x, obiekt.location.position.y, obiekt.dynamics.v.x, obiekt.dynamics.v.y, obiekt.dynamics.a.x, obiekt.dynamics.a.y));
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
    public void add(GameObject obiekt, GameObject platforma){
        this.obiekt = obiekt;
        this.platforma = platforma;
    }

    public void calculateDynamics(GameObject gameObject , double deltaTime) {
        //zmiana położenia
        double dx = gameObject.dynamics.v.x*deltaTime + (gameObject.dynamics.a.x/2)*deltaTime*deltaTime;
        double dy = gameObject.dynamics.v.y*deltaTime + (gameObject.dynamics.a.y/2)*deltaTime*deltaTime;

        //nowa prędkość
        double dvx = gameObject.dynamics.a.x*deltaTime;
        double dvy = gameObject.dynamics.a.y*deltaTime;

        //aktualizacja położenia
        gameObject.moveBy(dx, dy);

        //aktualizacja prędkości
        gameObject.dynamics.v.add(dvx, dvy);
    }

    public boolean collision(SRectangle sRectangle, Vector3D p) {
        int X = (int) (sRectangle.location.position.x - sRectangle.width/2);
        int Y = (int) (sRectangle.location.position.y - sRectangle.height/2);

        boolean w1 = (X <= p.x) & (p.x <= X+ sRectangle.width);
        boolean w2 = (Y <= p.y) & (p.y <= Y+ sRectangle.height);
        return w1 & w2;
    }

    public boolean collision(SRectangle sRectangle,double x, double y) {
        int X = (int) (sRectangle.location.position.x - sRectangle.width/2);
        int Y = (int) (sRectangle.location.position.y - sRectangle.height/2);

        boolean w1 = (X <= x) & (x <= X+ sRectangle.width);
        boolean w2 = (Y <= y) & (y <= Y+ sRectangle.height);
        return w1 & w2;
    }

    public boolean collision(SRectangle rectA, SRectangle rectB) {
        //TODO: KOLIZJA BEZ WIERZCHOŁKÓW WEWNATRZ DRUGIEGO PROSTOKĄTA, wziąć to z neta co fajne było

        boolean w1 = (
                collision(rectA, rectB.getLUpperVertex()) ||
                collision(rectA, rectB.getRUpperVertex()) ||
                collision(rectA, rectB.getRLowerVertex()) ||
                collision(rectA, rectB.getLLowerVertex())
        );

        boolean w2 = (
                collision(rectB, rectA.getLUpperVertex()) ||
                collision(rectB, rectA.getRUpperVertex()) ||
                collision(rectB, rectA.getRLowerVertex()) ||
                collision(rectB, rectA.getLLowerVertex())
        );

        return w1 || w2;
    }

    public Vector3D getWeightPointsVector(SRectangle rectA, SRectangle rectB) {
        Vector3D a = rectA.getGeometricMiddle();
        Vector3D b = rectB.getGeometricMiddle();

        return new Vector3D(a.x - b.x, a.y - b.y);
    }


    public void collisionResponse(SRectangle rect, SRectangle rectB) {
        //wersja mocno uproszczona
        Vector3D w = getWeightPointsVector(rect, rectB);
        Vector3D qVector = new Vector3D(rect.width/2 + rectB.width/2, rect.height/2 + rectB.height/2);
        double q = Math.abs(qVector.y / qVector.x);
        double k = Math.abs(w.y / w.x);

        double bounceScale = 0.3;

        if(q <= k){
            //REACT ON Y-AXIS
            if(Math.signum(w.y) == -1){
                if(0 < rect.dynamics.v.y){
                    rect.dynamics.v.y *= -1 * bounceScale;
                }
            }else{
                if(rect.dynamics.v.y < 0){
                    rect.dynamics.v.y *= -1 * bounceScale;
                }
            }

        }else{
            //REACT ON X-AXIS
            if(Math.signum(w.x) == -1){
                if(0 < rect.dynamics.v.x){
                    rect.dynamics.v.x *= -1 * bounceScale;
                }
            }else{
                if(rect.dynamics.v.x < 0){
                    rect.dynamics.v.x *= -1 * bounceScale;
                }
            }
        }


    }
}
