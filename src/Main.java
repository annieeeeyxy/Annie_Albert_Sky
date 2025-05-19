import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Robot Game");
        RobotGamePanel gamePanel = new RobotGamePanel();

        frame.add(gamePanel);
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        gamePanel.requestFocusInWindow(); // Focus for key input
    }
}



