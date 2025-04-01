package com.blackjack.GUI;

import com.blackjack.Models.Card; // Ensure Card model is accessible

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the sequence for an interactive, game-like Blackjack tutorial.
 * Each step includes visuals and the expected user action.
 * Covers betting, hit, stand, bust, push, surrender, insurance (accept/decline), and double down.
 */
public class TutorialScript {

    private List<TutorialStep> steps;
    private int currentStepIndex;

    public TutorialScript() {
        this.steps = new ArrayList<>();
        this.currentStepIndex = 0;
        populateInteractiveScript();
    }

    // Helper method for card creation (ensure it matches your Card constructor and image names)
    // Suits: H=Hearts, D=Diamonds, C=Clubs, S=Spades
    private Card card(String rank, String suit, int value) {
        // Convert short suit to full name if necessary for image loading
        String fullSuit;
        switch (suit.toUpperCase()) {
            case "H":
                fullSuit = "Hearts";
                break;
            case "D":
                fullSuit = "Diamonds";
                break;
            case "C":
                fullSuit = "Clubs";
                break;
            case "S":
                fullSuit = "Spades";
                break;
            default:
                fullSuit = suit; // Assume full name if not recognized abbreviation
        }
        // Assumes Card constructor takes Rank (String), Suit (String), Value (int)
        return new Card(rank, fullSuit, value);
    }


    private void populateInteractiveScript() {
        // --- ROUND 1: Basic Hit/Stand/Push --- (Keep as before)
        steps.add(new TutorialStep("<html><center>Let's play a practice round.<br>Click 'Bet 50' to start.</center></html>", "?", "?", true, TutorialAction.BET_50).addPlayerCard(null).addPlayerCard(null).addDealerCard(null).addDealerCard(null));
        steps.add(new TutorialStep("<html><center>Bet 50 placed. Cards dealt.<br>You: 14. Dealer shows 7.<br>Let's 'Hit'.</center></html>", "14", "7 + ?", true, TutorialAction.HIT).addPlayerCard(card("Ten", "H", 10)).addPlayerCard(card("Four", "C", 4)).addDealerCard(card("Seven", "S", 7)).addDealerCard(null));
        steps.add(new TutorialStep("<html><center>You Hit -> 3. Total: 17.<br>Dealer shows 7.<br>17 is good. Let's 'Stand'.</center></html>", "17", "7 + ?", true, TutorialAction.STAND).addPlayerCard(card("Ten", "H", 10)).addPlayerCard(card("Four", "C", 4)).addPlayerCard(card("Three", "D", 3)).addDealerCard(card("Seven", "S", 7)).addDealerCard(null));
        steps.add(new TutorialStep("<html><center>You Stand. Dealer reveals King.<br>Dealer has 17 (7+10).<br>Dealer stands.</center></html>", "17", "17", false, TutorialAction.NEXT_ROUND).addPlayerCard(card("Ten", "H", 10)).addPlayerCard(card("Four", "C", 4)).addPlayerCard(card("Three", "D", 3)).addDealerCard(card("Seven", "S", 7)).addDealerCard(card("King", "D", 10))); // Implicit dealer stand
        steps.add(new TutorialStep("<html><center>Both have 17 - Push (tie)!<br>Bet returned.<br>Click 'Next Round'.</center></html>", "17", "17", false, TutorialAction.NEXT_ROUND).addPlayerCard(card("Ten", "H", 10)).addPlayerCard(card("Four", "C", 4)).addPlayerCard(card("Three", "D", 3)).addDealerCard(card("Seven", "S", 7)).addDealerCard(card("King", "D", 10)));

        // --- ROUND 2: Bust Example --- (Keep as before)
        steps.add(new TutorialStep("<html><center>New Round! Place your bet.<br>Let's try 'Bet 100'.</center></html>", "?", "?", true, TutorialAction.BET_100).addPlayerCard(null).addPlayerCard(null).addDealerCard(null).addDealerCard(null));
        steps.add(new TutorialStep("<html><center>Bet 100. Cards dealt.<br>You: 12. Dealer shows Queen.<br>Let's 'Hit'.</center></html>", "12", "10 + ?", true, TutorialAction.HIT).addPlayerCard(card("Two", "H", 2)).addPlayerCard(card("King", "C", 10)).addDealerCard(card("Queen", "D", 10)).addDealerCard(null));
        steps.add(new TutorialStep("<html><center>You Hit -> Jack. Total 22 - Bust!<br>You lose.<br>Click 'Next Round'.</center></html>", "Bust (22)", "10 + ?", true, TutorialAction.NEXT_ROUND).addPlayerCard(card("Two", "H", 2)).addPlayerCard(card("King", "C", 10)).addPlayerCard(card("Jack", "S", 10)).addDealerCard(card("Queen", "D", 10)).addDealerCard(null));

        // --- ROUND 3: SURRENDER Example --- (Keep as before)
        steps.add(new TutorialStep("<html><center>Next Round! 'Bet 50'.</center></html>", "?", "?", true, TutorialAction.BET_50).addPlayerCard(null).addPlayerCard(null).addDealerCard(null).addDealerCard(null));
        steps.add(new TutorialStep("<html><center>Bet 50. Cards dealt.<br>You have 16 (bad!). Dealer shows Ten.<br>SURRENDER lets you quit the hand now, losing only HALF your bet. Available ONLY on your first two cards.<br>Click 'Surrender'.</center></html>", "16", "10 + ?", true, TutorialAction.SURRENDER)
                .addPlayerCard(card("Ten", "S", 10)).addPlayerCard(card("Six", "H", 6))
                .addDealerCard(card("Ten", "C", 10)).addDealerCard(null));
        steps.add(new TutorialStep("<html><center>You Surrendered!<br>You lose half your bet (25). Round ends.<br>Click 'Next Round'.</center></html>", "16", "10 + ?", false, TutorialAction.NEXT_ROUND) // Reveal dealer card for info
                .addPlayerCard(card("Ten", "S", 10)).addPlayerCard(card("Six", "H", 6))
                .addDealerCard(card("Ten", "C", 10)).addDealerCard(card("Seven", "D", 7)));

        // --- ROUND 4: INSURANCE Example (Accept & Win) --- (Keep as before, except outcome message)
        steps.add(new TutorialStep("<html><center>Next Round! 'Bet 100'.</center></html>", "?", "?", true, TutorialAction.BET_100).addPlayerCard(null).addPlayerCard(null).addDealerCard(null).addDealerCard(null));
        steps.add(new TutorialStep("<html><center>Bet 100. Cards dealt.<br>You: 19. Dealer shows an ACE!<br>Dealer might have Blackjack (Ace+10). INSURANCE is a side bet against this.<br>Click 'Insurance'.</center></html>", "19", "Ace (11) + ?", true, TutorialAction.INSURANCE)
                .addPlayerCard(card("Nine", "D", 9)).addPlayerCard(card("King", "S", 10))
                .addDealerCard(card("Ace", "H", 11)).addDealerCard(null));
        steps.add(new TutorialStep("<html><center>Insurance costs half your main bet (50 here).<br>It pays 2:1 ONLY if the Dealer *has* Blackjack.<br>You clicked 'Insurance'. Dealer checks...</center></html>", "19", "Ace (11) + ?", true, TutorialAction.NEXT_ROUND) // Show message then auto-advance to outcome
                .addPlayerCard(card("Nine", "D", 9)).addPlayerCard(card("King", "S", 10))
                .addDealerCard(card("Ace", "H", 11)).addDealerCard(null)); // Keep hole card hidden until reveal

        // **** THIS IS THE MODIFIED STEP ****
        steps.add(new TutorialStep(
                "<html><center>Dealer checks... Blackjack (Ace+Queen)!<br>Main Bet (-100) lost.<br>Insurance Bet (+100) won!<br>Net: 0. Round Over.<br>Click 'Next Round'.</center></html>", // Shortened & more breaks
                "19", "Blackjack (21)", false, TutorialAction.NEXT_ROUND)
                .addPlayerCard(card("Nine", "D", 9)).addPlayerCard(card("King", "S", 10))
                .addDealerCard(card("Ace", "H", 11)).addDealerCard(card("Queen", "C", 10)));
        // **** END OF MODIFIED STEP ****

        // --- ROUND 5: INSURANCE Example (Decline & Win) --- (Keep as before)
        steps.add(new TutorialStep("<html><center>Next Round! 'Bet 50'.</center></html>", "?", "?", true, TutorialAction.BET_50).addPlayerCard(null).addPlayerCard(null).addDealerCard(null).addDealerCard(null));
        steps.add(new TutorialStep("<html><center>Bet 50. Cards dealt.<br>You have 20! Dealer shows an ACE again.<br>Offer 'Insurance' or 'Decline Ins.'?<br>Click 'Decline Ins.' (often shown as 'Neither').</center></html>", "20", "Ace (11) + ?", true, TutorialAction.NEITHER)
                .addPlayerCard(card("Ten", "D", 10)).addPlayerCard(card("Ten", "C", 10))
                .addDealerCard(card("Ace", "S", 11)).addDealerCard(null));
        steps.add(new TutorialStep("<html><center>Insurance Declined. Good choice with 20!<br>Dealer checks... No Blackjack (Ace+6=17).<br>If you'd taken Insurance, you'd lose that side bet.<br>Now, your turn. Click 'Stand'.</center></html>", "20", "17", true, TutorialAction.STAND) // Dealer revealed hole card to check, now player turn
                .addPlayerCard(card("Ten", "D", 10)).addPlayerCard(card("Ten", "C", 10))
                .addDealerCard(card("Ace", "S", 11)).addDealerCard(card("Six", "C", 6)));
        steps.add(new TutorialStep("<html><center>You Stand on 20. Dealer has 17 and must Stand.<br>You Win (20 vs 17)!<br>Click 'Next Round'.</center></html>", "20", "17", false, TutorialAction.NEXT_ROUND)
                .addPlayerCard(card("Ten", "D", 10)).addPlayerCard(card("Ten", "C", 10))
                .addDealerCard(card("Ace", "S", 11)).addDealerCard(card("Six", "C", 6)));

        // --- ROUND 6: DOUBLE DOWN Example --- (Keep as before)
        steps.add(new TutorialStep("<html><center>Next Round! 'Bet 100'.</center></html>", "?", "?", true, TutorialAction.BET_100).addPlayerCard(null).addPlayerCard(null).addDealerCard(null).addDealerCard(null));
        steps.add(new TutorialStep("<html><center>Bet 100. Cards dealt.<br>You have 11 (6+5)! Dealer shows 5 (weak).<br>DOUBLE DOWN: Double your bet, get ONE more card, then auto-Stand. Good strategy here!<br>Click 'Double Down'.</center></html>", "11", "5 + ?", true, TutorialAction.DOUBLE_DOWN)
                .addPlayerCard(card("Six", "S", 6)).addPlayerCard(card("Five", "H", 5))
                .addDealerCard(card("Five", "C", 5)).addDealerCard(null));
        steps.add(new TutorialStep("<html><center>You Doubled Down! Bet is now 200.<br>You get one card...</center></html>", "11", "5 + ?", true, TutorialAction.NEXT_ROUND) // Show message then auto-advance to card reveal + dealer play
                .addPlayerCard(card("Six", "S", 6)).addPlayerCard(card("Five", "H", 5))
                .addDealerCard(card("Five", "C", 5)).addDealerCard(null)); // Keep hole hidden until reveal
        steps.add(new TutorialStep("<html><center>Your card is a 9. Final Hand: 20 (11 + 9). You Stand.<br>Dealer reveals King (5+10=15). Hits -> 3. Total 18.<br>Dealer Stands.</center></html>", "20", "18", false, TutorialAction.NEXT_ROUND) // Expect Next Round after showing full resolution
                .addPlayerCard(card("Six", "S", 6)).addPlayerCard(card("Five", "H", 5)).addPlayerCard(card("Nine", "C", 9)) // DD card
                .addDealerCard(card("Five", "C", 5)).addDealerCard(card("King", "H", 10)).addDealerCard(card("Three", "S", 3))); // Dealer cards
        steps.add(new TutorialStep("<html><center>You Win (20 vs 18)! Your doubled bet pays out.<br>Click 'Next Round'.</center></html>", "20", "18", false, TutorialAction.NEXT_ROUND) // Expect Next Round
                .addPlayerCard(card("Six", "S", 6)).addPlayerCard(card("Five", "H", 5)).addPlayerCard(card("Nine", "C", 9))
                .addDealerCard(card("Five", "C", 5)).addDealerCard(card("King", "H", 10)).addDealerCard(card("Three", "S", 3)));


        // --- TUTORIAL END ---
        steps.add(new TutorialStep("<html><center>That concludes the tutorial!<br>You've practiced Betting, Hit, Stand, Bust, Push,<br>Surrender, Insurance, Decline Insurance, and Double Down.<br>Click 'Exit Tutorial' to return to the main menu.</center></html>", "?", "?", true, TutorialAction.EXIT)
                .addPlayerCard(null).addPlayerCard(null).addDealerCard(null).addDealerCard(null));
    }

    // --- Methods for navigating script ---
    public TutorialStep getCurrentStep() {
        if (currentStepIndex >= 0 && currentStepIndex < steps.size()) {
            return steps.get(currentStepIndex);
        }
        return null;
    }

    public boolean advanceStep() {
        if (currentStepIndex < steps.size() - 1) {
            currentStepIndex++;
            return true;
        }
        return false;
    }

    public boolean previousStep() {
        if (currentStepIndex > 0) {
            currentStepIndex--;
            return true;
        }
        return false;
    }

    public boolean isAtEnd() {
        return currentStepIndex == steps.size() - 1;
    }

    public void reset() {
        currentStepIndex = 0;
    }
}