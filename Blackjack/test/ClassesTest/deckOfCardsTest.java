package ClassesTest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Classes.deckOfCards;
import Classes.Card;
import Classes.Hand;

class deckOfCardsTest {
	
	//Initializes a deckOfCards object for testing
	deckOfCards deck;
	
	//Before each test, create a new deckOfCards object for testing
	@BeforeEach
	void beforeEach() {
		deck = new deckOfCards(); 
	}
	
	//Tests if method hasCards returns the expected values
	@Test
	void testHasCards() {
		boolean result = deck.hasCards();
		assertTrue(result);
		deck.emptyDeck();
		result = deck.hasCards();
		assertFalse(result);
	}
	
	//Tests all three methods of addCards()
	@Test
	void testAddCards() {
		deckOfCards deck1 = new deckOfCards();
		deckOfCards deck2 = new deckOfCards();
		deckOfCards deck3 = new deckOfCards();
		deck1.emptyDeck();
		deck2.emptyDeck();
		deck3.emptyDeck();
		Hand hand = new Hand();
		Card card1 = new Card("Two", "Hearts", 2);
		Card card2 = new Card("Three", "Hearts", 3);
		hand.addCard(card1);
		hand.addCard(card2);
		ArrayList<Card> cards = new ArrayList<Card>();
		cards.add(card1);
		cards.add(card2);
		
		//Tests addCards(deckOfCards deck)
		deck1.addCards(deck);
		assertEquals(deck1, deck);
		
		//Tests addCards(Hand hand)
		deck2.addCards(hand);
		boolean result = true;
		for(int i = 0; i < hand.getHandSize(); i++) {
			 if(!(hand.getHand().get(i).equals(deck2.getDeck().get(i)))) {
				 result = false;
			 }
		 }
		assertTrue(result);
		
		//Tests addCards(ArrayList<Card> cards)
		deck3.addCards(cards);
		assertEquals(deck3, cards);
		
	}
	
	@Test
	void testEmptyDeck() {
		fail("Not yet implemented");
	}
	
	@Test
	void testGetCards() {
		fail("Not yet implemented");
	}
	
	@Test
	void testShuffle() {
		fail("Not yet implemented");
	}
	
	@Test
	void testReloadDeckFromDiscard() {
		fail("Not yet implemented");
	}
	
	@Test
	void testTakeCard() {
		fail("Not yet implemented");
	}
	
	@Test
	void testCardsLeft() {
		fail("Not yet implemented");
	}

}
