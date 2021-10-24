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
 * Syntheto sum type specifiers.
 */
public class TypeSum extends ASTNode {

    private final List<Alternative> alternatives;

    private TypeSum(List<Alternative> alternatives) {
        this.alternatives = alternatives;
    }

    /**
     * Builds a sum type specifier.
     * @param alternatives
     * @return The type specifier.
     */
    public static TypeSum make(List<Alternative> alternatives) {
        return new TypeSum(alternatives);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-TYPE-SUM");

    /**
     * Constructs a sum type specifier from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-TYPE-SUM :ALTERNATIVES (LIST alternative...))
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static TypeSum fromSExpression(SExpression buildFormArg) {
        if (! (buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 3)
            throw new IllegalArgumentException("List wrong length.");
        if (! (buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (! (buildForm.second().equals(SExpression.keyword("ALTERNATIVES"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.third() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :ALTERNATIVES argument.");
        // Look up the SExpressionList specifically.
        SExpressionList opSExpr = (SExpressionList) buildForm.third();
        if (opSExpr.isEmpty())
            throw new IllegalArgumentException("List of variables must start with the special symbol LIST.");
        SExpression listSExpr = opSExpr.first();
        if (! (listSExpr.equals(SExpression.LIST())))
            throw new IllegalArgumentException("List of variables must start with the special symbol LIST.");
        List<Alternative> exprs = new ArrayList<>();
        for (SExpression sexpr: opSExpr.rest().getElements()) {
            Object newExpr = ASTBuilder.fromSExpression(sexpr);
            if (! (newExpr instanceof Alternative))
                throw new IllegalArgumentException("Built class should be Alternative.");
            exprs.add((Alternative) newExpr);
        }

        return make(exprs);
    }

    public List<Alternative> getAlternatives() {
        return alternatives;
    }

    @Override
    public SExpression toSExpression() {
        List<SExpression> alternativeSExpressions = new ArrayList<>();
        for (Alternative alternative: alternatives) { alternativeSExpressions.add(alternative.toSExpression()); }
        return SExpression.list(classMakerFn,
                    SExpression.keyword("ALTERNATIVES"), SExpression.listMaker(alternativeSExpressions));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeSum sumType = (TypeSum) o;
        return alternatives.equals(sumType.alternatives);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alternatives);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    String toString(int indentLevel) {
        StringBuilder s = new StringBuilder();
        s.append(line(indentLevel, "TypeSum {"));
        for (Alternative alternative : this.alternatives)
            s.append(alternative.toString(indentLevel + 1));
        s.append(line(indentLevel, "}"));
        return new String(s);
    }
}
