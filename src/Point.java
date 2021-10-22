public class Point {
    double x, y ,z;

    Point(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Point getMovedPoint(Vector3D p, Vector3D v){
        return new Point(p.x + v.x, p.y + v.y, p.z + v.z);
    }
}
