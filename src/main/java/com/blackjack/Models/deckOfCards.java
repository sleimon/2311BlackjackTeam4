package com.blackjack.Models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class deckOfCards {
	
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
	
	public boolean hasCards() {
		if(this.deck.size() > 0) {
			return true;
		}
		return false;
	}
	
	public void addCards(ArrayList<Card> cards) {
		this.deck.addAll(cards);
	}
	
	public void addCards(Hand hand) {
		this.deck.addAll(hand.getHand());
	}
	
	public void addCards(deckOfCards deck1) {
		this.deck.addAll(deck1.deck);
	}
	
	public void emptyDeck() {
		deck.clear();
	}
	
	public ArrayList<Card> getDeck(){
		return this.deck;
	}
	
	public void shuffle() {
		Collections.shuffle(this.deck, new Random());
	}
	
	public void reloadDeckFromDiscard(deckOfCards discard) {
		this.addCards(discard.getDeck());
		this.shuffle();
		discard.emptyDeck();
	}
	
	public Card takeCard() {
		Card card = new Card(this.deck.get(0));
		this.deck.remove(0);
		return card;
	}
	
	public int cardsLeft() {
		return this.deck.size();
	}

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