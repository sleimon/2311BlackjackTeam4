package Classes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class deckOfCards {

	private ArrayList<Card> deck;

	public deckOfCards() {

		this.deck = new ArrayList<Card>();

		String[] suit = {"Hearts", "Diamonds", "Spades", "Clubs"};
		int suitNum = suit.length;

		String[] rank = {"Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Jack", "Queen", "King", "Ace"};
		int rankNum = rank.length;

		int[] value = {2, 3, 4, 5, 6, 7 , 8, 9, 10, 10, 10, 10, 11};

		//Populates deck with all 52 cards in a deck.  
		for (int i = 0; i < suitNum; i++) {

			for (int j = 0; j < rankNum; j++) {

				Card card = new Card(rank[j], suit[i], value[j]);
				deck.add(card);

			}
		}
	}

	public boolean hasCards() {
		if(this.deck.size() > 0) {
			return true;
		}
		return false;
	}

	public void addCard(Card card) {
		deck.add(card);
	}

	public void addCards(ArrayList<Card> cards) {
		deck.addAll(cards);
	}

	public void emptyDeck() {
		deck.clear();
	}

	public ArrayList<Card> getCards(){
		return this.deck;
	}

	public void shuffle() {
		Collections.shuffle(deck, new Random());
	}

	public void reloadDeckFromDiscard(deckOfCards discard) {
		this.addCards(discard.getCards());
		this.shuffle();
		discard.emptyDeck();
	}


	public Card takeCard() {
		Card card = new Card(deck.get(0));
		this.deck.remove(0);
		return card;
	}


	public int cardsLeft() {
		return this.deck.size();
	}

}