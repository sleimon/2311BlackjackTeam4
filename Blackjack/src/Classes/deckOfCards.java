package Classes;

import java.util.ArrayList;

public class deckOfCards {
    
    private ArrayList<Card> deck = new ArrayList<>();

    public ArrayList<Card> initDeck() {

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
        
        return deck;
        
    }


    public ArrayList<Card> shuffleDeck(ArrayList<Card> initDeck) {

        //Randomizes order of cards in the deck.  
        int max = 51;
        int min = 1;
        int range = max - min + 1;


        for (int init = 0; init < 51; init++) {
            
            int rand = (int)(Math.random() * range) + min; // Random number b/w 1 and 52. 
  
            Card tempCard = initDeck.get(init); // Temporarily stores whatever the card is at the initial position.  
            initDeck.set(init, initDeck.get(rand));
            initDeck.set(rand, tempCard);

        }

        return initDeck;
    }

    // To-do: Add a method that prints the deck and/or the shuffled deck, purely for visualization/internal testing.  

    public ArrayList<String> printDeck (ArrayList<Card> deck) {

        ArrayList<String> printedDeck = new ArrayList<>();
        
        for(int i = 0; i < 51; i++) {
           
            Card deck1 = deck.get(i);
            String RankSuitValue = deck1.getRank() + deck1.getSuit() + deck1.getValue();

            printedDeck.add(RankSuitValue);

        }

        return printedDeck;
    }
}
