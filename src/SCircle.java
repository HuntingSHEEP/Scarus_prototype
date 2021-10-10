import java.awt.*;
import java.awt.geom.Ellipse2D;

public class SCircle extends GameObject{
    public static final String myType = "SCIRCLE";
    double radius;

    SCircle(){
        super();
        this.radius = 1;
        this.skin = new Ellipse2D.Double(location.position.x-radius, location.position.y-radius, radius*2, radius*2);
        this.type = myType;
    }

    SCircle(double x, double y, double z, double radius){
        super(x,y,z);
        this.radius = radius;
        this.skin = new Ellipse2D.Double(location.position.x-radius, location.position.y-radius, radius*2, radius*2);
        this.type = myType;
    }

}
