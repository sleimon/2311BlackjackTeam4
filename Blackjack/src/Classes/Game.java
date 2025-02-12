package Classes;

import java.util.Scanner;

public class Game {

	private int wins;
	private int losses;
	private int pushes;
	private int roundsPlayed;

	private deckOfCards deck;
	private deckOfCards discarded;

	private Dealer dealer;
	private Player player;

	public Game(int startingChips) {
		//initialize the stats
		this.wins = 0;
		this.losses = 0;
		this.pushes = 0;
		this.roundsPlayed = 0;

		//create the people
		this.dealer = new Dealer();
		this.player = new Player(startingChips);

		//create the decks
		this.deck = new deckOfCards();
		this.discarded = new deckOfCards();
		this.discarded.emptyDeck();

		this.deck.shuffle();
		startRound();
	}

	private void startRound() {

		if(player.getChips() <= 0) {
			gameOver();
			return;
		}

		placeBet();

		resetHands();

		if(this.roundsPlayed > 0){
			System.out.println();
			System.out.println("Starting Next Round... Wins: " + wins + " Losses: "+ losses + " Pushes: "+ pushes);
		}

		//Check to make sure the deck has at least 4 cards left
		if(deck.cardsLeft() < 4){
			deck.reloadDeckFromDiscard(discarded);
		}

		dealCards();

		initialBlackjack();

		insuranceOrSurrender();

		dealerInitialBlackjack();

		playerTurn();

		dealerTurn();

		non21Win();

		this.roundsPlayed++;
	}

	private void gameOver() {
		System.out.println("Game Over! We've taken all your money!");
	}

	private void placeBet() {
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		System.out.println("You have " + player.getChips() + " chips.");
		System.out.print("Place your bet: ");
		int betAmount = scanner.nextInt();

		// Player places a bet
		player.placeBet(betAmount);
	}

	public void resetHands() {
		dealer.getHand().discardHandToDeck(discarded);
		player.getHand().discardHandToDeck(discarded);
	}

	public void dealCards() {
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
	}

	public void initialBlackjack() {
		//Check if player has BlackJack to start
		if(player.has21()){
			//Show the dealer has BlackJack
			dealer.printHand();

			//Check if the dealer also has BlackJack
			if(dealer.has21()){
				//End the round with a push
				System.out.println("You both have 21 - Push.");
				pushes++;
				startRound();
			}
			else{
				System.out.println("You have Blackjack. You win!");
				dealer.printHand();
				this.wins++;
				player.instant21();
				startRound();
			}
		}
	}

	
	public void insuranceOrSurrender() {
        @SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);
        int decision;
        System.out.println("Would you like to Surrender? Yes(1) or No(2)");
        decision = input.nextInt();
        if(decision == 1) {
            System.out.println("Insurance taken!");
            player.surrenderBet();
            startRound();
        }
        else{
            System.out.println("No insurance taken.");
			
        }
    }

	private void dealerInitialBlackjack() {
		//Check if dealer has blackjack to start
		//If we got to this point, we already know the player didn't have blackjack
		if(dealer.has21()){
			System.out.println("Dealer has Blackjack. You lose.");
			losses++;
			player.loseBet();
			startRound();
		}
	}

	public void playerTurn() {
		//Let the player decide what to do next
		player.makeDecision(deck, discarded);

		//Check if they busted
		if(player.getHand().calculatedValue() > 21){
			System.out.println("You have gone over 21.");
			losses ++;
			startRound();
		}
	}

	public void dealerTurn() {
		//Now it's the dealer's turn
		dealer.printHand();
		while(dealer.getHand().calculatedValue()<17){
			dealer.hit(deck, discarded);
		}
	}

	private void non21Win() {
		//Check who wins
		if(dealer.getHand().calculatedValue()>21){
			System.out.println("Dealer busts");
			player.winBet();
			wins++;
		}
		else if(dealer.getHand().calculatedValue() > player.getHand().calculatedValue()){
			System.out.println("You lose.");
			player.loseBet();
			losses++;
		}
		else if(player.getHand().calculatedValue() > dealer.getHand().calculatedValue()){
			System.out.println("You win.");
			player.winBet();
			wins++;
		}
		else{
			System.out.println("Push.");
			player.pushBet();
			pushes++;
		}
		//Start a new round
		startRound();
	}
}

