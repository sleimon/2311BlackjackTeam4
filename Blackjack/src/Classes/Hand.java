package Classes;

import java.util.ArrayList;

public class Hand {
    
    private ArrayList<String> hand = new ArrayList<>();

    public ArrayList<String> drawCard(ArrayList<String> shuffledDeck) {

        hand.add(shuffledDeck.get(0));
        shuffledDeck.remove(shuffledDeck.get(0));

        return hand;

    }

    public ArrayList<String> discardHand(ArrayList<String> hand, ArrayList<String> discardedDeck) {

        int handSize = hand.size();

        for (int i = 0; i < handSize; i++){

            discardedDeck.add(hand.get(i));
            hand.remove(i);

        }
        
        return discardedDeck;
        
    }

    public int handValue(ArrayList<String> hand) {

        int handSize = hand.size();
        int value = 0;
        int sum = 0;

        for (int i = 0; i < handSize; i++) {

           if(hand.get(i).contains("value: 2")) {

                value = 2;

            }

           else if(hand.get(i).contains("value: 3")) {
                
                value = 3;

            }

           else if(hand.get(i).contains("value: 4")) {
                
                value = 4;

            }

           else if(hand.get(i).contains("value: 5")) {
            
                value = 5;

            }

           else if(hand.get(i).contains("value: 6")) {
                
                value = 6;

            }

           else if(hand.get(i).contains("value: 7")) {
    
                value = 7;

            }

           else if(hand.get(i).contains("value: 8")) {
   
                value = 8;

            }

           else if(hand.get(i).contains("value: 9")) {
    
                value = 3;

            }

           else if(hand.get(i).contains("value: 10")) {
    
                value = 10;
            }

           else {
    
                value = 11;
            }

        }
        sum = sum + value;

        return sum;
    }

}

