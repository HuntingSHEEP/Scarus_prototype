import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

public class SilnikFizyki extends Thread {
    private World world;
    private double deltaTime;
    final long INTERVAL = (int) (1000000);
    Vector3D direction = new Vector3D();


    public SilnikFizyki(){
        deltaTime = 0.01;
    }

    public void run(){

        System.out.println("Uruchomiono silnik fizyki");
        System.out.println("Objects in world: " + world.gameObjectList.size());

        while(true){

            long start = System.nanoTime();
            //dla każdego obiektu zdefiniowanego w świecie
            GameObject someGameObject;

            boolean flaga = false;

            for(int i=0; i<world.gameObjectList.size(); i++){
                someGameObject = world.gameObjectList.get(i);
                //System.out.println("["+i+"] " + someGameObject.location.position);
                if(someGameObject.location.position.x.isNaN()){
                    flaga = true;
                }

            }
            if(flaga)
                break;


            //KROK 1 - Aktualizacja przemieszczenia
            for(int i=0; i<world.gameObjectList.size(); i++){
                someGameObject = world.gameObjectList.get(i);
                calculateDynamics(someGameObject, deltaTime);
            }

            //KROK 2 - Kolizje
            for(int i=0; i<world.gameObjectList.size(); i++){
                someGameObject = world.gameObjectList.get(i);
                boolean collision = calculateCollisions(someGameObject, world);
            }

            long end = System.nanoTime();
            deltaTime = end - start;
            deltaTime *= 1.0 /100000000;


            /*
            if(collision){
                    world.deregisterFromChunks(someGameObject);
                    world.registerInChunks(someGameObject);
                }

             */
        }
    }

    private void calculateRotation(GameObject someGameObject, double deltaTime) {
        //POBRANIE KĄTA FI
        double fiZ = someGameObject.dynamics.omega.length();

        //USTALENIE OSI OBROTU
        Vector3D rotationAxis = someGameObject.dynamics.omega.copy();
        rotationAxis.normalize();

        //REFERENCYJNY PUNKT WOKÓŁ KTÓREGO WYKONAĆ OBRÓT BRYŁY
        Vector3D referencePoint  = Vector3D.add(someGameObject.location.position.copy(), someGameObject.meshCollider.pointList.get(0).copy());

        rotateObject(someGameObject, rotationAxis, fiZ, null, deltaTime);
    }

    public void rotateObject(GameObject gameObject, Vector3D rotationAxis, double FI, Vector3D referencePoint, double deltaTime){
        if(FI != 0) {
            for (int q = 0; q < gameObject.meshCollider.pointList.size(); q++) {
                //WIERZCHOŁKI ZDEFINIOWANE SĄ W LOKALNYM UKŁADZIE WSPÓŁRZĘDNYCH, DLATEGO OBRACAJĄ SIĘ WOKÓŁ LOKALNEGO ŚRODKA
                Vector3D point = gameObject.meshCollider.pointList.get(q);
                Vector3D pointUpdate = rotatePoint(point,true, rotationAxis, FI, new Vector3D(), deltaTime);
                gameObject.meshCollider.pointList.set(q, pointUpdate);
            }

            boolean moveTheMiddle = referencePoint != null;

            //PRZEMIESZCZAMY ŚRODEK OBIEKTU TYLKO JEŚLI OBRACANY JEST WOKÓŁ INNEGO PUNKTU
            //ŚRODEK Z DEFINICJI ZDEFINIOWANY JEST W NADRZĘDYM UKŁADZIE WSPÓŁRZĘDNYCH
            if(moveTheMiddle) {
                Vector3D point = gameObject.location.position;
                Vector3D pointUpdate = rotatePoint(point,false , rotationAxis, FI, referencePoint, deltaTime);
                gameObject.location.position = pointUpdate;
            }
        }
    }

    public Vector3D rotatePoint(Vector3D point,boolean isPointDefinedInLocaleCoordinateSystem, Vector3D rotationAxis, double FI, Vector3D referencePoint, double deltaTime){
        Vector3D pointUpdate;

        if(isPointDefinedInLocaleCoordinateSystem){
            Vector3D pointDelta = Matrix.multiply(Matrix.rotationMatrix(rotationAxis, FI * deltaTime), point);
            pointUpdate = Vector3D.add(point, pointDelta);
        }
        else{
            Vector3D movedPoint = Vector3D.add(point, referencePoint.multiply(-1));
            Vector3D pointDelta = Matrix.multiply(Matrix.rotationMatrix(rotationAxis, FI * deltaTime), movedPoint);
            pointUpdate = Vector3D.add(point, pointDelta);
        }
        return  pointUpdate;
    }



    /**
     *
     * @param someGameObject
     * @param world
     * @return True if some collisions detected;
     */
    private boolean calculateCollisions(GameObject someGameObject, World world) {
        GameObject anotherGameObject;
        boolean collidedWithAnObject = false;

        for (int i = 0; i < world.gameObjectList.size(); i++) {
            anotherGameObject = world.gameObjectList.get(i);
            if(someGameObject != anotherGameObject){

                if(SRectangle.myType.compareTo(someGameObject.type) == 0){
                    SRectangle sRectObject = (SRectangle) someGameObject;
                    if(SRectangle.myType.compareTo(anotherGameObject.type) == 0){
                        SRectangle aRectObject = (SRectangle) anotherGameObject;

                        if(sphereCollision(sRectObject, aRectObject)){

                            Collision collision = meshCollision(sRectObject,  aRectObject);
                            if((collision != null) && collision.collided){
                                collidedWithAnObject = true;
                                /* 0) wykrycie kolizji
                                    1) wyciągnięcie obiektu z kolizji - czy na pewno?
                                    2) modyfikacja wektorów przyspieszeń (akcja - reakcja)
                                    3) odbicie prędkości
                                    4) obliczenie przesunięcia
                                    5) WYCOFANIE modyfikacji wektorów przyspieszeń
                                 */



                                if(collision.P != null){
                                    resolveCollision(sRectObject, aRectObject, collision);
                                    //resolveFriction(sRectObject, aRectObject, collision);
                                    //resolveRotation(sRectObject, aRectObject, collision);
                                }


                            }
                        }


                    }
                }
            }
        }
        return collidedWithAnObject;
    }

    private void resolveFriction(SRectangle A, SRectangle B, Collision collision) {
        //normalna
        Vector3D n = collision.collisionNormal;
        n.normalize();

        Vector3D rv = Vector3D.minus(B.dynamics.v, A.dynamics.v);
        Vector3D t = Vector3D.tripleXProduct(n, rv, n);
        t.normalize();

        if(Double.isNaN(t.x) || Double.isNaN(t.y) || Double.isNaN(t.z) )
            return;

        double e = Math.min(A.e, B.e);
        Vector3D V1_AB = Vector3D.minus(B.dynamics.v, A.dynamics.v);
        V1_AB.multiply(-(1 + e));

        Vector3D rAP = Vector3D.minus(collision.P, A.location.position);
        Vector3D rBP = Vector3D.minus(collision.P, B.location.position);

        Vector3D rAP_ = new Vector3D(-rAP.y, rAP.x, 0);
        Vector3D rBP_ = new Vector3D(-rBP.y, rBP.x, 0);

        rAP_.normalize();
        rBP_.normalize();

        double partA = Math.pow(Vector3D.dot(rAP_, n), 2) / A.dynamics.I;
        double partB = Math.pow(Vector3D.dot(rBP_, n), 2) / B.dynamics.I;
        double j = (Vector3D.dot(V1_AB, t))/(A.invertedMass + B.invertedMass + partA + partB);

        double jt = -Vector3D.dot(rv, t);
        jt = jt/(A.invertedMass + B.invertedMass);

        double mu = Math.sqrt(Math.pow(A.dynamics.staticFriction, 2) + Math.pow(B.dynamics.staticFriction, 2));

        Vector3D frictionImpulse;
        if(Math.abs(jt) < j * mu){
            frictionImpulse = Vector3D.multiply(t, jt);
        }
        else{
            double dynamicFriction = Math.sqrt(Math.pow(A.dynamics.dynamicFriction, 2) + Math.pow(B.dynamics.dynamicFriction, 2));
            frictionImpulse = Vector3D.multiply(t, -dynamicFriction*j);
        }

        double skala=1;
        Vector3D velocityDeltaA = Vector3D.multiply(frictionImpulse, A.invertedMass*skala);
        Vector3D velocityDeltaB = Vector3D.multiply(frictionImpulse, -B.invertedMass*skala);


        A.dynamics.v.add(velocityDeltaA);
        B.dynamics.v.add(velocityDeltaB);

    }

    private void resolveRotation(SRectangle A, SRectangle B, Collision collision) {
        //normalna
        Vector3D n = collision.collisionNormal;
        n.normalize();

        double e = Math.min(A.e, B.e);

        Vector3D V1_AB = Vector3D.minus(B.dynamics.v, A.dynamics.v);
        V1_AB.multiply(-(1 + e));

        Vector3D rAP = Vector3D.minus(collision.P, A.location.position);
        Vector3D rBP = Vector3D.minus(collision.P, B.location.position);

        Vector3D rAP_ = new Vector3D(-rAP.y, rAP.x, 0);
        Vector3D rBP_ = new Vector3D(-rBP.y, rBP.x, 0);

        rAP_.normalize();
        rBP_.normalize();

        double partA = Math.pow(Vector3D.dot(rAP_, n), 2) / A.dynamics.I;
        double partB = Math.pow(Vector3D.dot(rBP_, n), 2) / B.dynamics.I;

        double j = (Vector3D.dot(V1_AB, n))/(A.invertedMass + B.invertedMass + partA + partB);

        //DELTA OMEGA
        double deltaOmegaA = Vector3D.dot(rAP_ ,Vector3D.multiply(n, j)) / A.dynamics.I;
        double deltaOmegaB = Vector3D.dot(rBP_ ,Vector3D.multiply(n, -j)) / B.dynamics.I;

        //System.out.println("OMEGA " + A.dynamics.omega + " ; DELTA " + deltaOmegaA);

        if(!A.isFixed)
            A.dynamics.omega.add(new Vector3D(0,0, deltaOmegaA));
        if(!B.isFixed)
            B.dynamics.omega.add(new Vector3D(0,0, deltaOmegaB));
    }

    private void resolveCollision(SRectangle A, SRectangle B, Collision collision) {
        //normalna
        Vector3D n = collision.collisionNormal;

        if(Double.isNaN(n.x) || Double.isNaN(n.y) || Double.isNaN(n.z) ){
            System.out.println("NORMAL IS NAN!");
            //return;
        }
        n.normalize();

        double e = Math.min(A.e, B.e);

        Vector3D V1_AB = Vector3D.minus(B.dynamics.v, A.dynamics.v);

        V1_AB.multiply(-(1 + e));

        Vector3D rAP = Vector3D.minus(collision.P, A.location.position);
        Vector3D rBP = Vector3D.minus(collision.P, B.location.position);

        Vector3D rAP_ = new Vector3D(-rAP.y, rAP.x, 0);
        Vector3D rBP_ = new Vector3D(-rBP.y, rBP.x, 0);

        rAP_.normalize();
        rBP_.normalize();

        double partA = Math.pow(Vector3D.dot(rAP_, n), 2) / A.dynamics.I;
        double partB = Math.pow(Vector3D.dot(rBP_, n), 2) / B.dynamics.I;

        double j = (Vector3D.dot(V1_AB, n))/(A.invertedMass + B.invertedMass);

        Vector3D VA_delta = Vector3D.multiply(n, (-1)*j*A.invertedMass);
        Vector3D VB_delta = Vector3D.multiply(n, j*B.invertedMass);

       // System.out.println("A Delta " + VA_delta + "   B  Delta "+VB_delta);


        if(!A.isFixed)
            A.dynamics.v.add(VA_delta);
        if(!B.isFixed)
            B.dynamics.v.add(VB_delta);
    }

    /*
    private void resolveRotation(SRectangle sRectObj, double deltaTime) {
        if(sRectObj.collisionVector != null){
            if(sRectObj.collisionList != null && sRectObj.collisionList.length>1){
                System.out.println("spoczynek");
            }

            Vector3D r = Vector3D.copy(Vector3D.minus(sRectObj.collisionVector, sRectObj.location.position));
            // tak, r jest wektorem ramienia obrotu, jest więc skierowany ze środka obiektu do punktu obrotu

            Vector3D F = Vector3D.copy(sRectObj.dynamics.v);
            Vector3D M = Vector3D.cross(F, r);

            double masa = 1;

            //KONIECZNE JEST WYZNACZANIE WŁAŚCIWEGO MOMENTU BEZWŁĄDNOŚCI
            Vector3D e = Vector3D.multiply(M, 0.1 * (1/masa));
            Vector3D deltaOmega = Vector3D.multiply(e, deltaTime);

            //MODYFIKATOR MOMENTU OBROTOWEGO
            Vector3D T = Vector3D.cross(r.copy(), sRectObj.dynamics.omega.copy());
            T.multiply(1);

            //AKTUALIZOWANIE OMIEGI CIAŁA - OBRÓT WOKÓŁ PUNKTU MASY
            //sRectObj.dynamics.omega.add(deltaOmega);

            //MODYFIKATOR PRZYSPIESZENIA - SIŁA REAKCJI PODŁOŻA, masa równa 1
            double cosAlpha = Vector3D.dot(F, r) / (F.length() * r.length());

            double Fr = F.length() * cosAlpha * masa;
            r.normalize();
            Vector3D FArm = Vector3D.multiply(r, Fr);
            FArm.multiply(-1);
            //jakoże masa jest równa 1 więc, przyspieszenie jest równe sile - dodajemy siłę jako przyspieszenie
            Vector3D wypadkowePrzyspieszenie = Vector3D.add(FArm, T);
            Vector3D predkosc = wypadkowePrzyspieszenie.multiply(0.9);
            //sRectObj.dynamics.tempV = predkosc;
            //rotationEnergyLoss(sRectObj.dynamics.tempV);
            //sRectObj.dynamics.tempA = wypadkowePrzyspieszenie ;

            if(sRectObj.penetrationVector != null){
                double x = Vector3D.dot(sRectObj.dynamics.v, sRectObj.penetrationVector.copy().multiply(-1)) / sRectObj.penetrationVector.copy().length();
                Vector3D predkoscOdbita = sRectObj.penetrationVector.copy();
                predkoscOdbita.normalize();
                predkoscOdbita.multiply(x);
                predkoscOdbita.multiply(2);

                predkoscOdbita.multiply(0.3);

                System.out.println("PRĘDKOŚĆ " +sRectObj.dynamics.v + "  PRZYPIESZENIE " + sRectObj.dynamics.a + " " + wypadkowePrzyspieszenie);
                sRectObj.dynamics.v.add(predkoscOdbita);
            }
            if(sRectObj.collisionList != null){
                //sRectObj.dynamics.omega.multiply(0.1);
            }
        }


    }

     */

    private Collision meshCollision(SRectangle rect0, SRectangle rect1) {
       return GJK(rect0, rect1);
    }



    public Collision GJK(SRectangle rect0, SRectangle rect1){
        List<Vector3D> vertices = new ArrayList<Vector3D>();
        direction = new Vector3D();


        int result = EvolveResult.StillEvolving;
        while(result == EvolveResult.StillEvolving){
            result = evolveSimplex(vertices, rect0, rect1);
        }

        if(result == EvolveResult.FoundIntersection){
            //System.out.println("Przecinają się!");
            Collision collision = EPA(vertices, rect0, rect1);
            return collision;
        }

        return new Collision();
    }

    private Collision EPA(List<Vector3D> vertices, SRectangle rect0, SRectangle rect1) {
        if(checkIfEdgeOnMiddle(vertices)){
            List<Vector3D[]> lista0 = getPointsOnEdge(rect0, rect1);
            if ((lista0 != null) && (lista0.size() > 0)){
                Vector3D[] pointEdge = lista0.get(0);
                double distance = Vector3D.distance(pointEdge[2].copy(), rect0.location.position);

                for(int a=1; a<lista0.size(); a++){
                    Vector3D[] pE = lista0.get(a);
                    double dist = Vector3D.distance(pE[2], rect0.location.position);

                    if(dist < distance){
                        pointEdge = pE;
                        distance = dist;
                    }
                }

                rect0.collisionVector = pointEdge[2];
                Vector3D edgeVector = Vector3D.minus(pointEdge[0], pointEdge[1]);
                Vector3D normal = new Vector3D(-edgeVector.y, edgeVector.x, 0);

                return new Collision(true, true, normal , pointEdge[2]);
            }
        }

        for(int i=0; i<32; i++){
            Edge edge = findClosestEdge(vertices);
            Vector3D support = calculateSupport(edge.normal, rect0, rect1);
            double distance = Vector3D.dot(support, edge.normal);

            Vector3D penetrationVector = Vector3D.multiply(edge.normal, edge.distance);
            Vector3D normal = Vector3D.multiply(edge.normal, edge.distance).copy();

            if(Math.abs(distance - edge.distance) <= 1){
                if(rect0.isFixed){
                    rect1.location.position.add(Vector3D.multiply(edge.normal, edge.distance));
                }
                else if(rect1.isFixed){
                    rect0.location.position.add(Vector3D.multiply(edge.normal, edge.distance * (-1)));
                }
                else{
                    rect0.location.position.add(Vector3D.multiply(edge.normal, (-1) * edge.distance / 2.0 ));
                    rect1.location.position.add(Vector3D.multiply(edge.normal, edge.distance / 2.0));
                }



                //małe sprawdzanko
                Vector3D[] punktRotacjiLista = rect0.supportList(penetrationVector);
                //jeśli wierzchołki rect0 są bliżej środka masy rect0, to znaczy że opierają się w calośći na drugim obiekcie
                if(punktRotacjiLista.length >= 2){
                    rect0.collisionList = punktRotacjiLista;
                }


                //jeśli natomiast wierzchołek rect1 jest bliżej masy rect0, niż któryś z jego wierzchołków, znaczy że zalicza się on do punktów wsparcia

                Vector3D punktRotacji = rect0.support(penetrationVector); //powinien zwracać listę
                Vector3D innyPunkt = rect1.support(penetrationVector.multiply(-1)); //tu też

                double odlegosc = Vector3D.distance(rect0.location.position.copy(), punktRotacji.copy());
                double odlegosc1 = Vector3D.distance(rect0.location.position.copy(), innyPunkt.copy());

                if(odlegosc > odlegosc1){
                    punktRotacji = innyPunkt;
                    //System.out.println("inny punkt");
                }


                //koniec małego sprawdzanka

                if((rect0.collisionVector == null) || !Vector3D.equall(rect0.collisionVector, punktRotacji, 1)){
                    //System.out.println("NIE SĄ ZGRUBSZA PODOBNE");
                    //NIE SĄ ZGRUBSZA PODOBNE
                    //WSPÓŁCZYNIK STRATY ENERGII
                    //rotationEnergyLoss(rect0.dynamics.omega);

                }

                rect0.collisionVector = punktRotacji;
                //System.out.println("punkt rotacji " + punktRotacji);

                normal.normalize();

                if(normal.x.isNaN())
                    System.out.println("[1] --> NAN!");
                return new Collision(true, false, normal, punktRotacji);
            }
            else {
                vertices.add(edge.index, support);
            }

        }

        return null;
    }

    private List<Vector3D[]> getPointsOnEdge(SRectangle rect0, SRectangle rect1) {
        List<Vector3D[]> punkty = new ArrayList<Vector3D[]>();


        for(int a=0; a<rect0.meshCollider.pointList.size(); a++){
            int b = a + 1;
            if(b >= rect0.meshCollider.pointList.size())
                b = 0;

            Vector3D A = rect0.meshCollider.pointList.get(a).copy();
            Vector3D B = rect0.meshCollider.pointList.get(b).copy();

            A.add(rect0.location.position);
            B.add(rect0.location.position);

            for(int c=0; c<rect1.meshCollider.pointList.size(); c++){
                Vector3D C = rect1.meshCollider.pointList.get(c).copy();
                C.add(rect1.location.position);

                if( Math.abs((Vector3D.distance(A, C) + Vector3D.distance(C, B)) - Vector3D.distance(A, B)) < 0.005){
                    Vector3D[] krawedzPunkt = new Vector3D[3];
                    krawedzPunkt[0] = A;
                    krawedzPunkt[1] = B;
                    krawedzPunkt[2] = C;

                    punkty.add(krawedzPunkt);
                }
            }
        }

        for(int a=0; a<rect1.meshCollider.pointList.size(); a++){
            int b = a + 1;
            if(b >= rect1.meshCollider.pointList.size())
                b = 0;

            Vector3D A = rect1.meshCollider.pointList.get(a).copy();
            Vector3D B = rect1.meshCollider.pointList.get(b).copy();

            A.add(rect1.location.position);
            B.add(rect1.location.position);

            for(int c=0; c<rect0.meshCollider.pointList.size(); c++){
                Vector3D C = rect0.meshCollider.pointList.get(c).copy();
                C.add(rect0.location.position);

                if( Math.abs((Vector3D.distance(A, C) + Vector3D.distance(C, B)) - Vector3D.distance(A, B)) < 0.005){
                    Vector3D[] krawedzPunkt = new Vector3D[3];
                    krawedzPunkt[0] = A;
                    krawedzPunkt[1] = B;
                    krawedzPunkt[2] = C;

                    punkty.add(krawedzPunkt);
                }
            }
        }

        if(punkty.size() > 0)
            return punkty;
        else
            return null;
    }


    private void rotationEnergyLoss(Vector3D omega) {
        omega.multiply(0.95);
        //System.out.println("Energy loss");
    }

    private boolean checkIfEdgeOnMiddle(List<Vector3D> vertices) {
        for (int i=0; i<vertices.size(); i++){
            int j = i + 1;
            //zawijanie do pierwszego wierzchołka
            if(j >= vertices.size())
                j = 0;

            int k = j + 1;
            if(k >= vertices.size())
                k = 0;

            //wyciągnięcie wierzchołków a oraz b
            Vector3D a = vertices.get(i).copy();
            Vector3D b = vertices.get(j).copy();

            double a_To_Middle_Len = a.length();
            double b_To_Middle_Len = b.length();

            Vector3D AB = Vector3D.minus(b, a);

            double a_To_b_Len = AB.length();



            if(Math.abs((a_To_Middle_Len + b_To_Middle_Len) - a_To_b_Len) < 0.001){
                Vector3D q = Vector3D.minus(a, b);

                Vector3D prostopadly =  new Vector3D(-q.y, q.x, 0);
                prostopadly.normalize();

                //.out.println("THEY ARE EQUALL!!! " + prostopadly + " a: "+a+"  b: "+b);
                return true;
            }

        }
        //System.out.println("Return NULL");
        return false;
    }

    private Vector3D calculateSupport(Vector3D normal, SRectangle rect0, SRectangle rect1) {
        return Vector3D.difference(rect0.support(normal), rect1.support(Vector3D.multiply(normal, -1)) );
    }

    public Edge findClosestEdge(List<Vector3D> vertices) {
        //TODO jeżeli środek układu nie znajduje się wewnątrz simplexa, to może dawać błędny wynik - do sprawdzenia
        Vector3D a = vertices.get(0);
        Vector3D b = vertices.get(1);

        //bierzemy krawędź
        Vector3D abEdge = new Vector3D(b.x - a.x, b.y - a.y, b.z - a.z);

        //wyznaczamy normalną skierowaną do środka układu
        Vector3D cNormal = Vector3D.tripleXProduct(abEdge, Vector3D.multiply(a, -1), abEdge);
        //System.out.println("abEdge "+abEdge);
        //System.out.println("a      "+a);

        //kierujemy wektor na zewnątrz simplexa
        //normal.multiply(-1);

        //normalizowanie wektora - żeby odległości były
        cNormal.normalize();

        //odległość krawędzi od środka układu - wyjdą wartości {ujemne} dodatnie bo nie odwracam wektora na zewnątrz
        double cDistance = Vector3D.dot(cNormal, Vector3D.multiply(a, -1));
       // System.out.println("cNormal " + cNormal);
        //System.out.println("a       " + a);

        //indeks wierzchołka "a" najbliższej krawędzi
        int cIndex = 0;
        int cJndex = 1;

        for(int i=0; i<vertices.size(); i++){
            int j = i + 1;
            //zawijanie do pierwszego wierzchołka
            if(j >= vertices.size())
                j = 0;

            //wyciągnięcie wierzchołków a oraz b
            a = vertices.get(i);
            b = vertices.get(j);

            //bierzemy krawędź
            abEdge = new Vector3D(b.x - a.x, b.y - a.y, b.z - a.z);

            //wyznaczamy normalną skierowaną do środka układu
            Vector3D normal = Vector3D.tripleXProduct(abEdge, Vector3D.multiply(a, -1), abEdge);

            //kierujemy wektor na zewnątrz simplexa
            //normal.multiply(-1);

            //normalizowanie wektora - żeby odległości były
            normal.normalize();

            //odległość krawędzi od środka układu - wyjdą wartości {ujemne} dodatnie bo nie odwracam wektora na zewnątrz
            double distance = Vector3D.dot(normal, Vector3D.multiply(a, -1));


            if(distance < cDistance){
                cDistance = distance;
                cIndex = i;
                cJndex = j;
                cNormal = normal;
            }
        }

/*
        System.out.println("Closest distance " + cDistance);
        System.out.println("Closest normal   " + cNormal);
        System.out.println("Closest index a  " + cIndex);
        System.out.println("Vertice a " + vertices.get(cIndex));
        System.out.println("Vertice b " + vertices.get(cJndex));
 */

        return new Edge(cDistance, Vector3D.multiply(cNormal, -1) , cJndex);
    }

    class Edge{
        double distance;
        Vector3D normal;
        int index;

        Edge(double distance, Vector3D normal, int index){
            this.distance = distance;
            this.normal = normal;
            this.index = index;
        }
    }

    private boolean addSupport(Vector3D direction, List<Vector3D> vertices, SRectangle rect0, SRectangle rect1 ){
        //System.out.println("Before support " + direction );
        Vector3D newVertice = Vector3D.difference(rect0.support(direction), rect1.support(Vector3D.multiply(direction, -1)) );
        vertices.add(newVertice);
        //System.out.println("VERTICE: " + newVertice);
        return Vector3D.dot(direction, newVertice) >= 0;
    }

    private class EvolveResult{
        final static int NoIntersection = 0;
        final static int FoundIntersection = 1;
        final static int StillEvolving = 2;
    }

    private int evolveSimplex(List<Vector3D> vertices, SRectangle rect0, SRectangle rect1){
        switch (vertices.size()){
            case 0: {
                direction = Vector3D.difference(rect1.location.position, rect0.location.position);
                break;
            }
            case 1: {
                direction.multiply(-1);
                break;
            }
            case 2: {
                Vector3D b = vertices.get(1);
                Vector3D c = vertices.get(0);

                Vector3D cb= Vector3D.difference(b, c);
                //Vector3D c0= c.multiply(-1);
                Vector3D c0= Vector3D.multiply(c, -1);

                direction = Vector3D.cross(cb ,Vector3D.cross(c0, cb));
                //System.out.println("CASE 2 DIRECTION: "+direction);

                //DO TEGO MIEJSCA WSZYSTKO SIĘ ZGADZA, PUNKTY MINKOWSKIEGO POPRAWNIE WYZNACZONE

                //Vector3D vertice = Vector3D.difference(rect0.support(direction), rect1.support(direction.multiply(-1)));
                //vertices.add(vertice);
                break;
            }

            case 3: {
                Vector3D a = vertices.get(2);
                Vector3D b = vertices.get(1);
                Vector3D c = vertices.get(0);


                //Vector3D a0 = a.multiply(-1);
                Vector3D a0 = Vector3D.multiply(a, -1);
                Vector3D ab = Vector3D.difference(b, a);
                Vector3D ac = Vector3D.difference(c, a);

                Vector3D acPerp = Vector3D.tripleXProduct(ac, ab, ac);
                Vector3D abPerp = Vector3D.tripleXProduct(ab, ac, ab);

                double dotAB = Vector3D.dot(abPerp, a0);
                double dotAC = Vector3D.dot(acPerp, a0);

               // System.out.println("DOT pAB " + dotAB);
               // System.out.println("DOT pAC " + dotAC);

                if(Vector3D.dot(abPerp, a0) < 0){
                    //TODO a co z zerem? - skoro dot dodatni (nieujemny) dwóch wektorow mowi, ze są w tym samym kierunku, to nalezy zrobić dot() >= 0 - uwzględnenie krawędzi
                    vertices.remove(c);
                    direction = Vector3D.multiply(abPerp, -1) ; //* -1
                    //Vector3D vertice = Vector3D.difference(rect0.support(direction), rect1.support(direction.multiply(-1)));
                    //vertices.add(vertice);
                }
                else if(Vector3D.dot(acPerp, a0) < 0){
                    vertices.remove(b);
                    direction = Vector3D.multiply(acPerp, -1);
                    //Vector3D vertice = Vector3D.difference(rect0.support(direction), rect1.support(direction.multiply(-1)));
                    //vertices.add(vertice);
                }
                else {
                    //oba doty są dodatnie, znaczy środek leży w trójkącie!
                    //containsOrigin = true;
                    //System.out.println("DOT pAB " + Vector3D.dot(abPerp, a0));
                    //System.out.println("DOT pAC " + Vector3D.dot(acPerp, a0));
                    return  EvolveResult.FoundIntersection;
                }

                //System.out.println("CASE 3 DIRECTION: "+direction);

                break;
            }
        }

        return addSupport(direction, vertices, rect0, rect1) ? EvolveResult.StillEvolving : EvolveResult.NoIntersection;

    }



    private boolean triangleCollision2D(SRectangle rect0, SRectangle rect1) {
        //TODO: TO JEST NA RAZIE JEDYNIE DLA 2D - UWZGLĘDNIĆ 3D
        int DRIFT = 1;

        for(int i=0; i<rect0.meshCollider.triangleList.size(); i++){
            Triangle tri0 = rect0.meshCollider.triangleList.get(i);
            double poleT0 = tri0.field;

            for(int k=0; k<rect1.meshCollider.triangleList.size(); k++){
                Triangle triangle1 = rect1.meshCollider.triangleList.get(k);
                double P0 = tri0.fieldInRelationToPoint(rect0.location.position, triangle1.getVerticeA(rect1.location.position));
                double P1 = tri0.fieldInRelationToPoint(rect0.location.position, triangle1.getVerticeB(rect1.location.position));
                double P2 = tri0.fieldInRelationToPoint(rect0.location.position, triangle1.getVerticeC(rect1.location.position));

                boolean test0 = ((poleT0 - DRIFT) < P0) && (P0 < (poleT0 + DRIFT));
                boolean test1 = ((poleT0 - DRIFT) < P1) && (P1 < (poleT0 + DRIFT));
                boolean test2 = ((poleT0 - DRIFT) < P2) && (P2 < (poleT0 + DRIFT));

                //System.out.println("poleT: "+poleT0+" ; p0 "+tri0.fieldInRelationToPoint(rect0.location.position, triangle1.getVerticeA(rect1.location.position))+" ; p1 "+tri0.fieldInRelationToPoint(rect0.location.position, triangle1.getVerticeB(rect1.location.position))+" ; p2 "+tri0.fieldInRelationToPoint(rect0.location.position, triangle1.getVerticeC(rect1.location.position)) + "    TEST0: "+test0+"  TEST1: "+test1+"  TEST2: "+test2);
                //System.out.println("TEST0: "+test0+"  TEST1: "+test1+"  TEST2: "+test2);

                if(test0 || test1 || test2)
                    return true;
            }
        }
        return false;
    }

    private boolean sphereCollision(SRectangle sGameObj, SRectangle aGameObj) {
        double sRadius = sGameObj.getSphereRadius();
        double aRadius = aGameObj.getSphereRadius();
        double centersDistance = Vector3D.distance(sGameObj.location.position, aGameObj.location.position);

        return (centersDistance <= (sRadius + aRadius));
    }

    private void waitSomeTime() {
        //final long INTERVAL = (int) (100000); //10^7 * 2 daje 50fps
        long start = System.nanoTime();
        long end;
        do{
            end = System.nanoTime();
        }while(start + INTERVAL >= end);
    }

    public void calculateDynamics(GameObject gameObject , double deltaTime) {
        //zmiana położenia

        Vector3D a = Vector3D.add( gameObject.dynamics.a.copy(), gameObject.dynamics.tempA.copy());
        Vector3D v = gameObject.dynamics.v.copy();

        double dx = v.x*deltaTime + (a.x/2)*deltaTime*deltaTime;
        double dy = v.y*deltaTime + (a.y/2)*deltaTime*deltaTime;

        //nowa prędkość
        double dvx = a.x*deltaTime;
        double dvy = a.y*deltaTime;

        //aktualizacja położenia
        gameObject.moveBy(dx, dy);

        //aktualizacja prędkości
        gameObject.dynamics.v.add(dvx, dvy);
    }

    public boolean collision(SRectangle sRectangle, Vector3D p) {
        int X = (int) (sRectangle.location.position.x - sRectangle.width/2);
        int Y = (int) (sRectangle.location.position.y - sRectangle.height/2);

        boolean w1 = (X <= p.x) & (p.x <= X+ sRectangle.width);
        boolean w2 = (Y <= p.y) & (p.y <= Y+ sRectangle.height);
        return w1 & w2;
    }

    public boolean collision(SRectangle sRectangle,double x, double y) {
        int X = (int) (sRectangle.location.position.x - sRectangle.width/2);
        int Y = (int) (sRectangle.location.position.y - sRectangle.height/2);

        boolean w1 = (X <= x) & (x <= X+ sRectangle.width);
        boolean w2 = (Y <= y) & (y <= Y+ sRectangle.height);
        return w1 & w2;
    }

    public boolean collision(SRectangle rectA, SRectangle rectB) {
        //TODO: KOLIZJA BEZ WIERZCHOŁKÓW WEWNATRZ DRUGIEGO PROSTOKĄTA, wziąć to z neta co fajne było

        boolean w1 = (
                collision(rectA, rectB.getLUpperVertex()) ||
                collision(rectA, rectB.getRUpperVertex()) ||
                collision(rectA, rectB.getRLowerVertex()) ||
                collision(rectA, rectB.getLLowerVertex())
        );

        boolean w2 = (
                collision(rectB, rectA.getLUpperVertex()) ||
                collision(rectB, rectA.getRUpperVertex()) ||
                collision(rectB, rectA.getRLowerVertex()) ||
                collision(rectB, rectA.getLLowerVertex())
        );

        return w1 || w2;
    }

    public Vector3D getWeightPointsVector(SRectangle rectA, SRectangle rectB) {
        Vector3D a = rectA.getGeometricMiddle();
        Vector3D b = rectB.getGeometricMiddle();

        return new Vector3D(a.x - b.x, a.y - b.y);
    }


    public void collisionResponse(SRectangle rect, SRectangle rectB) {
        //System.out.println("Collision Response");
        //wersja mocno uproszczona
        Vector3D w = getWeightPointsVector(rect, rectB);
        Vector3D qVector = new Vector3D(rect.width/2 + rectB.width/2, rect.height/2 + rectB.height/2);
        double q = Math.abs(qVector.y / qVector.x);
        double k = Math.abs(w.y / w.x);

        //TODO: dodać pole ciało sprężyste!
        double bounceScale = 1;

        if(q <= k){
            //REACT ON Y-AXIS
            if(Math.signum(w.y) == -1){
                if(0 < rect.dynamics.v.y){
                    rect.dynamics.v.y *= -1 * bounceScale;
                }
            }else{
                if(rect.dynamics.v.y < 0){
                    rect.dynamics.v.y *= -1 * bounceScale;
                }
            }

        }else{
            //REACT ON X-AXIS
            if(Math.signum(w.x) == -1){
                if(0 < rect.dynamics.v.x){
                    rect.dynamics.v.x *= -1 * bounceScale;
                }
            }else{
                if(rect.dynamics.v.x < 0){
                    rect.dynamics.v.x *= -1 * bounceScale;
                }
            }
        }


    }



    public void setWorld(World world) {
        this.world = world;
    }
}
