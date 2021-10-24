package edu.kestrel.syntheto.sexpr;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void parseTop1() {
        String input = "(SYNTHETO::MAKE-LITERAL-CHARACTER :VAL (CODE-CHAR 255))";
        Reader r = new StringReader(input);
        Parser p = new Parser(r);
        SExpression l = p.parseTop();
        SExpression expected = SExpression.list(SExpression.syntheto("MAKE-LITERAL-CHARACTER"),
                SExpression.keyword("VAL"), SExpression.character(255));
        assertEquals(expected, l);
        assertEquals(expected.toString(), input);
    }

    @Test
    void parseTop2() {
        String input = "()";
        Reader r = new StringReader(input);
        Parser p = new Parser(r);
        SExpression l = p.parseTop();
        SExpression expected = SExpression.list();
        assertEquals(expected, l);
        assertEquals(expected.toString(), input);
    }

    @Test
    void parseTop3() {
        String input = "(())";
        Reader r = new StringReader(input);
        Parser p = new Parser(r);
        SExpression l = p.parseTop();
        SExpression expected = SExpression.list(SExpression.list());
        assertEquals(expected, l);
        assertEquals(expected.toString(), input);
    }

    @Test
    void parseTop4() {
        String input = "(LIST \"oo\")";
        Reader r = new StringReader(input);
        Parser p = new Parser(r);
        SExpression l = p.parseTop();
        SExpression expected = SExpression.listMaker(SExpression.string("oo"));
        assertEquals(expected, l);
        assertEquals(expected.toString(), input);
    }


    @Test
    void parseTop5() {
        String input = "(A::B :INVARIANT NIL)";
        Reader r = new StringReader(input);
        Parser p = new Parser(r);
        SExpression l = p.parseTop();
        SExpression expected = SExpression.list(SExpression.symbol("A","B"),
                SExpression.keyword("INVARIANT"), SExpression.NIL());
        assertEquals(expected, l);
        assertEquals(expected.toString(), input);
    }

    @Test
    void parseTop6() {
        String input = "(LIST 23847293842793847292323223313232)";
        Reader r = new StringReader(input);
        Parser p = new Parser(r);
        SExpression l = p.parseTop();
        SExpression expected = SExpression.listMaker(SExpression.integer("23847293842793847292323223313232"));
        assertEquals(expected, l);
        assertEquals(expected.toString(), input);
    }

    @Test
    void parseList() {
    }
}