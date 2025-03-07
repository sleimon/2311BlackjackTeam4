package ClassesTest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.blackjack.Models.*;

class DealerTest {
	
	//Initializes a Dealer
	Dealer dealer;
	
	//Before each test, create a new Dealer
	@BeforeEach
	void init(){
		dealer = new Dealer();
	}
	
	//Tests the constructor to see if it sets the correct name
	@Test
	void testDealer() {
		String expected = "DEALER";
		String actual = dealer.getName();

		assertEquals(expected, actual, "The dealer's name should be: DEALER");
	}
}
