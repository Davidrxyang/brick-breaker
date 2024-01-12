import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class BrickBreak extends JFrame {
 
    // private data members
    
    // ball representation
    private int ballX = 500;
    private int ballY = 500;
    private int ballSpeedX = 4;
    private int ballSpeedY = 4;

    // brick representation
    private int numBricks = 19;
    private int rowBricks = 10;

    // paddle representation
    private int paddleX = 150;

    private List<Brick> bricks;

    public BrickBreak() {
        setTitle("Brick Break!!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setSize(1000, 700);
        setResizable(false);

        bricks = createBricks();

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.BLUE);
                g.fillOval(ballX, ballY, 20, 20);
                g.setColor(Color.BLACK);
                g.fillRect(paddleX, getHeight() - 20, 80, 10);

                for (Brick brick : bricks) {
                    if (brick.isVisible()) {
                        g.setColor(Color.RED);
                        g.fillRect(brick.getX(), brick.getY(), brick.getWidth(), brick.getHeight());
                    }
                }
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

    private void moveBall() {
        ballX += ballSpeedX;
        ballY += ballSpeedY;

        if (ballX <= 0 || ballX >= getWidth() - 20) {
            ballSpeedX = -ballSpeedX;
        }

        if (ballY <= 0) {
            ballSpeedY = -ballSpeedY;
        }
    }

    private void checkPaddleCollision() {
        if (ballY >= getHeight() - 50 && ballX >= paddleX && ballX <= paddleX + 80) {
            ballSpeedY = -ballSpeedY;
        }
    }

    private void checkBrickCollision() {
        for (Brick brick : bricks) {
			if (brick.isVisible()) {

				if (ballX + 20 >= brick.getX() && ballX <= brick.getX() + brick.getWidth()
						&& ballY + 20 >= brick.getY() && ballY <= brick.getY() + brick.getHeight()) {
					ballSpeedY = -ballSpeedY;
					brick.setVisible(false);
				}
			}
        }
    }
    private void checkBottomCollision() {
        if (ballY >= getHeight()) {
            ballX = getWidth() / 2 - 10; // Center of the frame
            ballY = getHeight() / 2 + 50;
            randomizeDirection();
        }
    }

    private void randomizeDirection() {
        Random rand = new Random();
        ballSpeedX = rand.nextBoolean() ? ballSpeedX : -ballSpeedX;
        ballSpeedY = rand.nextBoolean() ? ballSpeedY : ballSpeedY;
    }


    private List<Brick> createBricks() {
        List<Brick> brickList = new ArrayList<>();
        int brickWidth = 40;
        int brickHeight = 10;

        for (int j = 0; j < rowBricks; j++) {

            for (int i = 0; i < numBricks; i++) {
                int brickX = i * (brickWidth + 10) + 30; // Spacing between bricks
                int brickY = 30 * (j + 1);
                brickList.add(new Brick(brickX, brickY, brickWidth, brickHeight));
            }

        }

        return brickList;
    }

    private static class Brick {
        private int x;
        private int y;
        private int width;
        private int height;
        private boolean visible;

        public Brick(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.visible = true;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public boolean isVisible() {
            return visible;
        }

        public void setVisible(boolean visible) {
            this.visible = visible;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BrickBreak());
    }
}
