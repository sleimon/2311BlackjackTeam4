package com.blackjack.Models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class deckOfCards {
	
	//Field: An ArrayList of Card objects that comprise the deck
	private ArrayList<Card> deck;
	
	//Constructor: Creates the standard 52 card deck
	public deckOfCards() {
		this.deck = new ArrayList<Card>();
		String[] suit = {"Hearts", "Diamonds", "Spades", "Clubs"};
		int suitNum = suit.length;
		String[] rank = {"Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Jack", "Queen", "King", "Ace"};
		int rankNum = rank.length;
		int[] value = {2, 3, 4, 5, 6, 7 , 8, 9, 10, 10, 10, 10, 11};
		for (int i = 0; i < suitNum; i++) {
			for (int j = 0; j < rankNum; j++) {
				Card card = new Card(rank[j], suit[i], value[j]);
				this.deck.add(card);
			}
		}
	}
	
	//Methods:
	
	//Checks if the deck has any cards
	public boolean hasCards() {
		if(this.deck.size() > 0) {
			return true;
		}
		return false;
	}
	
	//Adds all the cards in some ArrayList of Cards to a deck
	public void addCards(ArrayList<Card> cards) {
		this.deck.addAll(cards);
	}
	
	//Adds all the cards in a Hand Object to a deckOfCards object
	public void addCards(Hand hand) {
		this.deck.addAll(hand.getHand());
	}
	
	//Adds all the cards in a deckOfCards Object to a deckOfCards object
	public void addCards(deckOfCards deck1) {
		this.deck.addAll(deck1.deck);
	}
	
	//Empties the deck
	public void emptyDeck() {
		deck.clear();
	}
	
	//Returns the deck
	public ArrayList<Card> getDeck(){
		return this.deck;
	}
	
	//Shuffles the order of the Cards in the deck
	public void shuffle() {
		Collections.shuffle(this.deck, new Random());
	}
	
	//Refills this deck with the contents from another deck, shuffles it and empties the given deck
	public void reloadDeckFromDiscard(deckOfCards discard) {
		this.addCards(discard.getDeck());
		this.shuffle();
		discard.emptyDeck();
	}
	
	//Returns the first card in the deck and removes it form the deck
	public Card takeCard() {
		Card card = new Card(this.deck.get(0));
		this.deck.remove(0);
		return card;
	}
	
	//Returns how many cards are left in the deck
	public int cardsLeft() {
		return this.deck.size();
	}

	//Adds a card to the deck. Written for testing purposes only
	public void addCard(Card card) {
		this.deck.add(card);
	}

	//Returns the size of the Deck. Written for testing purposes only
	public int getDeckSize() {
		return this.deck.size();
	}

	//Overridden equals method for the deckOfCards class
	public boolean equals(Object obj) {
		if(this == obj) { return true; }
		if(obj == null || this.getClass() != obj.getClass()) { return false; }
		deckOfCards other = (deckOfCards) obj;
		boolean equals = true;
		for(int i = 0; i < this.deck.size(); i++) {
			if(!(this.deck.get(i).equals(other.deck.get(i)))) {
				equals = false;
			}
		}
		return equals;	
	}
	
	//Returns a String representation of a deckOfCards object
	public String toString() {
		String result = "";

		for(int i = 0; i < this.deck.size(); i++) {
			result += this.deck.get(i).toString();
			if(i < this.deck.size() - 1) {
				result += " - ";
			}
		}
		return result;
	}
}