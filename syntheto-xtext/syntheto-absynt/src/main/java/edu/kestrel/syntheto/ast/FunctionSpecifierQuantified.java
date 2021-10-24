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
 * Syntheto quantified function specifiers.
 */
public class FunctionSpecifierQuantified extends FunctionSpecifier {

    private final Quantifier quantifier;

    private final List<TypedVariable> variables;

    private final Expression matrix;

    public FunctionSpecifierQuantified(Quantifier quantifier,
                                       List<TypedVariable> variables,
                                       Expression matrix) {
        this.quantifier = quantifier;
        this.variables = variables;
        this.matrix = matrix;
    }

    public static FunctionSpecifierQuantified make
            (Quantifier quantifier,
             List<TypedVariable> variables,
             Expression matrix) {
        return new FunctionSpecifierQuantified(quantifier, variables, matrix);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-FUNCTION-SPECIFIER-QUANTIFIED");

    /**
     * Constructs a function specifier with quantifier from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-FUNCTION-SPECIFIER-QUANTIFIED :QUANTIFIER quantifier :VARIABLES (LIST typedVar...) :MATRIX expression)
     *
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static FunctionSpecifierQuantified fromSExpression(SExpression buildFormArg) {
        if (! (buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 7)
            throw new IllegalArgumentException("List not long enough");
        if (! (buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (! (buildForm.second().equals(SExpression.keyword("QUANTIFIER"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.third() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :QUANTIFIER argument.");
        Object qRaw = ASTBuilder.fromSExpression(buildForm.third());
        if (! (qRaw instanceof Quantifier))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :QUANTIFIER");
        Quantifier quantifier = (Quantifier) qRaw;

        if (! (buildForm.fourth().equals(SExpression.keyword("VARIABLES"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.fifth() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :VARIABLES argument.");
        // Look up the SExpressionList specifically.
        SExpressionList opSExpr = (SExpressionList) buildForm.fifth();
        if (opSExpr.isEmpty())
            throw new IllegalArgumentException("List of variables must start with the special symbol LIST.");
        SExpression listSExpr = opSExpr.first();
        if (! (listSExpr.equals(SExpression.LIST())))
            throw new IllegalArgumentException("List of variables must start with the special symbol LIST.");
        List<TypedVariable> vars = new ArrayList<>();
        for (SExpression sexpr: opSExpr.rest().getElements()) {
            Object newVar = ASTBuilder.fromSExpression(sexpr);
            if (!(newVar instanceof TypedVariable))
                throw new IllegalArgumentException("Built class should be TypedVariable.");
            vars.add((TypedVariable) newVar);
        }

        if (! (buildForm.sixth().equals(SExpression.keyword("MATRIX"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.seventh() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :MATRIX argument.");
        Object matrixRaw = ASTBuilder.fromSExpression(buildForm.seventh());
        if (! (matrixRaw instanceof Expression))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :MATRIX");
        Expression matrix = (Expression) matrixRaw;

        return make(quantifier, vars, matrix);
    }

    public Quantifier getQuantifier() {
        return quantifier;
    }

    public List<TypedVariable> getVariables() {
        return variables;
    }

    public Expression getMatrix() {
        return matrix;
    }

    @Override
    public SExpression toSExpression() {
        List<SExpression> variableSExpressions = new ArrayList<>();
        for (TypedVariable variable: variables) { variableSExpressions.add(variable.toSExpression()); }
        return SExpression.list(classMakerFn,
                SExpression.keyword("QUANTIFIER"), quantifier.toSExpression(),
                SExpression.keyword("VARIABLES"), SExpression.listMaker(variableSExpressions),
                SExpression.keyword("MATRIX"), matrix.toSExpression());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FunctionSpecifierQuantified that = (FunctionSpecifierQuantified) o;
        return quantifier == that.quantifier &&
                variables.equals(that.variables) &&
                matrix.equals(that.matrix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quantifier, variables, matrix);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    @Override
    String toString(int indentLevel) {
        StringBuilder s = new StringBuilder();
        s.append(line(indentLevel, "FunctionSpecifierQuantified {"));
        s.append(this.quantifier.toString(indentLevel + 1));
        for (TypedVariable var : this.variables)
            s.append(var.toString(indentLevel + 1));
        s.append(this.matrix.toString(indentLevel + 1));
        s.append(line(indentLevel, "}"));
        return new String(s);
    }
}
