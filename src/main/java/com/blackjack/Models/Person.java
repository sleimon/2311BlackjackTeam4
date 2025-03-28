package com.blackjack.Models;

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
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Hand getHand() {
		return this.hand;
	}
	
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

	public boolean equals(Object obj) {
		if(this == obj) { return true; }
		if(obj == null || this.getClass() != obj.getClass()) { return false; }
		Person other = (Person) obj;
		return this.name.equals(other.name) &&
				this.hand.equals(other.hand);
	}

	public String toString() {
		String result = this.name + " " + this.hand.toString();
		return result;
	}
}