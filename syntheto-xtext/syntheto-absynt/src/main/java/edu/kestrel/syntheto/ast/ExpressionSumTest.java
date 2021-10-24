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
 * Syntheto sum test expressions.
 *
 * target must return a value of sum type.
 * This expression tests if that value is of the alternative subproduct type.
 */
public class ExpressionSumTest extends Expression {

    private final Identifier type;

    private final Expression target;

    private final Identifier alternative;

    private ExpressionSumTest(Identifier type,
                              Expression target,
                              Identifier alternative) {
        this.type = type;
        this.target = target;
        this.alternative = alternative;
    }

    /**
     * @param type The name of the sum type that is returned by target.
     * @param target An expression that returns a value of *type*, any alternative.
     * @param alternative The name of a subproduct alternative of *type*.
     * @return An expression that, when evaluated, returns a boolean indicating whether the target value has type *type*::*alternative*.
     */
    public static ExpressionSumTest make(Identifier type,
                                        Expression target,
                                        Identifier alternative) {
        return new ExpressionSumTest(type, target, alternative);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-EXPRESSION-SUM-TEST");

    /**
     * Constructs a sum type alternative test expression from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-EXPRESSION-SUM-TEST :TYPE typename :TARGET expression :ALTERNATIVE identifier)
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static ExpressionSumTest fromSExpression(SExpression buildFormArg) {
        if (! (buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 7)
            throw new IllegalArgumentException("List wrong length.");
        if (! (buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (! (buildForm.second().equals(SExpression.keyword("TYPE"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.third() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :TYPE argument.");
        Object typeRaw = ASTBuilder.fromSExpression(buildForm.third());
        if (! (typeRaw instanceof Identifier))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :TYPE");
        Identifier type = (Identifier) typeRaw;

        if (! (buildForm.fourth().equals(SExpression.keyword("TARGET"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.fifth() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :TARGET argument.");
        Object valRaw = ASTBuilder.fromSExpression(buildForm.fifth());
        if (! (valRaw instanceof Expression))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :TARGET");
        Expression val = (Expression) valRaw;

        if (! (buildForm.sixth().equals(SExpression.keyword("ALTERNATIVE"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.seventh() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :ALTERNATIVE argument.");
        Object nameRaw = ASTBuilder.fromSExpression(buildForm.seventh());
        if (! (nameRaw instanceof Identifier))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :ALTERNATIVE");
        Identifier name = (Identifier) nameRaw;

        return make(type, val, name);
    }

    public Identifier getType() {
        return type;
    }

    public Expression getTarget() {
        return target;
    }

    public Identifier getAlternative() {
        return alternative;
    }

    @Override
    public SExpression toSExpression() {
        return SExpression.list(classMakerFn,
                SExpression.keyword("TYPE"), type.toSExpression(),
                SExpression.keyword("TARGET"), target.toSExpression(),
                SExpression.keyword("ALTERNATIVE"), alternative.toSExpression());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpressionSumTest that = (ExpressionSumTest) o;
        return type.equals(that.type) &&
                target.equals(that.target) &&
                alternative.equals(that.alternative);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, target, alternative);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    @Override
    public String toString(int indentLevel) {
        StringBuilder s = new StringBuilder();
        s.append(line(indentLevel,
                "ExpressionSumTest " + this.type + " " +
                        this.alternative + " {"));
        s.append(this.target.toString(indentLevel + 1));
        s.append(line(indentLevel, "}"));
        return new String(s);
    }
}
