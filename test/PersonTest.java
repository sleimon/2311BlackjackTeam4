package Classes;

import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayInputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Scanner;


class PersonTest {
    private Person person;
    private deckOfCards deck;
    private deckOfCards discard;

    @BeforeEach
    void init() {

        person = new person();
        deck = new deckOfCards();
        discard = new deckOfCards();

    }

   
}