package Classes;

import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayInputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Scanner;


class GameTest {
    private Game game;

    @BeforeEach
    void setUp() {
        game = new Game(100); // Initializing with 100 chips
    }

    @Test
    void testPlaceBet() {
        String input = "20\n"; // Simulate user input of betting 20 chips
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        game.placeBet();
        assertEquals(80, game.getPlayer().getChips(), "Player should have 80 chips after betting 20.");
    }
}

class HandTest {
    private Hand hand;
    private deckOfCards deck;

    @BeforeEach
    void setUp() {
        hand = new Hand();
        deck = new deckOfCards();
    }

    @Test
    void testCalculatedValue() {
        hand.getHand().add(new Card("Hearts", 10)); // Add a 10-value card
        hand.getHand().add(new Card("Spades", 11)); // Add an Ace
        assertEquals(21, hand.calculatedValue(), "Hand should be valued at 21.");
    }
}

class HandDeckInteractionTest {
    private Hand hand;
    private deckOfCards deck;

    @BeforeEach
    void setUp() {
        hand = new Hand();
        deck = new deckOfCards();
        deck.shuffle();
    }

    @Test
    void testTakeCardFromDeck() {
        int initialDeckSize = deck.cardsLeft();
        hand.takeCardFromDeck(deck);
        assertEquals(initialDeckSize - 1, deck.cardsLeft(), "Deck size should decrease by one after drawing a card.");
        assertEquals(1, hand.getHand().size(), "Hand should have one card after drawing.");
    }