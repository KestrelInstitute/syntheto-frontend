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
 * Syntheto function recursions (i.e. mutually recursive function definitions).
 */
public class FunctionRecursion extends ASTNode {

    private final List<FunctionDefinition> definitions;

    private FunctionRecursion(List<FunctionDefinition> definitions) {
        this.definitions = definitions;
    }

    public static FunctionRecursion make(List<FunctionDefinition> definitions) {
        return new FunctionRecursion(definitions);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-FUNCTION-RECURSION");

    /**
     * Constructs a function recursion group of function definitions from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-FUNCTION-RECURSION :DEFINITIONS (LIST def...))
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static FunctionRecursion fromSExpression(SExpression buildFormArg) {
        if (! (buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 3)
            throw new IllegalArgumentException("List wrong length.");
        if (! (buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (! (buildForm.second().equals(SExpression.keyword("DEFINITIONS"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.third() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :DEFINITIONS argument.");
        // Look up the SExpressionList specifically.
        SExpressionList opSExpr = (SExpressionList) buildForm.third();
        if (opSExpr.isEmpty())
            throw new IllegalArgumentException("List of type definitions must start with the special symbol LIST.");
        SExpression listSExpr = opSExpr.first();
        if (! (listSExpr.equals(SExpression.LIST())))
            throw new IllegalArgumentException("List of type definitions must start with the special symbol LIST.");
        List<FunctionDefinition> defs = new ArrayList<>();
        for (SExpression sexpr: opSExpr.rest().getElements()) {
            Object newExpr = ASTBuilder.fromSExpression(sexpr);
            if (! (newExpr instanceof FunctionDefinition))
                throw new IllegalArgumentException("Built class should be FunctionDefinition.");
            defs.add((FunctionDefinition) newExpr);
        }

        return make(defs);
    }

    public List<FunctionDefinition> getDefinitions() {
        return definitions;
    }

    @Override
    public SExpression toSExpression() {
        List<SExpression> definitionSExpressions = new ArrayList<>();
        for (FunctionDefinition definition: definitions) { definitionSExpressions.add(definition.toSExpression()); }
        return SExpression.list(classMakerFn,
            SExpression.keyword("DEFINITIONS"), SExpression.listMaker(definitionSExpressions));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FunctionRecursion that = (FunctionRecursion) o;
        return definitions.equals(that.definitions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(definitions);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    String toString(int indentLevel) {
        StringBuilder s = new StringBuilder();
        s.append(line(indentLevel, "FunctionRecursion {"));
        for (FunctionDefinition fundef : this.definitions)
            s.append(fundef.toString(indentLevel + 1));
        s.append(line(indentLevel, "}"));
        return new String(s);
    }
}
