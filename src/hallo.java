import javax.swing.*;
import java.util.ArrayList;
import java.util.List;


public class hallo {
    public static void main(String Args[]){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                JFrame frame = new JFrame();
                frame.setSize(900, 700);
                frame.setVisible(true);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                GameObject anonim = (GameObject) new SRectangle();
                SRectangle kwa = (SRectangle) anonim;
                System.out.println(kwa.height);
                kwa.height = 20;
                System.out.println(((SRectangle) anonim).height);

                World world = new World(20, 800);

                //SRectangle bohater   = new SRectangle(100,80, 0, 40, 40);
                int rozmiar = 40;

                List<Vector3D> wierzcholki = new ArrayList<Vector3D>();
                wierzcholki.add(new Vector3D(0, -30, 0));
                wierzcholki.add(new Vector3D(50, -20, 0));
                wierzcholki.add(new Vector3D(60, 5, 0));
                wierzcholki.add(new Vector3D(30, 50, 0));
                wierzcholki.add(new Vector3D(0, 60, 0));
                wierzcholki.add(new Vector3D(-35, 10, 0));
                wierzcholki.add(new Vector3D(-20, -25, 0));

                List<Vector3D> wierzcholki2 = new ArrayList<Vector3D>();
                wierzcholki2.add(new Vector3D(5,-50,0));
                wierzcholki2.add(new Vector3D(50,0,0));
                wierzcholki2.add(new Vector3D(-5,50,0));
                wierzcholki2.add(new Vector3D(-50,0,0));

                SRectangle bohater = new SRectangle(0,0,0, wierzcholki );
                //SRectangle platforma = new SRectangle(0,-200, 0, 80, 200);
                SRectangle platforma = new SRectangle(0,-50, 0, wierzcholki2);
                SRectangle platforma1 = new SRectangle(0,0,0, 300, 50);
                SRectangle platforma2 = new SRectangle(160,-120,0, 300, 20);
                SRectangle platforma3 = new SRectangle(160,-260,0, 20, 200);
                SRectangle platforma4 = new SRectangle(100,-260,0, 60, 20);

                Camera camera = new Camera(0,0,900, 700, new Vector3D(-500, -400));

                //world.add(bohater);
                world.add(platforma);
                world.add(platforma1);
                //world.add(platforma2);
                //world.add(platforma3);
                //world.add(platforma4);

                //bohater.setAcceleration(new Vector3D(0, 5, 0));
                double pi = 3.14159265359;
                //bohater.dynamics.omega = new Vector3D(0, 0, pi*0.051);
                //platforma.dynamics.omega = new Vector3D(0, 0, pi*0.051);
                //platforma.dynamics.omega = new Vector3D(0, pi*0.1, 0);

                SCircle kolko = new SCircle(0,0,0,25);
                MyPanel myPanel = new MyPanel(world, camera, bohater);
                frame.add(myPanel);


                RenderEngine renderEngine = new RenderEngine();
                renderEngine.setPanel(myPanel);
                renderEngine.setWorld(world);
                renderEngine.setCamera(camera);
                renderEngine.start();

                SilnikFizyki silnikFizyki = new SilnikFizyki();
                silnikFizyki.setWorld(world);
                silnikFizyki.start();


                //Vector3D pointUpdate = Matrix.multiply(Matrix.rotationMatrix(new Vector3D(0,0,1), -pi/2), new Vector3D(10, 0, 0));
                //System.out.println(pointUpdate);

                //silnikFizyki.GJK(bohater, platforma2);

                /*
                System.out.println("V a " + vertices.get(0));
                System.out.println("V b " + vertices.get(1));
                System.out.println("V c " + vertices.get(2));
                 */

                //triangleCollision2D(bohater, platforma);

                //trojkatZawieraPunkt00(new Vector3D(0,2,0), new Vector3D(3,1,0), new Vector3D(-3,1,0));
                //trojkatZawieraPunkt00(new Vector3D(0,2,0), new Vector3D(-33,33,0), new Vector3D(-3,-3,0));
                //System.out.println(          Vector3D.dot(Vector3D.tripleXProduct(new Vector3D(3, -3, 0), new Vector3D(1,0,0), new Vector3D(3, -3, 0)), new Vector3D(3,-3))      );
            }
        });

    }
    static void trojkatZawieraPunkt00(Vector3D a, Vector3D b, Vector3D c){
        System.out.println("hallo");
        Vector3D a0 = a.multiply(-1);
        Vector3D ab = Vector3D.difference(b, a);
        Vector3D ac = Vector3D.difference(c, a);

        System.out.println("a0 " + a0 );
        System.out.println("ab " + ab );
        System.out.println("ac " + ac );

        Vector3D abPerp = Vector3D.tripleXProduct(ab, ac, ab);
        Vector3D acPerp = Vector3D.tripleXProduct(ac, ab, ac);
        System.out.println("pAB " + abPerp);
        System.out.println("pAC " + acPerp);

        System.out.println("DOT pAB " + Vector3D.dot(abPerp, a0));
        System.out.println("DOT pAC " + Vector3D.dot(acPerp, a0));

        if(Vector3D.dot(abPerp, a0) < 0){

        }
        else if(Vector3D.dot(acPerp, a0) < 0){

        }
        else {
            System.out.println("Trójkąt zawiera środek układu!");
        }

    }

    static  boolean triangleCollision2D(SRectangle rect0, SRectangle rect1) {
        //TODO: TO JEST NA RAZIE JEDYNIE DLA 2D - UWZGLĘDNIĆ 3D
        int DRIFT = 1;


        for(int i=0; i<rect0.meshCollider.triangleList.size(); i++){
            Triangle tri0 = rect0.meshCollider.triangleList.get(i);
            double poleT0 = tri0.field;

            System.out.println("POLE A: "+tri0.field+"; POLE A':"+tri0.fieldInRelationToPoint(rect0.location.position, new Point(110, 90, 0)));

            for(int k=0; k<rect1.meshCollider.triangleList.size(); k++){
                //TODO +- DRIFT
                Triangle triangle1 = rect1.meshCollider.triangleList.get(k);
                double P0 = tri0.fieldInRelationToPoint(rect0.location.position, triangle1.getVerticeA(rect1.location.position));
                double P1 = tri0.fieldInRelationToPoint(rect0.location.position, triangle1.getVerticeB(rect1.location.position));
                double P2 = tri0.fieldInRelationToPoint(rect0.location.position, triangle1.getVerticeC(rect1.location.position));

                boolean test0 = ((poleT0 - DRIFT) < P0) && (P0 < (poleT0 + DRIFT));
                boolean test1 = ((poleT0 - DRIFT) < P1) && (P1 < (poleT0 + DRIFT));
                boolean test2 = ((poleT0 - DRIFT) < P2) && (P2 < (poleT0 + DRIFT));

                System.out.println("poleT: "+poleT0+" ; p0 "+tri0.fieldInRelationToPoint(rect0.location.position, triangle1.getVerticeA(rect1.location.position))+" ; p1 "+tri0.fieldInRelationToPoint(rect0.location.position, triangle1.getVerticeB(rect1.location.position))+" ; p2 "+tri0.fieldInRelationToPoint(rect0.location.position, triangle1.getVerticeC(rect1.location.position)) );
                System.out.println("TEST0: "+test0+"  TEST1: "+test1+"  TEST2: "+test2);

                if(test0 || test1 || test2)
                    return true;
            }
        }
        return false;
    }
}
