import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class MyPanel extends JPanel implements KeyListener, MouseMotionListener, MouseListener{
    World world;
    Graphics graphics;
    boolean repaint = false;
    char znak;
    Vector3D cameraVector;
    Camera camera;
    GameObject bohater;
    Vector3D mouseTip = new Vector3D();

    MyPanel(World world, Camera camera, GameObject bohater){
        super();
        this.world = world;
        this.camera = camera;
        this.bohater = bohater;

        setBackground(new Color(44, 52, 52));
        setLayout(null);
        setFocusable(true);
        addKeyListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);
    }


    @Override
    protected void paintComponent(Graphics g) {
        graphics = g;
        if(repaint)
            super.paintComponent(g);

        requestFocus();
        Graphics2D g2d = (Graphics2D) g;

        //GameObject g0 = world.gameObjectList.get(0);
        //GameObject g1 = world.gameObjectList.get(1);

        //rysowanie kursora
        int R = 20;
        int r = 5;
        int move = 0;
        g2d.setStroke(new BasicStroke(1f));
        g2d.setPaint(new Color(164, 102, 123));
        g.drawLine(mouseTip.x.intValue() - R-move, mouseTip.y.intValue()-move, mouseTip.x.intValue() - r-move, mouseTip.y.intValue()-move);
        g.drawLine(mouseTip.x.intValue() + r-move, mouseTip.y.intValue()-move, mouseTip.x.intValue() + R-move, mouseTip.y.intValue()-move);
        g.drawLine(mouseTip.x.intValue()-move, mouseTip.y.intValue() - R-move, mouseTip.x.intValue()-move, mouseTip.y.intValue() - r-move);
        g.drawLine(mouseTip.x.intValue()-move, mouseTip.y.intValue() + r-move, mouseTip.x.intValue()-move, mouseTip.y.intValue() + R-move);


        // układ współrzędnych
        g2d.setPaint(new Color(103, 107, 107));
        g.drawLine(cameraVector.x.intValue(), cameraVector.y.intValue()-300, cameraVector.x.intValue(), cameraVector.y.intValue()+300);
        g.drawLine(cameraVector.x.intValue()+300, cameraVector.y.intValue(), cameraVector.x.intValue()-300, cameraVector.y.intValue());

        GameObject someGameObject;
        for (int i = 0; i < world.gameObjectList.size(); i++) {
            someGameObject = world.gameObjectList.get(i);

            if(SRectangle.myType.compareTo(someGameObject.type) == 0){
                //((SRectangle) someGameObject).updateSkin();
                ((SRectangle) someGameObject).moveByCameraVector(cameraVector);
            }



            g2d.setPaint(new Color(255, 213, 0));
            g.drawOval(someGameObject.location.position.x.intValue()+cameraVector.x.intValue(), someGameObject.location.position.y.intValue()+cameraVector.y.intValue(), 2, 2);


            g2d.setPaint(new Color(210, 204, 210));
            g2d.setStroke(new BasicStroke(1f));

            Vector3D massMiddle = new Vector3D(0,0,0);

            for(int h=0; h<someGameObject.meshCollider.pointList.size(); h++){
                int k=h+1;
                if(k>=someGameObject.meshCollider.pointList.size())
                    k = 0;
                Vector3D a = Vector3D.addVectors(someGameObject.location.position, someGameObject.meshCollider.pointList.get(h));
                Vector3D b = Vector3D.addVectors(someGameObject.location.position, someGameObject.meshCollider.pointList.get(k));

                massMiddle = Vector3D.add(massMiddle, a);
                g.drawLine(a.x.intValue()+cameraVector.x.intValue(), a.y.intValue()+cameraVector.y.intValue(), b.x.intValue()+cameraVector.x.intValue(), b.y.intValue()+cameraVector.y.intValue());
            }




            //środek obiektu
            g2d.setPaint(new Color(0, 255, 208));
            g.drawOval(someGameObject.location.position.x.intValue()+cameraVector.x.intValue(), someGameObject.location.position.y.intValue()+cameraVector.y.intValue(), 4, 4);


            if(( someGameObject.collisionVector != null) && (someGameObject.location.position !=null)){

                Vector3D cV = someGameObject.collisionVector.copy();
                Vector3D p = someGameObject.location.position.copy();
                //wektor przecięcia
                g2d.setPaint(new Color(0, 253, 214));
                g.drawLine(cameraVector.x.intValue()+ cV.x.intValue(), cameraVector.y.intValue() +cV.y.intValue(), cameraVector.x.intValue()+p.x.intValue(), cameraVector.y.intValue()+p.y.intValue());

                g2d.setPaint(new Color(253, 0, 152));
                g.drawOval(cameraVector.x.intValue()+ cV.x.intValue(), cameraVector.y.intValue() +cV.y.intValue(), 4, 4);

            }

            if((someGameObject.collisionVector != null) && (someGameObject.penetrationVector != null)){
                //wektor przecięcia
                g2d.setPaint(new Color(0, 59, 253));
                g.drawLine(cameraVector.x.intValue()+ someGameObject.collisionVector.x.intValue(), cameraVector.y.intValue() +someGameObject.collisionVector.y.intValue(), cameraVector.x.intValue()+ someGameObject.collisionVector.x.intValue() + someGameObject.penetrationVector.x.intValue(), cameraVector.y.intValue() +someGameObject.collisionVector.y.intValue() + someGameObject.penetrationVector.y.intValue());

            }


        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        znak = e.getKeyChar();
        //System.out.println("BUTTON");

        int skok = 5;

/*

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

 */

        double wartosc = 3;

        if(znak == 'w'){
            bohater.dynamics.a.y = -wartosc;
        }else if(znak == 's'){
            bohater.dynamics.a.y = wartosc;
        }else if(znak == 'a'){
            bohater.dynamics.a.x = -wartosc*3;
        }else if(znak == 'd') {
            bohater.dynamics.a.x = wartosc*3;
        }




    }

    @Override
    public void keyPressed(KeyEvent e) {
        //System.out.println("PRESSED " + e.getKeyChar() + " ID "+e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        bohater.dynamics.a = new Vector3D();

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

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseTip.x = Double.valueOf(e.getX());
        mouseTip.y = Double.valueOf(e.getY());
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseTip.x = Double.valueOf(e.getX());
        mouseTip.y = Double.valueOf(e.getY());
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        e.getButton();

        java.util.List<Vector3D> wierzcholki = null;
        SRectangle platforma1 = null;

        if(e.getButton() == 1){
            wierzcholki = new ArrayList<Vector3D>();
            wierzcholki.add(new Vector3D(5,-50,0));
            wierzcholki.add(new Vector3D(50,0,0));
            wierzcholki.add(new Vector3D(-5,50,0));
            wierzcholki.add(new Vector3D(-50,0,0));

            platforma1 =  new SRectangle(mouseTip.x-cameraVector.x, mouseTip.y-cameraVector.y, 0, 100,100);
            //platforma1.dynamics.I = 700;

        }else if(e.getButton() == 3){
            wierzcholki = new ArrayList<Vector3D>();
            wierzcholki.add(new Vector3D(-100,60,0));
            wierzcholki.add(new Vector3D(700,-70,0));
            wierzcholki.add(new Vector3D(680,60,0));
            wierzcholki.add(new Vector3D(-120,140,0));

            platforma1 =  new SRectangle(mouseTip.x-cameraVector.x, mouseTip.y-cameraVector.y, 0, 40 , 60);
            //platforma1.dynamics.I = 700;
        }




        platforma1.setAcceleration(new Vector3D(0, 1, 0));
        platforma1.setMass(10, false);
        platforma1.e = 0.6;
        platforma1.dynamics.I = (1/6.0)*100*100*10.0; // 1/6 * masa * a^2 << moment bezwładności dla sześcianu w osi Z

        world.add(platforma1);
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
