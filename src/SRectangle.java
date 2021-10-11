import java.awt.*;

public class SRectangle extends GameObject{
    public static final String myType = "SRECTANGLE";
    double width, height;


    SRectangle(){
        super();
        this.width  = 10;
        this.height = 10;
        this.skin = new Rectangle(
                (int) (location.position.x.intValue() -  (width/2)),
                (int) (location.position.y.intValue() -  (height/2)),
                (int) width, (int) height);
        this.type = myType;
    }

    SRectangle(double x, double y, double z, double width, double height){
        super(x, y, z);
        this.width  = width;
        this.height = height;
        this.skin = new Rectangle((int) (x-(width/2)), (int) (y - (height/2)), (int) width, (int) height);
        this.type = myType;
    }

    public void updateSkin(){
        this.skin = new Rectangle(
                (int) (location.position.x.intValue() -  (width/2)),
                (int) (location.position.y.intValue() -  (height/2)),
                (int) width, (int) height);
    }


    public Vector3D getLUpperVertex(){
        return new Vector3D(location.position.x - width/2,location.position.y+height/2);
    }

    public Vector3D getRUpperVertex(){
        return new Vector3D(location.position.x + width/2,location.position.y+height/2);
    }

    public Vector3D getRLowerVertex(){
        return new Vector3D(location.position.x + width/2,location.position.y-height/2);
    }

    public Vector3D getLLowerVertex(){
        return new Vector3D(location.position.x - width/2,location.position.y-height/2);
    }

    public Vector3D getGeometricMiddle() {
        return location.position;
    }


    public void moveByCameraVector(Vector3D cameraVector) {
        this.skin = new Rectangle(
                (int) (location.position.x.intValue() -  (width/2) + cameraVector.x),
                (int) (location.position.y.intValue() -  (height/2) + cameraVector.y),
                (int) width, (int) height);
    }
}
