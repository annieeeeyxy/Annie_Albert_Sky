import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Random;

public class RobotBattle extends JPanel implements ActionListener, KeyListener {
    // constants for grid size
    public static final int GRID_WIDTH = 50;
    public static final int GRID_HEIGHT = 50;
    public static final int CELL_SIZE = 16;
    public static final int PANEL_WIDTH = GRID_WIDTH * CELL_SIZE;
    public static final int PANEL_HEIGHT = GRID_HEIGHT * CELL_SIZE;

    // game state variables
    private LinkedList<Point> snake1;
    private LinkedList<Point> snake2;
    private Point apple;
    private int dx1, dy1;
    private int dx2, dy2;
    private int score1, score2;
    private long startTime;
    private DefaultListModel<String> recordModel;

    // timer and states
    private Timer timer;
    private static final int DELAY = 100;
    private static final int TIME_LIMIT = 120000; // milliseconds
    private int gameState;  // 0 = start, 1 = playing, 2 = over
    private String resultText;

    public RobotBattle() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT + 30));
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(this);

        recordModel = new DefaultListModel<String>();
        gameState = 0;
        resultText = "";
    }

    public DefaultListModel<String> getRecordModel() {
        return recordModel;
    }

    // initialize or reset the game
    private void initGame() {
        score1 = 0;
        score2 = 0;
        dx1 = 1; dy1 = 0;  // player 1 moves right initially
        dx2 = -1; dy2 = 0; // player 2 moves left initially

        snake1 = new LinkedList<Point>();
        snake2 = new LinkedList<Point>();

        // place initial points for snakes
        for (int i = 2; i >= 0; i--) {
            snake1.add(new Point(i, GRID_HEIGHT - 1));
        }
        for (int i = GRID_WIDTH - 3; i < GRID_WIDTH; i++) {
            snake2.add(new Point(i, 0));
        }

        placeApple();
    }

    // randomly place apple not on snakes
    private void placeApple() {
        Random rand = new Random();
        Point p;
        do {
            int x = rand.nextInt(GRID_WIDTH);
            int y = rand.nextInt(GRID_HEIGHT);
            p = new Point(x, y);
        } while (snake1.contains(p) || snake2.contains(p));
        apple = p;
    }

    // start or restart the timer and game
    private void startGame() {
        initGame();
        startTime = System.currentTimeMillis();
        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(DELAY, this);
        gameState = 1;
        timer.start();
        repaint();
        requestFocusInWindow();
    }

    // called by the timer
    public void actionPerformed(ActionEvent e) {
        if (gameState != 1) {
            return;
        }
        updateGame();
        repaint();
    }

    // update positions, check collisions, handle end
    private void updateGame() {
        long elapsed = System.currentTimeMillis() - startTime;

        Point head1 = snake1.getFirst();
        Point next1 = new Point(head1.x + dx1, head1.y + dy1);
        Point head2 = snake2.getFirst();
        Point next2 = new Point(head2.x + dx2, head2.y + dy2);

        boolean die1 = checkDie(next1, snake2);
        boolean die2 = checkDie(next2, snake1);
        if (next1.equals(next2)) {
            die1 = true;
            die2 = true;
        }

        moveSnake(snake1, next1, die1, true);
        moveSnake(snake2, next2, die2, false);

        if (die1 || die2 || elapsed >= TIME_LIMIT) {
            endGame(elapsed, die1, die2);
        }
    }

    // check if next point hits wall or other snake
    private boolean checkDie(Point next, LinkedList<Point> other) {
        if (next.x < 0 || next.x >= GRID_WIDTH) {
            return true;
        }
        if (next.y < 0 || next.y >= GRID_HEIGHT) {
            return true;
        }
        if (other.contains(next)) {
            return true;
        }
        return false;
    }

    // move snake, grow if apple eaten
    private void moveSnake(LinkedList<Point> snake, Point next, boolean die, boolean isFirst) {
        if (!die) {
            snake.addFirst(next);
            if (next.equals(apple)) {
                if (isFirst) {
                    score1++;
                } else {
                    score2++;
                }
                placeApple();
            } else {
                snake.removeLast();
            }
        }
    }

    // wrap up game: stop timer, set result, record score
    private void endGame(long elapsed, boolean die1, boolean die2) {
        gameState = 2;
        timer.stop();

        if (die1 && !die2) {
            resultText = "Player 2 wins!";
        } else if (die2 && !die1) {
            resultText = "Player 1 wins!";
        } else if (elapsed >= TIME_LIMIT) {
            if (snake1.size() > snake2.size()) {
                resultText = "Time up: Player 1 wins!";
            } else if (snake2.size() > snake1.size()) {
                resultText = "Time up: Player 2 wins!";
            } else {
                resultText = "Draw!";
            }
        } else {
            resultText = "Draw!";
        }

        String record = "p1: " + score1 + " vs p2: " + score2 + " -> " + resultText;
        recordModel.addElement(record);
    }

    // draw the correct screen
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (gameState == 0) {
            drawStartScreen(g);
        } else if (gameState == 1) {
            drawGamePlay(g);
        } else {
            drawGameOver(g);
        }
    }

    // draw start instructions
    private void drawStartScreen(Graphics g) {
        g.setFont(new Font("Arial", Font.BOLD, 36));
        drawCentered(g, "ROBOT BATTLE", PANEL_WIDTH, PANEL_HEIGHT/2 - 40);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        drawCentered(g, "two robots collect apples to grow", PANEL_WIDTH, PANEL_HEIGHT/2);
        drawCentered(g, "p1: WASD    p2: arrows", PANEL_WIDTH, PANEL_HEIGHT/2 + 30);
        drawCentered(g, "press ENTER to start", PANEL_WIDTH, PANEL_HEIGHT/2 + 70);
    }

    // draw robots, apple, scores, time
    private void drawGamePlay(Graphics g) {
        int appleX = apple.x * CELL_SIZE;
        int appleY = apple.y * CELL_SIZE;
        g.setColor(Color.RED);
        g.fillOval(appleX, appleY, CELL_SIZE, CELL_SIZE);

        for (int i = 0; i < snake1.size(); i++) {
            Point p = snake1.get(i);
            drawRobot(g, p.x, p.y, Color.GREEN.darker());
        }
        for (int i = 0; i < snake2.size(); i++) {
            Point p = snake2.get(i);
            drawRobot(g, p.x, p.y, Color.BLUE.darker());
        }

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("p1: " + score1 + "    p2: " + score2, 10, PANEL_HEIGHT + 20);

        long secondsLeft = TIME_LIMIT/1000 - (System.currentTimeMillis() - startTime)/1000;
        if (secondsLeft < 0) {
            secondsLeft = 0;
        }
        g.drawString("time: " + secondsLeft + "s", 200, PANEL_HEIGHT + 20);
    }

    // draw end screen
    private void drawGameOver(Graphics g) {
        g.setFont(new Font("Arial", Font.BOLD, 36));
        drawCentered(g, resultText, PANEL_WIDTH, PANEL_HEIGHT/2 - 20);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        drawCentered(g, "press R to restart", PANEL_WIDTH, PANEL_HEIGHT/2 + 30);
    }

    // helper for drawing centered text
    private void drawCentered(Graphics g, String text, int width, int y) {
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int x = (width - textWidth) / 2;
        g.drawString(text, x, y);
    }

    // draw a single robot cell
    private void drawRobot(Graphics g, int gx, int gy, Color c) {
        int x = gx * CELL_SIZE;
        int y = gy * CELL_SIZE;
        g.setColor(c);
        g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
        int eye = CELL_SIZE / 5;
        g.setColor(Color.WHITE);
        g.fillOval(x + CELL_SIZE/4 - eye/2, y + CELL_SIZE/5, eye, eye);
        g.fillOval(x + CELL_SIZE*3/4 - eye/2, y + CELL_SIZE/5, eye, eye);
    }

    // handle key events
    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();
        if (gameState == 0 && k == KeyEvent.VK_ENTER) {
            startGame();
        } else if (gameState == 2 && k == KeyEvent.VK_R) {
            startGame();
        } else if (gameState == 1) {
            handlePlayer1Key(k);
            handlePlayer2Key(k);
        }
    }

    private void handlePlayer1Key(int k) {
        if (k == KeyEvent.VK_W) {
            if (dy1 != 1) { dx1 = 0; dy1 = -1; }
        }
        if (k == KeyEvent.VK_S) {
            if (dy1 != -1) { dx1 = 0; dy1 = 1; }
        }
        if (k == KeyEvent.VK_A) {
            if (dx1 != 1) { dx1 = -1; dy1 = 0; }
        }
        if (k == KeyEvent.VK_D) {
            if (dx1 != -1) { dx1 = 1; dy1 = 0; }
        }
    }

    private void handlePlayer2Key(int k) {
        if (k == KeyEvent.VK_UP) {
            if (dy2 != 1) { dx2 = 0; dy2 = -1; }
        }
        if (k == KeyEvent.VK_DOWN) {
            if (dy2 != -1) { dx2 = 0; dy2 = 1; }
        }
        if (k == KeyEvent.VK_LEFT) {
            if (dx2 != 1) { dx2 = -1; dy2 = 0; }
        }
        if (k == KeyEvent.VK_RIGHT) {
            if (dx2 != -1) { dx2 = 1; dy2 = 0; }
        }
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
}

