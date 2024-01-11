import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class DisappearingBallWithPaddle extends JFrame {

    private int ballX = 50;
    private int ballY = 50;
    private int ballSpeedX = 4;
    private int ballSpeedY = 4;

    private int paddleX = 150;



    public DisappearingBallWithPaddle() {
        setTitle("Disappearing Ball with Paddle");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.BLUE);
                g.fillOval(ballX, ballY, 20, 20);
                g.setColor(Color.GREEN);
                g.fillRect(paddleX, getHeight() - 20, 80, 10);
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

    private void checkBottomCollision() {
        if (ballY >= getHeight()) {
            ballX = getWidth() / 2 - 10; // Center of the frame
            ballY = getHeight() / 2 - 10;
            randomizeDirection();
        }
    }

    private void randomizeDirection() {
        Random rand = new Random();
        ballSpeedX = rand.nextBoolean() ? ballSpeedX : -ballSpeedX;
        ballSpeedY = rand.nextBoolean() ? ballSpeedY : -ballSpeedY;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DisappearingBallWithPaddle());
    }
}
