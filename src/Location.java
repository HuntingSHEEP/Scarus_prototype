public class Location {
    public Vector3D position, rotation, scale;

    /**
     * Domyślne wartości pól:
     * position [0,0,0]
     * rotation [0,0,0]
     * scale    [1,1,1]
     */
    Location(){
        position = new Vector3D();
        rotation = new Vector3D();
        scale    = new Vector3D(1,1,1);
    }

    Location(double positionX, double positionY, double positionZ){
        position = new Vector3D(positionX, positionY, positionZ);
        rotation = new Vector3D(0,0, 0);
        scale    = new Vector3D(1,1, 1);
    }
}
