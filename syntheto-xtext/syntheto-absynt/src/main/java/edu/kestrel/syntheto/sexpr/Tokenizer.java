/*
 * Copyright (C) 2020 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */

package edu.kestrel.syntheto.sexpr;

import java.io.*;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A simple S-Expression tokenizer, lexing from a stream and providing tokens
 * via .hasNext() and .next() according to the Iterator interface.
 *
 * The Common Lisp reader is complex.  This S-Expression language is simpler in many ways.
 * There are many sentences that the Lisp reader can read but that this language rejects.
 * However, this language also contains some sentences that Common Lisp cannot read
 * or reads differently.
 * Some examples:
 *  - a token consisting solely of 2 or more dots is not a legal symbol or number in
 *    Common Lisp, but lexes as a symbol here.
 *  - a string containing a backslash followed by n, r, t, or u.
 *    \n, \r, \t, or \ uHHHH (ignore the space there, that is so this file parses):
 *    in Common Lisp the backslash is ignored, but here these have the same meaning
 *    as they do in Java.
 * It should be straightforward to write a token checker to see if the token is
 * "Common Lisp compatible", i.e., if Common Lisp can read it and if that will
 * result in an atom with the intended meaning.
 *
 * The token classes are:
 * TokenOpenParen and TokenCloseParen
 * TokenT, TokenNIL
 * TokenDot
 * TokenSymbol (with package prefix)
 *   - We restrict the symbol start characters
 *   - TODO: we might need to handle vertical bars, but all the lowercase
 *           symbols I know of now are represented as strings in the AST constructor syntax.
 *   - TODO: rethink the subclass SExpressionSynthetoSymbol
 * TokenString (delimited by double quotes, with backslash an escape character)
 * TokenInteger, a decimal big signed integer represented as a java.math.BigInteger
 * TokenError, used to describe an IO Error or EOF or lexing problem
  */

public class Tokenizer implements Iterator<Token> {

    /**
     * These are the possible states the Tokenizer can be in.
     */
    enum State {
        NEW, // looking for a new token, e.g. after (, ), or whitespace
        INTEGER, // saw a digit or leading minus sign
        STRING, // inside a string
        STRING_ESCAPE, // inside a string, after seeing a backslash
        STRING_U, // inside a string, after a backslash u , which must be followed by four hex digits
                  // Note that there are actually 4 states, after reading 0,1,2, or 3 hex digits
                  // The 4 states are differentiated with the local variable string_U_NumDigits.
        SYMBOL // reading a symbol, NIL, T, or a consing dot.
    }

    // Here are the characters supported.
    static private final String whitespaceChars = " \r\n\t" ;
    // Warning: if you change the value of whitespaceChars you must also change the corresponding cases
    //          in the switch statements below
    static private final String symbolStartChars = ".:ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static private final String symbolContinueChars = symbolStartChars + ".-";
    // " is the string delimiter
    // \ is an escape for within strings.  After it, we allow these characters:
    static private final String afterBackslashStringChars = "\\\"rntu";
    // Here are the characters that may appear in a string without a backslash:
    static private final String standardStringChars = "\n !#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[]^_`abcdefghijklmnopqrstuvwxyz{|}~";
    // EricM TODO: just added literal newline to standardStringChars so the demo works.
    // This might be OK, but the exact string syntax needs more thought, since the ACL2 side
    // is not currently processing the strings prior to sending them back over the bridge.
    // Java just sees ACL2's literal string syntax, which is not 100% compatible.

    // Helper
    static String chars_to_string(ArrayList<Character> chars) {
        return chars.stream().map(Object::toString).collect(Collectors.joining());
    }

    private PushbackReader pbr;
    private boolean allDone = false;

    @Override
    public boolean hasNext() {
       return !allDone;
    }

    @Override
    public Token next() {

        ArrayList<Character> currentTokenChars;
        currentTokenChars = new ArrayList<Character>();
        State currentState = State.NEW;
        int nextCharInt;
        char nextChar;

        // substate for string backslash u escapes
        // These two initial values are unused.  They are here so the IDE does not complain.
        int string_U_NumDigits = 0;
        ArrayList<Character> string_U_Digits = new ArrayList<Character>();

        while (true) {
            // Read a char.  If IOError or EOF, return a TokenError describing the problem
            try {
                nextCharInt = pbr.read();
            } catch (IOException e) {
                allDone = true;
                return new TokenError("IO Error reading from stream", chars_to_string(currentTokenChars));
            }
            if (nextCharInt == -1) {
                allDone = true;
                return new TokenError("EOF encountered reading from stream", chars_to_string(currentTokenChars));
            }
            nextChar = (char) nextCharInt;
            //System.out.println("next char: " + nextChar);
            //System.out.println("state is " + currentState);
            switch (currentState)
            {
                case NEW:
                    switch (nextChar)
                    {
                        case '(':
                            return TokenOpenParen.get();
                        case ')':
                            return TokenCloseParen.get();
                        // ignore leading whitespace
                        case ' ': case '\r': case '\n': case '\t':
                            break;
                        // start a string token
                        case '"':
                            currentState = State.STRING;
                            break;
                        // integer may start with - or a digit
                        case '-':
                        case '0': case '1': case '2': case '3': case '4':
                        case '5': case '6': case '7': case '8': case '9':
                            currentState = State.INTEGER;
                            currentTokenChars.add(nextChar);
                            break;
                        default:
                            if ( symbolStartChars.indexOf(nextChar) != -1 ) {
                                currentState = State.SYMBOL;
                                currentTokenChars.add(nextChar);
                            } else {
                                return new TokenError("Invalid token start character", String.valueOf(nextChar));
                            }
                            break;
                    }
                    break;
                case INTEGER:
                    switch (nextChar)
                    {
                        // If we are lexing an integer, the following will end the integer
                        // and we have to push them back for the next token.
                        case '(': case ')': case '"':
                            try { pbr.unread(nextChar); }
                            catch (IOException e) { allDone = true; }
                            return new TokenInteger(chars_to_string(currentTokenChars));
                        // Whitespace ends the integer but doesn't need to be pushed back
                        case ' ': case '\r': case '\n': case '\t':
                            return new TokenInteger(chars_to_string(currentTokenChars));
                        // continue the integer
                        case '0': case '1': case '2': case '3': case '4':
                        case '5': case '6': case '7': case '8': case '9':
                            currentTokenChars.add(nextChar);
                            break;
                        // If we are in the middle of an integer, we disallow all other characters not mentioned above.
                        default:
                            currentTokenChars.add(nextChar);
                            return new TokenError("Invalid character encountered while lexing integer: "+nextChar,
                                                    chars_to_string(currentTokenChars));
                    }
                    break;
                case STRING:
                    switch (nextChar)
                    {
                        case '"':
                            return new TokenString(chars_to_string(currentTokenChars));
                        case '\\':
                            currentState = State.STRING_ESCAPE;
                            break;
                        default:
                            if ( standardStringChars.indexOf(nextChar) != -1 ) {
                                currentTokenChars.add(nextChar);
                            } else {
                                return new TokenError("Invalid character (code " + nextCharInt + ") encountered while lexing string",
                                                        chars_to_string(currentTokenChars));
                            }
                            break;
                    }
                    break;
                case STRING_ESCAPE:
                    switch (nextChar)
                    {
                        case 'u':
                            currentState = State.STRING_U;
                            string_U_NumDigits = 0;
                            string_U_Digits = new ArrayList<Character>();
                            break;
                        // Backslash and double-quote must be escaped, but after the escape
                        // they are literal.
                        case '\\': case '"':
                            currentState = State.STRING;
                            currentTokenChars.add(nextChar);
                            break;
                        // The following escapes describe certain common whitespace characters.
                        case 'r':
                            currentState = State.STRING;
                            currentTokenChars.add('\r');
                            break;
                        case 'n':
                            currentState = State.STRING;
                            currentTokenChars.add('\n');
                            break;
                        case 't':
                            currentState = State.STRING;
                            currentTokenChars.add('\t');
                            break;
                        // No other escapes are allowed in our string syntax.
                        default:
                            return new TokenError("Invalid character (code " + nextCharInt + ") after backslash escape while lexing string",
                                                   chars_to_string(currentTokenChars));
                    }
                    break;
                case STRING_U:
                    // We currently only support codes from \u0000 to \u00FF.
                    // Any other codes are errors.
                    boolean isFirst2 = (string_U_NumDigits == 0 || string_U_NumDigits == 1);
                    boolean isHexDigit = (('0' <= nextChar && nextChar <= '9') || ('A' <= nextChar && nextChar <= 'F'));
                    if ((isFirst2 && nextChar=='0')
                        || (!isFirst2 && isHexDigit)) {
                        string_U_NumDigits++;
                        string_U_Digits.add(nextChar);
                    } else {
                        return new TokenError("Invalid hex digit in string unicode escape (code " + nextCharInt + ")",
                                              chars_to_string(currentTokenChars));
                    }
                    if (string_U_NumDigits == 4) {
                        currentState = State.STRING;
                        int codePoint = Integer.parseUnsignedInt(chars_to_string(string_U_Digits), 16);
                        currentTokenChars.add((char) codePoint);
                    }
                    break;
                case SYMBOL:
                    // We don't try to lex out separately the package prefix and the package separator.
                    // For now, ':' is in the symbol continuation characters, and as long as
                    // we see symbol continuation characters, we continue to read the symbol.
                    // Then when the symbol is done, we check that the package prefix makes sense (see below).
                    // TODO: we might need to support vertical bars in the future.
                    if ( symbolContinueChars.indexOf(nextChar) != -1 ) {
                        currentTokenChars.add(nextChar);
                    } else {
                        // If nextChar is not a symbol continuation character, unread it and
                        // return the symbol token.
                        // System.out.println("About to unread " + nextChar);
                        try { pbr.unread(nextChar); }
                        catch (IOException e) {
                            // System.out.println("There was a problem unreading");
                            allDone = true; }
                        // System.out.println("No problem unreading.  CurrentTokenChars: " + chars_to_string(currentTokenChars));
                        return TokenSymbol.ParseSymbol(chars_to_string(currentTokenChars));
                    }
                    break;




            }
        }
    }

    // CEM: not implementing this one now.
    @Override
    public void remove() {

    }

    // CEM: I am not familiar with this sort of parameter type.
    //      I hope this method is not used by anything.
    @Override
    public void forEachRemaining(Consumer<? super Token> action) {
    }

    /**
     * Regular constructor that makes a tokenizer for a given input reader.
     *
     * @param r
     */
    public Tokenizer(Reader r) {
        this.pbr = new PushbackReader(r);

    }
}
