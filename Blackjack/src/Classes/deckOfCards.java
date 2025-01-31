package Classes;

import java.util.ArrayList;

public class deckOfCards {
    
    public ArrayList<String> initDeck() {

        String[] suit = {"Hearts", "Diamonds", "Spades", "Clubs"};
        int suitNum = suit.length;

        String[] rank = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};
        int rankNum = rank.length;

        int[] value = {2, 3, 4, 5, 6, 7 , 8, 9, 10, 10, 10, 10, 11};

        ArrayList<String> deck = new ArrayList<>();
        
        //Populates deck with all 52 cards in a deck.  
        for (int i = 0; i < suitNum; i++) {

            for (int j = 0; j < rankNum; j++) {
                               
                Card card = new Card(rank[j], suit[i], value[j]);

                String RankAndSuitAndValue = card.getRank() + " of " + card.getSuit() + " (value: " + card.getValue() + ")";

                deck.add(RankAndSuitAndValue);
            }
        }   
        return deck;
        
    }


    public ArrayList<String> shuffleDeck(ArrayList<String> deck) {

        //Randomizes order of cards in the deck.  
        int max = 51;
        int min = 1;
        int range = max - min + 1;


        for (int init = 0; init < 51; init++) {

            int rand = (int)(Math.random() * range) + min; // Random number b/w 1 and 52.
            String temp;
            String left = deck.get(init);
            String right = deck.get(rand);

            temp = left;
            left = right;
            right = temp;

            deck.set(init, left);
            deck.set(rand, right);

        }

        return deck;
    }
}



