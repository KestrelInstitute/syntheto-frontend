/*
 * Copyright (C) 2020 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */

package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

/**
 * Syntheto multiple value component selection expressions.
 */
public class ExpressionComponent extends Expression {

    private final Expression multi;

    private final int index;

    private ExpressionComponent(Expression multi, int index) {
        this.multi = multi;
        this.index = index;
    }

    public static ExpressionComponent make(Expression multi, int index) {
        return new ExpressionComponent(multi, index);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-EXPRESSION-COMPONENT");

    /**
     * Constructs a Component expression from an S-Expression.
     * that looks like
     * (SYNTHETO::MAKE-EXPRESSION-COMPONENT :MULTI expr :INDEX idx)
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static ExpressionComponent fromSExpression(SExpression buildFormArg) {
        if (! (buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 5)
            throw new IllegalArgumentException("List not long enough");
        if (! (buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (! (buildForm.second().equals(SExpression.keyword("MULTI"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.third() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :MULTI argument.");
        Object multiRaw = ASTBuilder.fromSExpression(buildForm.third());
        if (! (multiRaw instanceof Expression))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :MULTI");
        Expression multi = (Expression) multiRaw;

        if (! (buildForm.fourth().equals(SExpression.keyword("INDEX"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.fifth() instanceof SExpressionInteger))
            throw new IllegalArgumentException("Wrong type of :INDEX argument.");
        BigInteger idx = ((SExpressionInteger) buildForm.fifth()).getValue();
        if (idx.signum() == -1 )
            throw new IllegalArgumentException("Negative :INDEX not allowed.");
        if (idx.compareTo(BigInteger.valueOf(Integer.MAX_VALUE - 1)) > 0) {
            throw new IllegalArgumentException("Multiple value expression may not have more than Integer.MAX_VALUE components.");
        }
        return make(multi, idx.intValue());
    }

    public Expression getMulti() {
        return multi;
    }

    public int getIndex() { return index; }

    @Override
    public SExpression toSExpression() {

        return SExpression.list(classMakerFn,
                SExpression.keyword("MULTI"), multi.toSExpression(),
                SExpression.keyword("INDEX"), SExpression.integer(index));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpressionComponent that = (ExpressionComponent) o;
        return multi.equals(that.multi) &&
                index == that.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(multi, index);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    public String toString(int indentLevel) {
        StringBuilder s = new StringBuilder();
        s.append(line(indentLevel, "ExpressionComponent " + this.index + " {"));
        s.append(this.multi.toString(indentLevel + 1));
        s.append(line(indentLevel, "}"));
        return new String(s);
    }
}
