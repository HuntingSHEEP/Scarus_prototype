public class Triangle {
    Vector3D A, B, C;
    double field2;
    double field;

    /**
     * Trójkąt zdefiniowany jest za pomocą 3 wektorów względem określonego punktu
     * w przestrzeni. W innych funkcjach podawać środek masy jako punkt odniesienia
     * do wyznaczania wierzchołków.
     * @param A
     * @param B
     * @param C
     */
    Triangle(Vector3D A, Vector3D B, Vector3D C){
        this.A = A;
        this.B = B;
        this.C = C;
        computeField();
    }

    private void computeField() {
        double a = (new Vector3D(B.x-A.x, B.y-A.y, B.z-A.z)).length();
        double b = (new Vector3D(C.x-B.x, C.y-B.y, C.z-B.z)).length();
        double c = (new Vector3D(A.x-C.x, A.y-C.y, A.z-C.z)).length();
        double p = (a+b+c)/2;

        field2 = p*(p-a)*(p-b)*(p-c);
        field = Math.sqrt(field2);
    }

    public double fieldInRelationToPoint(Vector3D refPoint,Point K){
        Point pA = getVerticeA(refPoint);
        Point pB = getVerticeB(refPoint);
        Point pC = getVerticeC(refPoint);

        double a = (new Vector3D(pB.x-pA.x, pB.y-pA.y, pB.z-pA.z)).length();
        double b = (new Vector3D(pC.x-pB.x, pC.y-pB.y, pC.z-pB.z)).length();
        double c = (new Vector3D(pA.x-pC.x, pA.y-pC.y, pA.z-pC.z)).length();

        double ak = (new Vector3D(K.x-pA.x, K.y-pA.y, K.z-pA.z)).length();
        double ck = (new Vector3D(K.x-pC.x, K.y-pC.y, K.z-pC.z)).length();
        double bk = (new Vector3D(K.x-pB.x, K.y-pB.y, K.z-pB.z)).length();

        double p = (c+ak+ck)/2;
        double P0 = Math.sqrt(p*(p-c)*(p-ak)*(p-ck));

        p = (b+ck+bk)/2;
        double P1 = Math.sqrt(p*(p-b)*(p-ck)*(p-bk));

        p = (a+bk+ak)/2;
        double P2 = Math.sqrt(p*(p-a)*(p-bk)*(p-ak));

        return P0 + P1 + P2;
    }

    public Point getVerticeA(Vector3D refPoint){
        return Point.getMovedPoint(refPoint, A);
    }
    public Point getVerticeB(Vector3D refPoint){
        return Point.getMovedPoint(refPoint, B);
    }
    public Point getVerticeC(Vector3D refPoint){
        return Point.getMovedPoint(refPoint, C);
    }


}
