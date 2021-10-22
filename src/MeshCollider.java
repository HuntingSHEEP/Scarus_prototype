import java.util.ArrayList;
import java.util.List;

public class MeshCollider {
    List<Triangle> triangleList = new ArrayList<Triangle>();

    public void add(Triangle triangle){
        triangleList.add(triangle);
    }

}
