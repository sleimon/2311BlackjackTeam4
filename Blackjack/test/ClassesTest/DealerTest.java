package ClassesTest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import Classes.Dealer;

class DealerTest {

	private Dealer dealer;

	@BeforeEach
	void init(){

		dealer = new Dealer();

	}


	@Test
	void dealerTest() {
		
		String expected = "DEALER";
		String actual = d.getName();

		assertEquals(expected, actual, "The dealer's name should be: DEALER.  ");


	}

}
