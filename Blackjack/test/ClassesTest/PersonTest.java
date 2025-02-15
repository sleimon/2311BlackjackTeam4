package ClassesTest;

import static org.junit.jupiter.api.Assertions.*;

import java.beans.Transient;

import org.junit.jupiter.api.Test;

class PersonTest {

	private Person person;
    private deckOfCards deck;
    private deckOfCards discard;

    @BeforeEach
    void init() {

        person = new person();
        deck = new deckOfCards();
        discard = new deckOfCards();

    }

    @Test 
    void getNameTest() {

        string expected = "";
        string actual = person.getName();

        assertEquals(expected, actual, "Name should be the default value of a string which is nothing.  ");

    }

    @Test 
    void setNameTest() {

        string expected = "Jeff";
        person.setName("Jeff");
        String actual = person.getName();
        
        assertEquals(expected, actual, "This name should set to Jeff.  ");

    }

	@Test 

	void getHandTest() {

		assertNotNull(person.getHand(), "If the hand is null, then there are no cards in the hand.  ");
    }

}
