import java.awt.*;

public class Robot {
    private int x, y;
    private int size;

    public Robot(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }

    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(x, y, size, size);
    }
}
