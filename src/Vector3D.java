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

    public double get(int index){
        if(index == 0)
            return x;
        if(index == 1)
            return y;
        if(index == 2)
            return z;

        return -1;
    }

    public static double distance(Vector3D position, Vector3D position1) {
        return Vector3D.length(Vector3D.difference(position, position1));
    }

    private static double length(Vector3D difference) {
        return Math.sqrt(difference.x*difference.x + difference.y*difference.y + difference.z*difference.z);
    }

    public static Vector3D difference(Vector3D position, Vector3D position1) {
        return new Vector3D(position.x - position1.x, position.y - position1.y, position.z - position1.z);
    }

    public static Vector3D cross(Vector3D a, Vector3D b){
        return new Vector3D(a.y*b.z - a.z*b.y, a.z*b.x - a.x*b.z, a.x*b.y - a.y*b.x);
    }

    public static Vector3D tripleXProduct(Vector3D a, Vector3D b, Vector3D c){
        return Vector3D.cross(a ,Vector3D.cross(b, c));
    }

    public static boolean equall(Vector3D a, Vector3D b) {
        double epsilon = 0.001;

        boolean test0 = Math.abs(a.x - b.x) < epsilon;
        boolean test1 = Math.abs(a.y - b.y) < epsilon;
        boolean test2 = Math.abs(a.z - b.z) < epsilon;

        return test0 && test1 && test2;
    }

    public void add(Vector3D vector){
        this.x += vector.x;
        this.y += vector.y;
        this.z += vector.z;
    }

    public static Vector3D addVectors(Vector3D v1, Vector3D v2){
        return new Vector3D(v1.x+v2.x, v1.y+v2.y, v1.z+v2.z);
    }

    public static double dot(Vector3D v1, Vector3D v2){
        return v1.x*v2.x + v1.y*v2.y + v1.z*v2.z;
    }

    public static Vector3D add(Vector3D v1, Vector3D v2){
        return new Vector3D(v1.x+v2.x, v1.y+v2.y, v1.z+v2.z);
    }

    /**
     * Returns a new Vector3D and doesn't change the input vector!
     */
    public static Vector3D multiply(Vector3D v, double scale){
        return new Vector3D(v.x*scale, v.y*scale, v.z*scale);
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
    //TODO tak nie może być - trzeba jasno oddzielić działanie na konkretnym obiekcie od zwykłego przeliczaniatłu
        return new Vector3D(this.x, this.y, this.z);
    }

    double length(){
        return sqrt(x*x + y*y + z*z);
    }

    public String toString(){
        return String.format("Vector [%.2f, %.2f, %.2f]",x,y,z);
    }

    public void normalize() {
        this.multiply(1.0/this.length());
    }
}
