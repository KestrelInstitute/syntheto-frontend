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
 * Syntheto function specifications.
 */
public class FunctionSpecification extends ASTNode {

    private final Identifier name;

    private final List<FunctionHeader> functions;

    private final FunctionSpecifier specifier;

    private FunctionSpecification(Identifier name,
                                    List<FunctionHeader> functions,
                                    FunctionSpecifier specifier) {
        this.name = name;
        this.functions = functions;
        this.specifier = specifier;
    }

    public static FunctionSpecification make(Identifier name,
                                             List<FunctionHeader> functions,
                                             FunctionSpecifier specifier) {
        return new FunctionSpecification(name, functions, specifier);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-FUNCTION-SPECIFICATION");

    /**
     * Constructs a function specification from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-FUNCTION-SPECIFICATION :NAME identifier :FUNCTIONS (LIST functionheader...) :SPECIFIER functionspecifier)
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static FunctionSpecification fromSExpression(SExpression buildFormArg) {
        if (! (buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 7)
            throw new IllegalArgumentException("List not the right length.");
        if (! (buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (! (buildForm.second().equals(SExpression.keyword("NAME"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.third() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :NAME argument.");
        Object idRaw = ASTBuilder.fromSExpression(buildForm.third());
        if (! (idRaw instanceof Identifier))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :NAME");
        Identifier id = (Identifier) idRaw;

        if (! (buildForm.fourth().equals(SExpression.keyword("FUNCTIONS"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.fifth() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :FUNCTIONS argument.");
        // Look up the SExpressionList specifically.
        SExpressionList opSExpr = (SExpressionList) buildForm.fifth();
        if (opSExpr.isEmpty())
            throw new IllegalArgumentException("List of variables must start with the special symbol LIST.");
        SExpression listSExpr = opSExpr.first();
        if (! (listSExpr.equals(SExpression.LIST())))
            throw new IllegalArgumentException("List of variables must start with the special symbol LIST.");
        List<FunctionHeader> headers = new ArrayList<>();
        for (SExpression sexpr: opSExpr.rest().getElements()) {
            Object header = ASTBuilder.fromSExpression(sexpr);
            if (! (header instanceof FunctionHeader))
                throw new IllegalArgumentException("Built class should be FunctionHeader.");
            headers.add((FunctionHeader) header);
        }

        if (! (buildForm.sixth().equals(SExpression.keyword("SPECIFIER"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.seventh() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :SPECIFIER argument.");
        Object specRaw = ASTBuilder.fromSExpression(buildForm.seventh());
        if (! (specRaw instanceof FunctionSpecifier))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :SPECIFIER");
        FunctionSpecifier specifier = (FunctionSpecifier) specRaw;

        return make(id, headers, specifier);
    }

    public Identifier getName() {
        return name;
    }

    public List<FunctionHeader> getFunctions() {
        return functions;
    }

    public FunctionSpecifier getSpecifier() {
        return specifier;
    }

    @Override
    public SExpression toSExpression() {
        List<SExpression> functionSExpressions = new ArrayList<>();
        for (FunctionHeader function: functions) { functionSExpressions.add(function.toSExpression()); }
        return SExpression.list(classMakerFn,
                    SExpression.keyword("NAME"), name.toSExpression(),
                    SExpression.keyword("FUNCTIONS"), SExpression.listMaker(functionSExpressions),
                    SExpression.keyword("SPECIFIER"), specifier.toSExpression());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FunctionSpecification that = (FunctionSpecification) o;
        return name.equals(that.name) &&
                functions.equals(that.functions) &&
                specifier.equals(that.specifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, functions, specifier);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    String toString(int indentLevel) {
        StringBuilder s = new StringBuilder();
        s.append(line(indentLevel, "FunctionSpecification " + this.name + "{"));
        for (FunctionHeader header : this.functions)
            s.append(header.toString(indentLevel + 1));
        s.append(this.specifier.toString(indentLevel + 1));
        s.append(line(indentLevel, "}"));
        return new String(s);
    }
}
