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
		boolean result1 = true;
		for(int i = 0; i < cards.size(); i++) {
			 if(!(cards.get(i).equals(deck3.getDeck().get(i)))) {
				 result1 = false;
			 }
		 }
		assertTrue(result1);
	}
	
	//Tests to see if method emptyDeck empties the deck
	@Test
	void testEmptyDeck() {
		deck.emptyDeck();
		assertFalse(deck.hasCards());
	}
	
	//Tests if getDeck returns the correct deck
	@Test
	void testGetDeck() {
		ArrayList<Card> deck2 = deck.getDeck();
		boolean result = true;
		for(int i = 0; i < deck2.size(); i++) {
			 if(!(deck2.get(i).equals(deck.getDeck().get(i)))) {
				 result = false;
			 }
		 }
		assertTrue(result);
	}
	
	//Tests the reloadDeckFromDiscard method
	@Test
	void testReloadDeckFromDiscard() {
		Card card1 = new Card("Two", "Hearts", 2);
		Card card2 = new Card("Three", "Hearts", 3);
		deck.emptyDeck();
		deckOfCards discard = new deckOfCards();
		discard.emptyDeck();
		discard.addCard(card1);
		discard.addCard(card2);
		deck.reloadDeckFromDiscard(discard);
		assertTrue(deck.getDeck().get(0).equals(card1));
		assertTrue(deck.getDeck().get(1).equals(card2));
		assertFalse(discard.hasCards());
	}
	
	//Tests the takeCard and cardsLeft methods
	@Test
	void testTakeCardAndCardsLeft() {
		Card card1 = new Card("Two", "Hearts", 2);
		Card card2 = new Card("Three", "Hearts", 3);
		Card card11 = deck.takeCard();
		Card card22 = deck.takeCard();
		assertEquals(card1, card11);
		assertEquals(card2, card22);
		int expected = 50;
		int actual = deck.cardsLeft();
		assertEquals(actual, expected);
	}

	//Tests the toString and equals methods
	@Test
	void testEqualsAndToString() {
		deckOfCards deck2 = new deckOfCards();
		assertEquals(deck, deck2);
		deck.emptyDeck();
		deck2.emptyDeck();
		String actual = deck.toString();
		String expected = "";
		assertEquals(actual, expected);
		assertEquals(deck, deck2);
	}
}
