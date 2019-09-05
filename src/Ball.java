import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class Ball {
    private static final int ballSize = 8;
    public static int ballCount = 0;
    public Point2D.Double position;
    public Vector<Double> velocity;
    public int size;

    Ball(Point2D.Double spawnPoint, Vector<Double> velocity) {
        this.position = spawnPoint;
        this.velocity = velocity;
        this.size = ballSize;
        ballCount++;
    }

    public void draw(Graphics g) {
        g.fillOval((int) this.position.x, (int) this.position.y, this.size, this.size);
    }

    public void update() {
        this.position = new Point2D.Double(
                this.position.x + this.velocity.get(0),
                this.position.y + this.velocity.get(1));
        if (this.position.getX() < 0 || Game.frameWidth - this.position.getX() <= Game.brickSize/4.0) {
            this.velocity.set(0, -this.velocity.get(0));
            if (this.position.getX() < 0)
                this.position.x = 0;
            else
                this.position.x = Game.frameWidth-Game.brickSize/4.0;
        }
        if (this.position.getY() < 0) {
            this.velocity.set(1, -this.velocity.get(1));
            this.position.y = 0;
        }
    }

    public void bounce(Brick brick) {
        // GETTING PREVIOUS POSITION OF COLLIDING BALL
        double prevPosX = this.position.x - this.velocity.get(0);
        double prevPosY = this.position.y - this.velocity.get(1);

        if (prevPosX > brick.bounds[0] && prevPosX < brick.bounds[1])
            this.velocity.set(0, -this.velocity.get(0));

        if (prevPosY > brick.bounds[2] && prevPosY < brick.bounds[3])
            this.velocity.set(1, -this.velocity.get(1));

        this.position.x = prevPosX + this.velocity.get(0);
        this.position.y = prevPosY + this.velocity.get(1);

    }
}
