package Classes;

public class Player extends Person{

	private int chips;
	private int bet;

	public Player(int startingChips) {
		super.setName("PLAYER");
		this.chips = startingChips;
		this.bet = 0;
	}

	public int getChips() {
		return this.chips;
	}

	public void placeBet(int bet) {
		this.chips -= bet;
		this.bet = bet;
	}

	public void resetBet() {
		this.bet = 0;
	}

	public void winBet() {
		this.chips += (this.bet * 2);
		this.resetBet();
	}

	public void instant21() {
		this.chips += (this.bet * 2.5);
		this.resetBet();
	}

	public void loseBet() {
		this.resetBet();
	}

	public void pushBet() {
		this.chips += this.bet;
		this.resetBet();
	}

	public void surrenderBet() {
		this.chips += (this.bet / 2);
		this.resetBet();
	}
	
	public int getBet() {
		return this.bet;
	}
	
	public void insuranceBet() {
		this.chips -= this.bet;
		this.bet = this.bet * 2;
	}
	
	public void loseInsurance() {
		this.bet = this.bet / 2;	
	}
	
	public void winInsurance() {
		this.chips += (this.bet * 3);
	}
	
	public void doubleDown() {
		this.chips -= this.bet;
		this.bet = this.bet * 2;
	}
}