import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.*;
import javax.swing.*;
import java.io.*;

public class Game extends JPanel {

    static final int frameWidth = 540;
    static final int frameHeight = 960;
    static final Color fontColor = Color.WHITE;
    static final int brickSize = frameWidth/18;
    static final Font standardFont = new Font("Lucida Console", Font.BOLD, 12);
    static Point2D.Double mousePosition = new Point2D.Double();
    private static ArrayList<Ball> ballStack = new ArrayList<>();
    private static ArrayList<Brick> brickStack = new ArrayList<>();
    private static Point2D.Double spawnPoint;
    private static final double ballSpawnSpeed = 5.0D;
    private static boolean mouseIsDown = false;
    private static int overallScore = 0;



    public static void main(String[] args) throws InterruptedException {
        Game game = new Game();
        JFrame frame = getSetJFrame("BRIXnBALLS", game);
        frame.setVisible(true);
        attachMouseEvents(game);
        spawnPoint = new Point2D.Double(frame.getWidth() / 2.0D, frame.getHeight() - 50.0);

        loadStage(1);
        int ballSpawnFrameDelay = 4,
            ballSpawnIterator = ballSpawnFrameDelay;

        while (true) {
            if (mouseIsDown && ballSpawnIterator-- < 0) {
                spawnNewBall();
                ballSpawnIterator = ballSpawnFrameDelay;
            }


            for (int i = 0; i < ballStack.size(); i++) {

                ballStack.get(i).update();

                // COLLISION CHECK
                for (int j = 0; j < brickStack.size(); j++) {
                    if (brickStack.get(j).checkHit(ballStack.get(i))) {
                        ballStack.get(i).bounce(brickStack.get(j));
                        overallScore++;
                        if (--brickStack.get(j).value <= 0)
                            brickStack.remove(j);
                    }
                }

                // REMOVE BALLS LEAVING AT THE BOTTOM  OF THE SCREEN
                if (ballStack.get(i).position.y > frameHeight - 20)
                    ballStack.remove(i);
            }

            game.repaint();
            Thread.sleep(8);
        }
    }

    private static void attachMouseEvents(Game game) {
        game.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                mouseIsDown = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                mouseIsDown = false;
            }
        });
        game.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                mousePosition.setLocation(e.getPoint().x, e.getPoint().y - 20.0D);
                mouseIsDown = true;
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                mousePosition.setLocation(e.getPoint().x, e.getPoint().y - 20.0D);
                mouseIsDown = false;
            }
        });
    }

    private static JFrame getSetJFrame(String title, Game game) {
        JFrame frame = new JFrame(title);
        frame.add(game);
        frame.setSize(frameWidth, frameHeight);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(frameWidth+Game.brickSize/2, frameHeight));
        frame.pack();
        frame.setLocationRelativeTo(null);
        return frame;
    }

    private static void loadStage(int stage) {
        File file = new File("res/stage_" + stage + ".csv");
        Scanner inputStream;
        try {
            inputStream = new Scanner(file);
            int row = 1;
            while (inputStream.hasNext()) {
                String line = inputStream.next();
                String[] valueStrings = line.split(";");
                for (int i = 0; i < valueStrings.length; i++) {
                    int value = Integer.parseInt(valueStrings[i]);
                    if (value > 0)
                        brickStack.add(new Brick(new int[]{i, row}, value, Color.GRAY));
                }
                row++;
            }

            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void addBrick(int x, int y, Color color) {
        brickStack.add(new Brick(new int[]{x, y}, 10, color));
    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(0, 0, frameWidth, frameHeight);
        g2d.setColor(Color.YELLOW);

        for (Ball ball : ballStack)
            ball.draw(g2d);

        for (Brick brick : brickStack)
            brick.draw(g2d);

        if (!mouseIsDown)
            Ray.cast(g2d, spawnPoint, getNormal(spawnPoint, mousePosition));

        g2d.setFont(standardFont);
        g2d.setColor(Color.WHITE);
        g2d.drawString("ballsShot: " + Ball.ballCount, 10, frameHeight - 50);
        g2d.drawString("overallScore: " + overallScore, frameWidth - 200, frameHeight - 50);

    }

    private static void spawnNewBall() {
        Vector<Double> directionVector = getNormal(spawnPoint, mousePosition);

        double xSpeed = directionVector.get(0) * ballSpawnSpeed;
        double ySpeed = directionVector.get(1) * ballSpawnSpeed;
        Vector<Double> velocity = new Vector<>();
        velocity.add(xSpeed);
        velocity.add(ySpeed);

        Ball newBall = new Ball(spawnPoint, velocity);
        ballStack.add(newBall);
    }

    public static Vector<Double> getNormal(Point2D.Double a, Point2D.Double b) {
        Vector<Double> result = new Vector<>();
        double dx = a.x - b.x;
        double dy = a.y - b.y;
        double viewAngle = Math.atan2(dy, dx);
        result.add(-Math.cos(viewAngle));
        result.add(-Math.sin(viewAngle));
        return result;
    }
}
