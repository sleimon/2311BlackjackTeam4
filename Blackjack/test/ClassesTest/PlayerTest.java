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



}
