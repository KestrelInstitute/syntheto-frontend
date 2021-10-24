package edu.kestrel.syntheto.sexpr;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class SExpressionListTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void makeProper() {
        SExpression sexpr1 = SExpressionInteger.make(BigInteger.TEN);
        SExpression sexpr2 = SExpressionInteger.make(BigInteger.valueOf(2));
        SExpression sexpr3 = SExpressionInteger.make(BigInteger.ONE);
        SExpressionList sExpressionList = SExpressionList.makeProper(sexpr1,sexpr2,sexpr3);
        assertEquals("(10 2 1)", sExpressionList.toString());

    }

    @Test
    void makeDotted() {
    }

    @Test
    void testToString() {
    }

    @Test
    void testEquals() {
    }
}