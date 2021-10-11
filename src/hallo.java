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

                SRectangle bohater   = new SRectangle(59,30, 0, 40, 40);
                SRectangle platforma = new SRectangle(60,120,0, 580, 20);
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
            }
        });

    }
}
