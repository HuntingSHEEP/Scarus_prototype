import java.awt.*;

public class GameObject {
    Location location;
    Dynamics dynamics;
    Shape skin;
    String type;
    int layer;


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
}
