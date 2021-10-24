/*
 * Copyright (C) 2020 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */

package edu.kestrel.syntheto.sexpr;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TokenizerTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void chars_to_string() {
        ArrayList<Character> arr = new ArrayList<Character>();
        arr.add((char) 0); // NUL
        arr.add((char) 16);  // DLE
        arr.add((char) 32); // space
        arr.add((char) 48); // 0
        arr.add((char) 64); // @
        arr.add((char) 0xF7); // DEL
        arr.add((char) 255); // Latin Small Letter Y with diaeresis
        String str = Tokenizer.chars_to_string(arr);
        assertEquals("\u0000\u0010 0@\u00F7ÿ", str);
    }

    @Test
    void next() {
        Reader r = new StringReader("(SYNTHETO::MAKE-LITERAL-CHARACTER :VAL (COMMON-LISP::CODE-CHAR 255))");
        Tokenizer t = new Tokenizer(r);
        Token t1 = t.next();
        assertEquals(t1, TokenOpenParen.get());
        Token t2 = t.next();
        assertEquals(t2, new TokenSymbol("SYNTHETO", "MAKE-LITERAL-CHARACTER"));
        Token t3 = t.next();
        assertEquals(t3, new TokenSymbol("", "VAL"));
        Token t4 = t.next();
        assertEquals(t4, TokenOpenParen.get());
        Token t5 = t.next();
        assertEquals(t5, new TokenSymbol("COMMON-LISP", "CODE-CHAR"));
        Token t6 = t.next();
        assertEquals(t6, new TokenInteger("255"));
        Token t7 = t.next();
        assertEquals(t7, TokenCloseParen.get());
        Token t8 = t.next();
        assertEquals(t8, TokenCloseParen.get());
    }
}