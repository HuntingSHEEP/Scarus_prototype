import java.awt.*;

public class SRectangle extends GameObject{
    public static final String myType = "SRECTANGLE";
    double width, height;
    double sphereRadius;


    SRectangle(){
        super();
        this.width  = 10;
        this.height = 10;
        this.skin = new Rectangle(
                (int) (location.position.x.intValue() -  (width/2)),
                (int) (location.position.y.intValue() -  (height/2)),
                (int) width, (int) height);

        this.type = myType;
        updateSphereRadius();
        setUpMeshCollider();
    }

    SRectangle(double x, double y, double z, double width, double height){
        super(x, y, z);
        this.width  = width;
        this.height = height;
        this.skin = new Rectangle((int) (x-(width/2)), (int) (y - (height/2)), (int) width, (int) height);
        this.type = myType;
        updateSphereRadius();
        setUpMeshCollider();
    }

    private void setUpMeshCollider() {
        /*Procedura odpowiedzialna za przygotowanie Mesh Collidera*/
        meshCollider = new MeshCollider();

        // wyznaczam punkty siatki w odniesieniu do środka masy - w tym przypadku środka geometrycznego
        Vector3D A = new Vector3D(-width/2, -height/2);
        Vector3D B = new Vector3D(-width/2, height/2);
        Vector3D C = new Vector3D(width/2, height/2);
        Vector3D D = new Vector3D(width/2, -height/2);

        // wyznaczone punkty siatki dodaję do listy punktów MeshCollidera
        meshCollider.add(A);
        meshCollider.add(B);
        meshCollider.add(C);
        meshCollider.add(D);

        // z wyznaczonych punktów tworzę trójkąty siatki i dodaję do Collidera
        meshCollider.add(new Triangle(A, B, C));
        meshCollider.add(new Triangle(A, C, D));
    }

    public Vector3D support(Vector3D direction){
        /*Funkcja zakłada, że wektory punktów są w odniesieniu do lokalnego, nie globalnego układu współrzędnych*/

        Vector3D furthestVertice = meshCollider.pointList.get(0);
        double furthestDistance  = Vector3D.dot(meshCollider.pointList.get(0), direction);

        for(int x=1; x<meshCollider.pointList.size(); x++){
            Vector3D vertice = meshCollider.pointList.get(x);
            double  distance = Vector3D.dot(vertice, direction);
            if(distance > furthestDistance){
                furthestVertice  = vertice;
                furthestDistance = distance;
            }
        }

        //lecz wierzchołek zwraca już przeliczony na globalny układ współrzędnych
        return  Vector3D.add(furthestVertice, this.location.position);
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

    public double getSphereRadius() {
        return sphereRadius;
    }

    public void updateSphereRadius(){
        sphereRadius = Math.sqrt(width*width + height*height) / 2;
    }
}
