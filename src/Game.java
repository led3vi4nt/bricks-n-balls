import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.*;
import javax.swing.*;
import java.io.*;

public class Game extends JPanel {

    public static final int frameWidth = 465;
    public static final int frameHeight = 800;
    public static final Color fontColor = Color.WHITE;
    public static final int brickSize = 25;
    public static Font standardFont = new Font("Lucida Console", Font.BOLD, 12);
    private static Point2D.Double mousePosition = new Point2D.Double();
    private static ArrayList<Ball> ballStack = new ArrayList<>();
    private static ArrayList<Brick> brickStack = new ArrayList<>();
    private static Point2D.Double spawnPoint;
    private static double ballSpawnSpeed = 5.0D;
    public static boolean mouseIsDown = false;
    private static int overallScore = 0;

    public static void main(String[] args) throws InterruptedException {
        JFrame frame = new JFrame("BRIXnBALLS");
        Game game = new Game();
        frame.add(game);
        frame.setSize(frameWidth, frameHeight);
        spawnPoint = new Point2D.Double(frame.getWidth() / 2.0D, 750.0D);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // needs further investigation :P
        //frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        //frame.setUndecorated(true);

        // setBackground() doesn't seems to work
        frame.getContentPane().setBackground(Color.DARK_GRAY);
        frame.setPreferredSize(new Dimension(frameWidth, frameHeight));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
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
                boolean hit = false;
                for (int j = 0; j < brickStack.size(); j++) {
                    if (brickStack.get(j).checkHit(ballStack.get(i))) {
                        ballStack.get(i).bounce(brickStack.get(j));
                        hit = true;
                        overallScore++;
                        if (--brickStack.get(j).value <= 0)
                            brickStack.remove(j);
                    }
                }
                if (ballStack.get(i).position.y > frameHeight - 20)
                    ballStack.remove(i);
            }

            game.repaint();
            Thread.sleep(8);
        }
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
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, frameWidth, frameHeight);
        g.setColor(Color.YELLOW);

        for (Ball ball : ballStack)
            ball.draw(g2d);

        for (Brick brick : brickStack)
            brick.draw(g2d);

        g.setFont(standardFont);
        g.setColor(Color.WHITE);
        g.drawString("ballsShot: " + Ball.ballCount, 10, frameHeight - 50);
        g.drawString("overallScore: " + overallScore, frameWidth-200, frameHeight - 50);

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

    private static Vector<Double> getNormal(Point2D.Double a, Point2D.Double b) {
        Vector<Double> result = new Vector<>();
        double dx = a.x - b.x;
        double dy = a.y - b.y;
        double viewAngle = Math.atan2(dy, dx);
        result.add(-Math.cos(viewAngle));
        result.add(-Math.sin(viewAngle));
        return result;
    }
}
