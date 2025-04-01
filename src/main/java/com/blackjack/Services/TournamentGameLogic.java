package com.blackjack.Services;

// Assuming these models and services are correctly located/imported
import com.blackjack.stubdatabase.StubDatabase;
import com.blackjack.Main;
import com.blackjack.Models.*;
import com.blackjack.GUI.TournamentGUI; // Assuming GUI might be needed for context/updates later

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public class TournamentGameLogic {

    // --- Fields ---
    private User currentUser; // Master user record
    private deckOfCards deck;
    private deckOfCards discarded;
    private Dealer dealer;
    private Player player; // Player object specific to the game instance
    private GameState currentState;
    private String gameMessage;
    private boolean dealerCardHidden;
    public Tournament currentTournament; // Public for GUI access or provide getter


    // --- Enums ---
    public enum GameState {
        BETTING,
        INSURANCE_SURRENDER,
        PLAYER_TURN,
        DEALER_TURN,
        ROUND_OVER,
        GAME_OVER, // Generic game over (e.g., out of chips)
        TOURNAMENT_WON_BY_PLAYER, // Player won the current tournament
        TOURNAMENT_ENDED_OTHER_WINNER // Tournament was already inactive/won by someone else
    }

    // Added BACK_TO_MENU to PlayerAction
    public enum PlayerAction {
        HIT, STAND, DOUBLE_DOWN,
        SURRENDER,
        INSURANCE,
        NEITHER,
        BET_50, BET_100, BET_ALL,
        NEXT_ROUND, RESTART, EXIT,
        BACK_TO_MENU // Action to return to the main menu
    }

    // --- Constructor ---
    public TournamentGameLogic(String username, String tournamentName) {
        if (username == null || tournamentName == null) {
            throw new IllegalArgumentException("Username and Tournament Name cannot be null.");
        }
        loadOrCreateUser(username); // Loads the master user record

        // Ensure currentUser was loaded before proceeding
        if (this.currentUser == null) {
            System.err.println("FATAL: Cannot initialize TournamentGameLogic, currentUser is null for username: " + username);
            // Handle this critical failure - maybe throw an exception or set a specific error state?
            this.currentState = GameState.GAME_OVER;
            this.gameMessage = "Error: Could not load user data.";
            return; // Prevent further initialization
        }

        // Load the specific tournament
        this.currentTournament = TournamentService.getTournamentFromName(tournamentName);
        if (this.currentTournament == null) {
            System.err.println("FATAL: Cannot initialize TournamentGameLogic, tournament not found: " + tournamentName);
            this.currentState = GameState.GAME_OVER;
            this.gameMessage = "Error: Could not load tournament data.";
            return; // Prevent further initialization
        }

        // Create a player instance for this game session with starting tournament chips (e.g., 1000)
        // The player's chip count is independent of the currentUser's overall chip count during the tournament
        this.player = new Player(1000); // Standard starting chips for tournament game instance
        this.player.setName(username); // Associate player instance with username if needed

        // Standard game setup
        this.dealer = new Dealer();
        this.deck = new deckOfCards();
        this.discarded = new deckOfCards();
        this.discarded.emptyDeck();
        this.deck.shuffle();

        // Initial state setup
        this.dealerCardHidden = true;
        prepareInitialDeal(); // Deal cards first

        // Check for immediate tournament end state *after* dealing (though unlikely)
        updateTournamentGame(); // Check if already inactive or won

        // If tournament didn't end immediately, set to betting state
        if (this.currentState != GameState.TOURNAMENT_ENDED_OTHER_WINNER &&
                this.currentState != GameState.TOURNAMENT_WON_BY_PLAYER) {
            this.currentState = GameState.BETTING;
            this.gameMessage = "Place your bet for the tournament round.";
        }
    }

    // --- User Loading ---
    private void loadOrCreateUser(String username) {
        // This loads the MASTER user record, primarily for associating wins/losses if needed,
        // and for getting the user ID to record tournament winner.
        // The player's chips IN THE TOURNAMENT are managed by the 'player' object.
        this.currentUser = Main.useStubDatabase ? StubDatabase.getUser(username) : UserService.getUser(username);
        if (currentUser == null) {
            // In a tournament, user should exist. Don't create here. Log error.
            System.err.println("Error: User '" + username + "' not found. Cannot participate in tournament.");
            // Set currentUser to null, constructor will handle the fatal error.
            this.currentUser = null;
            // // Original logic if creating user was desired (less suitable for tournaments):
            // System.out.println("User not found, creating new user: " + username);
            // this.currentUser = new User(username, "defaultPass", 1000, 0, 0, 0);
            // if (Main.useStubDatabase) { StubDatabase.addUser(currentUser); }
            // else { if (!UserService.addUser(currentUser)) { System.err.println("Failed to add new user!"); this.currentUser = null; } }
        }
        if (this.currentUser != null) {
            System.out.println("[TournamentLogic] Loaded User: " + currentUser.getUsername() + " (Master Record)");
        }
    }

    // --- Game Setup ---
    public void prepareInitialDeal() {
        // Ensure core components are ready
        if (deck == null || discarded == null || player == null || dealer == null || player.getHand() == null || dealer.getHand() == null) {
            System.err.println("Error preparing deal: Game objects not initialized.");
            this.currentState = GameState.GAME_OVER; // Fatal error
            this.gameMessage = "Internal game error during deal setup.";
            return;
        }

        // Check deck size and reshuffle if needed
        if (deck.cardsLeft() < 4) {
            System.out.println("[TournamentLogic] Deck low, reshuffling from discard pile.");
            deck.reloadDeckFromDiscard(discarded);
            deck.shuffle(); // Shuffle after reloading
        }

        // Clear hands and deal
        player.getHand().discardHandToDeck(discarded);
        dealer.getHand().discardHandToDeck(discarded);
        dealer.getHand().takeCardFromDeck(deck); dealer.getHand().takeCardFromDeck(deck);
        player.getHand().takeCardFromDeck(deck); player.getHand().takeCardFromDeck(deck);
        System.out.println("[TournamentLogic] Cards dealt for new round.");
        this.dealerCardHidden = true; // Ensure dealer card is hidden initially
    }

    // --- Helper Method for Natural Blackjack ---
    private boolean isNaturalBlackjack(Person person) {
        return person != null && person.getHand() != null &&
                person.getHand().getSize() == 2 && person.getHand().calculatedValue() == 21;
    }

    // --- Betting Phase ---
    public void placeBet(int amount) {
        if (currentState != GameState.BETTING) return;
        if (player == null) { handleCriticalError("Player object is null during bet."); return; }

        if (amount <= 0 || amount > player.getChips()) {
            gameMessage = "Invalid bet amount (Available: " + player.getChips() + ")";
            return;
        }

        player.placeBet(amount);
        gameMessage = "Bet placed: " + amount + ".";
        dealerCardHidden = true; // Keep hidden until player turn or resolution

        // --- State Transition Logic after Bet ---
        // Check player/dealer hands *after* bet is placed
        boolean playerHasNaturalBj = isNaturalBlackjack(player);
        Card dealerUpCard = (dealer.getHand() != null && dealer.getHand().getSize() > 0) ? dealer.getHand().getCard(0) : null;

        if (dealerUpCard == null) { handleCriticalError("Dealer has no up-card after deal."); return; }

        int dealerUpCardValue = dealerUpCard.getValue();
        boolean dealerShowsAce = (dealerUpCardValue == 11);
        boolean dealerShowsTenValue = (dealerUpCardValue == 10);
        boolean offerInsuranceOpportunity = dealerShowsAce; // Standard rules: Offer only on Ace

        // Decide next state based on Blackjack and Insurance possibilities
        if (playerHasNaturalBj) {
            handleInitialPlayerBlackjack(); // This will resolve the round and set ROUND_OVER state
        } else if (offerInsuranceOpportunity) {
            currentState = GameState.INSURANCE_SURRENDER;
            String अफोर्डेबिलिटीText = player.getChips() >= (player.getBet() / 2) ? "" : " (Cannot Afford Insurance)"; // Standard insurance cost is half original bet
            gameMessage = "Dealer showing Ace. Insurance offered." + अफोर्डेबिलिटीText;
            // Surrender might also be offered here depending on rules
        } else {
            currentState = GameState.PLAYER_TURN;
            gameMessage = getPlayerTurnPrompt(); // Set initial prompt for player's action
        }
        // Note: updateTournamentGame() check is called AFTER round resolution normally
    }


    // --- Insurance Phase Actions ---
    public void requestInsurance() {
        if (currentState != GameState.INSURANCE_SURRENDER) return;
        if (player == null || dealer == null) { handleCriticalError("Player/Dealer null during insurance request."); return; }

        int insuranceCost = player.getBet() / 2; // Standard cost
        if (player.getChips() >= insuranceCost) {
            player.insuranceBet(); // Player object should handle chip deduction
            gameMessage = "Insurance purchased.";

            // Now check if dealer has Blackjack
            if (isNaturalBlackjack(dealer)) {
                player.winInsurance(); // Player object handles payout logic
                // Don't modify currentUser stats directly, focus on player object for game chips
                gameMessage += " Insurance Won! Dealer had Blackjack.";
                // The main bet is lost separately
                handleDealerBlackjack(); // This sets ROUND_OVER state and handles main bet loss
            } else {
                player.loseInsurance(); // Player object handles loss of insurance bet
                gameMessage += " Insurance Lost. Dealer does not have Blackjack.";
                currentState = GameState.PLAYER_TURN; // Continue to Player Turn
                gameMessage += " " + getPlayerTurnPrompt();
                dealerCardHidden = true;
            }
        } else {
            gameMessage = "Not enough chips for Insurance (" + insuranceCost + " needed).";
            // Stay in INSURANCE_SURRENDER state? Or auto-decline? Let's auto-decline.
            declineInsurance();
        }
    }

    public void declineInsurance() {
        if (currentState != GameState.INSURANCE_SURRENDER) return;
        if (dealer == null) { handleCriticalError("Dealer null during insurance decline."); return; }

        gameMessage = "Insurance declined.";
        // Check if dealer has Blackjack anyway
        if (isNaturalBlackjack(dealer)) {
            gameMessage += " Dealer reveals Blackjack!";
            handleDealerBlackjack(); // Resolve round, sets ROUND_OVER
        } else {
            gameMessage += " Dealer does not have Blackjack.";
            currentState = GameState.PLAYER_TURN; // Proceed to Player Turn
            gameMessage += " " + getPlayerTurnPrompt();
            dealerCardHidden = true;
        }
    }


    // --- Player Turn Actions ---
    public void hit() {
        if (currentState != GameState.PLAYER_TURN) return;
        if (player == null || deck == null || discarded == null) { handleCriticalError("Null object during hit."); return; }

        player.hit(deck, discarded);
        gameMessage = "Player Hits."; // Base message

        int playerValue = player.getHand().calculatedValue();
        if (playerValue > 21) {
            handlePlayerBust(); // Sets ROUND_OVER
        } else if (playerValue == 21) {
            gameMessage += " Player has 21!";
            stand(); // Automatically stand on 21
        } else {
            gameMessage = getPlayerTurnPrompt(); // Update prompt for next action
        }
    }

    public void stand() {
        if (currentState != GameState.PLAYER_TURN) return;
        currentState = GameState.DEALER_TURN;
        gameMessage = "Player Stands with " + player.getHand().calculatedValue() + ". Dealer's turn.";
        dealerCardHidden = false; // Reveal dealer's hole card
        executeDealerTurn();
    }

    public void doubleDown() {
        if (currentState != GameState.PLAYER_TURN) return;
        if (player == null || deck == null || discarded == null) { handleCriticalError("Null object during double down."); return; }

        // Rule: Only allowed on first two cards
        if (player.getHand().getSize() > 2) {
            gameMessage = "Can only Double Down on the first two cards.";
            return;
        }
        // Rule: Must be able to afford doubling the bet
        if (player.getChips() >= player.getBet()) {
            int originalBet = player.getBet();
            player.doubleDown(); // Player object doubles bet and deducts chips
            gameMessage = "Player Doubled Down (Bet: " + player.getBet() + ").";

            player.hit(deck, discarded); // Player receives exactly one more card
            gameMessage += " Receives " + player.getHand().getCard(player.getHand().getSize()-1).toStringShort() + ".";

            int playerValue = player.getHand().calculatedValue();
            if (playerValue > 21) {
                gameMessage += " Player Busts!";
                handlePlayerBust(); // Sets ROUND_OVER
            } else {
                gameMessage += " Player stands with " + playerValue + ".";
                currentState = GameState.DEALER_TURN; // End player's turn
                dealerCardHidden = false;
                gameMessage += " Dealer's turn.";
                executeDealerTurn();
            }
        } else {
            gameMessage = "Not enough chips to Double Down (Need: " + player.getBet() + ").";
        }
    }

    public void surrender() {
        if (currentState != GameState.PLAYER_TURN) return;
        if (player == null) { handleCriticalError("Player null during surrender."); return; }

        // Rule: Only allowed on first two cards
        if (player.getHand().getSize() > 2) {
            gameMessage = "Can only surrender on the first two cards.";
            return;
        }

        player.surrenderBet(); // Player object handles losing half bet, adding back half
        // No direct win/loss stat change for surrender in master record usually
        gameMessage = "Player Surrendered. Half bet returned.";
        currentState = GameState.ROUND_OVER;
        dealerCardHidden = false; // Reveal card even though round ended early
        // Check for tournament win *after* resolving the round/bet
        updateTournamentGame();
    }


    // --- Dealer's Turn and Round Resolution ---
    private void executeDealerTurn() {
        if (currentState != GameState.DEALER_TURN) return;
        if (dealer == null || deck == null || discarded == null) { handleCriticalError("Null object during dealer turn."); return; }

        dealerCardHidden = false; // Ensure card is revealed

        // Dealer hits according to rules (hit soft 17)
        while (dealer.getHand().calculatedValue() < 17 || (dealer.getHand().calculatedValue() == 17 && dealer.getHand().isSoft())) {
            gameMessage = "Dealer hits..."; // Provide feedback before hit
            dealer.hit(deck, discarded);
            // Optional: Update message after hit: gameMessage = "Dealer hits, has " + dealer.getHand().calculatedValue();
        }
        determineOutcome(); // Compare hands and set winner
    }

    private void determineOutcome() {
        // This method assumes player didn't bust and dealer's turn is complete
        if (currentState != GameState.DEALER_TURN) {
            // Avoid resolving outcome if state changed unexpectedly (e.g., player busted earlier)
            System.err.println("Warning: determineOutcome called outside DEALER_TURN state.");
            return;
        }
        if (player == null || dealer == null) { handleCriticalError("Null player/dealer during outcome."); return; }


        int dealerValue = dealer.getHand().calculatedValue();
        int playerValue = player.getHand().calculatedValue();

        gameMessage = "Dealer stands with " + dealerValue + ". "; // Start outcome message

        if (dealerValue > 21) {
            gameMessage += "Dealer Busts! You win!";
            player.winBet();
            // Don't update master stats (wins/losses) for tournament rounds
        } else if (dealerValue > playerValue) {
            gameMessage += "Dealer wins!";
            player.loseBet();
        } else if (playerValue > dealerValue) {
            gameMessage += "You win!";
            player.winBet();
        } else { // dealerValue == playerValue
            gameMessage += "Push!";
            player.pushBet();
        }

        currentState = GameState.ROUND_OVER;
        // Check for tournament win AFTER resolving the round and updating player chips
        updateTournamentGame();

        // Check if player is out of chips ONLY IF tournament hasn't ended
        if (currentState == GameState.ROUND_OVER && player.getChips() <= 0) {
            handleGameOver(); // Player is eliminated from tournament
        }
    }

    // --- Blackjack/Bust Handling ---
    private void handleInitialPlayerBlackjack() {
        // Called ONLY from placeBet if player gets natural 21
        if (dealer == null || player == null) { handleCriticalError("Null player/dealer during player BJ."); return; }
        dealerCardHidden = false; // Reveal dealer's hand

        if (isNaturalBlackjack(dealer)) {
            player.pushBet();
            gameMessage = "Push! Both have Natural Blackjack!";
        } else {
            player.instant21(); // Player wins 3:2
            gameMessage = "Player Blackjack! You win!";
        }
        currentState = GameState.ROUND_OVER;
        // Check for tournament win after resolving round
        updateTournamentGame();
    }

    private void handleDealerBlackjack() {
        // Called when dealer reveals natural 21 (either initially or after insurance phase)
        if (dealer == null || player == null) { handleCriticalError("Null player/dealer during dealer BJ."); return; }
        dealerCardHidden = false;

        // Player automatically loses their main bet unless they also had BJ (handled in player BJ case)
        // Insurance payout/loss handled in insurance methods.
        if (!isNaturalBlackjack(player)) { // Check if player didn't also have BJ
            player.loseBet();
            gameMessage = (gameMessage != null && gameMessage.contains("Insurance") ? gameMessage + " " : "") + "Dealer Blackjack! You lose.";
        } else {
            // This case is covered by handleInitialPlayerBlackjack (Push), message set there.
            // We just ensure the state is correct.
        }

        currentState = GameState.ROUND_OVER;
        // Check for tournament win after resolving round
        updateTournamentGame();
    }

    private void handlePlayerBust() {
        if (player == null) { handleCriticalError("Player null during bust."); return; }
        player.loseBet();
        gameMessage = "Player Busts with " + player.getHand().calculatedValue() + "! You lose.";
        currentState = GameState.ROUND_OVER;
        dealerCardHidden = false; // Reveal dealer card
        // Check for tournament win after resolving round
        updateTournamentGame();
        // Check if player is out of chips ONLY IF tournament hasn't ended
        if (currentState == GameState.ROUND_OVER && player.getChips() <= 0) {
            handleGameOver(); // Player is eliminated from tournament
        }
    }

    // --- Round/Game Lifecycle ---
    public void nextRound() {
        // Only proceed if the round is over AND the tournament is still active
        if (currentState != GameState.ROUND_OVER) return;
        if (player == null) { handleCriticalError("Player null during next round."); return; }

        // Check if player has enough chips to continue (e.g., minimum bet)
        if (player.getChips() <= 0) {
            handleGameOver(); // Should have been caught earlier, but double-check
            return;
        }

        // Reset hands and prepare for the next deal
        player.getHand().discardHandToDeck(discarded);
        dealer.getHand().discardHandToDeck(discarded);
        player.resetBet(); // Reset Player's internal bet amount for the new round

        prepareInitialDeal(); // Deal new cards
        currentState = GameState.BETTING;
        gameMessage = "Place your bet for the next round. Chips: " + player.getChips();
        dealerCardHidden = true;
        tournamentEndPopupShown = false; // Reset popup flag for the new round

        // Perform initial check again in case deck reshuffle causes issues etc.
        updateTournamentGame();
        // If tournament ended due to status change between rounds, override betting state
        if (currentState == GameState.TOURNAMENT_ENDED_OTHER_WINNER || currentState == GameState.TOURNAMENT_WON_BY_PLAYER) {
            System.out.println("[TournamentLogic] Tournament ended between rounds.");
        }
    }

    private void handleGameOver() {
        // This means the player is out of chips in the tournament
        gameMessage = "Game Over! You are out of chips and eliminated from the tournament.";
        currentState = GameState.GAME_OVER;
        dealerCardHidden = false; // Show final hands
        tournamentEndPopupShown = false; // Allow popup for game over
    }

    public void restartGame() {
        // Restarting typically isn't allowed mid-tournament.
        // This might be repurposed to "Leave Tournament" or handled by BACK_TO_MENU.
        // If restart means restarting the *entire* tournament instance for the player:
        System.out.println("[TournamentLogic] Restart action called - interpretation depends on game design.");
        // For now, let's make it behave like leaving the tournament.
        // The actual navigation back should be handled by the GUI calling Main.returnToMainMenu.
        currentState = GameState.GAME_OVER; // Set state so GUI shows limited options
        gameMessage = "Tournament abandoned. Select 'Main Menu' to leave.";
        tournamentEndPopupShown = false;
    }


    // --- Tournament Win Condition Check ---
    private boolean tournamentEndPopupShown = false; // Flag for GUI popup

    private void updateTournamentGame() {
        // Ensure critical objects exist
        if (this.currentTournament == null || this.player == null || this.currentUser == null) {
            System.err.println("[TournamentLogic] Cannot update tournament state: Missing critical objects.");
            // Potentially set error state if not already handled
            // if (this.currentState != GameState.GAME_OVER) {
            //     handleCriticalError("Critical object null during tournament update.");
            // }
            return;
        }

        // 1. Refresh tournament data from the service
        Tournament refreshedTournament = TournamentService.getTournamentFromName(currentTournament.getName());
        if (refreshedTournament == null) {
            System.err.println("[TournamentLogic] Failed to refresh tournament data for: " + currentTournament.getName());
            // Maybe the tournament was deleted? Treat as game over for this player?
            handleCriticalError("Could not find tournament data during update.");
            return;
        }
        this.currentTournament = refreshedTournament;

        // 2. Check if tournament is already inactive (won by someone else)
        // Use the refreshed status
        if ("inactive".equalsIgnoreCase(this.currentTournament.getStatus())) {
            // Make sure we don't overwrite the winner info if it happened before this player reached target
            if (this.currentState != GameState.TOURNAMENT_ENDED_OTHER_WINNER) {
                System.out.println("[TournamentLogic] Tournament " + currentTournament.getName() + " is now inactive (likely won by another player).");
                this.currentState = GameState.TOURNAMENT_ENDED_OTHER_WINNER;
                this.gameMessage = "Tournament has ended. Winner: " + getWinnerUsernameSafe();
                tournamentEndPopupShown = false; // Allow popup
            }
            return; // Stop further checks if already inactive
        }

        // 3. Check if the CURRENT player has met or exceeded the target chips
        if (player.getChips() >= currentTournament.getTargetChips()) {
            System.out.println("[TournamentLogic] Player " + currentUser.getUsername() + " reached target chips (" + player.getChips() + "/" + currentTournament.getTargetChips() + "). Attempting to claim win...");

            // Attempt to set this player as the winner and make inactive
            boolean winClaimed = TournamentService.setTournamentWinner(currentTournament.getTournamentId(), currentUser.getUserId());

            if (winClaimed) {
                // Successfully claimed the win
                System.out.println("[TournamentLogic] Successfully claimed win for tournament " + currentTournament.getName());
                this.currentState = GameState.TOURNAMENT_WON_BY_PLAYER;
                this.gameMessage = "Congratulations! You won the tournament!";
                tournamentEndPopupShown = false; // Allow popup

                // Refresh again to get the final 'won_by' confirmation if needed
                this.currentTournament = TournamentService.getTournamentFromName(currentTournament.getName());

            } else {
                // Failed to claim win - likely someone else claimed it between check and update (race condition)
                System.err.println("[TournamentLogic] Failed to claim tournament win for " + currentUser.getUsername() + ". Re-checking status...");
                // Re-fetch the tournament status *immediately*
                refreshedTournament = TournamentService.getTournamentFromName(currentTournament.getName());
                if (refreshedTournament != null && "inactive".equalsIgnoreCase(refreshedTournament.getStatus())) {
                    // Confirmed: Someone else won
                    System.out.println("[TournamentLogic] Confirmed: Tournament was won by another player.");
                    this.currentState = GameState.TOURNAMENT_ENDED_OTHER_WINNER;
                    this.currentTournament = refreshedTournament; // Update local ref
                    this.gameMessage = "Tournament ended just before you could claim victory! Winner: " + getWinnerUsernameSafe();
                    tournamentEndPopupShown = false; // Allow popup
                } else {
                    // Failed for another reason (DB error, tournament deleted?)
                    System.err.println("[TournamentLogic] Critical error: Failed to claim win, and tournament is not inactive.");
                    handleCriticalError("Database error during tournament win claim.");
                    // State might remain ROUND_OVER, but with an error message
                    this.gameMessage = "Error claiming tournament win. Please check connection or contact admin.";
                }
            }
        }
        // 4. If no win condition met and tournament still active, state remains as set by round logic (e.g., ROUND_OVER)
    }

    // --- Utility Methods ---
    private void handleCriticalError(String message) {
        System.err.println("CRITICAL ERROR in TournamentGameLogic: " + message);
        this.currentState = GameState.GAME_OVER;
        this.gameMessage = "Internal Error: " + message;
        tournamentEndPopupShown = false; // Allow popup for error
    }

    private String getWinnerUsernameSafe() {
        if (currentTournament == null || currentTournament.getWonBy() <= 0) {
            return "Unknown";
        }
        User winner = UserService.getUserbyId(currentTournament.getWonBy());
        return (winner != null) ? winner.getUsername() : "ID(" + currentTournament.getWonBy() + ")";
    }

    // Helper to check if player can afford double down
    private boolean canDoubleDown() {
        return player != null && player.getHand() != null && player.getChips() >= player.getBet() && player.getHand().getSize() == 2;
    }

    // Helper to check if player can afford insurance
    private boolean canAffordInsurance() {
        return player != null && player.getChips() >= (player.getBet() / 2);
    }

    // Helper to check if surrender is possible (first 2 cards)
    private boolean canSurrender() {
        return player != null && player.getHand() != null && player.getHand().getSize() == 2;
    }

    /**
     * Checks if insurance should be offered based on the dealer's upcard (must be an Ace).
     * @return true if the dealer shows an Ace, false otherwise.
     */
    private boolean canOfferInsurance() {
        // Check for nulls first
        if (dealer == null || dealer.getHand() == null || dealer.getHand().getSize() == 0) {
            return false;
        }
        Card upCard = dealer.getHand().getCard(0);
        if (upCard == null) {
            return false;
        }
        // Compare the String rank to "Ace"
        return "Ace".equals(upCard.getRank());
    }

    // --- Getters ---
    public User getCurrentUser() { return currentUser; }
    public Player getPlayer() { return player; }
    public Dealer getDealer() { return dealer; }
    public GameState getCurrentState() { return currentState; }
    public String getGameMessage() { return gameMessage; }
    public boolean isDealerCardHidden() {
        // Card is hidden during betting, player turn, insurance decision
        // Card is revealed during dealer turn, round over, game over states
        return dealerCardHidden && (currentState == GameState.BETTING ||
                currentState == GameState.PLAYER_TURN ||
                currentState == GameState.INSURANCE_SURRENDER);
    }
    public int getPlayerHandValue() { return (player != null && player.getHand() != null) ? player.getHand().calculatedValue() : 0; }
    public int getDealerHandValue() { return (dealer != null && dealer.getHand() != null) ? dealer.getHand().calculatedValue() : 0; }
    public int getDealerVisibleValue() { return (dealer != null && dealer.getHand() != null && dealer.getHand().getSize() > 0 && dealer.getHand().getCard(0) != null) ? dealer.getHand().getCard(0).getValue() : 0; }
    public List<Card> getPlayerCards() { return (player != null && player.getHand() != null) ? player.getHand().getCards() : Collections.emptyList(); }
    public List<Card> getDealerCards() { return (dealer != null && dealer.getHand() != null) ? dealer.getHand().getCards() : Collections.emptyList(); }
    public String getUsername() { return (currentUser != null) ? currentUser.getUsername() : "Unknown"; }
    public String getTournamentName() { return (currentTournament != null) ? currentTournament.getName() : "Unknown Tournament"; }
    public boolean getTournamentEndPopupShown() { return tournamentEndPopupShown; } // Allow GUI to check flag if needed


    // --- Helper for Player Turn Prompt ---
    private String getPlayerTurnPrompt() {
        // Checks for initial turn options
        StringBuilder prompt = new StringBuilder("Your turn: Hit or Stand");
        if (canDoubleDown()) prompt.append(", Double Down");
        if (canSurrender()) prompt.append(", or Surrender");
        prompt.append("?");
        return prompt.toString();
    }

    // --- Available Actions Logic ---
    public EnumSet<PlayerAction> getAvailableActions() {
        EnumSet<PlayerAction> actions = EnumSet.noneOf(PlayerAction.class);
        // Prevent actions if critical objects are missing
        if (player == null || dealer == null || currentUser == null || currentTournament == null) {
            // Allow exit even in error states
            actions.add(PlayerAction.EXIT);
            return actions;
        }


        switch (currentState) {
            case BETTING:
                // Allow betting only if player has enough for minimum bet (e.g., 50)
                if (player.getChips() >= 50) actions.add(PlayerAction.BET_50);
                if (player.getChips() >= 100) actions.add(PlayerAction.BET_100);
                if (player.getChips() > 0) actions.add(PlayerAction.BET_ALL); // Allow bet all even if < 50
                actions.add(PlayerAction.BACK_TO_MENU); // Allow leaving before betting
                actions.add(PlayerAction.EXIT);
                break;

            case INSURANCE_SURRENDER:
                actions.add(PlayerAction.NEITHER); // Decline Insurance
                if (canOfferInsurance() && canAffordInsurance()) {
                    actions.add(PlayerAction.INSURANCE);
                }
                // Should Surrender be offered here too? Add if needed:
                // if (canSurrender()) actions.add(PlayerAction.SURRENDER);
                actions.add(PlayerAction.BACK_TO_MENU); // Allow leaving
                actions.add(PlayerAction.EXIT);
                break;

            case PLAYER_TURN:
                actions.add(PlayerAction.HIT);
                actions.add(PlayerAction.STAND);
                if (canDoubleDown()) actions.add(PlayerAction.DOUBLE_DOWN);
                if (canSurrender()) actions.add(PlayerAction.SURRENDER);
                // Don't usually allow menu/exit mid-turn, but could add if desired
                break;

            case DEALER_TURN:
                // No player actions typically
                // Could allow EXIT here if desired, but not standard
                break;

            case ROUND_OVER:
                // Ready for next round
                actions.add(PlayerAction.NEXT_ROUND);
                actions.add(PlayerAction.BACK_TO_MENU);
                actions.add(PlayerAction.EXIT);
                break;

            // --- END STATES ---
            case TOURNAMENT_WON_BY_PLAYER:
            case TOURNAMENT_ENDED_OTHER_WINNER:
            case GAME_OVER: // Player eliminated or critical error
                actions.add(PlayerAction.BACK_TO_MENU); // Only allow leaving
                actions.add(PlayerAction.EXIT);
                // Do NOT add NEXT_ROUND or RESTART
                break;

            default:
                // Unexpected state, allow exit
                System.err.println("Warning: Reached unexpected game state: " + currentState);
                actions.add(PlayerAction.EXIT);
                break;
        }
        return actions;
    }
}