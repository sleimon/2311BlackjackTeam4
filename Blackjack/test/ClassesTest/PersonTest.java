package ClassesTest;

import static org.junit.jupiter.api.Assertions.*;

import java.beans.Transient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Classes.Card;
import Classes.Hand;
import Classes.Person;
import Classes.deckOfCards;

class PersonTest {

	private Person person;
    private deckOfCards deck;
    private deckOfCards discard;

    @BeforeEach
    void init() {

        person = new Person();
        deck = new deckOfCards();
        discard = new deckOfCards();

    }

    @Test 
    void getNameTest() {

        String expected = "";
        String actual = person.getName();

        assertEquals(expected, actual, "Name should be the default value of a string which is nothing.  ");

    }

    @Test 
    void setNameTest() {

        String expected = "Jeff";
        person.setName("Jeff");
        String actual = person.getName();
        
        assertEquals(expected, actual, "This name should set to Jeff.  ");

    }

	@Test
	void getHandTest() {

		assertNotNull(person.getHand(), "If the hand is null, then there are no cards in the hand.  ");
    }

//	@Test
//	void setHandTest() {
//
//		Hand hand  = new Hand();
//		newHand.takeCardFromDeck();
//		person.setHand(newHand);
//		int expected = 1;
//		int actual = person.getHand().size();
//
//		assertEquals(expected, actual, "The hand should contain 1 card.  ");
//
//    }
//
//	@Test
//	void hitTest() {
//		int ogDeckSize = deck.size;
//		person.hit(deck, discard);
//		int newDeckSize = deck.size();
//
//
//		int expected = ogDeckSize - 1;
//		int actual = newDeckSize;
//		assertEquals(expected, actual, "Size of the deck should've decreased by 1.   ");
//
//    }

	@Test 
	void has21Test() {

		person.getHand().addCard(new Card("King", "Spades", 10));
		person.getHand().addCard(new Card("Ace", "Hearts", 11));

		assertTrue(person.has21(), "The payer should have 21.  ");
    }

}
