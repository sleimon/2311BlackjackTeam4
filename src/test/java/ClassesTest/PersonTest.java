package ClassesTest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.blackjack.Models.*;


class PersonTest {
	
	//Initialize a Person object for testing
	Person person;
	
	//Create a Person object before each test
    @BeforeEach
    void beforeEach() {
        person = new Person();
    }
    
    //Test the getName method
    @Test 
    void testGetName() {
        String expected = "";
        String actual = person.getName();
        assertEquals(expected, actual, "Name should be the default value of a String which is the empty String");
    }
    
    //Tests the setName method
    @Test 
    void testSetName() {
        String expected = "Jeff";
        person.setName("Jeff");
        String actual = person.getName();
        assertEquals(expected, actual);

    }
    
    //Tests the setHand and getHand methods
    @Test
	void testSetHandAndGetHand() {
    	Hand hand = new Hand();
    	Card card1 = new Card("King", "Spades", 10);
    	Card card2 = new Card("Ace", "Hearts", 11);
    	hand.addCard(card1);
    	hand.addCard(card2);
    	person.setHand(hand);
    	boolean result = true;
		for(int i = 0; i < person.getHand().getHandSize(); i++) {
			 if(!(person.getHand().getCard(i).equals(hand.getCard(i)))) {
				 result = false;
			 }
		 }
		assertTrue(result);
    }
	
	//Tests the has21 method
	@Test 
	void testHas21() {
		person.getHand().addCard(new Card("King", "Spades", 10));
		person.getHand().addCard(new Card("Ace", "Hearts", 11));
		assertTrue(person.has21(), "The player should have 21");
    }
}
