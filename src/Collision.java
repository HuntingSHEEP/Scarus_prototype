public class Collision {
    boolean collided;
    boolean onEdge;
    Vector3D collisionNormal;

    Collision(){
        collided = false;
        onEdge = false;
        collisionNormal = new Vector3D();
    }

    Collision(boolean collided, boolean onEdge, Vector3D normal){
        this.collided = collided;
        this.onEdge = onEdge;
        this.collisionNormal = normal;
    }
}
