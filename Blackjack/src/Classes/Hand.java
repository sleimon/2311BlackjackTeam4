package Classes;

import java.util.ArrayList;

public class Hand {
	
	//Field: just an ArrayList to hold the particular cards that form the hand
	private ArrayList<Card> hand;
	
	//Constructor: creates an empty, brand new ArrayList of Cards
	public Hand() {
		this.hand = new ArrayList<Card>();
	}
	
	//Methods
	
	//Returns the hand field
	public ArrayList<Card> getHand() {
		return this.hand;
	}
	
	//Returns the size of the Hand
	public int getHandSize() {
		return this.hand.size();
	}
	
	//Returns the card in the hand at the specified index
	public Card getCard(int index) {
		return this.hand.get(index);
	}
	
	//Take the first card from the specified deck and add to hand
	public void takeCardFromDeck(deckOfCards deck) {
		this.hand.add(deck.takeCard());
	}
	
	//Calculate the value of the hand in blackjack
	public int calculatedValue() {
		int handSize = this.hand.size();
		int value = 0;
		int aces = 0;

		for(int i = 0; i < handSize; i++) {
			int cardValue = this.hand.get(i).getValue();
			value += cardValue;
			if(cardValue == 11) {
				aces++;
			}
		}

		while(value > 21 && aces > 0) {
			aces--;
			value -= 10;
		}

		return value;
	}
	
	//Returns the latest card added to the hand
	public Card getLatestCard() {
		return this.hand.get(this.hand.size() - 1);
	}
	
	//Empty the hand into the specified deck
	public void discardHandToDeck(deckOfCards discarded) {
		discarded.addCards(this.hand);
		this.hand.clear();
	}
	
	//Overridden equals method for the Hand class
	public boolean equals(Object obj) {
		 if(this == obj) { return true; }
		 if(obj == null || this.getClass() != obj.getClass()) { return false; }
		 Hand other = (Hand) obj;
		 boolean equals = true;
		 for(int i = 0; i < this.hand.size(); i++) {
			 if(!(this.hand.get(i).equals(other.hand.get(i)))) {
				 equals = false;
			 }
		 }
		 return equals;	
	}
	
	//Returns a String representation of the Hand object
	public String toString() {
		String result = "";

		for(int i = 0; i < this.hand.size(); i++) {
			result += this.hand.get(i).toString();
			if(i < this.hand.size() - 1) {
				result += " - ";
			}
		}
		return result;
	}	

	//Adds a card to the hand. Written for testing purposes only
	public void addCard(Card card) {
		this.hand.add(card);
	}
	
	//checks if the hand has cards
	public boolean hasCards() {
		if(this.hand.size() > 0) {
			return true;
		}
		return false;
	}	
}
