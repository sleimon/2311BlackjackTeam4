package ClassesTest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import Classes.Player;

class PlayerTest {

	private Player player;
    private deckOfCards deck;
    private deckOfCards discard;


	@BeforeEach
	void init() {

		player = new Player(500);
		deck = new deckOfCards();
		discard = new deckOfCards();

	}

	@Test
	void getChipsTest(){

		int expected = 500;
		int actual = player.getChips();

		assertEquals(expected, actual, "The player should start with 500 chips.  ");

	}

	@Test
	void placeBetTest(){

		player.placeBet(100);
		int expected = 400;
		int actual = player.getChips();

		assertEquals(expected, actual, "The player should have 100 less chips after placing a 100 chip bet.  ");
	}

	@Test
	void resetBetTest(){

		player.placeBet(100);
		player.resetBet();
		int expected = 0;
		int actual = player.getChips();

		assertEquals(expected, actual, "The player should have 0 chips in his bet after a successful reset.  ");
	}

	@Test
	void winBetTest(){

		player.placeBet(100);
		player.winBet();
		int expected = 600;
		int actual = player.getChips();

		assertEquals(expected, actual, "The player should have 600 chips after winning this bet.  ");
	}

	@Test
	void instant21Test(){

		player.placeBet(100);
		player.instant21();
		int expected = 750;
		int actual = player.getChips();

		assertEquals(expected, actual, "The player should have 750 chips after an instant 21, due to the 2.5 times multiplier.   ");
	}

	@Test
	void loseBetTest(){

		player.placeBet(100);
		player.loseBet();
		int expected = 400;
		int actual = player.getChips();

		assertEquals(expected, actual, "The player should have lost 100 chips after placing a bet for 100 chips and losing.    ");

	}

	@Test
	void pushBetTest(){

		player.placeBet(100);
		player.pushBet();
		int expected = 500;
		int actual = player.getChips();

		assertEquals(expected, actual, "The player should have 500 chips after a push. ");
}
