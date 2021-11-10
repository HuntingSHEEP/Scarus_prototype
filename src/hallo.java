import javax.swing.*;
import java.util.ArrayList;
import java.util.List;


public class hallo {
    public static void main(String Args[]){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                JFrame frame = new JFrame();
                frame.setSize(1900, 1700);
                frame.setVisible(true);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                World world = new World(20, 800);

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


                List<Vector3D> wierzcholki3 = new ArrayList<Vector3D>();
                wierzcholki3.add(new Vector3D(-100,-20,0));
                wierzcholki3.add(new Vector3D(700,-70,0));
                wierzcholki3.add(new Vector3D(680,60,0));
                wierzcholki3.add(new Vector3D(-120,140,0));

                SRectangle bohater = new SRectangle(0,0,0, wierzcholki );
                //SRectangle platforma = new SRectangle(150,-230, 0, 200, 60);
                SRectangle platforma = new SRectangle(250,-230,0, wierzcholki2);
                SRectangle platforma1 = new SRectangle(500,100, 0, 1300, 60);
                //SRectangle platforma1 = new SRectangle(50,0, 0, wierzcholki3);

                world.add(platforma);
                world.add(platforma1);


                double pi = 3.14159265359;
                //platforma.dynamics.omega = new Vector3D(0, 0, -pi*0.3);
                platforma.setAcceleration(new Vector3D(0, 1, 0));
                //platforma.setVelocity(new Vector3D( 11, 0, 0));

                platforma1.isFixed = true;


                Camera camera = new Camera(0,0,900, 700, new Vector3D(-500, -400));
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
