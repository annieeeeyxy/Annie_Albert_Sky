import javax.swing.*;
import java.awt.*;

public class ScorePanel extends JPanel {
    public ScorePanel(DefaultListModel<String> model) {
        setLayout(new BorderLayout());
        JLabel title = new JLabel("Score Records");
        title.setHorizontalAlignment(JLabel.CENTER);
        add(title, BorderLayout.NORTH);

        JList<String> list = new JList<String>(model);
        list.setFont(new Font("Consolas", Font.PLAIN, 14));
        JScrollPane pane = new JScrollPane(list);
        pane.setPreferredSize(new Dimension(300, RobotBattle.PANEL_HEIGHT));
        add(pane, BorderLayout.CENTER);
    }
}
