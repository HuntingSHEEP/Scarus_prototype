public class Collision {
    boolean collided;
    boolean onEdge;
    Vector3D collisionNormal;
    Vector3D P;

    Collision(){
        collided = false;
        onEdge = false;
        collisionNormal = new Vector3D();
        P = null;
    }

    Collision(boolean collided, boolean onEdge, Vector3D normal){
        this.collided = collided;
        this.onEdge = onEdge;
        this.collisionNormal = normal;
        this.P = null;
    }

    Collision(boolean collided, boolean onEdge, Vector3D normal, Vector3D P){
        this.collided = collided;
        this.onEdge = onEdge;
        this.collisionNormal = normal;
        this.P = P;
    }
}
