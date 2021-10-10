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

                World world = new World(1, 80);


                SRectangle bohater = new SRectangle(25, 150, 0, 50, 100);
                SRectangle platforma = new SRectangle(270, 400,0, 500, 20);
                SRectangle kwadracik = new SRectangle(0,0,0,50,50);
                SCircle kolko = new SCircle(0,0,0,25);



                JPanel myPanel = new MyPanel(bohater, platforma, kwadracik, kolko);
                frame.add(myPanel);

                OdswiezanieEkranu odswiezanieEkranu = new OdswiezanieEkranu();
                odswiezanieEkranu.setPanel(myPanel);
                odswiezanieEkranu.start();

                //SilnikFizyki silnikFizyki = new SilnikFizyki();
                //silnikFizyki.add(bohater, platforma);
                //silnikFizyki.start();
            }
        });

    }
}
