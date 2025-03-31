package com.blackjack.Models;

import java.util.ArrayList;
import java.util.Collections; // Needed for potential future improvement or copying
import java.util.List;      // Good practice to use interface type

public class Hand {

	//Field: just an ArrayList to hold the particular cards that form the hand
	private ArrayList<Card> hand; // Using ArrayList is fine

	//Constructor: creates an empty, brand new ArrayList of Cards
	public Hand() {
		this.hand = new ArrayList<Card>();
	}

	//Methods

	//Returns the hand field
	// Note: Returning the direct reference allows external modification.
	// If stricter encapsulation is desired later, consider returning a copy
	// or an unmodifiable view, but leaving as is per your request.
	public ArrayList<Card> getHand() {
		return this.hand;
	}

	// --- Added for compatibility with GameLogic/GUI ---
	/**
	 * Returns an immutable list view of the cards in the hand.
	 * Preferred way for external classes to read the hand without modifying it.
	 * @return An immutable List of Cards.
	 */
	public List<Card> getCards() {
		// If this causes issues elsewhere due to expecting ArrayList,
		// you might need to adjust consuming code or return new ArrayList<>(this.hand).
		// UnmodifiableList is generally safer for read-only access.
		return Collections.unmodifiableList(this.hand);
	}
	// --- End Added Code ---


	//Returns the size of the Hand
	// Renamed from getHandSize for more standard Java bean naming conventions
	// Kept original method below for compatibility.
	public int getSize() {
		return this.hand.size();
	}
	// Original method kept for compatibility
	public int getHandSize() {
		return this.hand.size();
	}


	//Returns the card in the hand at the specified index
	public Card getCard(int index) {
		// Add bounds checking for robustness, although ArrayList does this
		if (index < 0 || index >= this.hand.size()) {
			// Consider the impact of throwing an exception vs returning null
			// depending on how calling code handles it. Throwing is often preferred.
			throw new IndexOutOfBoundsException("Index " + index + " out of bounds for hand size " + this.hand.size());
		}
		return this.hand.get(index);
	}

	//Take the first card from the specified deck and add to hand
	public void takeCardFromDeck(deckOfCards deck) {
		// Assumes deck.takeCard() handles cases where the deck might be empty
		// and returns a valid Card object or throws an appropriate exception.
		Card card = deck.takeCard();
		if (card != null) { // Good practice to check if takeCard could return null
			this.hand.add(card);
		} else {
			// Handle case where deck is empty, if necessary
			System.err.println("Warning: Attempted to take card from empty deck.");
			// Or throw an exception
		}
	}

	//Calculate the value of the hand in blackjack
	public int calculatedValue() {
		// int handSize = this.hand.size(); // Not needed directly
		int value = 0;
		int aces = 0;

		// Standard loop is fine
		for(Card card : this.hand) { // Using enhanced for-loop is slightly cleaner
			int cardValue = card.getValue(); // Assumes Card.getValue() returns the point value
			value += cardValue;
			if(cardValue == 11) { // Check if it's an Ace (assuming 11 means Ace's initial value)
				aces++;
			}
		}

		// Adjust for Aces if value is over 21
		while(value > 21 && aces > 0) {
			value -= 10; // Change Ace value from 11 to 1
			aces--;
		}

		return value;
	}

	//Returns the latest card added to the hand
	public Card getLatestCard() {
		if (this.hand.isEmpty()) {
			// Handle empty hand case gracefully
			return null; // Or throw an exception like NoSuchElementException
		}
		return this.hand.get(this.hand.size() - 1);
	}

	//Empty the hand into the specified deck
	public void discardHandToDeck(deckOfCards discarded) {
		// Assumes discarded.addCards() accepts a Collection<Card>
		if (discarded != null) {
			discarded.addCards(this.hand); // Add current hand to discard pile
		} else {
			System.err.println("Warning: discardHandToDeck called with null discard pile.");
		}
		this.hand.clear(); // Empty this hand
	}

	//Overridden equals method for the Hand class
	@Override // Good practice to add Override annotation
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		// Consider instanceof check for slight flexibility, but getClass comparison is stricter.
		// if (!(obj instanceof Hand)) { return false; }
		if (obj == null || this.getClass() != obj.getClass()) { return false; }

		Hand other = (Hand) obj;

		// Check sizes MUST be equal for hands to be equal
		if (this.hand.size() != other.hand.size()) {
			return false;
		}

		// Existing loop to compare card by card (assumes order matters)
		// This implementation assumes the order of cards defines equality.
		// If only the *set* of cards matters (regardless of order),
		// a different comparison involving sorting or frequency maps would be needed.
		// Keeping the original order-dependent check as requested.
		for (int i = 0; i < this.hand.size(); i++) {
			// Check if cards at the same position are equal
			// Assumes Card has a correct .equals() method implementation
			Card thisCard = this.hand.get(i);
			Card otherCard = other.hand.get(i);
			// Add null checks if cards in the hand can potentially be null
			if (thisCard == null) {
				if (otherCard != null) return false; // One is null, the other isn't
			} else if (!thisCard.equals(otherCard)) {
				return false; // Cards are not equal
			}
			// If both are null, they are considered equal in this context, continue loop.
		}
		return true; // If loop completes, all cards matched in order.
	}

    /*
    // It's good practice to override hashCode when overriding equals
    // Removed as per user request. Be aware this breaks the equals/hashCode contract
    // and Hand objects may behave unexpectedly in HashMaps, HashSets, etc.
    @Override
    public int hashCode() {
        // A simple hash code implementation based on the list's hash code
        // Assumes Card has a reasonable hashCode() implementation
        return this.hand.hashCode();
    }
    */


	//Returns a String representation of the Hand object
	@Override // Good practice to add Override annotation
	public String toString() {
		// Using StringBuilder is more efficient for string concatenation in loops
		if (this.hand.isEmpty()) {
			return "[Empty Hand]";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.hand.size(); i++) {
			// Add null check if cards can be null
			sb.append(this.hand.get(i) != null ? this.hand.get(i).toString() : "null"); // Assumes Card.toString() is suitable
			if (i < this.hand.size() - 1) {
				sb.append(" - ");
			}
		}
		return sb.toString();
	}

	//Adds a card to the hand. Written for testing purposes only
	// This method is fine as is, clearly marked for testing.
	public void addCard(Card card) {
		if (card != null) { // Basic null check
			this.hand.add(card);
		} else {
			System.err.println("Warning (Test Method addCard): Attempted to add a null card.");
		}
	}

	//checks if the hand has cards
	public boolean hasCards() {
		// Simpler equivalent: return !this.hand.isEmpty();
		// Keeping original logic as requested.
		if(this.hand.size() > 0) {
			return true;
		}
		return false;
		// Could also be written as: return !this.hand.isEmpty();
	}
}