import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class MyPanel extends JPanel implements KeyListener{
    World world;
    Graphics graphics;
    boolean repaint = false;
    char znak;
    Vector3D cameraVector;
    Camera camera;
    GameObject bohater;

    MyPanel(World world, Camera camera, GameObject bohater){
        super();
        this.world = world;
        this.camera = camera;
        this.bohater = bohater;

        setBackground(new Color(180, 219, 25));
        setLayout(null);
        setFocusable(true);
        addKeyListener(this);
    }


    @Override
    protected void paintComponent(Graphics g) {
        graphics = g;
        if(repaint)
            super.paintComponent(g);

        requestFocus();
        Graphics2D g2d = (Graphics2D) g;

        GameObject someGameObject;
        for (int i = 0; i < world.gameObjectList.size(); i++) {
            someGameObject = world.gameObjectList.get(i);

            if(SRectangle.myType.compareTo(someGameObject.type) == 0){
                //((SRectangle) someGameObject).updateSkin();
                ((SRectangle) someGameObject).moveByCameraVector(cameraVector);
            }


            g2d.setPaint(new Color(148, 1, 221));
            g2d.fill(someGameObject.skin);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        znak = e.getKeyChar();

        int skok = 5;



        if(znak == 'w'){
            camera.location.position.y  += -skok;
            bohater.location.position.y += -skok;
            //camera.location = bohater.location;
        }else if(znak == 's'){
            camera.location.position.y  += skok;
            bohater.location.position.y += skok;
            //camera.location = bohater.location;
        }else if(znak == 'a'){
            camera.location.position.x  += -skok;
            bohater.location.position.x += -skok;
            //camera.location = bohater.location;
        }else if(znak == 'd') {
            camera.location.position.x  += skok;
            bohater.location.position.x += skok;
            //camera.location = bohater.location;
        }

        if(e.getKeyCode() == 38){

        }


        System.out.println("TYPED " + e.getKeyChar() + " ID "+e.getKeyCode());


    }

    @Override
    public void keyPressed(KeyEvent e) {
        //System.out.println("PRESSED " + e.getKeyChar() + " ID "+e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        /*
        System.out.println("RELEASED " + e.getKeyChar() + " ID "+e.getKeyCode());

        if(znak == 'w'){
            bohater.dynamics.a.y = 3.0;
        }else if(znak == 's'){
            bohater.dynamics.a.y = 3.0;
        }else if(znak == 'a'){
            bohater.dynamics.a.x = 0.0;
        }else if(znak == 'd') {
            bohater.dynamics.a.x = 0.0;
        }

         */
    }

    public void setCameraVector(Vector3D cameraVector) {
        this.cameraVector = cameraVector;
    }
}
