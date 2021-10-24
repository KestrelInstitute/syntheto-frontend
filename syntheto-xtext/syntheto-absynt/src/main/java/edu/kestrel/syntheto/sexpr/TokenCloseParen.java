/*
 * Copyright (C) 2020 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */

package edu.kestrel.syntheto.sexpr;

/**
 * A singleton class representing a lisp close parenthesis.
 */
public class TokenCloseParen extends Token {

    private static final TokenCloseParen INSTANCE = new TokenCloseParen();

    public static TokenCloseParen get() { return INSTANCE; }

    private TokenCloseParen() {}  // turn off constructor

    @Override
    public SExpression toSExpression() {
        throw new IllegalArgumentException("there is no separate S-Expression for TokenCloseParen");
    }

}
