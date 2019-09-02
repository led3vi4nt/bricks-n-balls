import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class Brick {

    int[] coords;
    int value;
    double[] bounds;
    Color color;
    boolean highlighted = false;

    Brick(int[] coords, int value, Color color) {
        this.coords = coords;
        this.value = value;
        this.color = color;
        this.bounds = getBounds(coords);
    }

    boolean checkHit(Ball ball) {
        if (ball.position.x > bounds[0] && ball.position.x < bounds[1] &&
                ball.position.y > bounds[2] && ball.position.y < bounds[3]) {
            this.highlighted = true;
            return true;
        }
        return false;
    }

    static double[] getBounds(int[] coords) {
        double[] result = new double[4];
        result[0] = coords[0] * Game.brickSize;
        result[1] = coords[0] * Game.brickSize + Game.brickSize;
        result[2] = coords[1] * Game.brickSize;
        result[3] = coords[1] * Game.brickSize + Game.brickSize;
        return result;

    }

    void draw(Graphics g) {
        if (this.highlighted) {
            g.setColor(Color.WHITE);
            this.highlighted = false;
        } else {
            g.setColor(this.color);
        }
        g.fillRect((int) this.bounds[0] + 1, (int) this.bounds[2] + 1, Game.brickSize - 2, Game.brickSize - 2);
        g.setColor(Game.fontColor);
        g.setFont(Game.standardFont);
        String valueString = "" + value;
        int valueStringWidth = g.getFontMetrics().stringWidth(valueString);
        g.drawString(valueString, (int) this.bounds[0] + (Game.brickSize / 2) - (valueStringWidth / 2), (int) (this.bounds[2] + Game.brickSize * 2 / 3));
    }
}
