/*
 * Copyright (C) 2020 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */

package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.*;

import java.util.Objects;

/**
 * Syntheto when-then-else expressions.
 */
public class ExpressionWhen extends Expression {

    private final Expression test;

    private final Expression then;

    private final Expression else_;

    private ExpressionWhen(Expression test, Expression then, Expression else_) {
        this.test = test;
        this.then = then;
        this.else_ = else_;
    }

    public static ExpressionWhen make(Expression test,
                                    Expression then,
                                    Expression else_) {
        return new ExpressionWhen(test, then, else_);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-EXPRESSION-WHEN");

    /**
     * Constructs a When expression from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-EXPRESSION-WHEN :TEST testExpression :THEN thenExpression :ELSE elseExpression)
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     * @return
     */
    public static ExpressionWhen fromSExpression(SExpression buildFormArg) {
        if (! (buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 7)
            throw new IllegalArgumentException("List not long enough");
        if (! (buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (! (buildForm.second().equals(SExpression.keyword("TEST"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.third() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :TEST argument.");
        Object testRaw = ASTBuilder.fromSExpression(buildForm.third());
        if (! (testRaw instanceof Expression))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :TEST");
        Expression test = (Expression) testRaw;

        if (! (buildForm.fourth().equals(SExpression.keyword("THEN"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.fifth() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :THEN argument.");
        Object thenRaw = ASTBuilder.fromSExpression(buildForm.fifth());
        if (! (thenRaw instanceof Expression))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :THEN");
        Expression then = (Expression) thenRaw;

        if (! (buildForm.sixth().equals(SExpression.keyword("ELSE"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.seventh() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :ELSE argument.");
        Object elseRaw = ASTBuilder.fromSExpression(buildForm.seventh());
        if (! (elseRaw instanceof Expression))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :ELSE");
        Expression else_ = (Expression) elseRaw;

        return make(test, then, else_);
    }

    public Expression getTest() {
        return test;
    }

    public Expression getThen() {
        return then;
    }

    public Expression getElse_() {
        return else_;
    }

    @Override
    public SExpression toSExpression() {
        return SExpression.list(classMakerFn,
                SExpression.keyword("TEST"), test.toSExpression(),
                SExpression.keyword("THEN"), then.toSExpression(),
                SExpression.keyword("ELSE"), else_.toSExpression());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpressionWhen that = (ExpressionWhen) o;
        return test.equals(that.test) &&
                then.equals(that.then) &&
                else_.equals(that.else_);
    }

    @Override
    public int hashCode() {
        return Objects.hash(test, then, else_);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    @Override
    public String toString(int indentLevel) {
        StringBuilder s = new StringBuilder();
        s.append(line(indentLevel, "ExpressionWhen {"));
        s.append(this.test.toString(indentLevel + 1));
        s.append(this.then.toString(indentLevel + 1));
        s.append(this.else_.toString(indentLevel + 1));
        s.append(line(indentLevel, "}"));
        return new String(s);
    }
}
