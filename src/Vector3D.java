import static java.lang.Math.sqrt;

class Vector3D {
    Double x, y, z;

    /**
     * Domyślna wartość wektora [0,0,0]
     */
    public Vector3D(){
        x = 0.0;
        y = 0.0;
        z = 0.0;
    }

    public Vector3D(double x, double y){
        this.x = x;
        this.y = y;
        this.z = 0.0;
    }

    public Vector3D(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static double distance(Vector3D position, Vector3D position1) {
        return Vector3D.length(Vector3D.difference(position, position1));
    }

    private static double length(Vector3D difference) {
        return Math.sqrt(difference.x*difference.x + difference.y*difference.y + difference.z*difference.z);
    }

    private static Vector3D difference(Vector3D position, Vector3D position1) {
        return new Vector3D(position.x - position1.x, position.y - position1.y, position.z - position1.z);
    }

    public void add(Vector3D vector){
        this.x += vector.x;
        this.y += vector.y;
        this.z += vector.z;
    }

    public static Vector3D addVectors(Vector3D v1, Vector3D v2){
        return new Vector3D(v1.x+v2.x, v1.y+v2.y, v1.z+v2.z);
    }

    public void add(double x, double y){
        this.x += x;
        this.y += y;
    }

    public void add(double x, double y, double z){
        this.x += x;
        this.y += y;
        this.z += z;
    }

    public Vector3D multiply(double scale){
        this.x *= scale;
        this.y *= scale;
        this.z *= scale;

        return new Vector3D(this.x, this.y, this.z);
    }

    double length(){
        return sqrt(x*x + y*y + z*z);
    }

    public String toString(){
        return String.format("Vector [%.2f, %.2f]",x,y);
    }

}
