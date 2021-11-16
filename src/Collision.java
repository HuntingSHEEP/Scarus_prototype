public class Collision {
    GameObject A, B;
    boolean collided;
    boolean onEdge;
    Vector3D collisionNormal;
    Vector3D P;

    Collision(){
        collided = false;
        onEdge = false;
        collisionNormal = new Vector3D();
        P = null;
        A = null;
        B = null;
    }

    Collision(boolean collided, boolean onEdge, Vector3D normal, GameObject A, GameObject B){
        this.collided = collided;
        this.onEdge = onEdge;
        this.collisionNormal = normal;
        this.P = null;
        this.A = A;
        this.B = B;
    }

    Collision(boolean collided, boolean onEdge, Vector3D normal, Vector3D P, GameObject A, GameObject B){
        this.collided = collided;
        this.onEdge = onEdge;
        this.collisionNormal = normal;
        this.P = P;
        this.A = A;
        this.B = B;
    }
}
