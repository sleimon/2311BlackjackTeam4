package ClassesTest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.blackjack.Models.*;


class PlayerTest {

	Player player;
    deckOfCards deck;
    deckOfCards discard;


	@BeforeEach
	void init() {

		player = new Player(500);
		deck = new deckOfCards();
		discard = new deckOfCards();

	}

	@Test
	void getChipsTest(){

		int expected = 500;
		int actual = player.getChips();

		assertEquals(expected, actual, "The player should start with 500 chips.  ");

	}

	@Test
	void placeBetTest(){

		player.placeBet(100);
		int expected = 400;
		int actual = player.getChips();

		assertEquals(expected, actual, "The player should have 100 less chips after placing a 100 chip bet.  ");
	}

	@Test
	void resetBetTest(){
		player.placeBet(100);
		player.resetBet();
		int expected = 0;
		int actual = player.getBet();
		assertEquals(expected, actual, "The player should have 0 chips in his bet after a successful reset.  ");
	}

	@Test
	void winBetTest(){

		player.placeBet(100);
		player.winBet();
		int expected = 600;
		int actual = player.getChips();

		assertEquals(expected, actual, "The player should have 600 chips after winning this bet.  ");
	}

	@Test
	void instant21Test(){

		player.placeBet(100);
		player.instant21();
		int expected = 650;
		int actual = player.getChips();

		assertEquals(expected, actual, "The player should have 750 chips after an instant 21, due to the 2.5 times multiplier.   ");
	}

	@Test
	void loseBetTest(){

		player.placeBet(100);
		player.loseBet();
		int expected = 400;
		int actual = player.getChips();

		assertEquals(expected, actual, "The player should have lost 100 chips after placing a bet for 100 chips and losing.    ");

	}

	@Test
	void pushBetTest(){

		player.placeBet(100);
		player.pushBet();
		int expected = 500;
		int actual = player.getChips();

		assertEquals(expected, actual, "The player should have 500 chips after a push. ");
	}

	@Test
	void surrenderBetTest(){

		player.placeBet(100);
		player.surrenderBet();
		int expected = 450;
		int actual = player.getChips();

		assertEquals(expected, actual, "The player should have gotten half the chips they bet back after a surrender.  ");
	}

	@Test
	void insuranceBetTest(){

		player.placeBet(100);
		player.insuranceBet();
		int actual = player.getBet();
		int expected = 200;

		assertEquals(expected, actual, "With insurance, original bet should be doubled.  ");
	}

	@Test
	void loseInsuranceTest(){

		player.placeBet(100);
		player.insuranceBet();
		player.loseInsurance();

		int expected = 300;
		int actual = player.getChips();

		assertEquals(expected, actual, "Half of the bet should be returned.  ");
	}

	@Test
	void winInsuranceTest(){

		player.placeBet(50);
		player.insuranceBet();
		player.winInsurance();
		int expected = 700;
		int actual = player.getChips();

		assertEquals(expected, actual, "Payout should be 3x the bet amount.  ");
	}

	@Test
	void doubleDownTest(){

		player.placeBet(100);
		player.doubleDown();
		player.winInsurance();
		int expected = 200;
		int actual = player.getBet();

		assertEquals(expected, actual, "After doubling down, 100 chips should be doubled to 200 chips");
	}
}
