package Classes;

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
		return hand.size();
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
}
