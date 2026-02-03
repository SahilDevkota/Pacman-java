import javax.swing.JFrame;
import java.awt.*;

public class App {

    public static void main(String[] args) throws Exception{




        //Game board settings
        int rowCount = 21;
        int columnCount = 19;
        int tileSize = 32;
        int boardWidth = columnCount * tileSize;
        int boardHeight = rowCount * tileSize;

        //Create JFrame

        JFrame frame = new JFrame("Pac Man");
        //frame.setVisible(true);
        frame.setSize(boardWidth,boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        PacMan pacmanGame = new PacMan();       //Creating a PacMan class
        frame.add(pacmanGame);                  //Adding the PacMan class to frame
        frame.pack();                           //Makes sure that the frame is big enough to fit everything
        pacmanGame.requestFocus();              //Let the pacmanGame receive Keyboard input
        frame.setVisible(true);                 //Make sure the frame is visible



    }
}
