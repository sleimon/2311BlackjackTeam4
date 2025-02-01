package Classes;

public class Card {
     
    private String cardRank;
    private String cardSuit;
    private int cardValue;

    public Card(String rank, String suit, int value) {

        cardRank = rank;
        cardSuit = suit;
        cardValue = value;
        
    }
    
    public Card(Card card) {
    	this.cardRank = card.cardRank;
    	this.cardSuit = card.cardSuit;
    	this.cardValue = card.cardValue;
    }

    public String getRank() {

      return cardRank;

    }

    public String getSuit() {

      return cardSuit;

    }

    public int getValue() {

      return cardValue;
      
    }

    @Override
    public String toString() {
      return cardRank + cardSuit;
    }

    
}
