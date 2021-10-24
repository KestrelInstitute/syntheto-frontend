/*
 * Copyright (C) 2020 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */

package edu.kestrel.syntheto.sexpr;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * S-expressions that are lists.
 * These include proper and dotted lists,
 * distinguished by the final {@code cdr} being {@code null} or an atom.
 * We use this class to represent any {@code cons} pairs,
 * by flattening the {@code cdr}s till the final one.
 * This representation is more convenient for the Syntheto S-expressions.
 */
public class SExpressionList extends SExpression {

    private final List<SExpression> elements;

    private final SExpressionAtom finalCdr;

    private SExpressionList(List<SExpression> elements,
                           SExpressionAtom finalCdr) {
        this.elements = elements;
        this.finalCdr = finalCdr;
    }

    private static List<SExpression> makeElements(SExpression... sexprs) {
        List<SExpression> elements = new LinkedList<>();
        Collections.addAll(elements, sexprs);
        return elements;
    }

    // Creation code.  We recommend using the interface methods from SExpression for creation.

    static SExpressionList makeProper(SExpression... sexprs) {
        return new SExpressionList(makeElements(sexprs), null);
    }
    static SExpressionList makeProper(List<SExpression> sexprs) {
        return new SExpressionList(sexprs, null);
    }

    static SExpressionList makeDotted(SExpressionAtom finalCdr,
                                      SExpression... sexprs) {
        return new SExpressionList(makeElements(sexprs), finalCdr);
    }

    static SExpressionList cons(SExpression first,
                                SExpressionList rest) {
        List<SExpression> newElements = new LinkedList<>(rest.elements);
        newElements.add(0, first);
        return new SExpressionList(newElements, null);
    }

    // Inspection code.  These are public.

    public List<SExpression> getElements() { return this.elements; }

    public int length() {
        return elements.size();
    }

    public boolean isEmpty() {
        return (this.length() == 0);
    }

    public SExpression first()  {
        if (this.isEmpty()) throw new IllegalArgumentException("Cannot get element of empty list.");
        return elements.get(0);
    }

    public SExpressionList rest()  {
        if (this.isEmpty()) throw new IllegalArgumentException("Cannot get rest of empty list.");
        return new SExpressionList(elements.subList(1, elements.size()), this.finalCdr);
    }

    public SExpression nth(int n)  {
        if (this.length() < n) throw new IllegalArgumentException("Cannot get nth element of list that is too small.");
        return elements.get(n - 1);
    }

    public SExpression second()  {
        if (this.length() < 2) throw new IllegalArgumentException("Cannot get nth element of list that is too small.");
        return elements.get(1);
    }

    public SExpression third()  {
        if (this.length() < 3) throw new IllegalArgumentException("Cannot get nth element of list that is too small.");
        return elements.get(2);
    }

    public SExpression fourth()  {
        if (this.length() < 4) throw new IllegalArgumentException("Cannot get nth element of list that is too small.");
        return elements.get(3);
    }

    public SExpression fifth()  {
        if (this.length() < 5) throw new IllegalArgumentException("Cannot get nth element of list that is too small.");
        return elements.get(4);
    }

    public SExpression sixth()  {
        if (this.length() < 6) throw new IllegalArgumentException("Cannot get nth element of list that is too small.");
        return elements.get(5);
    }

    public SExpression seventh()  {
        if (this.length() < 7) throw new IllegalArgumentException("Cannot get nth element of list that is too small.");
        return elements.get(6);
    }
    public SExpression eighth()  {
        if (this.length() < 8) throw new IllegalArgumentException("Cannot get nth element of list that is too small.");
        return elements.get(7);
    }

    public SExpression ninth()  {
        if (this.length() < 9) throw new IllegalArgumentException("Cannot get nth element of list that is too small.");
        return elements.get(8);
    }

    public SExpression tenth()  {
        if (this.length() < 10) throw new IllegalArgumentException("Cannot get nth element of list that is too small.");
        return elements.get(9);
    }


    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append('(');
        for (int i = 0; i < this.elements.size() - 1; ++i) {
            string.append(this.elements.get(i).toString());
            string.append(' ');
        }
        if (this.elements.size() > 0) {
            string.append(this.elements.get(this.elements.size() - 1).toString());
            if (this.finalCdr != null) {
                string.append(" . ");
                string.append(this.finalCdr.toString());
            }
        }
        string.append(')');
        return string.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SExpressionList that = (SExpressionList) o;

        // If either finalCdr is null but not both, then return false.
        // (The finalCdr.equals() below does not tolerate a null.)
        if ((finalCdr == null) != (that.finalCdr == null))
            return false;
        return elements.equals(that.elements) &&
                ( ((finalCdr == null) && (that.finalCdr == null))
                        || finalCdr.equals(that.finalCdr));
    }

    @Override
    public int hashCode() {
        return Objects.hash(elements, finalCdr);
    }
}
