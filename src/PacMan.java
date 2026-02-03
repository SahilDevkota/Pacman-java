import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Random;

public class PacMan extends JPanel implements ActionListener, KeyListener {



    class Block {
        int x,y;              //Position
        int width, height;      //Size
        Image image;            //Current image that is displayed
        Image originalImage;    //Copy of current image that is used in reset().

        int startX;                    //Stores the starting position for reset()
        int startY;

        char direction = 'U';
        int velocityX = 0;      //Speed in X
        int velocityY = 0;      //Speed in Y



        Block(Image image, int x, int y, int width, int height) {           //Block constructor
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
            this.originalImage = image;
        }

        void updateDirection(char direction){               //It updates the direction based on four characters i.e 'U', 'D','L','R' .
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();                               //Updates the velocity
            this.x += this.velocityX;
            this.y += this.velocityY;

            for(Block wall: walls){
                if(collision(this,wall)){
                    //If there is collision, then move back and reset direction
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }




        }

        void updateVelocity(){          //Updating the velocity according to the character
            if(this.direction =='U'){
                this.velocityX =0;
                this.velocityY = -tileSize/4;
            }
            else if(this.direction == 'D'){
                this.velocityX = 0;
                this.velocityY = tileSize/4;
            }
            else if(this.direction =='L'){
                this.velocityX = -tileSize/4;
                this.velocityY = 0;
            }
            else if(this.direction == 'R'){
                this.velocityX = tileSize / 4;
                this.velocityY = 0;

            }
        }

        void reset(){                               //This will reset the character in their original position and image.
            this.x = this.startX;
            this.y = this.startY;
            this.image = this.originalImage;
        }

    }

    //Game board settings
    private int rowCount = 21;
    private int columnCount = 19;
    private int tileSize = 32;
    private int boardWidth = columnCount * tileSize;
    private int boardHeight = rowCount * tileSize;

    //Game images
    private Image wallImage;
    private Image blueGhostImage;
    private Image orangeGhostImage;
    private Image pinkGhostImage;
    private Image redGhostImage;
    private Image cherryImage;
    private Image powerFoodImage;
    private Image scaredGhostImage;
    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;
    private Image pacmanRightImage;


    //X = wall, O = skip, P = pac man, ' ' = food
    //Ghosts: b = blue, o = orange, p = pink, r = red,*= specialPower
    private String[] tileMap = {
            "XXXXXXXXXXXXXXXXXXX",
            "*        X        *",
            "X XX XXX X XXX XX X",
            "X                 X",
            "X XX X XXXXX X XX X",
            "X    X       X    X",
            "XXXX XXXX XXXX XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXrXX X XXXX",
            "O       bpo       O",
            "XXXX X XXXXX X XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXXXX X XXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X  X     P     X  X",
            "XX X X XXXXX X X XX",
            "X*   X   X   X   *X",
            "X XXXXXX X XXXXXX X",
            "X                 X",
            "XXXXXXXXXXXXXXXXXXX"
    };

    //Game object sets
    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    HashSet<Block> specialPower;
    Block pacman;


    Timer gameLoop;         //Timer to start the game
    char[] directions = {'U','L','D','R'}; // up down left right
    Random random = new Random();

    //Game state
    int score = 0;
    int lives = 3;
    boolean cherryEaten = false;
    boolean cherryVisible = false;
    boolean gameOver = false;
    Block cherry;
    Boolean gameStopped = false;
    Boolean powerFoodEaten = false;
    Block scaredGhost;
    Timer ghostTimer ;




    //Pacman constructor
    PacMan() {



        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        //load images
        wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();

        cherryImage = new ImageIcon(getClass().getResource("./cherry.png")).getImage();
        scaredGhostImage = new ImageIcon(getClass().getResource("./scaredGhost.png")).getImage();
        powerFoodImage = new ImageIcon(getClass().getResource("./powerFood.png")).getImage();
        pacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();


        //Call the loadmap method
        loadMap();
        for(Block ghost: ghosts){
            //Ensures that ghost moves in random position based on the characters.
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
        //Game start
        gameLoop = new Timer(50,this);
        gameLoop.start();
    }

    //Load the map from tileMap array
    public void loadMap() {
        walls = new HashSet<Block>();
        foods = new HashSet<Block>();
        ghosts = new HashSet<Block>();
        specialPower = new HashSet<Block>();

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                String row = tileMap[r];
                char tileMapChar = row.charAt(c);


                int x = c * tileSize;
                int y = r * tileSize;

                if (tileMapChar == 'X') { //block wall
                    Block wall = new Block(wallImage, x, y, tileSize, tileSize);
                    walls.add(wall);
                } else if (tileMapChar == 'b') { //blue ghost
                    Block ghost = new Block(blueGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);

                } else if (tileMapChar == 'r') { //red ghost
                    Block ghost = new Block(redGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                } else if (tileMapChar == 'p') { //pink ghost
                    Block ghost = new Block(pinkGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }else if (tileMapChar =='o') { //orange ghost
                    Block ghost = new Block(orangeGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                } else if (tileMapChar == 'P') { //pacman
                    pacman = new Block(pacmanRightImage, x,y, tileSize,tileSize);

                }else if(tileMapChar == ' ') { //food
                    Block food = new Block(null, x + 14, y + 14, 4, 4);
                    foods.add(food);
                }
                else if(tileMapChar =='*'){ //specialPower
                    Block power = new Block(powerFoodImage,x + 14,y + 14,16,16);
                    specialPower.add(power);
                }



            }

        }
        //Set Cherry position
        cherry = new Block(cherryImage,9*tileSize + 8, 11 * tileSize +8,16,16 );


    }

    //Paint method called automatically
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    //Drawing everything on screen
    public void draw(Graphics g){
        //Draws pacman
        g.drawImage(pacman.image,pacman.x,pacman.y,pacman.width, pacman.height,null);

        //Draws ghosts
        for(Block ghost: ghosts){
            g.drawImage(ghost.image,ghost.x,ghost.y,ghost.width,ghost.height,null);
        }

        //Draws walls
        for(Block wall:walls){
            g.drawImage(wall.image,wall.x,wall.y,wall.width,wall.height,null);
        }

        g.setColor(Color.WHITE);
        //Draws foods
        for(Block food : foods){
            g.fillRect(food.x,food.y,food.width,food.height);
        }

        //Draws specialPowerFood
        for(Block power: specialPower){
            g.drawImage(power.image,power.x,power.y,power.width,power.height,null);
        }

            //Draws cherry
        if(cherryVisible){
            g.drawImage(cherry.image,cherry.x,cherry.y + 8,cherry.width,cherry.height,null);
        }

        //If special power food is eaten then scaredGhost is drawn.
        for(Block ghost: ghosts){
            if(powerFoodEaten){
                g.drawImage(scaredGhostImage,ghost.x,ghost.y,ghost.width,ghost.height,null);
            }else{
                g.drawImage(ghost.originalImage,ghost.x,ghost.y,ghost.width,ghost.height,null);
            }
        }





        //score
        g.setFont(new Font("Arial", Font.PLAIN,18));
        if (gameOver){
            g.drawString("Game over: " + String.valueOf(score), tileSize/2, tileSize/2);

        }
        else{
            g.drawString("x" + String.valueOf(lives) + " Score: " + String.valueOf(score),tileSize/2,tileSize/2);
        }

        g.setColor(Color.YELLOW);
        //Show pause messages
        if(gameStopped){
            g.drawString("Game Paused. Press S to start",11 * tileSize,10 * tileSize);
        }



    }

    public void move(){
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        //Check wall collisions
        for (Block wall: walls){
            if(collision(pacman,wall)){
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }

        //Check ghost collision
        for(Block ghost: ghosts){
            if(ghost.y == tileSize * 9 && ghost.direction !='U' && ghost.direction !='D'){
                ghost.updateDirection('U');
            }

            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;

            //Change direction if ghost hits the wall
            for(Block wall : walls){
                if (collision(ghost,wall) || ghost.x < 0 || ghost.x + ghost.width >= boardWidth){
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    char newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                }
            }

            //Is used to draw cherry
            if(!cherryEaten && score >=70){
                cherryVisible = true;
            }

            //Removes food and replaces it with cherry
            if(cherryVisible){
                Block doToRemove = null;

                for(Block dot: foods){
                    if(collision(dot,cherry)){
                        doToRemove = dot;
                        break;
                    }
                }

                if(doToRemove != null){
                    foods.remove(doToRemove);
                }

            }

            if(cherryVisible && !cherryEaten){
                if(collision(cherry,pacman)){
                    cherryEaten = true;
                    cherryVisible = false;
                    score += 50;
                }
            }







            //If pacman goes towards an empty space
            if(pacman.x < 0){
                pacman.x = boardWidth;
            }
            else if(pacman.x > boardWidth){
                pacman.x = 0;
            }
        }

        //Eat food
        Block foodEaten = null;
        for(Block food: foods){
            if (collision(pacman,food)){
                foodEaten = food;
                score += 10;
            }
        }
        //Eating food removes the dot.
        foods.remove(foodEaten);

        //If all food is gone, reload the map
        if(foods.isEmpty()){
            loadMap();
        }


        //pacman and special power
        Block powerEaten = null;
        for(Block power: specialPower){
            if(collision(power,pacman)){
                powerEaten = power;
                break;
            }
        }
        if(powerEaten != null){
            specialPower.remove(powerEaten);
            powerFoodEaten = true;

            //If a timer already exist, then stop it.
            if(ghostTimer != null){
                ghostTimer.stop();
            }

            //Creating a new ghostTimer with a delay of 5000.
            ghostTimer = new Timer(5000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    powerFoodEaten = false;
                    ghostTimer.stop(); //Stops the timer, all the ghosts are back to their original form
                }
            });

            ghostTimer.start(); //Timer starts
        }


        //collision between powerUpFood and pacman

        Block powerUp = null;
        for(Block power: specialPower){
            if(collision(power,pacman)){
                powerUp = power;
            }
        }
        if(powerUp != null){
            specialPower.remove(powerUp);
        }

        //collision between ghost and pacman
        for(Block ghost:ghosts){
                if(collision(ghost,pacman)){
                    if(powerFoodEaten){
                        ghost.reset();  //If pacman eats scaredGhost, that ghost is reset to it's original position
                        score += 100;
                    }else {
                        lives--; //pacman life decreases
                        pacman.reset();
                        powerFoodEaten = false;


                        for (Block g : ghosts) g.reset();


                        if (lives <= 0) {
                            gameOver = true;
                            gameLoop.stop(); //if pacman life is less than 0, the entire game stops.

                        }


                        break;
                    }

            }
        }



    }

    //check collision between two objects
    public boolean collision(Block a, Block b){
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    //Reset all position
    public void resetPosition(){
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY=0;

        for(Block ghost: ghosts){
            ghost.reset();
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
    }




    //Timer callback
    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();

    }


    //Key Listener methods
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

        //Reset if game is over
        if(gameOver){
            loadMap();
            resetPosition();
            lives =3;
            score = 0;
            gameOver = false;
            gameLoop.start();
        }

        //Updates the direction based on characters
        if (e.getKeyCode() == KeyEvent.VK_UP){
            pacman.updateDirection('U');
        }
        else if(e.getKeyCode() == KeyEvent.VK_DOWN){
            pacman.updateDirection('D');
        }
        else if(e.getKeyCode() == KeyEvent.VK_LEFT){
            pacman.updateDirection('L');
        }else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
            pacman.updateDirection('R');
        }

        //Updates pacman image based on characters
        if(pacman.direction =='U'){
            pacman.image = pacmanUpImage;
        } else if(pacman.direction == 'D'){
            pacman.image = pacmanDownImage;
        }
        else if(pacman.direction == 'L'){
            pacman.image = pacmanLeftImage;
        }
        else if(pacman.direction == 'R'){
            pacman.image = pacmanRightImage;
        }


        //If a user presses 'S' or 's' then game starts
        if(e.getKeyChar() == 'S' || e.getKeyChar() =='s'){
            gameLoop.start();
            gameStopped = false;

        }
        //If a user presses 'P' or 'p' then game stops
        else if(e.getKeyChar() =='P' || e.getKeyChar() =='p'){
            gameLoop.stop();
            gameStopped = true;
            repaint();
        }



    }




}




