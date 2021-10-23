import java.util.ArrayList;
import java.util.List;

public class MeshCollider {
    List<Triangle> triangleList = new ArrayList<Triangle>();
    List<Vector3D> pointList = new ArrayList<Vector3D>();

    public void add(Triangle triangle){
        triangleList.add(triangle);
    }

    public void add(Vector3D point){
        pointList.add(point);
    }

}
