import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class BrickBreak {

	// describes two types of particles
	public enum Type {HOT, COLD};

	// the rate at which the speed affects movement
	double delta = 0.01;

	// define background colors
	Color lightRed = new Color(255, 204, 203);
	Color lightBlue = new Color(0, 195, 227);

	// internal state of the particles
	Ball[] balls;
	int ballCount = 1;

	// java swing objects
	JFrame frame;
	Game gamePanel;
	JPanel controlPanel;
	JPanel temperaturePanel;

	// dimensions
	int frameWidth = 900;
	int frameHeight = 720;

	int gameWidth = frameWidth;
	int gameHeight = 635;

	int wallWidth = 30;

	int ballRadius = 10;

	// maximum amount of particles allowed
	int maxParticles = 10000;

	// ball speed
	int speed = 1500;

	// represents whether door is open or not
	boolean doorOpen = false;

	/**
	 * main - creates an instance of BrickBreak game object
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new BrickBreak();
	}

	/**
	 * BrickBreak constructor
	 * 
	 * here lies the main logic of the game. A mouse listener listens to the mouse click
	 * and release and opens or closes the door accordingly. Buttons for adding particles
	 * and reset are added to control panel. Particles are initialized, two each for hot and cold.
	 * Below control panel game panel is created. Timer is created to handle particle animation
	 * and frame refresh. The frame is set to visible after it is ready.
	 */
	public BrickBreak() {

		// create the main frame
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("BrickBreak");
		frame.setBackground(Color.white);
		frame.setLayout(new BorderLayout());
		frame.setResizable(false);

		// listen for door close or open
		frame.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				doorOpen = true;
			} 

			public void mouseReleased(MouseEvent e) {
				doorOpen = false;
			} 
		});

		// create and add the control panel
		controlPanel = new JPanel();

		JButton addButton = new JButton("Add Particles");
        addButton.setBounds(50, 50, 50, 50);
        addButton.addMouseListener(new AddMouseListener("Add Particle"));
        addButton.addFocusListener(new MyFocusListener("Add Particle"));
        controlPanel.add(addButton);

        JButton resetButton = new JButton("Reset");
        resetButton.setBounds(50, 50, 50, 50);
        resetButton.addMouseListener(new ResetMouseListener("Reset"));
        resetButton.addFocusListener(new MyFocusListener("Reset"));
		controlPanel.add(resetButton);

		frame.add(controlPanel, BorderLayout.NORTH);

		// initializing the balls

		balls = new Ball[ballCount];

		// Create the play area
		gamePanel = new Game();
		frame.add(gamePanel, BorderLayout.CENTER);

		// Create a timer
		Timer tick = new Timer(100, new Animator());
		tick.start();

		// make visible 
		frame.setSize(frameWidth, frameHeight);
		frame.setVisible(true);
	}

	/**
	 * Ball - ball that bounces and breaks bricks
	 */

	public class Ball {
		double x, y, vx, vy;
		boolean isActive;

		/**
		 * constructor - sets values to initial values
		 */
		public Ball() {
			x = (int) (Math.random() * (gameWidth / 2) * 0.8);
			y = (int) (Math.random() * (gameHeight / 4) + gameHeight / 2);
			vx = speed;
			vy = speed;
			isActive = true;
		}

		/**
		 * moves the ball based on delta rate and accounts for collisions
		 * 
		 * @param delta
		 */
		public void move(double delta) {
			x += vx * delta;
			y += vy * delta;
			stayOnScreen();
			handlePlatform();
			handleBrick();
		}

		/**
		 * handles collision with screen borders
		 */
		public void stayOnScreen() {
			// Check bounces off each edge of screen
			if (x < 0)
				vx *= -1;
			if (x > gameWidth)
				vx *= -1;
			if (y < 0)
				vy *= -1;
			if (y > gameHeight)
				outOfBounds();
		}

		/**
		 * handles collision with player controlled platform
		 */
		public void handlePlatform() {

		}

		/**
		 * handles collision with brick objects
		 */
		public void handleBrick() {
			
		}

		/**
		 * handles ball falling out of game from bottom 
		 */
		public void outOfBounds() {
			isActive = false;
		}

		public boolean isActive() {return isActive;}

		/**
		 * the balls draw themselves
		 * 
		 * @param g graphics
		 */
		public void draw(Graphics g) {
			g.setColor(Color.black);
			g.fillOval((int) (x - ballRadius / 2), (int) (y - ballRadius / 2), ballRadius, ballRadius);
		}
	}

	/**
	 * the game panel 
	 */
	public class Game extends JPanel {

		/**
		 * paints the game panel, balls, bricks, platform
		 * 
		 * @param g graphics to be drawn on
		 */
		@Override
		public void paintComponent(Graphics g) {
			for (int i = 0; i < ballCount; i++) {
				balls[i].draw(g);
			}
		}
	}

	/**
	 * the controller - handles game refresh
	 */
	public class Animator implements ActionListener {

		/**
		 * handles the game loop, every tick of the timer an action is triggerd and the 
		 * game state is updated
		 * 
		 * @param e the tick event
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			for (int i = 0; i < ballCount; i++) {
				balls[i].move(delta);
			}
			gamePanel.repaint();
		}
	}

	/**
	 * mouse focus listener
	 */
	class MyFocusListener implements FocusListener {
        private String id;

		/**
		 * constructor
		 * 
		 * @param id the id of the listener
		 */
        public MyFocusListener(String id) {
            this.id = id;
        }

		/**
		 * mouse focus gained
		 * 
		 * @param e mouse event
		 */
        public void focusGained(FocusEvent e) {
            System.out.println(id + ": Focus Gained");
        }

		/**
		 * focus lost
		 * 
		 * @parm e mouse event
		 */
        public void focusLost(FocusEvent e) {
            System.out.println(id + ": Focus Lost");
        }
    }

	/**
	 * handles add particle events
	 */
    class AddMouseListener extends MouseAdapter {
        private String id;

		/**
		 * constructor
		 * 
		 * @param id the id of the listener
		 */
        public AddMouseListener(String id) {
            this.id = id;
        }

		/**
		 * mouse clicked on the button to add particles, add particles
		 * 
		 * @param e mouse event
		 */
        @Override
        public void mouseClicked(MouseEvent e) {
            System.out.println(id + ": Mouse Clicked");

			//if (hotCount + coldCount < maxParticles) {
			//hotParticles[hotCount++] = new Particle(Type.HOT, Type.HOT);
			//hotParticles[hotCount++] = new Particle(Type.HOT, Type.COLD);
			//coldParticles[coldCount++] = new Particle(Type.COLD, Type.HOT);
			//coldParticles[coldCount++] = new Particle(Type.COLD, Type.COLD);

		} // if
    }


	/**
	 * handles game reset
	 */
    class ResetMouseListener extends MouseAdapter {
        private String id;

		/**
		 * constructor
		 * 
		 * @param id the id of the listener
		 */
        public ResetMouseListener(String id) {
            this.id = id;
        }

		/**
		 * resets the game state
		 * 
		 * @param e mouse event
		 */
        @Override
        public void mouseClicked(MouseEvent e) {
            System.out.println(id + ": Mouse Clicked");

			//hotCount = 0;
			//coldCount = 0;

			//hotParticles = new Particle[maxParticles];
			//coldParticles = new Particle[maxParticles];
			//hotParticles[hotCount++] = new Particle(Type.HOT, Type.HOT);
			//hotParticles[hotCount++] = new Particle(Type.HOT, Type.COLD);
			//coldParticles[coldCount++] = new Particle(Type.COLD, Type.HOT);
			//coldParticles[coldCount++] = new Particle(Type.COLD, Type.COLD);
        }
    }
}
