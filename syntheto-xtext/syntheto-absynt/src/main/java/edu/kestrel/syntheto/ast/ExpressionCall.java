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
 * Syntheto function call expressions.
 */
public class ExpressionCall extends Expression {

    private final Identifier function;

    private final List<Type> types;

    private final List<Expression> arguments;

    private ExpressionCall(Identifier function,
                           List<Type> types,
                           List<Expression> arguments) {
        this.function = function;
        this.types = types;
        this.arguments = arguments;
    }

    /**
     * TODO: fix the Javadoc
     * @param function
     * @param types
     * @param arguments
     * @return
     */
    public static ExpressionCall make(Identifier function,
                                      List<Type> types,
                                      List<Expression> arguments) {
        return new ExpressionCall(function, types, arguments);
    }

    /**
     * TODO: fix the Javadoc
     * @param function
     * @param arguments
     * @return
     */
    public static ExpressionCall make(Identifier function,
                                      List<Expression> arguments) {
        return new ExpressionCall(function, new ArrayList<>(), arguments);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-EXPRESSION-CALL");

    /**
     * Constructs a call expression from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-EXPRESSION-CALL :FUNCTION identifier :TYPES (LIST type...) :ARGUMENTS (LIST expression...))
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static ExpressionCall fromSExpression(SExpression buildFormArg) {
        if (! (buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 7)
            throw new IllegalArgumentException("List not long enough");
        if (! (buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (! (buildForm.second().equals(SExpression.keyword("FUNCTION"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.third() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :FUNCTION argument.");
        Object idRaw = ASTBuilder.fromSExpression(buildForm.third());
        if (! (idRaw instanceof Identifier))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :FUNCTION");
        Identifier id = (Identifier) idRaw;

        if (! (buildForm.fourth().equals(SExpression.keyword("TYPES"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.fifth() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :TYPES argument.");
        // Look up the SExpressionList specifically.
        SExpressionList opSExpr = (SExpressionList) buildForm.fifth();
        if (opSExpr.isEmpty())
            throw new IllegalArgumentException("List of variables must start with the special symbol LIST.");
        SExpression listSExpr = opSExpr.first();
        if (! (listSExpr.equals(SExpression.LIST())))
            throw new IllegalArgumentException("List of variables must start with the special symbol LIST.");
        List<Type> types = new ArrayList<>();
        for (SExpression sexpr: opSExpr.rest().getElements()) {
            Object newType = ASTBuilder.fromSExpression(sexpr);
            if (! (newType instanceof Type))
                throw new IllegalArgumentException("Built class should be Type.");
            types.add((Type) newType);
        }

        if (! (buildForm.sixth().equals(SExpression.keyword("ARGUMENTS"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.seventh() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :ARGUMENTS argument.");
        // Look up the SExpressionList specifically.
        SExpressionList opSExpr2 = (SExpressionList) buildForm.seventh();
        if (opSExpr2.isEmpty())
            throw new IllegalArgumentException("List of variables must start with the special symbol LIST.");
        SExpression listSExpr2 = opSExpr2.first();
        if (! (listSExpr2.equals(SExpression.LIST())))
            throw new IllegalArgumentException("List of variables must start with the special symbol LIST.");
        List<Expression> exprs = new ArrayList<>();
        for (SExpression sexpr2: opSExpr2.rest().getElements()) {
            Object newExpr = ASTBuilder.fromSExpression(sexpr2);
            if (! (newExpr instanceof Expression))
                throw new IllegalArgumentException("Built class should be Expression.");
            exprs.add((Expression) newExpr);
        }

        return make(id, types, exprs);
    }

    public Identifier getFunction() {
        return function;
    }

    public List<Type> getTypes() { return types; }

    public List<Expression> getArguments() {
        return arguments;
    }

    @Override
    public SExpression toSExpression() {
        List<SExpression> typeSExpressions = new ArrayList<>();
        for (Type type : types) {
            typeSExpressions.add(type.toSExpression());
        }
        List<SExpression> argumentSExpressions = new ArrayList<>();
        for (Expression argument : arguments) {
            argumentSExpressions.add(argument.toSExpression());
        }
        return SExpression.list(classMakerFn,
                SExpression.keyword("FUNCTION"), function.toSExpression(),
                SExpression.keyword("TYPES"), SExpression.listMaker(typeSExpressions),
                SExpression.keyword("ARGUMENTS"), SExpression.listMaker(argumentSExpressions));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpressionCall that = (ExpressionCall) o;
        return function.equals(that.function) &&
                types.equals(that.types) &&
                arguments.equals(that.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(function, types, arguments);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    @Override
    public String toString(int indentLevel) {
        StringBuilder s = new StringBuilder();
        s.append(line(indentLevel, "ExpressionCall " + this.function + " {"));
        for (Type type: this.types)
            s.append(type.toString(indentLevel + 1));
        for (Expression arg : this.arguments)
            s.append(arg.toString(indentLevel + 1));
        s.append(line(indentLevel, "}"));
        return new String(s);
    }
}
