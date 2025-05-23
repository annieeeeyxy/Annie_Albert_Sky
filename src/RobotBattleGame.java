import javax.swing.*;
import java.awt.*;

public class RobotBattleGame {
    public static void main(String[] args) {
        // schedule the GUI creation on the Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createGameWindow();
            }
        });
    }

    private static void createGameWindow() {
        JFrame frame = new JFrame();
        frame.setTitle("Robot Battle");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // create the game panel
        RobotBattle battlePanel = new RobotBattle();
        frame.add(battlePanel, BorderLayout.CENTER);

        // create the score panel
        ScorePanel scorePanel = new ScorePanel(battlePanel.getRecordModel());
        frame.add(scorePanel, BorderLayout.EAST);

        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // give keyboard focus to the game panel
        battlePanel.requestFocusInWindow();
    }
}