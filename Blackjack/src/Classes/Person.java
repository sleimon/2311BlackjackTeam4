package Classes;

public class Person {
	private String name;
	private Hand hand;
	private Hand splitHand;

	public Person() {
		this.name = "";
		this.hand = new Hand();
		this.splitHand = new Hand();
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

	public Hand getSplitHand() {
		return splitHand;
	}

	public void setHand(Hand hand) {
		this.hand = hand;
	}

	public void setSplitHand(Hand splitHand) {
		this.splitHand = splitHand;
	}

	public void printHand(){
		System.out.println(this.name + "'s hand looks like this:");
		System.out.println(this.hand + " | Valued at: " + this.hand.calculatedValue());
	}

	public void printSplitHand(){
		System.out.println(this.name + "'s split hand looks like this:");
		System.out.println(this.splitHand + " | Valued at: " + this.splitHand.calculatedValue());
	}

	public void hit(deckOfCards deck, deckOfCards discard){
		if (!deck.hasCards()) {
			deck.reloadDeckFromDiscard(discard);
		}
		this.hand.takeCardFromDeck(deck);
		System.out.println(this.name + " gets a new card added to his original hand| " + this.hand.getLatestCard());
		this.getHand();
	}

	public void hitSplit(deckOfCards deck, deckOfCards discard){
		if (!deck.hasCards()) {
			deck.reloadDeckFromDiscard(discard);
		}
		this.splitHand.takeCardFromDeck(deck);
		System.out.println(this.name + " gets a new card addd to his split hand| " + this.splitHand.getLatestCard());
		this.getSplitHand();
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
