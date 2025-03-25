package com.blackjack.Models;

public class Player extends Person{
	
	//Fields: The number of chips the player has and the number of chips they're betting
	private int chips;
	private int bet;
	private int wins;
	private int losses;
	private int pushes;
	private String username;
	private String password;
	
	//Constructor: Set name as PLAYER and take the starting chips as the player's chip count
	public Player(int startingChips, String password, String username) {
		super.setName("PLAYER");
		this.chips = startingChips;
		this.bet = 0;
		this.wins = 0;
		this.losses = 0;
		this.pushes = 0;
		this.username = username;
		this.password = password;
	}
	//temporary constructor
	public Player(int startingChips){
		super.setName("PLAYER");
		this.chips = startingChips;
		this.bet = 0;
	}
	
	//Methods
	
	//Returns the amount of chips in the player's possession
	public int getChips() {
		return this.chips;
	}
	
	//Updates the bet field with how much the player wants to gamble and subtracts the bet from the total chips
	public void placeBet(int bet) {
		this.chips -= bet;
		this.bet = bet;
	}
	
	//Sets the bet back to 0
	public void resetBet() {
		this.bet = 0;
	}
	
	//Standard winning that the player may get when winning a regular hand
	public void winBet() {
		this.chips += (this.bet * 2);
		this.resetBet();
	}
	
	//If the player gets an instant blackjack, they win a bit more chips
	public void instant21() {
		this.chips += (this.bet * 2.5);
		this.resetBet();
	}
	
	//Standard loss if the player loses the round
	public void loseBet() {
		this.resetBet();
	}
	
	//Called when the player ties the round with the dealer, just gets their bet back
	public void pushBet() {
		this.chips += this.bet;
		this.resetBet();
	}
	
	//Get half of bet back if they surrender round
	public void surrenderBet() {
		this.chips += (this.bet / 2);
		this.resetBet();
	}
	
	//Returns the bet made by the player
	public int getBet() {
		return this.bet;
	}
	
	//Called when the player chooses insurance, takes the same bet as their initial (bet * 2), subtracts from total chips
	public void insuranceBet() {
		this.chips -= this.bet;
		this.bet = this.bet * 2;
	}
	
	//Lose half your bet if the dealer doesn't have a natural blackjack (they were dealt 21)
	public void loseInsurance() {
		this.bet = this.bet / 2;	
	}
	
	//Win 3 times your bet if the dealer has a natural blackjack
	public void winInsurance() {
		this.chips += (this.bet * 3);
	}
	
	//Called when the player wants to double down, double the bet and subtracts from total chips
	public void doubleDown() {
		this.chips -= this.bet;
		this.bet = this.bet * 2;
	}

	public int getWins() {
		return wins;
	}

	public void setWins(int wins) {
		this.wins = wins;
	}

	public int getLosses() {
		return losses;
	}

	public void setLosses(int losses) {
		this.losses = losses;
	}

	public int getPushes() {
		return pushes;
	}

	public void setPushes(int pushes) {
		this.pushes = pushes;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void oneWin(){
		this.wins += 1;
	}

	public void oneLoss(){
		this.losses += 1;
	}

	public void onePush(){
		this.pushes += 1;
	}
}