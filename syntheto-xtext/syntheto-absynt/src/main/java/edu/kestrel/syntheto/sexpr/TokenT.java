/*
 * Copyright (C) 2020 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */

package edu.kestrel.syntheto.sexpr;

/**
 * A singleton class representing a lisp T.
 */
public class TokenT extends Token {

    private static final TokenT INSTANCE = new TokenT();

    public static TokenT get() { return INSTANCE; }

    private TokenT() {}  // turn off constructor

    @Override
    public SExpression toSExpression() {
        return SExpression.T();
    }

}
