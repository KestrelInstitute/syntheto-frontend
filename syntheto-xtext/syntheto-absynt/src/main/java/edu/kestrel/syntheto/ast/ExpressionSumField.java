/*
 * Copyright (C) 2020 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */

package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Syntheto sum field expressions.
 */
public class ExpressionSumField extends Expression {

    private final Identifier type;

    private final Expression target;

    private final Identifier alternative;

    private final Identifier field;

    private ExpressionSumField(Identifier type,
                               Expression target,
                               Identifier alternative,
                               Identifier field) {
        this.type = type;
        this.target = target;
        this.alternative = alternative;
        this.field = field;
    }

    /**
     * @param type The name of the sum type that is returned by target.
     * @param target An expression that returns a value of *type*::*alternative*.
     * @param alternative  The name of a subproduct alternative of *type*.
     * @param field The field of the value that is selected.
     * @return An expression that, when evaluated, returns the value of the given field of target.
     */
    public static ExpressionSumField make(Identifier type,
                                          Expression target,
                                          Identifier alternative,
                                          Identifier field) {
        return new ExpressionSumField(type, target, alternative, field);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-EXPRESSION-SUM-FIELD");

    /**
     * Constructs a sum field selection expression from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-EXPRESSION-SUM-FIELD :TYPE typename :TARGET expression :ALTERNATIVE altId :FIELD fieldId)
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     * @return
     */
    public static ExpressionSumField fromSExpression(SExpression buildFormArg) {
        if (! (buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 9)
            throw new IllegalArgumentException("List not long enough");
        if (! (buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (! (buildForm.second().equals(SExpression.keyword("TYPE"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.third() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :TYPE argument.");
        Object nameRaw = ASTBuilder.fromSExpression(buildForm.third());
        if (! (nameRaw instanceof Identifier))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :TYPE");
        Identifier typename = (Identifier) nameRaw;

        if (! (buildForm.fourth().equals(SExpression.keyword("TARGET"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.fifth() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :TARGET argument.");
        Object exprRaw = ASTBuilder.fromSExpression(buildForm.fifth());
        if (! (exprRaw instanceof Expression))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :TARGET");
        Expression expr = (Expression) exprRaw;

        if (! (buildForm.sixth().equals(SExpression.keyword("ALTERNATIVE"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.seventh() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :ALTERNATIVE argument.");
        Object altIdRaw = ASTBuilder.fromSExpression(buildForm.seventh());
        if (! (altIdRaw instanceof Identifier))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :ALTERNATIVE");
        Identifier altId = (Identifier) altIdRaw;

        if (! (buildForm.eighth().equals(SExpression.keyword("FIELD"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.ninth() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :FIELD argument.");
        Object fieldIdRaw = ASTBuilder.fromSExpression(buildForm.ninth());
        if (! (fieldIdRaw instanceof Identifier))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :FIELD");
        Identifier fieldId = (Identifier) fieldIdRaw;

        return make(typename, expr, altId, fieldId);
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

    public Identifier getField() {
        return field;
    }

    @Override
    public SExpression toSExpression() {
        return SExpression.list(classMakerFn,
                SExpression.keyword("TYPE"), type.toSExpression(),
                SExpression.keyword("TARGET"), target.toSExpression(),
                SExpression.keyword("ALTERNATIVE"), alternative.toSExpression(),
                SExpression.keyword("FIELD"), field.toSExpression());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpressionSumField that = (ExpressionSumField) o;
        return type.equals(that.type) &&
                target.equals(that.target) &&
                alternative.equals(that.alternative) &&
                field.equals(that.field);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, target, alternative, field);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    @Override
    public String toString(int indentLevel) {
        StringBuilder s = new StringBuilder();
        s.append(line(indentLevel,
                "ExpressionSumField " + this.type + " " +
                        this.alternative + " " + this.field + " {"));
        s.append(this.target.toString(indentLevel + 1));
        s.append(line(indentLevel, "}"));
        return new String(s);
    }
}
