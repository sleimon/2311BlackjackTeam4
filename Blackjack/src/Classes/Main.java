package Classes;
import database.StubDatabase;
import javax.swing.*;

public class Main {

	public static void main(String[] args) {
		String username = JOptionPane.showInputDialog("Enter your username:");
        if (username == null || username.trim().isEmpty()) {
            System.out.println("No username entered. Exiting...");
            return;
        }
		JFrame frame = new JFrame("BlackJack");
		Game game = new Game(1000);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.add(game);
		frame.setContentPane(game);
		frame.setVisible(true);

		// printing user data (for debugging)
		StubDatabase.printUsers();
	}
}