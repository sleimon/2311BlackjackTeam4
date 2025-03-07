package ClassesTest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.blackjack.Models.*;

class CardTest {
	
	//Initialize a Card object for testing
	Card card;
	
	//before each test, create the same card, the ace of spades
	@BeforeEach
	void beforeEach() {
		card = new Card("Ace", "Spades", 11);
	}
	
	//test to see if the getRank method returns the expected value
	@Test
	void testGetRank() {
		String actual = card.getRank();
		String expected = "Ace";
		assertEquals(actual, expected);
	}
	
	//test to see if the getSuit method returns the expected value
	@Test
	void testGetSuit() {
		String actual = card.getSuit();
		String expected = "Spades";
		assertEquals(actual, expected);
	}
	
	//test to see if the getValue method returns the expected value
	@Test
	void testGetValue() {
		int actual = card.getValue();
		int expected = 11;
		assertEquals(actual, expected);
	}
	
	//test to see if the toString method returns the expected value
	@Test
	void testToString() {
		String actual = card.toString();
		String expected = "Ace of Spades(11)";
		assertEquals(actual, expected);
	}

}
