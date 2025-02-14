package Classes;

public class Card {
	
	//Fields: three fields consisting of rank, suit and value in a blackjack game
	private String cardRank;
	private String cardSuit;
	private int cardValue;
	
	//Constructor: creates a card with the input being the rank, suit and value
	public Card(String rank, String suit, int value) {
		cardRank = rank;
		cardSuit = suit;
		cardValue = value;
	}
	
	//Constructor: copy constructor
	public Card(Card card) {
		this.cardRank = card.cardRank;
		this.cardSuit = card.cardSuit;
		this.cardValue = card.cardValue;
	}
	
	//Methods
	
	//Get the rank of the card
	public String getRank() {
		return cardRank;
	}
	
	//Get the suit of the card
	public String getSuit() {
		return cardSuit;
	}
	
	//Get the value the card is worth in blackjack
	public int getValue() {
		return cardValue;
	}
	
	//toString method for the card
	public String toString() {
		return cardRank + " of " + cardSuit + "(" + this.cardValue + ")";
	}
	
	//Overridden equals method to compare cards
	public boolean equals(Object obj) {
		 if(this == obj) { return true; }
		 if(obj == null || this.getClass() != obj.getClass()) { return false; }
		 Card other = (Card) obj;
		 return 
				 this.cardRank == other.cardRank &&
				 this.cardSuit == other.cardSuit &&
				 this.cardValue == other.cardValue;
	}

}