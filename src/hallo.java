import javax.swing.*;


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

                SRectangle bohater   = new SRectangle(100,40, 0, 40, 40);
                SRectangle platforma = new SRectangle(120,120, 0, 200, 20);
                SRectangle platforma1 = new SRectangle(-320,20,0, 580, 20);
                SRectangle platforma2 = new SRectangle(160,-100,0, 580, 20);

                Camera camera = new Camera(0,0,900, 700, new Vector3D(-400, -300));



                world.add(bohater);
                world.add(platforma);
                world.add(platforma1);
                world.add(platforma2);

                bohater.setAcceleration(new Vector3D(0, 9, 0));

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

                //triangleCollision2D(bohater, platforma);
            }
        });

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
