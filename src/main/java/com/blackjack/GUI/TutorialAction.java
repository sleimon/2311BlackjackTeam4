package com.blackjack.GUI;

/**
 * Represents the specific action expected from the user
 * during an interactive tutorial step.
 */
public enum TutorialAction {
    START_TUTORIAL, // Can be used for initial state if needed, though popup handles start now
    BET_50,
    BET_100,
    BET_ALL,
    HIT,
    STAND,
    DOUBLE_DOWN,
    SURRENDER,
    INSURANCE,
    NEITHER,        // Decline Insurance
    NEXT_ROUND,
    RESTART,        // Keep if needed, though tutorial script doesn't use it currently
    EXIT
}