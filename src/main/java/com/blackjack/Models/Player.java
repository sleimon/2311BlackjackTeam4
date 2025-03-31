package com.blackjack.Models;

// Assuming this extends a Person class that provides Hand management
public class Player extends Person {

	private int chips;
	private int bet;
	// Player stats - GameLogic will manage persistent stats via User object
	private int wins;
	private int losses;
	private int pushes;

	public Player(int startingChips) {
		super.setName("PLAYER"); // Assumes Person has setName
		this.chips = startingChips;
		this.bet = 0;
		this.wins = 0; // Session stats if needed
		this.losses = 0;
		this.pushes = 0;
	}

	public int getChips() { return this.chips; }
	public void placeBet(int bet) {
		// Assuming bet is validated for affordability before calling this
		if (bet > 0 && bet <= this.chips) { // Add affordability check just in case
			this.chips -= bet;
			this.bet = bet;
		} else {
			this.bet = 0; // Ensure bet is 0 if invalid
		}
	}
	public void resetBet() { this.bet = 0; } // Insurance Amount is not tracked separately
	public void winBet() { this.chips += (this.bet * 2); /*this.resetBet();*/ } // Return original + winnings
	public void instant21() { this.chips += (this.bet * 2.5); /*this.resetBet();*/ }
	public void loseBet() { /*this.resetBet();*/ } // Chips already deducted
	public void pushBet() { this.chips += this.bet; /*this.resetBet();*/ } // Return original bet
	public void surrenderBet() { this.chips += (this.bet / 2); /*this.resetBet();*/ } // Return half
	public int getBet() { return this.bet; }

	// --- Non-Standard Insurance/Double Down Methods (Keep as provided by user) ---
	public void insuranceBet() { this.chips -= this.bet; this.bet = this.bet * 2; } // Doubles main bet
	public void loseInsurance() { this.bet = this.bet / 2;	} // Halves the (doubled) main bet? Seems odd.
	public void winInsurance() { this.chips += (this.bet * 3); } // Adds 3x the (doubled) main bet? Very odd payout.
	public void doubleDown() { this.chips -= this.bet; this.bet = this.bet * 2; } // Doubles main bet

	// --- Player Session Stats Methods (Keep, but won't be primary focus) ---
	public int getWins() { return wins; }
	public void setWins(int wins) { this.wins = wins; }
	public int getLosses() { return losses; }
	public void setLosses(int losses) { this.losses = losses; }
	public int getPushes() { return pushes; }
	public void setPushes(int pushes) { this.pushes = pushes; }
	public void oneWin(){ this.wins += 1; }
	public void oneLoss(){ this.losses += 1; }
	public void onePush(){ this.pushes += 1; }

}