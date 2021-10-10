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

    public void add(Vector3D vector){
        this.x += vector.x;
        this.y += vector.y;
        this.z += vector.z;
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

    void multiply(double scale){
        this.x *= scale;
        this.y *= scale;
        this.z *= scale;
    }

    double length(){
        return sqrt(x*x + y*y + z*z);
    }

}
