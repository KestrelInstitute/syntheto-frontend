/*
 * Copyright (C) 2020 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */

package edu.kestrel.syntheto.sexpr;

import java.io.*;
import java.math.BigInteger;
import java.util.List;
import java.util.LinkedList;


/**
 * A simple S-Expression parser for Syntheto
 * (make-.. ) constructors.
 * To use, in a try with catch of SExprParser.ParseException:
 * (1) given a Reader {@code r}, make an instance of this class
 *     {@code p = new Parser(r)}
 * (2) call {@code p.parseList()}
 */

public class Parser {
 
    private Tokenizer tokenizer;

    public SExpression parseTop() {
        Token firstToken = tokenizer.next();
        //System.out.println("firstToken: " + firstToken.toString());
        if (!(firstToken instanceof TokenOpenParen)) {
            throw new IllegalArgumentException("top-level list must start with open parenthesis");
        }
        return parseList(1);
    }

    public SExpression parseList(int depth) {
        // parses the rest of a list after an open parenthesis
        Token nextToken = tokenizer.next();
        //System.out.println("nextToken: " + nextToken.toString());

        // thisListElements may contain a mixture of SExpression and Token objects.
        List<Object> thisListElements = new LinkedList<>();

        while (true) {
            if (nextToken instanceof TokenEOF) {
                throw new IllegalArgumentException("EOF in middle of reading list");
            } else if (nextToken instanceof TokenError) {
                throw new IllegalArgumentException("Error in middle of reading list");
            } else if (nextToken instanceof TokenOpenParen) {
                thisListElements.add(parseList(depth + 1));
            } else if (nextToken instanceof TokenCloseParen) {
                // TODO Things we need to do here:
                // * check list for (CODE-CHAR num)
                // * check for dotted cdr
                // * maybe check depth?  maybe not.
                // * ccnvert tokens to SExpressions
                return finishList(thisListElements);
            } else {
                // we can accumulate the other Token types
                thisListElements.add(nextToken);
            }
            nextToken = tokenizer.next();
        }
    }

    private int validCharCode (BigInteger value) {
        if ((value.compareTo(BigInteger.ZERO) >= 0)
                && (value.compareTo(BigInteger.valueOf(256)) < 0)) {
            return value.intValueExact();
        } else {
            return -1;
        }
    }

    // finishes converting a list of tokens and SExpressions to an SExpressionList
    private SExpression finishList (List<Object> items) {
        // First handle (CODE-CHAR num)
        if (items.size() == 2
                && items.get(0).equals(TokenSymbol.CodeChar())
                && (items.get(1) instanceof TokenInteger)
                && (0 <= validCharCode(((TokenInteger) items.get(1)).value))) {
            return SExpression.character((char) validCharCode(((TokenInteger) items.get(1)).value));
        } else if (false) {
            // TODO: add condition and code to handle TokenDot
            return null;
        } else {
            List<SExpression> sexprs = new LinkedList<>();
            for (Object item: items) {
                sexprs.add((item instanceof SExpression) ? ((SExpression) item) : ((Token) item).toSExpression());
            }
            return SExpression.list(sexprs);
        }
    }

    /**
     * Construct an S-Expression Parser for a given Tokenizer.
     *
     * @param t
     */
    public Parser(Tokenizer t) {
        this.tokenizer = t;
    }

    public Parser(Reader r) {
        this(new Tokenizer(r));
    }

    public Parser(String s) {
        this(new StringReader(s));
    }

}
