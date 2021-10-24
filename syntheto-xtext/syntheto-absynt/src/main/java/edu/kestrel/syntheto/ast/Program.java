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
 * Syntheto programs.
 */
public class Program extends ASTNode {

    private final List<TopLevel> tops;

    private Program(List<TopLevel> tops) {
        this.tops = tops;
    }

    public static Program make(List<TopLevel> tops) {
        return new Program(tops);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-PROGRAM");

    /**
     * Constructs a Syntheto program from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-PROGRAM :TOPS (LIST topleveldef...))
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static Program fromSExpression(SExpression buildFormArg) {
        if (! (buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 3)
            throw new IllegalArgumentException("List not long enough");
        if (! (buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (! (buildForm.second().equals(SExpression.keyword("TOPS"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.third() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :TOPS argument.");
        // Look up the SExpressionList specifically.
        SExpressionList opSExpr = (SExpressionList) buildForm.third();
        if (opSExpr.isEmpty())
            throw new IllegalArgumentException("List of top level S-Expressions must start with the special symbol LIST.");
        SExpression listSExpr = opSExpr.first();
        if (! (listSExpr.equals(SExpression.LIST())))
            throw new IllegalArgumentException("List of top level S-Expressions must start with the special symbol LIST.");
        List<TopLevel> tops = new ArrayList<>();
        for (SExpression sexpr: opSExpr.rest().getElements()) {
            Object newTop = ASTBuilder.fromSExpression(sexpr);
            if (! (newTop instanceof TopLevel))
                throw new IllegalArgumentException("Built class should be TopLevel.");
            tops.add((TopLevel) newTop);
        }

        return make(tops);
    }

    public List<TopLevel> getTops() {
        return tops;
    }

    @Override
    public SExpression toSExpression() {
        List<SExpression> topSExpressions = new ArrayList<>();
        for (TopLevel top: tops) { topSExpressions.add(top.toSExpression()); }
        return SExpression.list(classMakerFn,
                SExpression.keyword("TOPS"), SExpression.listMaker(topSExpressions));
    }

    @Override
    public boolean equals(Object o) {  
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Program program = (Program) o;
        return tops.equals(program.tops);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tops);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    String toString(int indentLevel) {
        StringBuilder s = new StringBuilder();
        s.append(line(indentLevel, "Program {"));
        for (TopLevel top : this.tops)
            s.append(top.toString(indentLevel + 1));
        s.append(line(indentLevel, "}"));
        return new String(s);
    }
}
