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
 * Syntheto function definitions.
 */
public class FunctionDefinition extends ASTNode {

    private final FunctionHeader header;

    private final Expression precondition;

    private final Expression postcondition;

    private final FunctionDefiner definer;

    protected FunctionDefinition(FunctionHeader header,
                                 Expression precondition,
                                 Expression postcondition,
                                 FunctionDefiner definer) {
        this.header = header;
        this.precondition = precondition;
        this.postcondition = postcondition;
        this.definer = definer;
    }

    public static FunctionDefinition make(FunctionHeader header,
                                          Expression precondition,
                                          Expression postcondition,
                                          FunctionDefiner definer) {
        return new FunctionDefinition(header, precondition, postcondition, definer);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-FUNCTION-DEFINITION");

    /**
     * Constructs a function definition expression from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-FUNCTION-DEFINITION :HEADER functionheader :PRECONDITION precondition :POSTCONDITION postcondition :DEFINER functiondefiner)
     *
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static FunctionDefinition fromSExpression(SExpression buildFormArg) {
        if (! (buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 9)
            throw new IllegalArgumentException("List wrong length.");
        if (! (buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (! (buildForm.second().equals(SExpression.keyword("HEADER"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.third() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :HEADER argument.");
        Object functionheaderRaw = ASTBuilder.fromSExpression(buildForm.third());
        if (! (functionheaderRaw instanceof FunctionHeader))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :HEADER");
        FunctionHeader functionheader = (FunctionHeader) functionheaderRaw;

        if (! (buildForm.fourth().equals(SExpression.keyword("PRECONDITION"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        Expression precondition = null;
        if (! (buildForm.fifth().equals(SExpression.NIL()))) {
            if (! (buildForm.fifth() instanceof SExpressionList))
                throw new IllegalArgumentException("Wrong type of :PRECONDITION argument.");
            Object preRaw = ASTBuilder.fromSExpression(buildForm.fifth());
            if (!(preRaw instanceof Expression))
                throw new IllegalArgumentException("Wrong AST class returned by maker for :PRECONDITION");
            precondition = (Expression) preRaw;
        }

        if (! (buildForm.sixth().equals(SExpression.keyword("POSTCONDITION"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        Expression postcondition = null;
        if (! (buildForm.seventh().equals(SExpression.NIL()))) {
            if (! (buildForm.seventh() instanceof SExpressionList))
                throw new IllegalArgumentException("Wrong type of :POSTCONDITION argument.");
            Object postRaw = ASTBuilder.fromSExpression(buildForm.seventh());
            if (!(postRaw instanceof Expression))
                throw new IllegalArgumentException("Wrong AST class returned by maker for :POSTCONDITION");
            postcondition = (Expression) postRaw;
        }

        if (! (buildForm.eighth().equals(SExpression.keyword("DEFINER"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.ninth() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :DEFINER argument.");
        Object testRaw = ASTBuilder.fromSExpression(buildForm.ninth());
        if (! (testRaw instanceof FunctionDefiner))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :DEFINER");
        FunctionDefiner functiondefiner = (FunctionDefiner) testRaw;

        return make(functionheader, precondition, postcondition, functiondefiner);
    }

    public FunctionHeader getHeader() {
        return header;
    }

    public Expression getPrecondition() {
        return precondition;
    }

    public Expression getPostcondition() {
        return postcondition;
    }

    public FunctionDefiner getDefiner() {
        return definer;
    }

    @Override
    public SExpression toSExpression() {
        return SExpression.list(classMakerFn,
                    SExpression.keyword("HEADER"), header.toSExpression(),
                    SExpression.keyword("PRECONDITION"),
                    (precondition == null) ? SExpression.NIL() : precondition.toSExpression(),
                    SExpression.keyword("POSTCONDITION"),
                    (postcondition == null) ? SExpression.NIL() : postcondition.toSExpression(),
                    SExpression.keyword("DEFINER"), definer.toSExpression());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FunctionDefinition that = (FunctionDefinition) o;
        // If this or that precondition is null but not both are null, then return false.
        // (The .equals() below does not tolerate a null.)
        if ((precondition == null) != (that.precondition == null))
            return false;
        // similarly for the postcondition
        if ((postcondition == null) != (that.postcondition == null))
            return false;
        return header.equals(that.header) &&
                ( ((precondition == null) && (that.precondition == null))
                        || precondition.equals(that.precondition)) &&
                ( ((postcondition == null) && (that.postcondition == null))
                        || postcondition.equals(that.postcondition)) &&
                definer.equals(that.definer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(header, precondition, postcondition, definer);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    String toString(int indentLevel) {
        StringBuilder s = new StringBuilder();
        s.append(line(indentLevel, "FunctionDefinition {"));
        s.append(this.header.toString(indentLevel + 1));
        if (this.precondition != null)
            s.append(this.precondition.toString(indentLevel + 1));
        else
            s.append(line(indentLevel + 1, "/* no precondition */"));
        if (this.postcondition != null)
            s.append(this.postcondition.toString(indentLevel + 1));
        else
            s.append(line(indentLevel + 1, "/* no postcondition */"));
        s.append(this.definer.toString(indentLevel + 1));
        s.append(line(indentLevel, "}"));
        return new String(s);
    }
}
