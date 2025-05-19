import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RobotGamePanel extends JPanel implements KeyListener {
    private Robot robot;

    public RobotGamePanel() {
        robot = new Robot(50, 50, 20);

        setFocusable(true);
        addKeyListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        robot.draw(g);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            robot.move(-10, 0);
        } else if (key == KeyEvent.VK_RIGHT) {
            robot.move(10, 0);
        } else if (key == KeyEvent.VK_UP) {
            robot.move(0, -10);
        } else if (key == KeyEvent.VK_DOWN) {
            robot.move(0, 10);
        }

        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}
}
