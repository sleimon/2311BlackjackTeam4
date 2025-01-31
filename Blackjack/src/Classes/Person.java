package Blackjack;

public class Person {
	private String name;
	private Hand hand;
	
	public Person() {
		this.name = "";
		this.hand = new hand();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Hand getHand() {
		return hand;
	}

	public void setHand(Hand hand) {
		this.hand = hand;
	}
	
	public void hit(Deck deck, Deck discard){
	    if (!deck.hasCards()) {
	        deck.reloadDeckFromDiscard(discard);
	    }
	    this.hand.takeCardFromDeck(deck);
	    System.out.println(this.name + " gets a new card");
	    this.getHand();
	}
	
	public boolean has21(){
	    if(this.getHand().calculatedValue() == 21){
	        return true;
	    }
	    else{
	        return false;
	    }
	}

}
