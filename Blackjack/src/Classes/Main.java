package Classes;
import database.StubDatabase;
import javax.swing.*;

public class Main {

	public static void main(String[] args) {
		String username = JOptionPane.showInputDialog("Enter your username:");
        if (username == null || username.trim().isEmpty()) {
            System.out.println("No username entered. Exiting the program.");
            return;
        }
		// Print all users before loading the game (just for debugging)
        System.out.println("Users before loading the game:");
        StubDatabase.printUsers();

		//creating game frame
		JFrame frame = new JFrame("BlackJack");
		// Initializing the game with the username
		Game game = new Game(username);// passing username to Game constructor
		//	Game game = new Game(1000);

		//setting up the frame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.add(game);
		frame.setContentPane(game);
		frame.setVisible(true);

		// printing user data after loading game(just for debugging)
		StubDatabase.printUsers();
	}
}
