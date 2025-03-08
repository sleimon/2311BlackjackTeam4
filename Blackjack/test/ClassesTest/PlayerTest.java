package ClassesTest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Classes.Player;
import Classes.deckOfCards;

class PlayerTest {

	//initializes the objects we need
	Player player;
	deckOfCards deck;
	deckOfCards discard;

	//Before each test, call this method, creating the objects we need
	@BeforeEach
	void init() {
		player = new Player(500);
		deck = new deckOfCards();
		discard = new deckOfCards();
	}

	//tests to see if getChips returns the correct value
	@Test
	void getChipsTest(){
		int expected = 500;
		int actual = player.getChips();
		assertEquals(expected, actual, "The player should start with 500 chips.");
	}

	//tests to see if placeBet places the correct bet
	//also tests getBet to return the correct bet
	@Test
	void placeBetTest(){
		//places 100 chip bet
		player.placeBet(100);

		//the expected amount of chips owned by the player should be less by the amount of the bet placed
		int expected = 400;
		int actual = player.getChips();
		assertEquals(expected, actual, "The player should have 100 less chips after placing a 100 chip bet.");

		//make sure the placed bet is correct
		int act = player.getBet();
		int exp = 100;
		assertEquals(act, exp, "The bet returned is incorrect");
	}

	//checks if the resetBet method sets the bet attribute to 0
	@Test
	void resetBetTest(){
		//place a bet of 100 and then set it to 0
		player.placeBet(100);
		player.resetBet();

		int expected = 0;
		int actual = player.getBet();
		assertEquals(expected, actual, "The player should have 0 chips in his bet after a successful reset.");

		//checks to make sure the chips are unchanged
		int exp = 400;
		int act = player.getChips();
		assertEquals(act, exp, "The chips should be unchanged");
	}

	//checks to see if winBet method gives the correct amount of chips
	@Test
	void winBetTest(){
		//place a bet of 100 and then win
		player.placeBet(100);
		player.winBet();

		//expected is (chips - bet) + bet * 2
		int expected = 600;
		int actual = player.getChips();
		assertEquals(expected, actual, "The player should have 600 chips after winning this bet.  ");

		//checks to see if the bet resets
		int act = player.getBet();
		int exp = 0;
		assertEquals(exp, act, "The bet should be 0 after winBet is called");
	}

	//tests to see if the instant21 method gives back the correct amount of chips
	@Test
	void instant21Test(){
		//place a bet of 100 and then get an instant 21
		player.placeBet(100);
		player.instant21();

		//should get 2.5 times of the bet back
		int expected = 650;
		int actual = player.getChips();
		assertEquals(expected, actual, "The player should have 650 chips after an instant 21, due to the 2.5 times multiplier.");
	}

	//tests if the chips are correct when the loseBet method is called
	@Test
	void loseBetTest(){
		//place a bet of 100 and then lose
		player.placeBet(100);
		player.loseBet();

		//should just be the original chips minus the bet
		int expected = 400;
		int actual = player.getChips();
		assertEquals(expected, actual, "The player should have lost whatever they bet from the total amount of chips they have");
	}

	//test to see if the chips are correct when the pushBet method is called
	@Test
	void pushBetTest(){
		//place a bet of 100 and then call pushBet to just give the bet back
		player.placeBet(100);
		player.pushBet();

		//should just be the original chip amount
		int expected = 500;
		int actual = player.getChips();
		assertEquals(expected, actual, "The player should have the original amount of chips(500) after a push.");
	}

	//test to see if the chips are correct when the surrenderBet method is called
	@Test
	void surrenderBetTest(){
		//place a bet of 100 and then call surrenderBet to give back half the bet
		player.placeBet(100);
		player.surrenderBet();

		//give back half the bet
		int expected = 450;
		int actual = player.getChips();
		assertEquals(expected, actual, "The player should have gotten half the chips they bet back after a surrender.");
	}

	//tests to see if insuranceBet method works
	@Test
	void insuranceBetTest(){
		//should double the current bet
		player.placeBet(100);
		player.insuranceBet();
		int actual = player.getBet();
		int expected = 200;
		assertEquals(expected, actual, "With insurance, original bet should be doubled.");

		//check if the chips are correct
		int act = player.getChips();
		int exp = 300;
		assertEquals(act, exp, "The chips haven't been updated properly after calling insuranceBet()");
	}

	//tests to see if the loseInsurance method works
	@Test
	void loseInsuranceTest(){
		//places a bet of 100, doubles it, then lose half when the insurance gamble is lost

		//checks to see if the correct bet is placed which is the original bet doubled
		player.placeBet(100);
		player.insuranceBet();
		int act = player.getBet();
		int exp = 200;
		assertEquals(act, exp, "The bet hasn't been doubled after calling insuranceBet()");

		//checks to see if half the bet is gone when the insurance gamble is lose
		player.loseInsurance();
		int a = player.getBet();
		int e = 100;
		assertEquals(a, e, "The bet hasn't been halved after calling loseInsurance()");

		//check to see if the chips are correct
		int expected = 300;
		int actual = player.getChips();
		assertEquals(expected, actual, "The chips haven't updated properly after loseInsurance()");
	}

	//tests to see if the winInsurance method works
	@Test
	void winInsuranceTest(){
		//Places a bet of 50, doubles it and then gives back 3x the amount

		//checks to see if the correct bet is placed which is the original bet doubled
		player.placeBet(50);
		player.insuranceBet();
		int act = player.getBet();
		int exp = 100;
		assertEquals(act, exp, "The bet hasn't been doubled after calling insuranceBet()");

		//tests to see if winInsurance gives back 3x the bet
		player.winInsurance();
		int expected = 700;
		int actual = player.getChips();
		assertEquals(expected, actual, "Payout should be 3x the bet amount.");
	}

	//tests to see if the double down method doubles the bet
	@Test
	void doubleDownTest(){
		//Checks if bet doubles
		player.placeBet(100);
		player.doubleDown();
		int expected = 200;
		int actual = player.getBet();
		assertEquals(expected, actual, "After doubling down, 100 chips should be doubled to 200 chips");

		//Checks if chips are updated properly
		int exp = player.getChips();
		int act = 300;
		assertEquals(exp, act, "Chips aren't updating properly after the doubleDown method");
	}
}
