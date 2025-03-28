package com.blackjack.Models;

public class Card {
	
	//three fields consisting of rank, suit and value
	//synonymous with an actual card's attributes
	private String cardRank;
	private String cardSuit;
	private int cardValue;

	public Card(String rank, String suit, int value) {
		cardRank = rank;
		cardSuit = suit;
		cardValue = value;
	}

	//This constructor is needed to make it easier to deep copy another card object
	public Card(Card card) {
		this.cardRank = card.cardRank;
		this.cardSuit = card.cardSuit;
		this.cardValue = card.cardValue;
	}
	
	public String getRank() {
		return this.cardRank;
	}
	
	public String getSuit() {
		return this.cardSuit;
	}
	
	public int getValue() {
		return this.cardValue;
	}
	
	public String toString() {
		return this.cardRank + " of " + this.cardSuit + "(" + this.cardValue + ")";
	}
	
	//Overridden equals method to compare cards for debugging
	public boolean equals(Object obj) {
		 if(this == obj) { return true; }
		 if(obj == null || this.getClass() != obj.getClass()) { return false; }
		 Card other = (Card) obj;
		 return 
				 this.cardRank.equals(other.cardRank) &&
				 this.cardSuit.equals(other.cardSuit) &&
				 this.cardValue == other.cardValue;
	}
}