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
 * Syntheto multiple value expressions.
 */
public class ExpressionMulti extends Expression {

    private final List<Expression> arguments;

    private ExpressionMulti(List<Expression> arguments) {
        this.arguments = arguments;
    }

    public static ExpressionMulti make(List<Expression> arguments) {
        return new ExpressionMulti(arguments);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-EXPRESSION-MULTI");

    /**
     * Constructs a multi value expression from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-EXPRESSION-MULTI :ARGUMENTS (LIST expression...))
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static ExpressionMulti fromSExpression(SExpression buildFormArg) {
        if (! (buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 3)
            throw new IllegalArgumentException("List not long enough");
        if (! (buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (! (buildForm.second().equals(SExpression.keyword("ARGUMENTS"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.third() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :ARGUMENTS argument.");
        // Look up the SExpressionList specifically.
        SExpressionList opSExpr = (SExpressionList) buildForm.third();
        if (opSExpr.isEmpty())
            throw new IllegalArgumentException("List of variables must start with the special symbol LIST.");
        SExpression listSExpr = opSExpr.first();
        if (! (listSExpr.equals(SExpression.LIST())))
            throw new IllegalArgumentException("List of variables must start with the special symbol LIST.");
        List<Expression> exprs = new ArrayList<>();
        for (SExpression sexpr: opSExpr.rest().getElements()) {
            Object newExpr = ASTBuilder.fromSExpression(sexpr);
            if (! (newExpr instanceof Expression))
                throw new IllegalArgumentException("Built class should be Expression.");
            exprs.add((Expression) newExpr);
        }

        return make(exprs);
    }

    public List<Expression> getarguments() {
        return arguments;
    }

    @Override
    public SExpression toSExpression() {
        List<SExpression> argumentSExpressions = new ArrayList<>();
        for (Expression argument : arguments) {
            argumentSExpressions.add(argument.toSExpression());
        }
        return SExpression.list(classMakerFn,
                SExpression.keyword("ARGUMENTS"), SExpression.listMaker(argumentSExpressions));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpressionMulti that = (ExpressionMulti) o;
        return arguments.equals(that.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(arguments);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    @Override
    public String toString(int indentLevel) {
        StringBuilder s = new StringBuilder();
        s.append(line(indentLevel, "ExpressionMulti {"));
        for (Expression arg : this.arguments)
            s.append(arg.toString(indentLevel + 1));
        s.append(line(indentLevel, "}"));
        return new String(s);
    }
}
