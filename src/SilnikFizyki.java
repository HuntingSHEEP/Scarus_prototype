import java.awt.*;

public class SilnikFizyki extends Thread {
    private World world;
    private double deltaTime;
    final long INTERVAL = (int) (1000000);


    public SilnikFizyki(){
        deltaTime = 0.01;
    }

    public void run(){

        System.out.println("Uruchomiono silnik fizyki");
        System.out.println("Objects in world: " + world.gameObjectList.size());

        while(true){
            //dla każdego obiektu zdefiniowanego w świecie
            GameObject someGameObject;
            for (int i = 0; i < world.gameObjectList.size(); i++) {
                someGameObject = world.gameObjectList.get(i);

                calculateDynamics(someGameObject, deltaTime);
                boolean collision = calculateCollisions(someGameObject, world);

                if(collision){
                    world.deregisterFromChunks(someGameObject);
                    world.registerInChunks(someGameObject);
                }



            }

            /*
            //TODO: obliczenia powinny byc wykonywane dla wszystkich obiektów w tym samym momencie, np poprzez stworzenie listy danych LOKALIZACJI dla każdego z obiektóœ i uakualnienie dopiero na końcu


            System.out.println(String.format("XY [%.2f, %.2f]   V [%.2f, %.2f]   A [%.2f, %.2f]", obiekt.location.position.x, obiekt.location.position.y, obiekt.dynamics.v.x, obiekt.dynamics.v.y, obiekt.dynamics.a.x, obiekt.dynamics.a.y));
             */

            waitSomeTime();

        }
    }

    /**
     *
     * @param someGameObject
     * @param world
     * @return True if some collisions detected;
     */
    private boolean calculateCollisions(GameObject someGameObject, World world) {
        GameObject anotherGameObject;
        boolean collidedWithAnObject = false;

        for (int i = 0; i < world.gameObjectList.size(); i++) {
            anotherGameObject = world.gameObjectList.get(i);
            if(someGameObject != anotherGameObject){

                if(SRectangle.myType.compareTo(someGameObject.type) == 0){
                    if(SRectangle.myType.compareTo(anotherGameObject.type) == 0)

                        if(collision((SRectangle) someGameObject,(SRectangle) anotherGameObject)){
                            collidedWithAnObject = true;
                            collisionResponse((SRectangle) someGameObject,(SRectangle) anotherGameObject);
                        }


                }

            }
        }
        return collidedWithAnObject;
    }

    private void waitSomeTime() {
        //final long INTERVAL = (int) (100000); //10^7 * 2 daje 50fps
        long start = System.nanoTime();
        long end;
        do{
            end = System.nanoTime();
        }while(start + INTERVAL >= end);
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

        //TODO: dodać pole ciało sprężyste!
        double bounceScale = 1;

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

    public void setWorld(World world) {
        this.world = world;
    }
}
