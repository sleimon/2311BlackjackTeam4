package Classes;

import javax.swing.*;

public class Main {

	public static void main(String[] args) {
		JFrame frame = new JFrame("BlackJack");
		Game game = new Game(1000);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.add(game);
		frame.setContentPane(game);
		frame.setVisible(true);
	}
}
