/*
 * Copyright (C) 2020 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */

package edu.kestrel.syntheto.sexpr;

import java.util.Objects;

/**
 * S-expressions that are strings.
 */
public class SExpressionString extends SExpressionAtom {

    private final String value;

    private SExpressionString(String value) {
        this.value = value;
    }

    static SExpressionString make(String value) {
        return new SExpressionString(value);
    }

    public String getValue() { return this.value; }

    @Override
    public String toString() {
        // TODO: handle strings with characters not representable in "..."
        return '"' + this.value + '"';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SExpressionString that = (SExpressionString) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
