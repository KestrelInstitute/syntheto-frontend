package edu.kestrel.syntheto.sexpr;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SExpressionCharacterTest {

    @Test
    void testToString() {
        SExpressionCharacter sExpressionCharacter = SExpressionCharacter.make('0');
        String charString = sExpressionCharacter.toString();
        assertEquals("(CODE-CHAR 48)", charString);
    }
}