/*
 * Copyright (C) 2020 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */

package edu.kestrel.syntheto.sexpr;

/**
 * A singleton class representing the end of a stream of tokens.
 * TODO: we are using TakenError instead, for this purpose.  Consider removing this class.
 */
public class TokenEOF extends Token {

    private static final TokenEOF INSTANCE = new TokenEOF();

    public static TokenEOF get() { return INSTANCE; }

    private TokenEOF() {}  // turn off constructor

    @Override
    public SExpression toSExpression() {
        throw new IllegalArgumentException("there is no separate S-Expression for TokenEOF");
    }
}
