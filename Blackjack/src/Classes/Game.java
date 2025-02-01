package Classes;

public class Game {
	
	private int wins;
	private int losses;
	private int pushes;
	private int roundsPlayed;
	
	private deckOfCards deck;
	private deckOfCards discarded;
	
	private Dealer dealer;
	private Player player;
	
	public Game() {
		//initialize the stats
		this.wins = 0;
		this.losses = 0;
		this.pushes = 0;
		this.roundsPlayed = 0;
		
		//create the people
		this.dealer = new Dealer();
		this.player = new Player();
		
		//create the decks
		this.deck = new deckOfCards();
		this.discarded = new deckOfCards();
		this.discarded.emptyDeck();
		
		this.deck.shuffle();
		startRound();
	}
	
	private void startRound() {
		 if(this.roundsPlayed > 0){
	            System.out.println();
	            System.out.println("Starting Next Round... Wins: " + wins + " Losses: "+ losses+ " Pushes: "+pushes);
	            dealer.getHand().discardHandToDeck(discarded);
	            player.getHand().discardHandToDeck(discarded);
	        }

	        //Check to make sure the deck has at least 4 cards left
	        if(deck.cardsLeft() < 4){
	            deck.reloadDeckFromDiscard(discarded);
	        }

	        //Give the dealer two cards
	        dealer.getHand().takeCardFromDeck(deck);
	        dealer.getHand().takeCardFromDeck(deck);

	        //Give the player two cards
	        player.getHand().takeCardFromDeck(deck);
	        player.getHand().takeCardFromDeck(deck);

	        //Show the dealers hand with one card face down
	        dealer.printFirstHand();

	        //Show the player's hand
	        player.printHand();

	        //Check if dealer has BlackJack to start
	        if(dealer.has21()){
	            //Show the dealer has BlackJack
	            dealer.printHand();

	            //Check if the player also has BlackJack
	            if(player.has21()){
	                //End the round with a push
	                System.out.println("You both have 21 - Push.");
	                pushes++;
	                startRound();
	            }
	            else{
	                System.out.println("Dealer has BlackJack. You lose.");
	                dealer.printHand();
	                losses++;
	                startRound();
	            }
	        }

	        //Check if player has blackjack to start
	        //If we got to this point, we already know the dealer didn't have blackjack
	        if(player.has21()){
	            System.out.println("You have Blackjack! You win!");
	            wins++;
	            startRound();
	        }

	        //Let the player decide what to do next
	        player.makeDecision(deck, discarded);

	        //Check if they busted
	        if(player.getHand().calculatedValue() > 21){
	            System.out.println("You have gone over 21.");
	            losses ++;
	            startRound();
	        }

	        //Now it's the dealer's turn
	        dealer.printHand();
	        while(dealer.getHand().calculatedValue()<17){
	            dealer.hit(deck, discarded);
	        }

	        //Check who wins
	        if(dealer.getHand().calculatedValue()>21){
	            System.out.println("Dealer busts");
	            wins++;
	        }
	        else if(dealer.getHand().calculatedValue() > player.getHand().calculatedValue()){
	            System.out.println("You lose.");
	            losses++;
	        }
	        else if(player.getHand().calculatedValue() > dealer.getHand().calculatedValue()){
	            System.out.println("You win.");
	            wins++;
	        }
	        else{
	            System.out.println("Push.");
	            pushes++;
	        
	        }
	        
	        this.roundsPlayed++;
	        //Start a new round
	        startRound();
	    }
	}

