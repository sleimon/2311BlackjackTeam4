package Blackjack;

import java.util.Scanner;

public class Player extends Person{

	public Player() {
		super.setName("PLAYER");
	}
	
	Scanner input = new Scanner(System.in);
	
	public void makeDecision(Deck deck, Deck discard) {
		boolean getNum = true;
		int decision = 0;
		
	    while(getNum){
	    	try{
	    		System.out.println("Would you like to (1)hit or (2)stand?");
	            decision = input.nextInt();
	            getNum = false;

	            }
	            catch(Exception e){
	                System.out.println("Invalid");
	                input.next();
	            }
	    	 if (decision == 1) {
	             this.hit(deck, discard);
	             
	             if(this.getHand().calculatedValue()>21){
	            	 System.out.println("BUST!");
	                 return;
	             }
	             else{
	                 this.makeDecision(deck, discard);
	             }
	         } 
	    	 else {
	             System.out.println("You stand.");
	         }
	        }
	    }
	}


