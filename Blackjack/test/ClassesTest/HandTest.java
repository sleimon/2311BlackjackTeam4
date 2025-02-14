package ClassesTest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Classes.Hand;
import Classes.deckOfCards;
import Classes.Card;

class HandTest {
	
	//initializes a Hand object and a deckOfCards object
	Hand hand;
	deckOfCards deck;
	
	/* before each test, initialize an empty Hand object and deckOfCards object to be 
	 * a deck of cards that hasn't shuffled, therefore the first two cards will always be
	 * the two of hearts and the three of hearts */
	@BeforeEach
	void beforeEach() {
		hand = new Hand();
		deck = new deckOfCards();
	}
	
	//Test to see if method takeCardFromDeck takes cards off the top of the deck
	@Test
	void testTakeCardFromDeck() {
		hand.takeCardFromDeck(deck); //two of hearts
		hand.takeCardFromDeck(deck); //three of hearts
		Card expected1 = new Card("Two", "Hearts", 2);
		Card expected2 = new Card("Three", "Hearts", 3);
		Card actual1 = hand.getCard(0);
		Card actual2 = hand.getCard(1);
		assertEquals(actual1, expected1);
		assertEquals(actual2, expected2);
	}
	
	
	//Test to see if the getHand method returns the expected values
	@Test
	void testGetHand() {
		ArrayList<Card> actual = hand.getHand();
		ArrayList<Card> expected = new ArrayList<Card>();
		assertEquals(actual, expected);
		hand.takeCardFromDeck(deck); //two of hearts
		hand.takeCardFromDeck(deck); //three of hearts
		actual = hand.getHand();
		Card card1 = new Card("Two", "Hearts", 2);
		Card card2 = new Card("Three", "Hearts", 3);
		expected.add(card1);
		expected.add(card2);
		boolean result = true;
		for(int i = 0; i < actual.size(); i++) {
			 if(!(actual.get(i).equals(expected.get(i)))) {
				 result = false;
			 }
		 }
		assertTrue(result);
	}
	
	//Test to see if the getHandSize method returns the expected values
	@Test
	void testGetHandSize() {
		int actual = hand.getHandSize();
		int expected = 0;
		assertEquals(actual, expected);
		hand.takeCardFromDeck(deck); //two of hearts
		hand.takeCardFromDeck(deck); //three of hearts
		actual = hand.getHandSize();
		expected = 2;
		assertEquals(actual, expected);
	}
	
	//Test to see if the getCard method returns the expected values
	@Test
	void testGetCard() {
		hand.takeCardFromDeck(deck); //two of hearts
		hand.takeCardFromDeck(deck); //three of hearts
		Card actual = hand.getCard(0);
		Card expected = new Card("Two", "Hearts", 2);
		assertEquals(actual, expected);
		actual = hand.getCard(1);
		Card expected1 = new Card("Three", "Hearts", 3);
		assertEquals(actual, expected1);
	}
	
	/*Test to see if the getCard method returns the expected values
	 * Also, if an Ace (worth 11) takes the total value of the hand over 21
	 * it should then be worth 1, whichever is most beneficial for the player
	 * and dealer
	 */
	@Test
	void testCalculatedValue() {
		int actual = hand.calculatedValue();
		int expected = 0;
		assertEquals(actual, expected);
		hand.takeCardFromDeck(deck); //two of hearts
		hand.takeCardFromDeck(deck); //three of hearts
		actual = hand.calculatedValue();
		expected = 5;
		assertEquals(actual, expected);
		Card card1 = new Card("Ace", "Hearts", 11);
		Card card2 = new Card("Ace", "Spades", 11);
		hand.addCard(card1);
		hand.addCard(card2);
		actual = hand.calculatedValue(); 
		expected = 17;
		assertEquals(actual, expected);
	}
	
	//Tests to see if method getLatestCard returns the expected values
	@Test
	void testGetLatestCard() {
		hand.takeCardFromDeck(deck); //two of hearts
		Card actual = hand.getLatestCard();
		Card expected = hand.getHand().get(0);
		assertEquals(actual, expected);
		hand.takeCardFromDeck(deck); //three of hearts
		actual = hand.getLatestCard();
		expected = hand.getHand().get(1);
		assertEquals(actual, expected);
	}
	
	//Tests to see if method discardHandToDeck empties all the cards in the hand into the correct deck
	@Test
	void testDiscardHandToDeck() {
		hand.takeCardFromDeck(deck); //two of hearts
		hand.takeCardFromDeck(deck); //three of hearts
		deckOfCards discard = new deckOfCards();
		discard.emptyDeck(); //empties deck
		hand.discardHandToDeck(discard);
		int actual1 = hand.getHandSize();
		int expected1 = 0;
		assertEquals(actual1, expected1);
		Card card1 = new Card("Two", "Hearts", 2);
		Card card2 = new Card("Three", "Hearts", 3);
		Hand hand1 = new Hand();
		hand1.addCard(card1);
		hand1.addCard(card2);
		boolean result = true;
		for(int i = 0; i < discard.getCards().size(); i++) {
			 if(!(discard.getCards().get(i).equals(hand1.getCard(i)))) {
				 result = false;
			 }
		 }
		assertTrue(result);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
