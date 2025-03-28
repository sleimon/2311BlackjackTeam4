package com.blackjack.Models;

import java.util.ArrayList;

public class Hand {
	
	private ArrayList<Card> hand;
	
	public Hand() {
		this.hand = new ArrayList<Card>();
	}

	public ArrayList<Card> getHand() {
		return this.hand;
	}
	
	public int getHandSize() {
		return this.hand.size();
	}
	
	public Card getCard(int index) {
		return this.hand.get(index);
	}
	
	public void takeCardFromDeck(deckOfCards deck) {
		this.hand.add(deck.takeCard());
	}
	
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
	
	public Card getLatestCard() {
		return this.hand.get(this.hand.size() - 1);
	}
	
	public void discardHandToDeck(deckOfCards discarded) {
		discarded.addCards(this.hand);
		this.hand.clear();
	}
	
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
	
	//checks if the hand has cards. Written for units tests
	public boolean hasCards() {
		if(this.hand.size() > 0) {
			return true;
		}
		return false;
	}	
}
