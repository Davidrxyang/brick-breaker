import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BrickBreakerGame extends JFrame {

    private static final int WIDTH = 400;
    private static final int HEIGHT = 600;
    private static final int PADDLE_WIDTH = 60;
    private static final int PADDLE_HEIGHT = 10;
    private static final int BALL_SIZE = 20;
    private static final int BRICK_WIDTH = 50;
    private static final int BRICK_HEIGHT = 20;
    private static final int NUM_BRICKS_ROW = 8;
    private static final int NUM_BRICKS_COL = 5;

    private int paddleX;
    private int ballX, ballY, ballSpeedX, ballSpeedY;
    private boolean ballMoving;

    private boolean[] bricks;

    public BrickBreakerGame() {
        setTitle("Brick Breaker");
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        paddleX = WIDTH / 2 - PADDLE_WIDTH / 2;
        ballX = WIDTH / 2;
        ballY = HEIGHT - PADDLE_HEIGHT - BALL_SIZE - 10;
        ballSpeedX = 3;
        ballSpeedY = -3;
        ballMoving = false;

        bricks = new boolean[NUM_BRICKS_ROW * NUM_BRICKS_COL];
        for (int i = 0; i < bricks.length; i++) {
            bricks[i] = true;
        }

        addKeyListener(new KeyAdapter());
        setFocusable(true);
        Timer timer = new Timer(10, new TimerListener());
        timer.start();
    }

    private class KeyAdapter extends java.awt.event.KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_LEFT && paddleX > 0) {
                paddleX -= 10;
            } else if (key == KeyEvent.VK_RIGHT && paddleX < WIDTH - PADDLE_WIDTH) {
                paddleX += 10;
            } else if (key == KeyEvent.VK_SPACE) {
                ballMoving = true;
            }
        }
    }

    private class TimerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (ballMoving) {
                ballX += ballSpeedX;
                ballY += ballSpeedY;

                // Check for collisions with walls
                if (ballX <= 0 || ballX >= WIDTH - BALL_SIZE) {
                    ballSpeedX = -ballSpeedX;
                }
                if (ballY <= 0) {
                    ballSpeedY = -ballSpeedY;
                }

                // Check for collisions with paddle
                if (ballY >= HEIGHT - PADDLE_HEIGHT - BALL_SIZE &&
                        ballX >= paddleX && ballX <= paddleX + PADDLE_WIDTH) {
                    ballSpeedY = -ballSpeedY;
                }

                // Check for collisions with bricks
                for (int i = 0; i < NUM_BRICKS_ROW * NUM_BRICKS_COL; i++) {
                    if (bricks[i] && ballX >= i % NUM_BRICKS_ROW * BRICK_WIDTH
                            && ballX <= i % NUM_BRICKS_ROW * BRICK_WIDTH + BRICK_WIDTH
                            && ballY >= i / NUM_BRICKS_ROW * BRICK_HEIGHT
                            && ballY <= i / NUM_BRICKS_ROW * BRICK_HEIGHT + BRICK_HEIGHT) {
                        bricks[i] = false;
                        ballSpeedY = -ballSpeedY;
                    }
                }

                // Check if the ball goes below the paddle
                if (ballY >= HEIGHT) {
                    ballMoving = false;
                    ballX = WIDTH / 2;
                    ballY = HEIGHT - PADDLE_HEIGHT - BALL_SIZE - 10;
                }
            }

            repaint();
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // Draw paddle
        g.fillRect(paddleX, HEIGHT - PADDLE_HEIGHT, PADDLE_WIDTH, PADDLE_HEIGHT);

        // Draw ball
        g.fillOval(ballX, ballY, BALL_SIZE, BALL_SIZE);

        // Draw bricks
        int brickIndex = 0;
        for (int i = 0; i < NUM_BRICKS_COL; i++) {
            for (int j = 0; j < NUM_BRICKS_ROW; j++) {
                if (bricks[i * NUM_BRICKS_ROW + j]) {
                    g.fillRect(j * BRICK_WIDTH, i * BRICK_HEIGHT, BRICK_WIDTH, BRICK_HEIGHT);
                }
                brickIndex++;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BrickBreakerGame game = new BrickBreakerGame();
            game.setVisible(true);
        });
    }
}
