import javax.swing.*;
import java.awt.*;

public class ScorePanel extends JPanel {
    public ScorePanel(DefaultListModel<String> model) {
        setLayout(new BorderLayout());
        JList<String> recordList = new JList<>(model);
        recordList.setFont(new Font("Consolas", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(recordList);
        scroll.setPreferredSize(new Dimension(300, RobotBattle.PANEL_H));
        scroll.setBorder(BorderFactory.createTitledBorder("Score Records"));
        add(scroll, BorderLayout.CENTER);
    }
}
