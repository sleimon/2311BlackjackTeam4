package Classes;

import java.util.Scanner;

public class Player extends Person{

	private int chips;
	private int bet;

	public Player(int startingChips) {
		super.setName("PLAYER");
		this.chips = startingChips;
		this.bet = 0;
	}

	Scanner input = new Scanner(System.in);

	public void makeDecision(deckOfCards deck, deckOfCards discard) {
		boolean getNum = true;
		int decision = 0;
		int x = 0;
		while(getNum){
			try{

				if(this.getHand().getCard(0).getRank() == this.getHand().getCard(1).getRank() && x == 0){

					System.out.println("Would you like to (1)hit or (2)stand or (3)double down or would you like to (4)split your hand.  ");
					decision = input.nextInt();
					getNum = false;
					x++;
				}
				else{
					System.out.println("Would you like to (1)hit or (2)stand or (3)double down");
					decision = input.nextInt();
					getNum = false;

					if(getSplitHand().getCard(0).getValue() > 0) {

						System.out.println("Would you like to (1)hit or (2)stand or (3)double down on your split hand? ");
						decision = input.nextInt();


					}
				}

			}
			catch(Exception e){
				System.out.println("Invalid");
				input.next();
			}
			if (decision == 1) {
				this.hit(deck, discard);
				printHand();
				
				if(this.getHand().calculatedValue() > 21){
					System.out.println("BUST!");
					this.loseBet();
					return;
				}
				
				else {
					this.makeDecision(deck, discard);
				}
			}	

			  else if (decision == 3) {
				doubleDown(deck, discard);
			 } 
			else if(decision == 4) {
				splitHand(this.getHand(), deck, discard);
			 } else {
				System.out.println("You stand.");
			}

			
		}
	}
	
	private void doubleDown(deckOfCards deck, deckOfCards discard) {
		this.chips -= this.bet;
		this.bet *= 2;
		this.hit(deck, discard);
		if(this.getHand().calculatedValue() > 21){
			System.out.println("BUST!");
			this.loseBet();
			return;
		}
	}

	private void splitHand(Hand hand, deckOfCards deck, deckOfCards discard){

		if(this.chips < this.bet) {

			System.out.println("You don't have enough chips to split!");
			return;

		}

		Card copyCard = this.getHand().getCard(1);
		Hand newHand = new Hand(); 
		newHand.getHand().add(copyCard); // Stores 1 half of the split hand.
		setSplitHand(newHand); // Sets the split hand to have the copied card as it's only card. 

		hand.getHand().remove(1); // Removes the card from the original hand.  

		this.chips -= this.bet; 

		// The original hand should now store 1 card, and the new hand should store the other card from the orignal hand.
		this.hit(deck, discard); // Adds a new card to the original hand.  
		this.hitSplit(deck, discard); // Adds a new card to the split hand.
		  
		printHand();
		printSplitHand();
		this.chips -= this.bet;
		this.bet *= 2;
		makeDecision(deck, discard);
		
		
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
}


