import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Random;

public class RobotBattle extends JPanel implements ActionListener, KeyListener {
    // grid & rendering
    static final int GRID_W = 50, GRID_H = 50;
    static final int CELL_SIZE = 16;
    static final int PANEL_W = GRID_W * CELL_SIZE;
    static final int PANEL_H = GRID_H * CELL_SIZE;

    // game state
    private LinkedList<Point> snake1, snake2;
    private int dx1, dy1, dx2, dy2;
    private Point apple;
    private int score1, score2;
    private long startTime;

    // score records model
    private final DefaultListModel<String> recordModel = new DefaultListModel<>();

    // timer & state
    private Timer timer;
    private static final int DELAY = 100;
    private static final int TIME_LIMIT = 120_000;
    private int gameState = 0; // 0=start, 1=playing, 2=over
    private String resultText = "";

    public RobotBattle() {
        setPreferredSize(new Dimension(PANEL_W, PANEL_H + 30));
        setBackground(Color.white);
        setFocusable(true);
        addKeyListener(this);
    }

    public DefaultListModel<String> getRecordModel() {
        return recordModel;
    }

    private void initGame() {
        score1 = score2 = 0;
        dx1 =  1; dy1 =  0;
        dx2 = -1; dy2 =  0;

        snake1 = new LinkedList<>();
        snake2 = new LinkedList<>();
        for (int i = 2; i >= 0; i--) snake1.add(new Point(i, GRID_H - 1));
        for (int i = GRID_W - 3; i < GRID_W; i++) snake2.add(new Point(i, 0));
        placeApple();
    }

    private void placeApple() {
        Random rand = new Random();
        do {
            apple = new Point(rand.nextInt(GRID_W), rand.nextInt(GRID_H));
        } while (snake1.contains(apple) || snake2.contains(apple));
    }

    private void startGame() {
        initGame();
        startTime = System.currentTimeMillis();
        if (timer != null) timer.stop();
        timer = new Timer(DELAY, this);
        gameState = 1;
        timer.start();
        repaint();
        requestFocusInWindow();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameState != 1) return;

        long elapsed = System.currentTimeMillis() - startTime;
        Point head1 = snake1.getFirst();
        Point head2 = snake2.getFirst();
        Point next1 = new Point(head1.x + dx1, head1.y + dy1);
        Point next2 = new Point(head2.x + dx2, head2.y + dy2);

        boolean die1 = hitWall(next1) || snake2.contains(next1);
        boolean die2 = hitWall(next2) || snake1.contains(next2);
        if (next1.equals(next2)) die1 = die2 = true;

        if (!die1) {
            snake1.addFirst(next1);
            if (next1.equals(apple)) { score1++; placeApple(); }
            else snake1.removeLast();
        }
        if (!die2) {
            snake2.addFirst(next2);
            if (next2.equals(apple)) { score2++; placeApple(); }
            else snake2.removeLast();
        }

        if (die1 || die2 || elapsed >= TIME_LIMIT) {
            gameState = 2;
            timer.stop();
            if      (die1 && !die2) resultText = "player 2 wins!";
            else if (die2 && !die1) resultText = "player 1 wins!";
            else if (elapsed >= TIME_LIMIT) {
                if      (snake1.size() > snake2.size()) resultText = "time up: player 1 wins!";
                else if (snake2.size() > snake1.size()) resultText = "time up: player 2 wins!";
                else                                     resultText = "draw!";
            } else resultText = "draw!";
            recordModel.addElement(
                    String.format("p1: %2d vs p2: %2d → %s", score1, score2, resultText)
            );
        }

        repaint();
    }

    private boolean hitWall(Point p) {
        return p.x < 0 || p.x >= GRID_W || p.y < 0 || p.y >= GRID_H;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        switch (gameState) {
            case 0 -> drawStartScreen(g);
            case 1 -> drawGame(g);
            case 2 -> drawGameOver(g);
        }
    }

    private void drawStartScreen(Graphics g) {
        g.setFont(new Font("Arial", Font.BOLD, 36));
        drawCentered(g, "ROBOT BATTLE", PANEL_W, PANEL_H/2 - 40);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        drawCentered(g, "two robots collect apples to grow", PANEL_W, PANEL_H/2);
        drawCentered(g, "p1: WASD    p2: arrows", PANEL_W, PANEL_H/2 + 30);
        drawCentered(g, "press ENTER to start", PANEL_W, PANEL_H/2 + 70);
    }

    private void drawGame(Graphics g) {
        // apple
        g.setColor(Color.red);
        g.fillOval(apple.x * CELL_SIZE, apple.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);

        // robots
        snake1.forEach(p -> drawRobot(g, p.x, p.y, Color.green.darker()));
        snake2.forEach(p -> drawRobot(g, p.x, p.y, Color.blue.darker()));

        // scores & time
        g.setColor(Color.black);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("p1: " + score1 + "    p2: " + score2, 10, PANEL_H + 20);
        long rem = Math.max(0, TIME_LIMIT/1000 - (System.currentTimeMillis() - startTime)/1000);
        g.drawString("time: " + rem + "s", 200, PANEL_H + 20);
    }

    private void drawGameOver(Graphics g) {
        g.setFont(new Font("Arial", Font.BOLD, 36));
        drawCentered(g, resultText, PANEL_W, PANEL_H/2 - 20);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        drawCentered(g, "press R to restart", PANEL_W, PANEL_H/2 + 30);
    }

    private void drawRobot(Graphics g, int gx, int gy, Color body) {
        int x = gx * CELL_SIZE, y = gy * CELL_SIZE;
        g.setColor(body);
        g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
        int s = CELL_SIZE/5;
        g.setColor(Color.white);
        g.fillOval(x + CELL_SIZE/4 - s/2, y + CELL_SIZE/5, s, s);
        g.fillOval(x + CELL_SIZE*3/4 - s/2, y + CELL_SIZE/5, s, s);
    }

    private void drawCentered(Graphics g, String text, int w, int y) {
        FontMetrics fm = g.getFontMetrics();
        int x = (w - fm.stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();
        if      (gameState == 0 && k == KeyEvent.VK_ENTER) startGame();
        else if (gameState == 2 && k == KeyEvent.VK_R)     startGame();
        else if (gameState == 1) {
            if (k == KeyEvent.VK_W && dy1 != 1)  { dx1 = 0; dy1 = -1; }
            if (k == KeyEvent.VK_S && dy1 != -1) { dx1 = 0; dy1 =  1; }
            if (k == KeyEvent.VK_A && dx1 != 1)  { dx1 = -1;dy1 =  0; }
            if (k == KeyEvent.VK_D && dx1 != -1) { dx1 =  1;dy1 =  0; }
            if (k == KeyEvent.VK_UP    && dy2 != 1)  { dx2 = 0; dy2 = -1; }
            if (k == KeyEvent.VK_DOWN  && dy2 != -1) { dx2 = 0; dy2 =  1; }
            if (k == KeyEvent.VK_LEFT  && dx2 != 1)  { dx2 = -1;dy2 =  0; }
            if (k == KeyEvent.VK_RIGHT && dx2 != -1) { dx2 =  1;dy2 =  0; }
        }
    }
    @Override public void keyReleased(KeyEvent e) { }
    @Override public void keyTyped   (KeyEvent e) { }
}
