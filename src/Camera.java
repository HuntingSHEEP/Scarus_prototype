public class Camera extends SRectangle{
    public static final String myType= "CAMERA";
    Vector3D offset;

    Camera(){
        super();
        this.type = myType;
        this.skin = null;

        width  = 500;
        height = 400;
        offset = new Vector3D(300, 300);
    }

    Camera(int width, int height){
        super(0,0,0, width, height);
        this.type = myType;
        offset = new Vector3D(300, 300);
    }

    Camera(int x, int y, int width, int height, Vector3D offset){
        super(x,y,0, width, height);
        this.type = myType;
        this.offset = offset;
    }

    public  Vector3D getCameraVector() {
        return Vector3D.addVectors(this.location.position, offset).multiply(-1);
    }
}
