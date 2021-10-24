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
 * Syntheto conditional expressions.
 */
public class ExpressionCond extends Expression {

    private final List<Branch> branches;

    private ExpressionCond(List<Branch> branches) {
        this.branches = branches;
    }

    public static ExpressionCond make(List<Branch> branches) {
        return new ExpressionCond(branches);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-EXPRESSION-COND");

    /**
     * Constructs a conditional expression from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-EXPRESSION-COND :BRANCHES (LIST branch...))
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static ExpressionCond fromSExpression(SExpression buildFormArg) {
        if (! (buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 3)
            throw new IllegalArgumentException("List not the right length.");
        if (! (buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (! (buildForm.second().equals(SExpression.keyword("BRANCHES"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.third() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :BRANCHES argument.");
        // Look up the SExpressionList specifically.
        SExpressionList branchesSExpr = (SExpressionList) buildForm.third();
        if (branchesSExpr.isEmpty())
            throw new IllegalArgumentException("List of branches must start with the special symbol LIST.");
        SExpression listSExpr = branchesSExpr.first();
        if (! (listSExpr.equals(SExpression.LIST())))
            throw new IllegalArgumentException("List of branches must start with the special symbol LIST.");
        List<Branch> branches = new ArrayList<>();
        for (SExpression sexpr: branchesSExpr.rest().getElements()) {
            Object newExpr = ASTBuilder.fromSExpression(sexpr);
            if (! (newExpr instanceof Branch))
                throw new IllegalArgumentException("Built class should be Branch.");
            branches.add((Branch) newExpr);
        }

        return make(branches);
    }

    public List<Branch> getBranches() {
        return branches;
    }

    @Override
    public SExpression toSExpression() {
        List<SExpression> branchSExpressions = new ArrayList<>();
        for (Branch branch : branches) {
            branchSExpressions.add(branch.toSExpression());
        }
        return SExpression.list(classMakerFn,
                SExpression.keyword("BRANCHES"), SExpression.listMaker(branchSExpressions));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpressionCond that = (ExpressionCond) o;
        return branches.equals(that.branches);
    }

    @Override
    public int hashCode() {
        return Objects.hash(branches);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    public String toString(int indentLevel) {
        StringBuilder s = new StringBuilder();
        s.append(line(indentLevel, "ExpressionCond {"));
        for (Branch branch : this.branches)
            s.append(branch.toString(indentLevel + 1));
        s.append(line(indentLevel, "}"));
        return new String(s);
    }
}
