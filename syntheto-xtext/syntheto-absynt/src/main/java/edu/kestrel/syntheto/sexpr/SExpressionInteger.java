/*
 * Copyright (C) 2020 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */

package edu.kestrel.syntheto.sexpr;

import java.math.BigInteger;
import java.util.Objects;

/**
 * S-expressions that are integers.
 * These may be negative, unlike Syntheto literal integers.
 */
public class SExpressionInteger extends SExpressionAtom {

    private final BigInteger value;

    private SExpressionInteger(BigInteger value) {
        this.value = value;
    }

    // Creation code.  We recommend using the interface methods from SExpression for creation.

    static SExpressionInteger make(BigInteger value) {
        return new SExpressionInteger(value);
    }

    // Inspection code.  These are public.

    public BigInteger getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SExpressionInteger that = (SExpressionInteger) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
