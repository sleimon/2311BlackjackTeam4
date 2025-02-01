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

		String[] rank = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};
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


	//    public ArrayList<Card> shuffleDeck(ArrayList<Card> initDeck) {
	//
	//        //Randomizes order of cards in the deck.  
	//        int max = 51;
	//        int min = 1;
	//        int range = max - min + 1;
	//
	//
	//        for (int init = 0; init < 51; init++) {
	//            
	//            int rand = (int)(Math.random() * range) + min; // Random number b/w 1 and 52. 
	//  
	//            Card tempCard = initDeck.get(init); // Temporarily stores whatever the card is at the initial position.  
	//            initDeck.set(init, initDeck.get(rand));
	//            initDeck.set(rand, tempCard);
	//
	//        }
	//
	//        return initDeck;
	//    }
	//
	//    // To-do: Add a method that prints the deck and/or the shuffled deck, purely for visualization/internal testing.  
	//
	//    public ArrayList<String> printDeck (ArrayList<Card> deck) {
	//
	//        ArrayList<String> printedDeck = new ArrayList<>();
	//        
	//        for(int i = 0; i < 51; i++) {
	//           
	//            Card deck1 = deck.get(i);
	//            String RankSuitValue = deck1.getRank() + deck1.getSuit() + deck1.getValue();
	//
	//            printedDeck.add(RankSuitValue);
	//
	//        }
	//
	//        return printedDeck;
	//    }


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
