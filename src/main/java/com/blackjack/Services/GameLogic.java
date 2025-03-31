package com.blackjack.Services;
import com.blackjack.Models.*;
import com.blackjack.stubdatabase.StubDatabase;

import com.blackjack.Main;

import java.util.EnumSet;
import java.util.List;

public class GameLogic {

	private User currentUser;
	private deckOfCards deck;
	private deckOfCards discarded;
	private Dealer dealer;
	private Player player;

	private GameState currentState;
	private String gameMessage;
	private boolean dealerCardHidden;

	// Enum to represent the current state of the game round
	public enum GameState {
		BETTING,
		INSURANCE_SURRENDER, // Optional phase after betting, before main play
		PLAYER_TURN,
		DEALER_TURN,
		ROUND_OVER,
		GAME_OVER
	}

	// Enum to represent actions available to the player
	public enum PlayerAction {
		HIT, STAND, DOUBLE_DOWN, SURRENDER, INSURANCE, BET_50, BET_100, BET_ALL, NEXT_ROUND, RESTART, EXIT, NEITHER // NEITHER for insurance/surrender
	}

	// Constructor
	public GameLogic(String username) {
		loadOrCreateUser(username);
		// Initialize player with current user's chips from the loaded/created user
		this.player = new Player(currentUser.getChips());
		this.dealer = new Dealer();
		this.deck = new deckOfCards();
		this.discarded = new deckOfCards();
		this.discarded.emptyDeck();
		this.deck.shuffle();
		this.currentState = GameState.BETTING; // Start in betting phase
		this.gameMessage = "Place your bet.";
		this.dealerCardHidden = true; // Initially hide dealer's second card
		// Deal initial cards but keep them "hidden" logically until bet is placed
		prepareInitialDeal();
	}

	private void loadOrCreateUser(String username) {
		this.currentUser = Main.useStubDatabase ? StubDatabase.getUser(username) : UserService.getUser(username);
		if (currentUser == null) {
			System.out.println("User not found, creating new user: " + username);
			this.currentUser = new User(username, "defaultPass", 1000, 0, 0, 0);
			if (Main.useStubDatabase) {
				StubDatabase.addUser(currentUser);
			} else {
				UserService.addUser(currentUser);
			}
		} else {
			System.out.println("Loaded User: " + currentUser);
		}
	}

	// --- Game Flow Methods ---

	public void prepareInitialDeal() {
		// Ensure deck has enough cards
		if (deck.cardsLeft() < 4) {
			System.out.println("Deck low, reloading from discard pile.");
			deck.reloadDeckFromDiscard(discarded);
		}
		// Deal cards but don't evaluate state yet
		dealer.getHand().takeCardFromDeck(deck);
		dealer.getHand().takeCardFromDeck(deck);
		player.getHand().takeCardFromDeck(deck);
		player.getHand().takeCardFromDeck(deck);
	}

	public void placeBet(int amount) {
		if (currentState != GameState.BETTING) return;
		if (amount > player.getChips()) {
			gameMessage = "Not enough chips to bet " + amount;
			return; // Or handle insufficient funds appropriately
		}

		player.placeBet(amount);
		updateUserChips(); // Update user object immediately after bet
		gameMessage = "Bet placed: " + amount;
		dealerCardHidden = true; // Keep dealer card hidden visually for now

		// Check for initial Blackjacks AFTER bet is placed
		if (player.has21()) {
			handleInitialPlayerBlackjack();
		} else if (dealer.getHand().getCard(0).getValue() == 11) { // Check if dealer showing Ace for insurance
			currentState = GameState.INSURANCE_SURRENDER;
			gameMessage = "Dealer showing Ace. Insurance or Surrender?";
		} else {
			// If no player blackjack and no dealer Ace showing, proceed to player turn
			currentState = GameState.PLAYER_TURN;
			gameMessage = "Hit, Stand, or Double Down?";
			dealerCardHidden = true; // Keep hidden until player stands or busts
		}
		// Note: Dealer Blackjack check happens either in insurance phase or after player stands
	}

	public void chooseNeitherInsuranceSurrender() {
		if (currentState != GameState.INSURANCE_SURRENDER) return;
		// Player chose neither, check if dealer has Blackjack *now*
		if (dealer.has21()) {
			handleDealerBlackjack();
		} else {
			// Dealer does not have Blackjack, proceed to player's normal turn
			currentState = GameState.PLAYER_TURN;
			gameMessage = "Hit, Stand, or Double Down?";
			dealerCardHidden = true; // Keep hidden
		}
	}

	public void requestInsurance() {
		if (currentState != GameState.INSURANCE_SURRENDER) return;
		if (player.getChips() < player.getBet() / 2) {
			gameMessage = "Not enough chips for insurance.";
			// Stay in INSURANCE_SURRENDER state or revert? Revert might be better.
			currentState = GameState.PLAYER_TURN; // Allow normal play
			gameMessage = "Insufficient funds for insurance. Hit, Stand, or Double?";
			return;
		}

		player.insuranceBet();
		updateUserChips(); // Update chips for insurance cost

		if (dealer.has21()) {
			// Insurance pays out, main bet is lost (unless player also had BJ)
			player.winInsurance();
			gameMessage = "Insurance paid! Dealer had Blackjack.";
			// Since dealer had BJ, player loses original bet regardless
			handleDealerBlackjack(); // This handles the loss of the main bet too
			currentState = GameState.ROUND_OVER;
			dealerCardHidden = false; // Reveal dealer hand
		} else {
			// Insurance lost, dealer does not have BJ
			player.loseInsurance();
			gameMessage = "Insurance lost. Dealer doesn't have Blackjack. Hit, Stand, or Double?";
			currentState = GameState.PLAYER_TURN;
			dealerCardHidden = true; // Keep hidden
		}
		updateUserChips(); // Update chips after insurance win/loss
	}


	public void hit() {
		if (currentState != GameState.PLAYER_TURN) return;

		player.hit(deck, discarded);
		gameMessage = "Player Hits.";
		dealerCardHidden = true; // Keep hidden

		if (player.getHand().calculatedValue() > 21) {
			handlePlayerBust();
		} else if (player.getHand().calculatedValue() == 21) {
			gameMessage = "Player has 21!";
			// Player got 21, but not Blackjack (more than 2 cards). Move to dealer's turn.
			stand(); // Automatically stand on 21
		} else {
			gameMessage = "Hit or Stand?";
			// Stay in PLAYER_TURN
		}
	}

	public void stand() {
		if (currentState != GameState.PLAYER_TURN) return;

		currentState = GameState.DEALER_TURN;
		gameMessage = "Player Stands. Dealer's turn.";
		dealerCardHidden = false; // Reveal dealer's card
		executeDealerTurn();
	}

	public void doubleDown() {
		if (currentState != GameState.PLAYER_TURN || player.getHand().getHandSize() != 2 || player.getChips() < player.getBet()) {
			gameMessage = "Cannot Double Down now.";
			return;
		}

		player.doubleDown(); // Doubles the bet internally
		updateUserChips(); // Reflect the doubled bet cost
		player.hit(deck, discarded); // Player gets exactly one more card
		gameMessage = "Player Doubled Down.";

		if (player.getHand().calculatedValue() > 21) {
			handlePlayerBust();
		} else {
			// Player doubled and didn't bust, proceed immediately to dealer's turn
			currentState = GameState.DEALER_TURN;
			dealerCardHidden = false; // Reveal dealer's card
			gameMessage += " Dealer's turn.";
			executeDealerTurn();
		}
	}


	public void surrender() {
		// Typically only allowed as the very first action on the first two cards
		if (currentState != GameState.INSURANCE_SURRENDER && !(currentState == GameState.PLAYER_TURN && player.getHand().getHandSize() == 2)) {
			gameMessage = "Cannot surrender now.";
			return;
		}

		player.surrenderBet(); // Player gets half the bet back
		currentUser.setLosses(currentUser.getLosses() + 1);
		// Chips are adjusted within player.surrenderBet(), update user object
		updateUserChips();
		saveUserData();

		gameMessage = "Player Surrendered. Half bet returned.";
		currentState = GameState.ROUND_OVER;
		dealerCardHidden = false; // Reveal dealer hand
	}


	private void executeDealerTurn() {
		if (currentState != GameState.DEALER_TURN) return;
		dealerCardHidden = false; // Make sure it's revealed

		// Check if dealer had Blackjack initially (if insurance wasn't offered/taken)
		if (dealer.has21() && dealer.getHand().getHandSize() == 2) {
			handleDealerBlackjack();
			return; // Round ends immediately
		}

		while (dealer.getHand().calculatedValue() < 17) {
			dealer.hit(deck, discarded);
		}
		determineOutcome();
	}

	private void handleInitialPlayerBlackjack() {
		dealerCardHidden = false; // Reveal dealer card to check for push
		if (dealer.has21()) { // Dealer also has Blackjack
			player.pushBet();
			currentUser.setPushes(currentUser.getPushes() + 1);
			gameMessage = "Push! Both have Blackjack!";
		} else { // Player wins with Blackjack
			player.instant21(); // Pays 3:2
			currentUser.setWins(currentUser.getWins() + 1);
			gameMessage = "Player Blackjack! You win!";
		}
		updateUserChips();
		saveUserData();
		currentState = GameState.ROUND_OVER;
	}

	private void handleDealerBlackjack() {
		dealerCardHidden = false; // Reveal hand
		// Player loses main bet unless they also had Blackjack (handled in handleInitialPlayerBlackjack)
		if (!player.has21()) { // Avoid double counting if it was a push
			player.loseBet();
			currentUser.setLosses(currentUser.getLosses() + 1);
			gameMessage = "Dealer Blackjack! You lose.";
		} else if (player.has21()) {
			// This case (both BJ) should have been caught by handleInitialPlayerBlackjack
			// If reached here via insurance path where player didn't have BJ, it's correct loss.
			// If somehow player had BJ and insurance was involved, push was already handled.
			// Let's assume loss if not player BJ.
			player.loseBet();
			currentUser.setLosses(currentUser.getLosses() + 1);
			gameMessage = "Dealer Blackjack! You lose.";
		}
		updateUserChips();
		saveUserData();
		currentState = GameState.ROUND_OVER;
	}

	private void handlePlayerBust() {
		player.loseBet();
		currentUser.setLosses(currentUser.getLosses() + 1);
		updateUserChips();
		saveUserData();
		gameMessage = "Player Busts! You lose.";
		currentState = GameState.ROUND_OVER;
		dealerCardHidden = false; // Reveal dealer hand
	}


	private void determineOutcome() {
		if (currentState != GameState.DEALER_TURN) return; // Should only be called after dealer turn

		int dealerValue = dealer.getHand().calculatedValue();
		int playerValue = player.getHand().calculatedValue();

		if (dealerValue > 21) {
			gameMessage = "Dealer Busts! You win!";
			player.winBet();
			currentUser.setWins(currentUser.getWins() + 1);
		} else if (dealerValue > playerValue) {
			gameMessage = "Dealer wins!";
			player.loseBet();
			currentUser.setLosses(currentUser.getLosses() + 1);
		} else if (playerValue > dealerValue) {
			gameMessage = "You win!";
			player.winBet();
			currentUser.setWins(currentUser.getWins() + 1);
		} else { // Push
			gameMessage = "Push!";
			player.pushBet();
			currentUser.setPushes(currentUser.getPushes() + 1);
		}

		updateUserChips();
		saveUserData();
		currentState = GameState.ROUND_OVER;

		if (player.getChips() <= 0) {
			handleGameOver();
		}
	}

	public void nextRound() {
		if (currentState != GameState.ROUND_OVER) return;

		// Check for game over condition again before starting new round
		if (player.getChips() <= 0) {
			handleGameOver();
			return;
		}


		// Move played cards to discard pile
		player.getHand().discardHandToDeck(discarded);
		dealer.getHand().discardHandToDeck(discarded);
		player.resetBet(); // Reset bet amount for the new round

		// Prepare for the next round
		prepareInitialDeal();
		currentState = GameState.BETTING;
		gameMessage = "Place your bet for the next round.";
		dealerCardHidden = true;
	}

	private void handleGameOver() {
		gameMessage = "Game Over! No more chips.";
		currentState = GameState.GAME_OVER;
		// User data is already saved round by round
	}

	public void restartGame() {
		// Reset player stats and chips based on *initial* user load or a fixed amount
		// For simplicity, let's reset to the state when the user was first loaded/created for this session
		// Or reset to a fixed default like 1000 chips and 0 stats
		currentUser.setChips(1000); // Reset to default starting chips
		currentUser.setWins(0);
		currentUser.setLosses(0);
		currentUser.setPushes(0);
		saveUserData(); // Persist the reset state

		// Re-initialize game components
		this.player = new Player(currentUser.getChips()); // Create new player with reset chips
		this.dealer = new Dealer();
		this.deck = new deckOfCards();
		this.discarded = new deckOfCards();
		this.discarded.emptyDeck();
		this.deck.shuffle();

		// Reset state for a new game
		prepareInitialDeal();
		currentState = GameState.BETTING;
		gameMessage = "Game Restarted. Place your bet.";
		dealerCardHidden = true;
	}


	// --- Utility and State Update Methods ---

	private void updateUserChips() {
		// Sync the User object's chip count with the Player object's chip count
		currentUser.setChips(player.getChips());
		// No need to call saveUserData here, it's called after win/loss determination
	}

	private void saveUserData() {
		// Persist the current user data
		if (Main.useStubDatabase) {
			StubDatabase.updateUser(currentUser);
		} else {
			UserService.updateUser(currentUser);
		}
		System.out.println("User data saved: " + currentUser); // For debugging
	}

	// --- Getters for GUI ---

	public User getCurrentUser() {
		return currentUser;
	}

	public Player getPlayer() {
		return player;
	}

	public Dealer getDealer() {
		return dealer;
	}

	public GameState getCurrentState() {
		return currentState;
	}

	public String getGameMessage() {
		return gameMessage;
	}

	public boolean isDealerCardHidden() {
		return dealerCardHidden && currentState != GameState.ROUND_OVER && currentState != GameState.GAME_OVER;
	}

	public int getPlayerHandValue() {
		return player.getHand().calculatedValue();
	}

	public int getDealerHandValue() {
		// Return 0 or a specific value if card is hidden? Or let GUI handle display?
		// Let GUI handle the display logic based on isDealerCardHidden()
		return dealer.getHand().calculatedValue();
	}

	public int getDealerVisibleValue() {
		if (dealer.getHand().getHandSize() > 0) {
			return dealer.getHand().getCard(0).getValue(); // Assumes Ace value logic is handled in Card/Hand
		}
		return 0;
	}


	public List<Card> getPlayerCards() {
		return player.getHand().getCards(); // Assuming Hand has getCards()
	}

	public List<Card> getDealerCards() {
		return dealer.getHand().getCards(); // Assuming Hand has getCards()
	}

	// Determine which actions (buttons) should be available based on the current state
	// Inside GameLogic.java -> getAvailableActions() method

	public EnumSet<PlayerAction> getAvailableActions() {
		EnumSet<PlayerAction> actions = EnumSet.noneOf(PlayerAction.class);
		Player player = getPlayer(); // Assuming you have access to player object
		Dealer dealer = getDealer(); // Assuming access to dealer

		switch (currentState) {
			case BETTING:
				// ... existing betting logic ...
				if (player.getChips() >= 50) actions.add(PlayerAction.BET_50);
				if (player.getChips() >= 100) actions.add(PlayerAction.BET_100);
				if (player.getChips() > 0) actions.add(PlayerAction.BET_ALL);
				break;

			// ---> THIS IS THE KEY STATE <---
			case INSURANCE_SURRENDER:
				System.out.println("[DEBUG GameLogic] In INSURANCE_SURRENDER state. Adding actions."); // DEBUG
				// Always offer Neither
				actions.add(PlayerAction.NEITHER);

				// Offer Surrender (check specific house rules, often only allowed now)
				// Example: Assuming surrender allowed on first 2 cards
				if (player.getHand().getSize() == 2) { // Or check specific flag
					actions.add(PlayerAction.SURRENDER);
					System.out.println("  -> Adding SURRENDER"); // DEBUG
				}

				// Offer Insurance only if dealer shows Ace AND player can afford it
				// Assuming getDealerVisibleValue() gets the upcard's value correctly
				if (dealer.getHand().getCard(0).getValue() == 11 && // Dealer showing Ace
						player.getChips() >= player.getBet() / 2) {      // Can afford insurance bet
					actions.add(PlayerAction.INSURANCE);
					System.out.println("  -> Adding INSURANCE"); // DEBUG
				}
				break;

			case PLAYER_TURN:
				// ... existing hit/stand logic ...
				actions.add(PlayerAction.HIT);
				actions.add(PlayerAction.STAND);

				// Offer Double Down (only on first two cards and if affordable)
				if (player.getHand().getSize() == 2 && player.getChips() >= player.getBet()) {
					actions.add(PlayerAction.DOUBLE_DOWN);
				}

//				// Optional: Offer Surrender as first action? (If rules allow & not already handled in INSURANCE_SURRENDER)
//				 if (player.getHand().getSize() == 2 && /* check if it's the very first decision */) {
//				    actions.add(PlayerAction.SURRENDER);
//				 }
				break;

			case DEALER_TURN:
				// No player actions
				break;
			case ROUND_OVER:
				actions.add(PlayerAction.NEXT_ROUND);
				break;
			case GAME_OVER:
				actions.add(PlayerAction.RESTART);
				actions.add(PlayerAction.EXIT);
				break;
		}
		System.out.println("[DEBUG GameLogic] Available Actions: " + actions); // DEBUG
		return actions;
	}
}