package Classes;

import java.awt.Image;

import javax.swing.*;

public class Person {
	
	//Fields: A name to identify the person and whatever combination of cards there are holding in their hand
	private String name;
	private Hand hand;
	
	//Constructor: The name is the empty string because it will be set by the child classes. The hand is an empty new hand
	public Person() {
		this.name = "";
		this.hand = new Hand();
		this.splitHand = new Hand();
	}
	
	//Methods
	
	//Returns the name of the person
	public String getName() {
		return this.name;
	}
	
	//Sets the name of the person
	public void setName(String name) {
		this.name = name;
	}
	
	//Returns the hand the person is holding
	public Hand getHand() {
		return this.hand;
	}
	
	//Sets the hand the person is holding
	public void setHand(Hand hand) {
		this.hand = hand;
	}
	
	//Prints the hand into the Game class into the GUI as card images
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
	
	//The method to hit on the person's turn
	public void hit(deckOfCards deck, deckOfCards discard){
		if (!deck.hasCards()) {
			deck.reloadDeckFromDiscard(discard);
		}
		
		this.hand.takeCardFromDeck(deck);
		this.getHand();
	}
	
	//Returns whether the person has a blackjack or not
	public boolean has21(){
		if(this.getHand().calculatedValue() == 21){
			return true;
		}else {
			return false;
		}
	}

	//Overridden equals method for the Person class
	public boolean equals(Object obj) {
		if(this == obj) { return true; }
		if(obj == null || this.getClass() != obj.getClass()) { return false; }
		Person other = (Person) obj;
		return this.name.equals(other.name) &&
				this.hand.equals(other.hand);
	}

	//Returns a String representation of the Person object
	public String toString() {
		String result = this.name + " " + this.hand.toString();
		return result;
	}
}