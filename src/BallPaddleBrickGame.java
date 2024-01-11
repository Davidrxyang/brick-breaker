import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A simple Swing application demonstrating a bouncing ball, a paddle, and bricks.
 */
public class BallPaddleBrickGame extends JFrame {

    private int ballX = 50;
    private int ballY = 50;
    private int ballSpeedX;
    private int ballSpeedY;

    private int paddleX = 150;

    private List<Brick> bricks;

    /**
     * Constructs the main frame of the application.
     */
    public BallPaddleBrickGame() {
        setTitle("Ball, Paddle, and Bricks Game");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        bricks = createBricks();

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawComponents(g);
            }
        };

        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                paddleX = e.getX() - 40; // Center the paddle under the cursor
                panel.repaint();
            }
        });

        Timer timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveBall();
                checkPaddleCollision();
                checkBrickCollision();
                checkBottomCollision();
                panel.repaint();
            }
        });
        timer.start();

        add(panel);

        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    /**
     * Moves the ball based on its current speed.
     */
    private void moveBall() {
        ballX += ballSpeedX;
        ballY += ballSpeedY;
    }

    /**
     * Checks for collisions with the paddle and changes the ball's direction accordingly.
     */
    private void checkPaddleCollision() {
        if (ballY >= getHeight() - 30 && ballX >= paddleX && ballX <= paddleX + 80) {
            ballSpeedY = -ballSpeedY;
        }
    }

    /**
     * Checks for collisions with the bricks and changes the ball's direction accordingly.
     * Removes the brick upon collision.
     */
    private void checkBrickCollision() {
        for (Brick brick : bricks) {
            if (brick.isVisible() && ballX + 20 >= brick.getX() && ballX <= brick.getX() + brick.getWidth()
                    && ballY + 20 >= brick.getY() && ballY <= brick.getY() + brick.getHeight()) {
                ballSpeedY = -ballSpeedY;
                brick.setVisible(false);
            }
        }
    }

    /**
     * Checks if the ball has reached the bottom of the frame. If so, repositions the ball
     * at the center and randomizes its direction.
     */
    private void checkBottomCollision() {
        if (ballY >= getHeight()) {
            ballX = getWidth() / 2 - 10; // Center of the frame
            ballY = getHeight() / 2 - 10;
            randomizeDirection();
        }
    }

    /**
     * Randomly sets the direction of the ball by assigning random speed values.
     */
    private void randomizeDirection() {
        Random rand = new Random();
        ballSpeedX = rand.nextBoolean() ? 2 : -2;
        ballSpeedY = rand.nextBoolean() ? 2 : -2;
    }

    /**
     * Draws the components on the panel, including the background, ball, paddle, and bricks.
     *
     * @param g the graphics context
     */
    private void drawComponents(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.BLUE);
        g.fillOval(ballX, ballY, 20, 20);
        g.setColor(Color.GREEN);
        g.fillRect(paddleX, getHeight() - 20, 80, 10);

        for (Brick brick : bricks) {
            if (brick.isVisible()) {
                g.setColor(Color.YELLOW);
                g.fillRect(brick.getX(), brick.getY(), brick.getWidth(), brick.getHeight());
            }
        }
    }

    /**
     * Creates and returns a list of bricks for the top half of the screen.
     *
     * @return a list of bricks
     */
    private List<Brick> createBricks() {
        List<Brick> brickList = new ArrayList<>();
        int brickWidth = 40;
        int brickHeight = 10;

        for (int i = 0; i < 4; i++) {
            int brickX = i * (brickWidth + 10) + 30; // Spacing between bricks
            int brickY = 30;
            brickList.add(new Brick(brickX, brickY, brickWidth, brickHeight));
        }

        return brickList;
    }

    /**
     * Represents a brick in the game.
     */
    private static class Brick {
        private int x;
        private int y;
        private int width;
        private int height;
        private boolean visible;

        /**
         * Constructs a brick with the specified parameters.
         *
         * @param x      the x-coordinate of the top-left corner of the brick
         * @param y      the y-coordinate of the top-left corner of the brick
         * @param width  the width of the brick
         * @param height the height of the brick
         */
        public Brick(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.visible = true;
        }

        /**
         * Gets the x-coordinate of the top-left corner of the brick.
         *
         * @return the x-coordinate
         */
        public int getX() {
            return x;
        }

        /**
         * Gets the y-coordinate of the top-left corner of the brick.
         *
         * @return the y-coordinate
         */
        public int getY() {
            return y;
        }

        /**
         * Gets the width of the brick.
         *
         * @return the width
         */
        public int getWidth() {
            return width;
        }

        /**
         * Gets the height of the brick.
         *
         * @return the height
         */
        public int getHeight() {
            return height;
        }

        /**
         * Checks if the brick is visible.
         *
         * @return true if the brick is visible, false otherwise
         */
        public boolean isVisible() {
            return visible;
        }

        /**
         * Sets the visibility of the brick.
         *
         * @param visible the visibility to set
         */
        public void setVisible(boolean visible) {
            this.visible = visible;
        }
    }

    /**
     * The entry point of the application. Creates an instance of {@code BallPaddleBrickGame}.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BallPaddleBrickGame());
    }
}
