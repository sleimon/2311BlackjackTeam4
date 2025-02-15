package Classes;
import database.StubDatabase;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {

	public static void main(String[] args) {
		// Create a panel for the login/sign up form
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(4, 2));

		// Username and password fields
		JLabel usernameLabel = new JLabel("Username:");
		JTextField usernameField = new JTextField(20);
		JLabel passwordLabel = new JLabel("Password:");
		JPasswordField passwordField = new JPasswordField(20);

		// Login and Sign Up buttons
		JButton loginButton = new JButton("Login");
		JButton signUpButton = new JButton("Sign Up");

		panel.add(usernameLabel);
		panel.add(usernameField);
		panel.add(passwordLabel);
		panel.add(passwordField);
		panel.add(loginButton);
		panel.add(signUpButton);

		// Create the frame and add the panel
		JFrame frame = new JFrame("BlackJack Login");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 200);
		frame.add(panel);
		frame.setVisible(true);

		loginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String username = usernameField.getText().trim();
				String password = new String(passwordField.getPassword()).trim();

				if (username.isEmpty() || password.isEmpty()) {
					JOptionPane.showMessageDialog(frame, "Please enter both username and password.");
					return;
				}

				// Check if user exists and if the password matches
				if (StubDatabase.validatePassword(username, password)) {
					// If user exists and password matches, proceed to the game
					System.out.println("User logged in: " + username);
					startGame(username);
					frame.dispose(); // Close the login window
				} else {
					JOptionPane.showMessageDialog(frame, "Incorrect username or password.");
				}
			}
		});

		// Action listener for Sign Up Button
		signUpButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String username = usernameField.getText().trim();
				String password = new String(passwordField.getPassword()).trim();

				if (username.isEmpty() || password.isEmpty()) {
					JOptionPane.showMessageDialog(frame, "Please enter both username and password.");
					return;
				}

				// Check if user already exists
				User existingUser = StubDatabase.getUser(username);
				if (existingUser != null) {
					JOptionPane.showMessageDialog(frame, "Username already exists. Please choose a different username.");
				} else {
					// Create new user and add to the database
					User newUser = new User(username, password, 1000, 0, 0, 0); // 1000 chips by default
					StubDatabase.addUser(newUser);
					JOptionPane.showMessageDialog(frame, "User created successfully.");
					startGame(username);
					frame.dispose(); // Close the sign-up window
				}
			}
		});
	}

	private static void startGame(String username) {
		// Create and show the game window
		Game game = new Game(username);
		JFrame gameFrame = new JFrame("BlackJack");
		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameFrame.setSize(800, 600);
		gameFrame.add(game);
		gameFrame.setVisible(true);
	}
}