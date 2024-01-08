import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Maxwell {

	// describes two types of particles
	public enum Type {HOT, COLD};

	// the rate at which the speed affects movement
	double delta = 0.01;

	// define background colors
	Color lightRed = new Color(255, 204, 203);
	Color lightBlue = new Color(0, 195, 227);

	// internal state of the particles
	Particle[] hotParticles;
	Particle[] coldParticles;
	int hotCount;
	int coldCount;

	// temperatures of the boxes
	int temperatureLeft;
	int temperatureRight;

	JTextArea leftTemp;
	JTextArea rightTemp;

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

	int particleRadius = 10;
	int wallWidth = 30;
	int bounceCompensation = 10;
	int doorCompensation = 5;

	// maximum amount of particles allowed
	int maxParticles = 10000;

	// particle speeds

	// translates to 
	// hot = 3 cm/s 
	// cold = 5 cm/s
	// variance = 1 cm/s

	int hotSpeed = 1875;
	int coldSpeed = 1125;
	int speedVariance = 375;

	// represents whether door is open or not
	boolean doorOpen = false;

	/**
	 * main - creates an instance of Maxwell game object
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new Maxwell();
	}

	/**
	 * Maxwell constructor
	 * 
	 * here lies the main logic of the game. A mouse listener listens to the mouse click
	 * and release and opens or closes the door accordingly. Buttons for adding particles
	 * and reset are added to control panel. Particles are initialized, two each for hot and cold.
	 * Below control panel game panel is created. Timer is created to handle particle animation
	 * and frame refresh. The frame is set to visible after it is ready.
	 */
	public Maxwell() {

		// create the main frame
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Maxwell's Demon");
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

		// initializing the particles
		hotParticles = new Particle[maxParticles];
		coldParticles = new Particle[maxParticles];
		hotParticles[hotCount++] = new Particle(Type.HOT, Type.HOT);
		hotParticles[hotCount++] = new Particle(Type.HOT, Type.COLD);
		coldParticles[coldCount++] = new Particle(Type.COLD, Type.HOT);
		coldParticles[coldCount++] = new Particle(Type.COLD, Type.COLD);

		// Create the play area
		gamePanel = new Game();
		frame.add(gamePanel, BorderLayout.CENTER);

		// Create the temperature display panel
		temperaturePanel = new JPanel();
		//temperaturePanel.setLayout(new GridLayout(1, 2));

		leftTemp = new JTextArea("0");


		rightTemp = new JTextArea("0");
		temperaturePanel.add(leftTemp);
		temperaturePanel.add(rightTemp);

		frame.add(temperaturePanel, BorderLayout.SOUTH);

		// Create a timer
		Timer tick = new Timer(100, new Animator());
		tick.start();

		// make visible 
		frame.setSize(frameWidth, frameHeight);
		frame.setVisible(true);
	}

	/**
	 * Particle - represents both hot and cold particles
	 */
	public class Particle {

		Type  type;
		double x, y;
		double vx, vy;
		double oldx, oldy;

		public Particle(Type t, Type chamber) {
			
			type = t;

			if (type == Type.HOT)
			{
				if (chamber == Type.HOT) {
					x = (int) (Math.random() * (gameWidth / 2) * 0.8); // 0, gameWidth / 2
					y = (int) (Math.random() * (gameHeight));
				}
				else {
					x = (int) ((gameWidth / 2) + Math.random() * (gameWidth / 2) * 0.8 + (0.2 * gameWidth / 2)); // gameWidth / 2, gameWidth
					y = (int) (Math.random() * (gameHeight));
				}

				vx = (int) (hotSpeed + ((Math.random() * speedVariance) - (2 * speedVariance)));
				vy = (int) (hotSpeed + ((Math.random() * speedVariance) - (2 * speedVariance)));
			}
			else
			{
				if (chamber == Type.HOT) {
					x = (int) (Math.random() * (gameWidth / 2) * 0.8); // 0, gameWidth / 2
					y = (int) (Math.random() * (gameHeight));
				}
				else {
					x = (int) ((gameWidth / 2) + Math.random() * (gameWidth / 2) * 0.8 + (0.2 * gameWidth / 2)); // gameWidth / 2, gameWidth
					y = (int) (Math.random() * (gameHeight));
				}
				vx = (int) (coldSpeed + ((Math.random() * speedVariance) - (2 * speedVariance)));
				vy = (int) (coldSpeed + ((Math.random() * speedVariance) - (2 * speedVariance)));	
			}
		}

		/**
		 * returns the actual speed
		 * 
		 * @return the speed 
		 */
		public double getSpeed() {
			return Math.sqrt(vx * vx + vy + vy);
		}

		/**
		 * moves the particle based on delta rate
		 * 
		 * @param delta
		 */
		public void move(double delta) {
			oldx = x;
			oldy = y;
			x += vx * delta;
			y += vy * delta;
			stayOnScreen();
			handleWall();
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
				vy *= -1;
		}

		/**
		 * handles collision with center wall, open or closed door
		 */
		public void handleWall() {			
			// first scenario - door is open
			if (doorOpen) {

				if ((vx > 0) && (x > gameWidth / 2 - wallWidth / 2) && x < gameWidth / 2
				&& ((y > gameHeight * 2 / 3) || (y < gameHeight / 3))) {
					x = gameWidth / 2 - wallWidth / 2 - bounceCompensation;
					vx *= -1;
				}
				if (vx < 0 && x < gameWidth / 2 + wallWidth / 2 && x > gameWidth / 2
				&& ((y > gameHeight * 2 / 3) || (y < gameHeight / 3))) {
					x = gameWidth / 2 + wallWidth / 2 + bounceCompensation;
					vx *= -1;
				}
				
				// handles the top and bottom edges of the door
				if ((gameWidth / 2 - wallWidth / 2 - bounceCompensation < x && x <  gameWidth / 2 + wallWidth / 2 + bounceCompensation)
				&& ((gameHeight / 3 - doorCompensation < y && y < gameHeight / 3 + doorCompensation) || (gameHeight * 2 / 3 - doorCompensation < y && y < gameHeight * 2 / 3 + doorCompensation))
				) {
					if ((gameHeight / 3 - doorCompensation < y && y < gameHeight / 3 + doorCompensation)) {
						y = gameHeight / 3 + doorCompensation + 2;
					}
					if ((gameHeight * 2 / 3 - doorCompensation < y && y < gameHeight * 2 / 3 + doorCompensation)) {
						y = gameHeight * 2 / 3 - doorCompensation - 2;
					}
					vy *= -1;	
				}
	
				
				
				} // if
			else {

				if ((vx > 0) && (x > gameWidth / 2 - wallWidth / 2) && x < gameWidth / 2) {
					x = gameWidth / 2 - wallWidth / 2 - bounceCompensation;
					vx *= -1;
				}
				if (vx < 0 && x < gameWidth / 2 + wallWidth / 2 && x > gameWidth / 2) {
					x = gameWidth / 2 + wallWidth / 2 + bounceCompensation;
					vx *= -1;
				}
			} // if

			// make sure we don't get stuck inside the wall
			if (gameWidth / 2 - wallWidth / 2 < x && x < gameWidth / 2 + wallWidth / 2) {
				//x = (int)(Math.random() * 20 - 10);
				//x = 0;
			}
		}

		/**
		 * particles draw themselves
		 * 
		 * @param g graphics to be drawn on
		 */
		public void draw(Graphics g) {

			if (type == Type.HOT) {
				g.setColor(Color.RED);
				g.fillOval((int) (x - particleRadius / 2), (int) (y - particleRadius / 2), particleRadius, particleRadius);
			}
			else {
				g.setColor(Color.BLUE);
				g.fillOval((int) (x - particleRadius / 2), (int) (y - particleRadius / 2), particleRadius, particleRadius);
			}
		}
	}

	/**
	 * the game panel 
	 */
	public class Game extends JPanel {

		/**
		 * paints the game panel and the particles
		 * 
		 * @param g graphics to be drawn on
		 */
		@Override
		public void paintComponent(Graphics g) {

			// the game has two panels left and righ, hot and cold
			
			g.setColor(lightRed);
			g.fillRect(0,0,gameWidth / 2, gameHeight);

			g.setColor(lightBlue);
			g.fillRect(gameWidth / 2, 0, gameWidth, gameHeight);

			// draw the wall in the middle
			g.setColor(Color.black);

			if (doorOpen) {
				g.fillRect(gameWidth / 2 - wallWidth / 2, 0, wallWidth, gameHeight / 3);
				g.fillRect(gameWidth / 2 - wallWidth / 2, gameHeight * 2 / 3, wallWidth, gameHeight);

			}
			else {
				g.fillRect(gameWidth / 2 - wallWidth / 2, 0, wallWidth, gameHeight);
			}

			for (int i = 0; i < hotCount; i++) {
				hotParticles[i].draw(g);
				coldParticles[i].draw(g);
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
			for (int i = 0; i < hotCount; i++) {
				hotParticles[i].move(delta);
				coldParticles[i].move(delta);
			}
			gamePanel.repaint();

			// also calculate the temperature here
			int leftParticles = 0;
			int rightParticles = 0;
			double leftSum = 0;
			double rightSum = 0;

			for (int i = 0; i < hotCount; i++) {
				double speed = hotParticles[i].getSpeed();
				if (hotParticles[i].x < gameWidth / 2) {
					leftParticles++;
					leftSum += (speed * speed);
				} 
				else {
					rightParticles++;
					rightSum += (speed * speed);
				} 
			}

			for (int i = 0; i < coldCount; i++) {
				double speed = coldParticles[i].getSpeed();
				if (coldParticles[i].x < gameWidth / 2) {
					leftParticles++;
					leftSum += (speed * speed);
				} 
				else {
					rightParticles++;
					rightSum += (speed * speed);
				} 
			}

			temperatureLeft = (int) leftSum / leftParticles;
			temperatureRight = (int) rightSum / rightParticles;

			leftTemp.setText("Red Side Temperature:  " + Integer.toString(temperatureLeft) + "   ");
			rightTemp.setText("Blue Side Temperature:  " + Integer.toString(temperatureRight) + "   ");
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
			if (hotCount + coldCount < maxParticles) {
			hotParticles[hotCount++] = new Particle(Type.HOT, Type.HOT);
			hotParticles[hotCount++] = new Particle(Type.HOT, Type.COLD);
			coldParticles[coldCount++] = new Particle(Type.COLD, Type.HOT);
			coldParticles[coldCount++] = new Particle(Type.COLD, Type.COLD);

			} // if
        }
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
			hotCount = 0;
			coldCount = 0;

			hotParticles = new Particle[maxParticles];
			coldParticles = new Particle[maxParticles];
			hotParticles[hotCount++] = new Particle(Type.HOT, Type.HOT);
			hotParticles[hotCount++] = new Particle(Type.HOT, Type.COLD);
			coldParticles[coldCount++] = new Particle(Type.COLD, Type.HOT);
			coldParticles[coldCount++] = new Particle(Type.COLD, Type.COLD);
        }
    }
}
