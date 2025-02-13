package Classes;

import java.awt.Image;

import javax.swing.*;

public class Person {
	
	private String name;
	private Hand hand;

	public Person() {
		this.name = "";
		this.hand = new Hand();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Hand getHand() {
		return hand;
	}

	public void setHand(Hand hand) {
		this.hand = hand;
	}

	public void printHand(JLabel[] cards){
	    for(int i = 0; i < 11; i++){
	        cards[i].setVisible(false);
	    }
	    
	    for(int i = 0; i < this.hand.getHandSize(); i++){
	        String rank = this.hand.getCard(i).getRank();
	        String suit = this.hand.getCard(i).getSuit();
	        String filename = rank + suit + ".png";
	        cards[i].setIcon(new ImageIcon(new ImageIcon(Game.IMAGE_DIR+filename).getImage().getScaledInstance(Game.CARD_WIDTH, Game.CARD_HEIGHT, Image.SCALE_SMOOTH)));
	        cards[i].setVisible(true);
	    }
	}

	public void hit(deckOfCards deck, deckOfCards discard){
		if (!deck.hasCards()) {
			deck.reloadDeckFromDiscard(discard);
		}
		
		this.hand.takeCardFromDeck(deck);
		this.getHand();
	}

	public boolean has21(){
		if(this.getHand().calculatedValue() == 21){
			return true;
		}else {
			return false;
		}
	}
}