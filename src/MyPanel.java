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

        setBackground(new Color(29, 31, 31));
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

        //GameObject g0 = world.gameObjectList.get(0);
        //GameObject g1 = world.gameObjectList.get(1);

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


            g2d.setPaint(new Color(255, 0, 235));
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

            // srodek masy
            massMiddle.multiply(1.0/someGameObject.meshCollider.pointList.size());
            g2d.setPaint(new Color(0, 255, 217));
            g.drawOval(massMiddle.x.intValue()+cameraVector.x.intValue(), massMiddle.y.intValue()+cameraVector.y.intValue(), 3, 3);

            //PUNKT OBROTU
            Vector3D referencePoint  = Vector3D.add(someGameObject.location.position.copy(), someGameObject.meshCollider.pointList.get(0).copy().multiply(2));
            g2d.setPaint(new Color(1, 255, 141));
            g.drawOval(referencePoint.x.intValue()+cameraVector.x.intValue(),referencePoint.y.intValue()+cameraVector.y.intValue(), 3, 3);


            Vector3D srodek = new Vector3D(100,100, 0);

            //linia od środka obrotu do środka obiektu
            //g2d.setPaint(new Color(63, 255, 1));
            //g.drawLine(cameraVector.x.intValue()+ srodek.x.intValue(), cameraVector.y.intValue() +srodek.y.intValue(), cameraVector.x.intValue()+someGameObject.location.position.x.intValue(), cameraVector.y.intValue()+someGameObject.location.position.y.intValue());

            //środek obiektu
            g2d.setPaint(new Color(0, 255, 208));
            g.drawOval(someGameObject.location.position.x.intValue()+cameraVector.x.intValue(), someGameObject.location.position.y.intValue()+cameraVector.y.intValue(), 4, 4);



            //wektor przecięcia
            g2d.setPaint(new Color(221, 1, 89));
            g.drawLine(cameraVector.x.intValue()+ someGameObject.collisionVector.x.intValue(), cameraVector.y.intValue() +someGameObject.collisionVector.y.intValue(), cameraVector.x.intValue()+someGameObject.location.position.x.intValue(), cameraVector.y.intValue()+someGameObject.location.position.y.intValue());



            //środek obrotu
            //g2d.setPaint(new Color(255, 0, 0));
            //g.drawOval(cameraVector.x.intValue() + srodek.x.intValue(), cameraVector.y.intValue() + srodek.y.intValue(), 4, 4);



/*
            g2d.setPaint(new Color(124, 255, 0));
            for(int c=0; c<g0.meshCollider.pointList.size(); c++){
                for(int z=0; z<g1.meshCollider.pointList.size(); z++){
                    Vector3D p = g0.meshCollider.pointList.get(c);
                    Vector3D r = g1.meshCollider.pointList.get(z);

                    Vector3D p0 = Vector3D.add(p, g0.location.position);
                    Vector3D p1 = Vector3D.add(r, g1.location.position);

                    Vector3D mink = Vector3D.difference(p0, p1);
                    g.drawOval(mink.x.intValue() + cameraVector.x.intValue(), mink.y.intValue()+cameraVector.y.intValue(), 2, 2);


                }
            }

 */

            //g2d.fill(someGameObject.skin);
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


        //System.out.println("TYPED " + e.getKeyChar() + " ID "+e.getKeyCode());


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
