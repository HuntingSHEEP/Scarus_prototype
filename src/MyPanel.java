import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class MyPanel extends JPanel implements KeyListener{
    GameObject bohater, platforma;
    char znak;
    GameObject kwadrat;
    GameObject kolko;

    MyPanel(GameObject obiekt,GameObject platforma, GameObject kwarat, GameObject kolko){
        super();
        bohater = obiekt;
        this.platforma = platforma;
        this.kwadrat = kwarat;
        this.kolko = kolko;

        setBackground(new Color(180, 219, 25));
        setLayout(null);
        setFocusable(true);

        addKeyListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        requestFocus();
        Graphics2D g2d = (Graphics2D) g;

        //rysowanie bohatera
        g2d.setPaint(new Color(5, 211, 236));
        g2d.fill(bohater.skin);

        //rysowanie platformy
        g2d.setPaint(new Color(1, 121, 118));
        g2d.fill(platforma.skin);

        //rysowanie kwadratu
        g2d.setPaint(new Color(221, 1, 89));
        g2d.fill(kwadrat.skin);

        //rysowanie kolka
        g2d.setPaint(new Color(1, 45, 221));
        g2d.fill(kolko.skin);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        znak = e.getKeyChar();


        if(znak == 'w'){
            bohater.dynamics.a.y = -4.0;
        }else if(znak == 's'){
            bohater.dynamics.a.y = 4.0;
        }else if(znak == 'a'){
            bohater.dynamics.a.x = -2.0;
        }else if(znak == 'd') {
            bohater.dynamics.a.x = 2.0;
        }
        System.out.println("TYPED " + e.getKeyChar() + " ID "+e.getKeyCode());
    }

    @Override
    public void keyPressed(KeyEvent e) {
        //System.out.println("PRESSED " + e.getKeyChar() + " ID "+e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
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
    }
}
