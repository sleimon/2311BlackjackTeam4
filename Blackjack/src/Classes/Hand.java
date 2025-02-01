package Classes;

import java.util.ArrayList;

public class Hand {
    
    private ArrayList<Card> hand = new ArrayList<>();
    private ArrayList<Card> discardedDeck = new ArrayList<>();

    public ArrayList<Card> drawCard(ArrayList<Card> shuffledDeck) {

        this.hand.add(shuffledDeck.get(0));
        shuffledDeck.remove(shuffledDeck.get(0));

        return this.hand;

    }

    public ArrayList<Card> discardHand() {

        int handSize = hand.size();

        for (int i = 0; i < handSize; i++){

            this.discardedDeck.add(hand.get(i));
            this.hand.remove(i);

        }
        
        return discardedDeck;
        
    }

    public int handValue() {

        int handSize = this.hand.size();
        int value = 0;
        int sum = 0;

        for (int i = 0; i < handSize; i++) {
            
            Card tempCard = this.hand.get(i);
            value = tempCard.getValue();

            sum += value;
        }

        return sum;
    }

    public ArrayList<Card> getHand() {

        return this.hand;

    }

	public Card getCard(int index) {
		return this.hand.get(index);
	}

	public void takeCardFromDeck(deckOfCards deck) {
		this.hand.add(deck.takeCard());
		
	}

	public int calculatedValue() {
		
		int value = 0;
		int aces = 0;
		
		for(Card card: hand) {
			value += card.getValue();
			if(card.getValue() == 11) {
				aces++;
			}
		}
		
		if(value > 21 && aces >= 1) {
			while(aces >= 1 && value > 21) {
				aces--;
				value -= 10;
			}
		}
		
		return value;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
