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
 * Syntheto branches (of conditional expressions).
 */
public class Branch extends ASTNode {

    private final Expression condition;

    private final Expression action;

    private Branch(Expression condition, Expression action) {
        this.condition = condition;
        this.action = action;
    }

    public static Branch make(Expression condition, Expression action) {
        return new Branch(condition, action);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-BRANCH");

    /**
     * Constructs a branch from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-BRANCH :CONDITION condition :ACTION action)
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static Branch fromSExpression(SExpression buildFormArg) {
        if (! (buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 5)
            throw new IllegalArgumentException("List not long enough");
        if (! (buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (! (buildForm.second().equals(SExpression.keyword("CONDITION"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.third() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :CONDITION argument.");
        Object conditionRaw = ASTBuilder.fromSExpression(buildForm.third());
        if (! (conditionRaw instanceof Expression))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :CONDITION");
        Expression condition = (Expression) conditionRaw;

        if (! (buildForm.fourth().equals(SExpression.keyword("ACTION"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.fifth() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :ACTION argument.");
        Object actionRaw = ASTBuilder.fromSExpression(buildForm.fifth());
        if (! (actionRaw instanceof Expression))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :ACTION");
        Expression action = (Expression) actionRaw;

        return make(condition, action);
    }

    public Expression getCondition() {
        return condition;
    }

    public Expression getAction() {
        return action;
    }

    @Override
    public SExpression toSExpression() {
        return SExpression.list(classMakerFn,
                SExpression.keyword("CONDITION"), condition.toSExpression(),
                SExpression.keyword("ACTION"), action.toSExpression());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Branch branch = (Branch) o;
        return condition.equals(branch.condition) &&
                action.equals(branch.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(condition, action);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    String toString(int indentLevel) {
        StringBuilder s = new StringBuilder();
        s.append(line(indentLevel, "Branch {"));
        s.append(this.condition.toString(indentLevel + 1));
        s.append(this.action.toString(indentLevel + 1));
        s.append(line(indentLevel, "}"));
        return new String(s);
    }
}
