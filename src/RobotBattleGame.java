import javax.swing.*;
import java.awt.*;

public class RobotBattleGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Robot Battle");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            // game panel
            RobotBattle battle = new RobotBattle();
            frame.add(battle, BorderLayout.CENTER);

            // score‐keeping panel
            ScorePanel scorePanel = new ScorePanel(battle.getRecordModel());
            frame.add(scorePanel, BorderLayout.EAST);

            frame.pack();
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // ensure key events go to the game panel
            battle.requestFocusInWindow();
        });
    }
}
