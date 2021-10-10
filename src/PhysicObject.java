import java.awt.*;


public interface PhysicObject {
    void moveBy(double dx, double dy);

    void setVelocity(Vector3D v);
    void addVelocity(Vector3D v);

    void setAcceleration(Vector3D a);
    void addAcceleration(Vector3D a);

    void setGravity(double g);
    void calculateDynamics(double deltaTime);

    boolean collision(Point p);
    boolean collision(double x, double y);
    boolean collision(PhysicRectangle rect);

    void collisionResponse(PhysicRectangle rect);

    Point getGeometricMiddle();
    Point getLUpperVertex();
    Point getRUpperVertex();
    Point getRLowerVertex();
    Point getLLowerVertex();

    Vector3D getWeightPointsVector(PhysicRectangle rect);


}
