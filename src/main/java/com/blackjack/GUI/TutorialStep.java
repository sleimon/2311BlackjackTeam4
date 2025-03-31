package com.blackjack.GUI;

import com.blackjack.Models.Card;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a single step in the interactive Blackjack tutorial.
 * Includes the visual state and the action the user is expected to take.
 */
public class TutorialStep {
    private String message;
    private List<Card> playerCards;
    private List<Card> dealerCards;
    private String playerValueText;
    private String dealerValueText;
    private boolean dealerCardHidden;
    private TutorialAction expectedAction; // The action user must perform

    // Constructor now requires the expected action
    public TutorialStep(String message, String playerValueText, String dealerValueText,
                        boolean dealerCardHidden, TutorialAction expectedAction) {
        this.message = message;
        this.playerCards = new ArrayList<>();
        this.dealerCards = new ArrayList<>();
        this.playerValueText = playerValueText;
        this.dealerValueText = dealerValueText;
        this.dealerCardHidden = dealerCardHidden;
        this.expectedAction = expectedAction;
    }

    // --- Getters ---
    public String getMessage() { return message; }
    public List<Card> getPlayerCards() { return playerCards; }
    public List<Card> getDealerCards() { return dealerCards; }
    public String getPlayerValueText() { return playerValueText; }
    public String getDealerValueText() { return dealerValueText; }
    public boolean isDealerCardHidden() { return dealerCardHidden; }
    public TutorialAction getExpectedAction() { return expectedAction; }


    public TutorialStep addPlayerCard(Card card) {
        this.playerCards.add(card); // Allow null
        return this;
    }

    public TutorialStep addDealerCard(Card card) {
        this.dealerCards.add(card); // Allow null
        return this;
    }
}