package Blackjack;

public class Dealer extends Person {
	public Dealer() {
		super.setName("DEALER");
	}
	public void printFirstHand(){
	    System.out.println("The dealer's hand: ");
	    System.out.println(super.getHand().getCard(0));
	    System.out.println("The second card is face down.");
	}

}
