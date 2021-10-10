import java.awt.*;


public class PhysicRectangle extends Rectangle implements PhysicObject{
    Vector3D v = new Vector3D();
    Vector3D a = new Vector3D();

    double X, Y;


    PhysicRectangle(int x, int y, int width, int height){
        super(x, y, width, height);
        X = x;
        Y = y;
    }

    @Override
    public void moveBy(double dx, double dy) {
        X += dx;
        Y += dy;
        updateRectPos();
    }

    private void updateRectPos() {
        x = (int) X;
        y = (int) Y;
    }

    @Override
    public void setVelocity(Vector3D v) {
        this.v = v;
    }

    @Override
    public void setAcceleration(Vector3D a) {
        this.a = a;
    }

    @Override
    public void addVelocity(Vector3D v) {
        this.v.add(v);
    }

    @Override
    public void addAcceleration(Vector3D a) {
        this.a.add(a);
    }

    @Override
    public void setGravity(double g) {
        addAcceleration(new Vector3D(0, g));
    }


    @Override
    public void calculateDynamics(double deltaTime) {
        //zmiana położenia
        double dx = v.x*deltaTime + (a.x/2)*deltaTime*deltaTime;
        double dy = v.y*deltaTime + (a.y/2)*deltaTime*deltaTime;

        //nowa prędkość
        double dvx = a.x*deltaTime;
        double dvy = a.y*deltaTime;

        //aktualizacja położenia
        //leftUpperVertex.moveBy(dx, dy);

        moveBy(dx, dy);
        //aktualizacja prędkości
        v.add(dvx, dvy);
    }

    @Override
    public boolean collision(Point p) {
        boolean w1 = (X <= p.x) & (p.x <= X+ width);
        boolean w2 = (Y <= p.y) & (p.y <= Y+ height);
        return w1 & w2;
    }

    @Override
    public boolean collision(double x, double y) {
        boolean w1 = (X<= x) & (x <= X + width);
        boolean w2 = (Y <= y) & (y <= Y + height);
        return w1 & w2;
    }

    @Override
    public boolean collision(PhysicRectangle rect) {
        //TODO: KOLIZJA BEZ WIERZCHOŁKÓW WEWNATRZ DRUGIEGO PROSTOKĄTA
        boolean w1 = (
                collision(rect.getLUpperVertex()) ||
                collision(rect.getRUpperVertex()) ||
                collision(rect.getRLowerVertex()) ||
                collision(rect.getLLowerVertex())
        );

        boolean w2 = (
                rect.collision(getLUpperVertex()) ||
                rect.collision(getRUpperVertex()) ||
                rect.collision(getRLowerVertex()) ||
                rect.collision(getLLowerVertex())
        );

        return w1 || w2;
    }


    @Override
    public void collisionResponse(PhysicRectangle rect) {
        //wersja mocno uproszczona
        Vector3D w = getWeightPointsVector(rect);
        Vector3D qVector = new Vector3D(rect.width/2 + width/2, rect.height/2 + height/2);
        double q = Math.abs(qVector.y / qVector.x);
        double k = Math.abs(w.y / w.x);

        double bounceScale = 0.3;

        if(q <= k){
            //REACT ON Y-AXIS
            if(Math.signum(w.y) == -1){
                if(0 < v.y){
                    v.y *= -1 * bounceScale;
                }
            }else{
                if(v.y < 0){
                    v.y *= -1 * bounceScale;
                }
            }

        }else{
            //REACT ON X-AXIS
            if(Math.signum(w.x) == -1){
                if(0 < v.x){
                    v.x *= -1 * bounceScale;
                }
            }else{
                if(v.x < 0){
                    v.x *= -1 * bounceScale;
                }
            }
        }


    }

    @Override
    public Point getGeometricMiddle() {
        double x = X + width/2;
        double y = Y + height/2;
        return new Point((int) x,(int) y);
    }

    @Override
    public Point getLUpperVertex() {
        return new Point((int) X, (int) Y);
    }

    @Override
    public Point getRUpperVertex() {
        return new Point((int) X + width, (int) Y);
    }

    @Override
    public Point getRLowerVertex() {
        return new Point((int) X + width, (int) Y + height);
    }

    @Override
    public Point getLLowerVertex() {
        return new Point((int) X, (int) Y + height);
    }

    @Override
    public Vector3D getWeightPointsVector(PhysicRectangle rect) {
        Point a = getGeometricMiddle();
        Point b = rect.getGeometricMiddle();

        Vector3D w = new Vector3D(a.x - b.x, a.y - b.y);
        return w;
    }

}
