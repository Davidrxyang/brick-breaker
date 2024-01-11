import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RedWindowWithBall extends JFrame {

    private int ballX = 50;
    private int ballY = 50;
    private int ballSpeedX = 2;
    private int ballSpeedY = 2;

    public RedWindowWithBall() {
        setTitle("Red Window with Moving Ball");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.RED);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.BLUE);
                g.fillOval(ballX, ballY, 20, 20);
            }
        };

        Timer timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveBall();
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

        if (ballY <= 0 || ballY >= getHeight() - 20) {
            ballSpeedY = -ballSpeedY;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RedWindowWithBall());
    }
}
