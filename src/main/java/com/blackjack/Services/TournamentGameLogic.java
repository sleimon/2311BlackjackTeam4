package com.blackjack.Services;

import com.blackjack.stubdatabase.StubDatabase;
import com.blackjack.Main;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import com.blackjack.Models.*;

public class TournamentGameLogic {

    // --- Fields ---
    private User currentUser; // Master record for stats/chips
    private deckOfCards deck;
    private deckOfCards discarded;
    private Dealer dealer; // Assuming Dealer extends Person
    private Player player; // Assuming Player extends Person
    private GameState currentState;
    private String gameMessage;
    private boolean dealerCardHidden;
    public Tournament currentTournament;


    // --- Enums ---
    public enum GameState {
        BETTING,
        INSURANCE_SURRENDER, // For Insurance decision vs Ace/Ten
        PLAYER_TURN,
        DEALER_TURN,
        ROUND_OVER,
        GAME_OVER
    }

    public enum PlayerAction {
        HIT, STAND, DOUBLE_DOWN,
        SURRENDER,       // Action available ONLY on first 2 cards
        INSURANCE,       // Action available during INSURANCE_SURRENDER
        NEITHER,         // Action (decline insurance) available during INSURANCE_SURRENDER
        BET_50, BET_100, BET_ALL,
        NEXT_ROUND, RESTART, EXIT
    }

    // --- Constructor ---
    public TournamentGameLogic(String username, String currentTournamentName) {
        loadOrCreateUser(username);
        this.currentTournament = TournamentService.getTournamentFromName(currentTournamentName);
        if (this.currentUser != null) {
            this.player = new Player(1000);
        } else {
            System.err.println("FATAL: Cannot initialize GameLogic, currentUser is null.");
            this.player = new Player(0); // Avoid null pointer
        }
        this.dealer = new Dealer();
        this.deck = new deckOfCards();
        this.discarded = new deckOfCards();
        this.discarded.emptyDeck();
        this.deck.shuffle();
        this.currentState = GameState.BETTING;
        this.gameMessage = "Place your bet.";
        this.dealerCardHidden = true;
        if (this.player != null) {
            prepareInitialDeal();
        }
    }



    // --- User Loading ---
    private void loadOrCreateUser(String username) {
        this.currentUser = Main.useStubDatabase ? StubDatabase.getUser(username) : UserService.getUser(username);
        if (currentUser == null) {
            System.out.println("User not found, creating new user: " + username);
            this.currentUser = new User(username, "defaultPass", 1000, 0, 0, 0); // Use default pass
            if (Main.useStubDatabase) { StubDatabase.addUser(currentUser); }
            else { if (!UserService.addUser(currentUser)) { System.err.println("Failed to add new user!"); this.currentUser = null; } }
        }
        if (this.currentUser != null) {
            System.out.println("Loaded User: " + currentUser.getUsername() + " Chips: " + currentUser.getChips());
        }
    }

    // --- Game Setup ---
    public void prepareInitialDeal() {
        if (deck == null || discarded == null || player == null || dealer == null || player.getHand() == null || dealer.getHand() == null) {
            System.err.println("Error preparing deal: Game objects not initialized."); return;
        }
        if (deck.cardsLeft() < 4) { deck.reloadDeckFromDiscard(discarded); }
        player.getHand().discardHandToDeck(discarded);
        dealer.getHand().discardHandToDeck(discarded);
        dealer.getHand().takeCardFromDeck(deck); dealer.getHand().takeCardFromDeck(deck);
        player.getHand().takeCardFromDeck(deck); player.getHand().takeCardFromDeck(deck);
        System.out.println("[GameLogic] Cards dealt for new round.");
        this.dealerCardHidden = true;
    }

    // --- Helper Method for Natural Blackjack ---
    private boolean isNaturalBlackjack(Person person) {
        return person != null && person.getHand() != null &&
                person.getHand().getSize() == 2 && person.getHand().calculatedValue() == 21;
    }

    // --- Betting Phase ---
    public void placeBet(int amount) {
        if (currentState != GameState.BETTING) return;
        if (amount <= 0 || amount > currentUser.getChips()) { gameMessage = "Invalid bet amount."; return; }
        player.placeBet(amount); updateTournamentGame();
        gameMessage = "Bet placed: " + amount + "."; dealerCardHidden = true;

        // State Transition Logic
        boolean playerHasNaturalBj = isNaturalBlackjack(player);
        int dealerUpCardValue = 0; Card dealerUpCard = null;
        if (dealer.getHand() != null && dealer.getHand().getSize() > 0) { dealerUpCard = dealer.getHand().getCard(0); dealerUpCardValue = dealerUpCard.getValue(); }
        else { System.err.println("Error: Dealer has no cards after deal!"); return; }
        boolean dealerShowsAce = (dealerUpCardValue == 11);
        boolean dealerShowsTenValue = (dealerUpCardValue == 10);
        boolean offerInsuranceOpportunity = dealerShowsAce || dealerShowsTenValue;

        if (playerHasNaturalBj) { handleInitialPlayerBlackjack(); }
        else if (offerInsuranceOpportunity) {
            currentState = GameState.INSURANCE_SURRENDER; // State for Insurance decision
            String affordabilityText = player.getChips() >= player.getBet() ? "" : " (Cannot Afford)";
            if (dealerShowsAce) gameMessage = "Dealer showing Ace. Insurance offered." + affordabilityText;
            else gameMessage = "Dealer showing " + dealerUpCard.getRank() + ". Insurance offered." + affordabilityText;
            dealerCardHidden = true;
        } else { // Go directly to player's turn
            currentState = GameState.PLAYER_TURN;
            gameMessage = getPlayerTurnPrompt(); // Set initial prompt using helper
            dealerCardHidden = true;
        }
    }

    // --- Insurance Phase Actions ---
    public void requestInsurance() {
        if (currentState != GameState.INSURANCE_SURRENDER) return;
        if (player.getChips() >= player.getBet()) { // Affordability for non-std insurance
            player.insuranceBet(); updateTournamentGame();
            if (isNaturalBlackjack(dealer)) {
                player.winInsurance(); currentUser.setWins(currentUser.getWins() + 1);
                gameMessage = "Insurance Won!";
                updateTournamentGame(); /*saveUserData();*/ currentState = GameState.ROUND_OVER; dealerCardHidden = false;
            } else {
                player.loseInsurance(); updateTournamentGame();
                gameMessage = "Insurance Lost.";
                currentState = GameState.PLAYER_TURN; // Go to Player Turn
                gameMessage += " " + getPlayerTurnPrompt(); // Set prompt for start of player turn
                dealerCardHidden = true;
            }
        } else { gameMessage = "Not enough chips for Insurance."; }
    }
    public void declineInsurance() {
        if (currentState != GameState.INSURANCE_SURRENDER) return;
        gameMessage = "Insurance declined.";
        if (isNaturalBlackjack(dealer)) {
            gameMessage += " Dealer reveals Blackjack!"; handleDealerBlackjack();
        } else {
            gameMessage += " Dealer does not have Blackjack.";
            currentState = GameState.PLAYER_TURN; // Go to Player Turn
            gameMessage += " " + getPlayerTurnPrompt(); // Set prompt for start of player turn
            dealerCardHidden = true;
        }
    }

    // --- Player Turn Actions ---
    public void hit() {
        if (currentState != GameState.PLAYER_TURN) return;
        player.hit(deck, discarded);
        // After hitting, Surrender and Double Down are no longer available
        gameMessage = "Player Hits. Hit or Stand?";
        if (player.getHand().calculatedValue() > 21) { handlePlayerBust(); }
        else if (player.getHand().calculatedValue() == 21) { gameMessage = "Player has 21!"; stand(); }
        // No need to update prompt otherwise, next action updates GUI
    }
    public void stand() {
        if (currentState != GameState.PLAYER_TURN) return;
        currentState = GameState.DEALER_TURN;
        gameMessage = "Player Stands. Dealer's turn.";
        dealerCardHidden = false;
        executeDealerTurn();
    }
    public void doubleDown() {
        // Only allowed on first two cards
        if (currentState != GameState.PLAYER_TURN || player.getHand().getSize() > 2) {
            gameMessage = "Can only Double Down on first two cards."; return;
        }
        if (player.getChips() >= player.getBet()) { // Check affordability
            player.doubleDown(); updateTournamentGame();
            player.hit(deck, discarded);
            gameMessage = "Player Doubled Down.";
            if (player.getHand().calculatedValue() > 21) { handlePlayerBust(); }
            else { currentState = GameState.DEALER_TURN; dealerCardHidden = false; gameMessage += " Dealer's turn."; executeDealerTurn(); }
        } else { gameMessage = "Not enough chips to Double Down."; }
    }
    public void surrender() {
        // Only allowed on first two cards
        if (currentState != GameState.PLAYER_TURN || player.getHand().getSize() > 2) {
            gameMessage = "Can only surrender on first two cards."; return;
        }
        player.surrenderBet(); currentUser.setLosses(currentUser.getLosses() + 1);
        updateTournamentGame();
        //        updateUserChips(); saveUserData();
        gameMessage = "Player Surrendered. Half bet returned.";
        currentState = GameState.ROUND_OVER; dealerCardHidden = false;
    }

    // --- Dealer's Turn and Round Resolution ---
    private void executeDealerTurn() {
        if (currentState != GameState.DEALER_TURN) return;
        dealerCardHidden = false;
        if (isNaturalBlackjack(dealer)) { handleDealerBlackjack(); return; }
        while (dealer.getHand().calculatedValue() < 17 || (dealer.getHand().calculatedValue() == 17 && dealer.getHand().isSoft())) {
            dealer.hit(deck, discarded);
        }
        determineOutcome();
    }
    private void determineOutcome() {
        if (currentState != GameState.DEALER_TURN) return;
        int dealerValue = dealer.getHand().calculatedValue();
        int playerValue = player.getHand().calculatedValue();
        int finalPlayerBet = player.getBet();

        if (dealerValue > 21) { gameMessage = "Dealer Busts! You win!"; player.winBet(); currentUser.setWins(currentUser.getWins() + 1); }
        else if (dealerValue > playerValue) { gameMessage = "Dealer wins!"; player.loseBet(); currentUser.setLosses(currentUser.getLosses() + 1); }
        else if (playerValue > dealerValue) { gameMessage = "You win!"; player.winBet(); currentUser.setWins(currentUser.getWins() + 1); }
        else { gameMessage = "Push!"; player.pushBet(); currentUser.setPushes(currentUser.getPushes() + 1); }
        updateTournamentGame();
//        updateUserChips(); saveUserData();
        currentState = GameState.ROUND_OVER;
        if (currentUser.getChips() <= 0) { handleGameOver(); }
    }

    // --- Blackjack/Bust Handling ---
    private void handleInitialPlayerBlackjack() {
        dealerCardHidden = false;
        if (isNaturalBlackjack(dealer)) { player.pushBet(); currentUser.setPushes(currentUser.getPushes() + 1); gameMessage = "Push! Both have Natural Blackjack!"; }
        else { player.instant21(); currentUser.setWins(currentUser.getWins() + 1); gameMessage = "Player Blackjack! You win!"; }
        updateTournamentGame();
        //        updateUserChips(); saveUserData();
        currentState = GameState.ROUND_OVER;
    }
    private void handleDealerBlackjack() {
        dealerCardHidden = false; player.loseBet(); currentUser.setLosses(currentUser.getLosses() + 1);
        gameMessage = (gameMessage.contains("Insurance declined") ? gameMessage + " " : "") + "Dealer Blackjack! You lose.";
        updateTournamentGame();
        //        updateUserChips(); saveUserData();
        currentState = GameState.ROUND_OVER;
    }
    private void handlePlayerBust() {
        player.loseBet(); currentUser.setLosses(currentUser.getLosses() + 1);
        updateTournamentGame();
        //        updateUserChips(); saveUserData();
        gameMessage = "Player Busts! You lose.";
        currentState = GameState.ROUND_OVER; dealerCardHidden = false;
    }

    // --- Round/Game Lifecycle ---
    public void nextRound() {
        if (currentState != GameState.ROUND_OVER) return;
        if (currentUser.getChips() <= 0) { handleGameOver(); return; }
        player.getHand().discardHandToDeck(discarded); dealer.getHand().discardHandToDeck(discarded);
        player.resetBet(); // Reset Player's internal bet state
        prepareInitialDeal(); currentState = GameState.BETTING;
        gameMessage = "Place your bet for the next round."; dealerCardHidden = true;
    }
    private void handleGameOver() { gameMessage = "Game Over! No more chips."; currentState = GameState.GAME_OVER; }
    public void restartGame() {
        currentUser.setChips(1000); currentUser.setWins(0); currentUser.setLosses(0); currentUser.setPushes(0);
//        saveUserData();
        this.player = new Player(currentUser.getChips()); this.dealer = new Dealer();
        this.deck = new deckOfCards(); this.discarded = new deckOfCards();
        this.discarded.emptyDeck(); this.deck.shuffle();
        prepareInitialDeal(); currentState = GameState.BETTING;
        gameMessage = "Game Restarted. Place your bet."; dealerCardHidden = true;
    }

    // --- Utility Methods ---
//    private void updateUserChips() {
//        if(player != null && currentUser != null) currentUser.setChips(player.getChips()); }
//    private void saveUserData() {
//        if (currentUser == null) return;
//        if (Main.useStubDatabase) {
//            StubDatabase.updateUser(currentUser);
//        }
//        else {
//            UserService.updateUser(currentUser);
//        }
//    }

    private void updateTournamentGame() {
        // Refresh tournament data
        this.currentTournament = TournamentService.getTournamentFromName(currentTournament.getName());

        if (TournamentService.isTournamentInactive(currentTournament.getTournamentId())) {
            System.out.println("[Tournament] Tournament already won by: " + UserService.getUser(currentTournament.getWonBy()).getUsername());
            return;
        }

        // Check if player has met or exceeded the target chips
        if (player.getChips() >= currentTournament.getTargetChips()) {
            TournamentService.setTournamentWinner(currentTournament.getTournamentId(), UserService.getUser(currentUser.getUsername()).getUserId());
            TournamentService.updateTournamentStatus(currentTournament.getTournamentId(), "inactive");
            System.out.println("[Tournament] You have won the tournament. Congrats!");
        }
    }



    // --- Getters ---
    public User getCurrentUser() { return currentUser; }
    public Player getPlayer() { return player; }
    public Dealer getDealer() { return dealer; }
    public GameState getCurrentState() { return currentState; }
    public String getGameMessage() { return gameMessage; }
    public boolean isDealerCardHidden() { return dealerCardHidden && currentState != GameState.DEALER_TURN && currentState != GameState.ROUND_OVER && currentState != GameState.GAME_OVER; }
    public int getPlayerHandValue() { return (player != null && player.getHand() != null) ? player.getHand().calculatedValue() : 0; }
    public int getDealerHandValue() { return (dealer != null && dealer.getHand() != null) ? dealer.getHand().calculatedValue() : 0; }
    public int getDealerVisibleValue() { return (dealer != null && dealer.getHand() != null && dealer.getHand().getSize() > 0 && dealer.getHand().getCard(0) != null) ? dealer.getHand().getCard(0).getValue() : 0; }
    public List<Card> getPlayerCards() { return (player != null && player.getHand() != null) ? player.getHand().getCards() : Collections.emptyList(); }
    public List<Card> getDealerCards() { return (dealer != null && dealer.getHand() != null) ? dealer.getHand().getCards() : Collections.emptyList(); }

    // --- Helper for Player Turn Prompt ---
    private String getPlayerTurnPrompt() {
        // Checks for initial turn options
        boolean canDouble = (player.getHand().getSize() == 2 && player.getChips() >= player.getBet());
        boolean canSurrender = (player.getHand().getSize() == 2); // Only on first 2 cards

        StringBuilder prompt = new StringBuilder("Hit or Stand");
        if (canDouble) prompt.append(", Double Down");
        if (canSurrender) prompt.append(", or Surrender"); // Changed phrasing slightly
        prompt.append("?");
        return prompt.toString();
    }

    // --- Available Actions Logic ---
    public EnumSet<PlayerAction> getAvailableActions() {
        EnumSet<PlayerAction> actions = EnumSet.noneOf(PlayerAction.class);
        if (player == null || dealer == null || player.getHand() == null || dealer.getHand() == null || currentUser == null) return actions;

        switch (currentState) {
            case BETTING:
                if (currentUser.getChips() >= 50) actions.add(PlayerAction.BET_50);
                if (currentUser.getChips() >= 100) actions.add(PlayerAction.BET_100);
                if (currentUser.getChips() > 0) actions.add(PlayerAction.BET_ALL);
                break;

            case INSURANCE_SURRENDER: // Only for Insurance decision
                actions.add(PlayerAction.NEITHER); // Decline Insurance
                int dealerUpVal = (dealer.getHand().getSize() > 0) ? dealer.getHand().getCard(0).getValue() : 0;
                boolean canAffordIns = player.getChips() >= player.getBet(); // Non-std check
                boolean offerIns = (dealerUpVal == 11 || dealerUpVal == 10);
                if (offerIns && canAffordIns) actions.add(PlayerAction.INSURANCE);
                break;

            case PLAYER_TURN:
                actions.add(PlayerAction.HIT);
                actions.add(PlayerAction.STAND);

                // Double down (Rule: first 2 cards & affordable)
                if (player.getHand().getSize() == 2 && player.getChips() >= player.getBet()) {
                    actions.add(PlayerAction.DOUBLE_DOWN);
                }
                // Surrender (Rule: first 2 cards ONLY)
                if (player.getHand().getSize() == 2) {
                    actions.add(PlayerAction.SURRENDER);
                }
                break;

            case DEALER_TURN: break; // No player actions

            case ROUND_OVER:
                if (currentUser.getChips() > 0) actions.add(PlayerAction.NEXT_ROUND);
                else { actions.add(PlayerAction.RESTART); actions.add(PlayerAction.EXIT); }
                break;

            case GAME_OVER:
                actions.add(PlayerAction.RESTART); actions.add(PlayerAction.EXIT);
                break;
        }
        return actions;
    }
}