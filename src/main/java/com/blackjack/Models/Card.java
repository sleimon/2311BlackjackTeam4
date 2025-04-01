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

	/**
	 * Returns a short string representation of the card (e.g., "AH", "KD", "TS").
	 * @return A 2-character string representing the card's rank and suit.
	 */
	public String toStringShort() {
		String rankStr;
		// Map the full rank string to its short form
		switch (this.cardRank) { // Switch on the String rank
			case "Ace":   rankStr = "A"; break;
			case "Two":   rankStr = "2"; break;
			case "Three": rankStr = "3"; break;
			case "Four":  rankStr = "4"; break;
			case "Five":  rankStr = "5"; break;
			case "Six":   rankStr = "6"; break;
			case "Seven": rankStr = "7"; break;
			case "Eight": rankStr = "8"; break;
			case "Nine":  rankStr = "9"; break;
			case "Ten":   rankStr = "T"; break; // Use 'T' for Ten
			case "Jack":  rankStr = "J"; break;
			case "Queen": rankStr = "Q"; break;
			case "King":  rankStr = "K"; break;
			default:      rankStr = "?"; break; // Handle unexpected ranks
		}

		String suitStr;
		// Map the full suit string to its short form
		switch (this.cardSuit) { // Switch on the String suit
			case "Clubs":    suitStr = "C"; break;
			case "Diamonds": suitStr = "D"; break;
			case "Hearts":   suitStr = "H"; break;
			case "Spades":   suitStr = "S"; break;
			default:         suitStr = "?"; break; // Handle unexpected suits
		}
		return rankStr + suitStr; // e.g., "AC", "KD", "7H"
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