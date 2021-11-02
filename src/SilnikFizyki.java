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
            //dla każdego obiektu zdefiniowanego w świecie
            GameObject someGameObject;
            for (int i = 0; i < world.gameObjectList.size(); i++) {
                someGameObject = world.gameObjectList.get(i);

                //TODO: TO TYLKO PROWIZORKA!
                if(someGameObject.isFixed)
                    continue;

                calculateDynamics(someGameObject, deltaTime);
                calculateRotation(someGameObject, deltaTime);
                boolean collision = calculateCollisions(someGameObject, world);

                if(collision){
                    world.deregisterFromChunks(someGameObject);
                    world.registerInChunks(someGameObject);
                }

            }
            //TODO: obliczenia powinny byc wykonywane dla wszystkich obiektów w tym samym momencie, np poprzez stworzenie listy danych LOKALIZACJI dla każdego z obiektóœ i uakualnienie dopiero na końcu
            waitSomeTime();
        }
    }

    private void calculateRotation(GameObject someGameObject, double deltaTime) {
        //POBRANIE KĄTA FI
        double fiZ = someGameObject.dynamics.omega.length();

        //USTALENIE OSI OBROTU
        Vector3D rotationAxis = someGameObject.dynamics.omega.copy();
        rotationAxis.normalize();

        //REFERENCYJNY PUNKT WOKÓŁ KTÓREGO WYKONAĆ OBRÓT BRYŁY
        Vector3D referencePoint  = Vector3D.add(someGameObject.location.position.copy(), someGameObject.meshCollider.pointList.get(0).copy().multiply(2));

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
                Vector3D pointUpdate = rotatePoint(point,false ,rotationAxis, FI, referencePoint, deltaTime);
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
                    SRectangle sRectObj = (SRectangle) someGameObject;
                    if(SRectangle.myType.compareTo(anotherGameObject.type) == 0){
                        SRectangle aRectObj = (SRectangle) anotherGameObject;

                        if(sphereCollision(sRectObj, aRectObj)){
                            if(meshCollision(sRectObj,  aRectObj)){
                                collidedWithAnObject = true;
                                /* 0) wykrycie kolizji
                                    1) wyciągnięcie obiektu z kolizji - czy na pewno?
                                    2) modyfikacja wektorów przyspieszeń (akcja - reakcja)
                                    3) odbicie prędkości
                                    4) obliczenie przesunięcia
                                    5) WYCOFANIE modyfikacji wektorów przyspieszeń
                                 */
                                resolveRotation(sRectObj, deltaTime);
                                //collisionResponse((SRectangle) someGameObject, (SRectangle) anotherGameObject);
                            }
                        }


                    }
                }
            }
        }
        return collidedWithAnObject;
    }

    private void resolveRotation(SRectangle sRectObj, double deltaTime) {
        Vector3D r = Vector3D.copy(Vector3D.minus(sRectObj.location.position  ,sRectObj.collisionVector));
        r.multiply(-1);
        Vector3D F = Vector3D.copy(sRectObj.dynamics.a);
        Vector3D M = Vector3D.cross(F, r);

        Vector3D e = Vector3D.multiply(M, 0.0015);
        Vector3D deltaOmega = Vector3D.multiply(e, deltaTime);

        //AKTUALIZOWANIE OMIEGI CIAŁA - OBRÓT WOKÓŁ PUNKTU MASY
        sRectObj.dynamics.omega.add(deltaOmega);

        //WSPÓŁCZYNIK STRATY ENERGII
        //sRectObj.dynamics.omega.multiply(0.9999);

        Vector3D deltaFi    = Vector3D.multiply(deltaOmega, deltaTime);

        /*

        //omegaFI - to się żle nazywa
        double omegaFI = sRectObj.collisionVectorOmega.z * deltaTime;

        //TODO: DORAŹNIE NAPRAWIONE NIEPRAWIDŁOWE PRZESUWANIE SIĘ OBIEKTU PODCZAS OPADANIA - ZDIAGNOZOWAĆ PRZYCZYNĘ, PRZECIWNY WEKTOR
        Vector3D rotationPoint = Vector3D.add(sRectObj.location.position, Vector3D.multiply(sRectObj.collisionVector, -1) );
        M.normalize();

        rotateObject(sRectObj, M, deltaFi.z + omegaFI, rotationPoint, deltaTime);
        //System.out.println("resolve rotation");

        sRectObj.collisionVectorOmega.add(deltaOmega);

         */
    }

    private boolean meshCollision(SRectangle rect0, SRectangle rect1) {
       // if(triangleCollision2D(rect0, rect1))
        //    return true;

        if (GJK(rect0, rect1))
            return true;

        return false;
    }



    public boolean GJK(SRectangle rect0, SRectangle rect1){
        List<Vector3D> vertices = new ArrayList<Vector3D>();
        direction = new Vector3D();

        int result = EvolveResult.StillEvolving;
        while(result == EvolveResult.StillEvolving){
            result = evolveSimplex(vertices, rect0, rect1);
        }

        if(result == EvolveResult.FoundIntersection){
            //System.out.println("Przecinają się!");
            EPA(vertices, rect0, rect1);
        }

        return result == EvolveResult.FoundIntersection;
    }

    private void EPA(List<Vector3D> vertices, SRectangle rect0, SRectangle rect1) {
        for(int i=0; i<10; i++){
            //zanim podejdziemy do sprawdzania wektora, trzeba zobaczyć czy przypadkiem żadna z krawędzi nie znajduje się w środku układu współrzędnych
            if (checkIfEdgeOnMiddle(vertices)){
                //TODO: uwzględnić aktualizację wektora kolizji
                //System.out.println("return");
                return;
            }


            Edge edge = findClosestEdge(vertices);
            Vector3D support = calculateSupport(edge.normal, rect0, rect1);
            double distance = Vector3D.dot(support, edge.normal);

            Vector3D penetrationVector = Vector3D.multiply(edge.normal, edge.distance);

           // System.out.println("EDGE DISTANCE: " + edge.distance);
            if(Math.abs(distance - edge.distance) <= 1){
                //System.out.println("PENETRATION VECTOR: " + penetrationVector);
                rect0.location.position.add(Vector3D.multiply(penetrationVector, -1));

                //TODO: uwzględnić przypadek równowagi albo dwóch wierzchołków wspierających!
                //TODO: przypięcie do faktycznego wierzchołka!

                Vector3D punktRotacji = rect0.support(penetrationVector);

                if(Vector3D.equall(rect0.collisionVector, punktRotacji, 1)){
                    //znaczy że są zgrubsza podobne
                }
                else{
                    //NIE SĄ ZGRUBSZA PODOBNE
                    //WSPÓŁCZYNIK STRATY ENERGII
                    System.out.println("PRZEKRĘCENIE NA DRUGI BOK!");
                    rect0.dynamics.omega.multiply(0.25);
                }

                rect0.collisionVector = punktRotacji;
                return;
            }
            else {
                vertices.add(edge.index, support);
            }

        }
    }

    private boolean checkIfEdgeOnMiddle(List<Vector3D> vertices) {
        for (int i=0; i<vertices.size(); i++){
            int j = i + 1;
            //zawijanie do pierwszego wierzchołka
            if(j >= vertices.size())
                j = 0;

            //wyciągnięcie wierzchołków a oraz b
            Vector3D a = vertices.get(i);
            Vector3D b = vertices.get(j);

            Vector3D aCopy = new Vector3D(a.x, a.y, a.z);
            Vector3D bCopy = new Vector3D(b.x, b.y, b.z);

            //normalizowanie wektora a oraz b
            aCopy.normalize();
            bCopy.normalize();

            //odwracanie dowolnego z nich
            aCopy.multiply(-1);

            if(Vector3D.equall(aCopy, bCopy)){
                //System.out.println("THEY ARE EQUALL!!!");
                return true;
            }

        }
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
        double dx = gameObject.dynamics.v.x*deltaTime + (gameObject.dynamics.a.x/2)*deltaTime*deltaTime;
        double dy = gameObject.dynamics.v.y*deltaTime + (gameObject.dynamics.a.y/2)*deltaTime*deltaTime;

        //nowa prędkość
        double dvx = gameObject.dynamics.a.x*deltaTime;
        double dvy = gameObject.dynamics.a.y*deltaTime;

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
