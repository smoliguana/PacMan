package pacman;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.util.Random;

public class Model extends JPanel implements ActionListener {

    // Game constants
    private final int BlockSize = 48; // block size in px
    private final int NBlocks = 15; // number of blocks
    private final int ScreenSize = NBlocks * BlockSize;
    private final int MaxGhosts = 10;
    private final int validSpeeds[] = {1, 2, 3, 4, 6, 8};
    private final int PacmanSpeed = 6;
    private int randomIndex;

    // Game state variables
    private boolean inGame = false;
    private boolean dying = false;
    private boolean gameOver = false;
    private boolean Won = false;
    private boolean youWin = false;
    private long youWinTimer = 0;
    private int NGhosts = 5;
    private int lives, score;
    private int currentSpeed = 3;

    // Initializing game data
    private Dimension d;
    private Timer timer;
    private short[] screenData;

    // Pacman position and direction
    private int pacman_x, pacman_y, pacmand_x, pacmand_y;
    private int req_dx, req_dy;

    // Ghost position and direction
    private int[] dx, dy;
    private int[] ghost_x, ghost_y, ghost_dx, ghost_dy, ghostSpeed;

    // Import images & font
    private Font customFont;
    private Image heart;
    private Image up, down, left, right; // pacman gif file position
    private int[] ghostImageIndex; // index to access the ghost's array
    private Image[] ghostImages; // puts the ghosts into an array

    private final short[][] maps = {{ //assigns a value to every block
                    //0 = blue, 1 = left border, 2 = top border, 4 = right border,
                    // 8 = bottom border, 16 = white dots
                    19, 18, 18, 18, 18, 18, 18, 18, 26, 26, 26, 26, 26, 26, 22,
                    17, 16, 16, 16, 16, 24, 16, 20,  0,  0,  0,  0,  0,  0, 21,
                    25, 24, 24, 24, 28,  0, 17, 20,  0, 19, 18, 18, 22,  0, 21,
                    0,  0,  0,  0,  0,   0, 17, 16, 18, 16, 16, 16, 16, 18, 20,
                    19, 18, 18, 18, 18, 18, 16, 24, 24, 24, 24, 24, 24, 24, 28,
                    17, 16, 16, 16, 16, 16, 20,  0,  0,  0,  0,  0,  0,   0, 0,
                    17, 16, 16, 16, 16, 16, 16, 18, 18, 18, 18,  18, 18, 18, 22,
                    17, 16, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 16, 20,
                    17, 20, 0,   0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 17, 20,
                    17, 16, 18, 18, 18, 18, 18, 18, 22, 0, 19, 18, 18, 16, 20,
                    17, 16, 16, 24, 16, 16, 16, 16, 20, 0, 17, 16, 24, 16, 20,
                    17, 16, 20, 0,  17, 16, 16, 16, 16, 18, 16, 20, 0, 17, 20,
                    17, 24, 28, 0,  25, 24, 24, 16, 16, 24, 24, 28, 0, 25, 20,
                    21,  0,  0,  0,  0,  0,  0, 17, 20,  0,  0,  0, 0,  0, 21,
                    25, 26, 26, 26, 26, 26, 26, 24, 24, 26, 26, 26, 26, 26, 28},
            { //map 2
                    19, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 26, 26, 26, 22,
                    17, 16, 16, 16, 16, 24, 16, 16, 16, 16, 20,  0,  0,  0, 21,
                    25, 24, 24, 24, 28, 0, 25, 16, 16, 16, 16, 18, 18, 18, 20,
                    0,   0,  0,  0,  0,  0,  0, 17, 16, 16, 16, 16, 16, 16, 20,
                    19, 18, 18, 18, 18, 26, 26, 24, 24, 24, 24, 24, 24, 24, 20,
                    17, 16, 16, 16, 20,  0,  0,  0,  0,  0,  0,  0,  0,  0, 21,
                    17, 16, 16, 16, 16, 18, 18, 18, 18, 18, 18, 18, 22,  0, 21,
                    17, 16, 16, 16, 24, 16, 16, 16, 16, 16, 16, 16, 20,  0, 21,
                    17, 16, 16, 20,  0, 17, 16, 16, 16, 16, 16, 24, 28,  0, 21,
                    17, 24, 24, 28,  0, 25, 24, 24, 16, 16, 20,  0,  0,  0, 21,
                    21,  0,  0,  0,  0,  0,  0,  0, 17, 16, 16, 18, 18, 18, 20,
                    21,  0, 19, 18, 18, 18, 18, 18, 16, 16, 16, 16, 24, 24, 28,
                    21,  0, 25, 24, 24, 24, 24, 24, 24, 24, 16, 20,  0,  0,  0,
                    21,  0,  0,  0,  0,  0,  0,  0,  0,  0, 17, 16, 18, 18, 22,
                    25, 26, 26, 26, 26, 26, 26, 26, 26, 26, 24, 24, 24, 24, 28

            },
    };


    public Model() { // model constructor
        loadImages();
        loadCustomFont();
        initVariables();
        addKeyListener(new TAdapter());
        setFocusable(true);
        initGame();
    }

    private void loadImages() { //load images in the game
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            down = new ImageIcon(classLoader.getResource("images/down.gif")).getImage();
            up = new ImageIcon(classLoader.getResource("images/up.gif")).getImage();
            left = new ImageIcon(classLoader.getResource("images/left.gif")).getImage();
            right = new ImageIcon(classLoader.getResource("images/right.gif")).getImage();
            heart = new ImageIcon(classLoader.getResource("images/heart.png")).getImage();
            // Load ghost images into an array
            ghostImages = new Image[3];
            ghostImages[0] = new ImageIcon(classLoader.getResource("images/ghostOr.gif")).getImage();
            ghostImages[1] = new ImageIcon(classLoader.getResource("images/ghostRed.gif")).getImage();
            ghostImages[2] = new ImageIcon(classLoader.getResource("images/ghostPink.gif")).getImage();
            // Debugging
            System.out.println("Images loaded successfully");

        } catch (Exception e) {
            System.out.println("Error loading images: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadCustomFont() { //load custom font into the game
        try {
            // Change the path to where your custom font file is located
            File fontFile = new File("src/resources/font/LLPIXEL3.ttf");
            customFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(Font.BOLD, 35);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);

            // Debugging
            System.out.println("Custom font loaded successfully");

        } catch (FontFormatException | IOException e) {
            System.out.println("Error loading custom font: " + e.getMessage());
            e.printStackTrace();
            // Fallback to a default font in case of an error
            customFont = new Font("Arial", Font.BOLD, 14);
        }
    }

    private void initVariables() { //initializes game variable and start timer
        screenData = new short[NBlocks * NBlocks];
        d = new Dimension(800, 800);
        ghost_x = new int[MaxGhosts];
        ghost_dx = new int[MaxGhosts];
        ghost_y = new int[MaxGhosts];
        ghost_dy = new int[MaxGhosts];
        ghostSpeed = new int[MaxGhosts];
        ghostImageIndex = new int[MaxGhosts];
        dx = new int[4];
        dy = new int[4];

        timer = new Timer(40, this);
        timer.start();
    }

    // Game controls
    class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (inGame) {
                if (key == KeyEvent.VK_LEFT) { //arrow key left, move left
                    req_dx = -1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_RIGHT) { //arrow key right, move right
                    req_dx = 1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_UP) { //arrow key up, move up
                    req_dx = 0;
                    req_dy = -1;
                } else if (key == KeyEvent.VK_DOWN) { //arrow key down, move down
                    req_dx = 0;
                    req_dy = 1;
                } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) { //esc to exit the game
                    inGame = false;
                }
            } else {
                if (key == KeyEvent.VK_SPACE) { //space to start the game
                    inGame = true;
                    initGame();
                }
            }
        }
    }

    private void playGame(Graphics2D g2d) {
        if (dying) {
            death(); //check if the player is dead
        } else { //if not continue the game
            movePacman();
            drawPacman(g2d);
            moveGhosts(g2d);
            checkMaze();
        }
    }

    private void initGame() { // initialize the game
        lives = 3;
        score = 0;
        selectRandomMap(); // Select a random map
        initLevel();
        NGhosts = 6;
        currentSpeed = 3;
    }

    private void selectRandomMap() {
        Random rand = new Random();
        randomIndex = rand.nextInt(maps.length);
        screenData = new short[NBlocks * NBlocks]; // Reset screenData here
    }


    private void initLevel() {
        // Randomly select a map from the maps array
        randomIndex = (int) (Math.random() * maps.length);

        // Ensure maps[randomIndex] exists and has the correct dimensions
        if (randomIndex < 0 || randomIndex >= maps.length || maps[randomIndex] == null) {
            throw new IllegalArgumentException("Invalid randomIndex or maps structure");
        }

        // Reset screenData
        screenData = new short[NBlocks * NBlocks];

        // Get the selected map
        short[] selectedMap = maps[randomIndex];

        // Flatten the selected map's data into screenData
        for (int i = 0; i < NBlocks * NBlocks; i++) {
            screenData[i] = selectedMap[i];
        }

        continueLevel();
    }

    private void continueLevel() {
        int dx = 1;
        int random;
        for (int i = 0; i < NGhosts; i++) {
            ghost_y[i] = 4 * BlockSize; // start position
            ghost_x[i] = 4 * BlockSize;
            ghost_dy[i] = 0;
            ghost_dx[i] = dx;
            dx = -dx;
            random = (int) (Math.random() * (currentSpeed + 1));
            if (random > currentSpeed) {
                random = currentSpeed;
            }
            ghostSpeed[i] = validSpeeds[1];
            ghostImageIndex[i] = (int) (Math.random() * ghostImages.length); // Assign a random color
        }
        pacman_x = 7 * BlockSize; // start position
        pacman_y = 11 * BlockSize;
        pacmand_x = 0; // reset direction move
        pacmand_y = 0;
        req_dx = 0; // reset direction controls
        req_dy = 0;
        dying = false;
    }
    private void death() { //method if the player dies
        lives--; //reduce life
        if (lives == 0) {
            inGame = false;
            gameOver = true;  // Set game over flag
        }
        continueLevel(); //start a new game
    }

    public void paintComponent(Graphics g) { //method to draw the game components
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, d.width, d.height);
        drawMaze(g2d);
        drawScore(g2d);
        if (inGame) {
            playGame(g2d);
        } else if (Won) {  // Check if the player has won
            showYouWinScreen(g2d);
        } else if (gameOver) {  // Check if the game is over
            showGameOverScreen(g2d);
        } else {
            showIntroScreen(g2d);
        }
        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }

    private void drawScore(Graphics2D g) { //draw the bottom part of the game (score and 3 lives)
        g.setFont(customFont);
        g.setColor(new Color(5, 181, 79));
        String s = "Score: " + score;
        g.drawString(s, ScreenSize / 2 + 150, ScreenSize + 45);
        for (int i = 0; i < lives; i++) {
            g.drawImage(heart, i * 50 + 20, ScreenSize + 10, this);
        }
    }

    private void drawMaze(Graphics2D g2d) {
        short i = 0;
        int x, y;
        for (y = 0; y < ScreenSize; y += BlockSize) {
            for (x = 0; x < ScreenSize; x += BlockSize) {
                g2d.setColor(new Color(0, 72, 251));
                g2d.setStroke(new BasicStroke(5));
                if ((screenData[i] & 1) != 0) {
                    g2d.drawLine(x, y, x, y + BlockSize - 1);
                }
                if ((screenData[i] & 2) != 0) {
                    g2d.drawLine(x, y, x + BlockSize - 1, y);
                }
                if ((screenData[i] & 4) != 0) {
                    g2d.drawLine(x + BlockSize - 1, y, x + BlockSize - 1, y + BlockSize - 1);
                }
                if ((screenData[i] & 8) != 0) {
                    g2d.drawLine(x, y + BlockSize - 1, x + BlockSize - 1, y + BlockSize - 1);
                }
                if ((screenData[i] & 16) != 0) {
                    g2d.setColor(new Color(255, 255, 255));
                    g2d.fillOval(x + 20, y + 20, 10, 10);
                }
                i++;
            }
        }
    }


    private void checkMaze() { //check the state of the maze
        boolean finished = true;  // Assume the level is finished
        for (int i = 0; i < NBlocks * NBlocks; i++) {
            if ((screenData[i] & 16) != 0) {  // If there are still dots (value 16) left
                finished = false;
                break;  // No need to check further, we found a dot
            }
        }
        if (finished) {
            score += 50;  // Add bonus points for completing the level
            Won = true;
            playerWon();
            inGame = false;
        }
    }

    private void drawPacman(Graphics2D g2d) {

        if (req_dx == -1) {
            g2d.drawImage(left, pacman_x + 1, pacman_y + 1, this);
        } else if (req_dx == 1) {
            g2d.drawImage(right, pacman_x + 1, pacman_y + 1, this);
        } else if (req_dy == -1) {
            g2d.drawImage(up, pacman_x + 1, pacman_y + 1, this);
        } else {
            g2d.drawImage(down, pacman_x + 1, pacman_y + 1, this);
        }
    }

    private void movePacman() {
        int pos;
        short ch;
        if (pacman_x % BlockSize == 0 && pacman_y % BlockSize == 0) {
            pos = pacman_x / BlockSize + NBlocks * (int) (pacman_y / BlockSize);
            ch = screenData[pos];
            if ((ch & 16) != 0) {
                screenData[pos] = (short) (ch & 15); // Remove the dot from the screenData
                score++;
            }
            if (req_dx != 0 || req_dy != 0) {
                if (!((req_dx == -1 && req_dy == 0 && (ch & 1) != 0)
                        || (req_dx == 1 && req_dy == 0 && (ch & 4) != 0)
                        || (req_dx == 0 && req_dy == -1 && (ch & 2) != 0)
                        || (req_dx == 0 && req_dy == 1 && (ch & 8) != 0))) {
                    pacmand_x = req_dx;
                    pacmand_y = req_dy;
                }
            }
            // Check for standstill
            if ((pacmand_x == -1 && pacmand_y == 0 && (ch & 1) != 0)
                    || (pacmand_x == 1 && pacmand_y == 0 && (ch & 4) != 0)
                    || (pacmand_x == 0 && pacmand_y == -1 && (ch & 2) != 0)
                    || (pacmand_x == 0 && pacmand_y == 1 && (ch & 8) != 0)) {
                pacmand_x = 0;
                pacmand_y = 0;
            }
        }
        pacman_x = pacman_x + PacmanSpeed * pacmand_x;
        pacman_y = pacman_y + PacmanSpeed * pacmand_y;
    }

    private void drawGhost(Graphics2D g2d, int x, int y, int index) {
        g2d.drawImage(ghostImages[index], x, y, this);
    }

    private void moveGhosts(Graphics2D g2d) {
        int pos;
        int count;
        for (int i = 0; i < NGhosts; i++) {
            if (ghost_x[i] % BlockSize == 0 && ghost_y[i] % BlockSize == 0) {
                pos = ghost_x[i] / BlockSize + NBlocks * (int) (ghost_y[i] / BlockSize);
                count = 0;
                if ((screenData[pos] & 1) == 0 && ghost_dx[i] != 1) {
                    dx[count] = -1;
                    dy[count] = 0;
                    count++;
                } if ((screenData[pos] & 2) == 0 && ghost_dy[i] != 1) {
                    dx[count] = 0;
                    dy[count] = -1;
                    count++;
                } if ((screenData[pos] & 4) == 0 && ghost_dx[i] != -1) {
                    dx[count] = 1;
                    dy[count] = 0;
                    count++;
                } if ((screenData[pos] & 8) == 0 && ghost_dy[i] != -1) {
                    dx[count] = 0;
                    dy[count] = 1;
                    count++;
                } if (count == 0) {
                    if ((screenData[pos] & 15) == 15) {
                        ghost_dx[i] = 0;
                        ghost_dy[i] = 0;
                    } else {
                        ghost_dx[i] = -ghost_dx[i];
                        ghost_dy[i] = -ghost_dy[i];
                    }
                } else {
                    count = (int) (Math.random() * count);
                    if (count > 3) {
                        count = 3;
                    }
                    ghost_dx[i] = dx[count];
                    ghost_dy[i] = dy[count];
                }

            }
            ghost_x[i] = ghost_x[i] + (ghost_dx[i] * ghostSpeed[i]);
            ghost_y[i] = ghost_y[i] + (ghost_dy[i] * ghostSpeed[i]);
            drawGhost(g2d, ghost_x[i] + 1, ghost_y[i] + 1, ghostImageIndex[i]);
            if (pacman_x > (ghost_x[i] - 12) && pacman_x < (ghost_x[i] + 12)
                    && pacman_y > (ghost_y[i] - 12) && pacman_y < (ghost_y[i] + 12)
                    && inGame) {

                dying = true;
            }
        }
    }

    private void showIntroScreen(Graphics2D g2d) { //make the font for the start of the game
        String start = "Press SPACE to start";
        g2d.setColor(Color.yellow);
        g2d.drawString(start, (ScreenSize) / 4, 230);
    }

    private void showYouWinScreen(Graphics2D g2d) {
        if (youWin) {
            String youWinMessage = "You Win!";
            g2d.setColor(Color.yellow);
            g2d.setFont(customFont.deriveFont(96f));  // Larger font for win message
            FontMetrics metrics = g2d.getFontMetrics();
            int x = (ScreenSize - metrics.stringWidth(youWinMessage)) / 2;
            int y = ScreenSize / 2;
            g2d.drawString(youWinMessage, x, y);

            // Check if 3 seconds have passed since the "you win" state was set
            if (System.currentTimeMillis() - youWinTimer > 3000) {
                initLevel();
                youWin = false;  // Reset the "you win" state
            }
        }
    }

    // Call this method when the player wins
    private void playerWon() {
        youWin = true;
        youWinTimer = System.currentTimeMillis();
    }

    private void showGameOverScreen(Graphics2D g2d) { //show game over when the player died
        String gameOverMessage = "Game Over";
        g2d.setColor(Color.red);
        g2d.setFont(customFont.deriveFont(96f));  // Larger font for game over message
        FontMetrics metrics = g2d.getFontMetrics();
        int x = (ScreenSize - metrics.stringWidth(gameOverMessage)) / 2;
        int y = (ScreenSize) / 2;
        g2d.drawString(gameOverMessage, x, y);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
}