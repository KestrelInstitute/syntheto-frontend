/*
 * Copyright (C) 2020 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */

package edu.kestrel.syntheto.sexpr;

/**
 * A singleton class representing a lisp consing dot.
 */
public class TokenDot extends Token {

    private static final TokenDot INSTANCE = new TokenDot();

    public static TokenDot get() { return INSTANCE; }

    private TokenDot() {}  // turn off constructor

    @Override
    public SExpression toSExpression() {
        throw new IllegalArgumentException("there is no separate S-Expression for a lone dot");
    }

}