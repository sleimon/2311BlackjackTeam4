package com.blackjack.Models;

public class Player extends Person{
	
	private int chips;
	private int bet;
	private int wins;
	private int losses;
	private int pushes;
	private String username;
	private String password;
	
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
	public Player(int startingChips){
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