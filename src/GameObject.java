import java.awt.*;

public class GameObject {
    Location location;
    Dynamics dynamics;
    Shape skin;
    String type;
    int layer;
    //TODO: KONIECZNIE POLE COLLIDERA GŁÓWNEGO! albo rozwiązać to interfejsem
    MeshCollider meshCollider;
    Vector3D collisionVector = new Vector3D(); // jest konkretnym punktem zawieszonym w tym samym układzie współrzędnych co środek obiektu
    Vector3D[] collisionList = new Vector3D[]{};
    Vector3D penetrationVector = null;

    boolean isFixed = false;



    GameObject(){
        location = new Location();
        dynamics = new Dynamics();
        layer = 0;
    }

    GameObject(double x, double y, double z){
        location = new Location(x, y, z);
        dynamics = new Dynamics();
        layer = 0;
    }
    public String toString(){
        return String.format("Object XY [%.2f, %.2f]", location.position.x, location.position.y);
    }

    public void moveBy(double dx, double dy){
        location.position.x += dx;
        location.position.y += dy;
    }

    public void setVelocity(Vector3D v) {
        dynamics.v = v;
    }
    public void addVelocity(Vector3D v) {
        dynamics.v.add(v);
    }

    public void setAcceleration(Vector3D a) {
        dynamics.a = a;
    }
    public void addAcceleration(Vector3D a) {
        dynamics.a.add(a);
    }



}
