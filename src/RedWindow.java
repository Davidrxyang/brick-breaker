import javax.swing.*;
import java.awt.*;

public class RedWindow extends JFrame {

    public RedWindow() {
        setTitle("Red Window");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setBackground(Color.RED);

        add(panel);

        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RedWindow());
    }
}
