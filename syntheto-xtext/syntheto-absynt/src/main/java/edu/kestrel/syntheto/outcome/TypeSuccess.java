package edu.kestrel.syntheto.outcome;

import edu.kestrel.syntheto.sexpr.SExpression;
import edu.kestrel.syntheto.sexpr.SExpressionList;
import edu.kestrel.syntheto.sexpr.SExpressionString;
import edu.kestrel.syntheto.sexpr.SExpressionSymbol;

import java.util.Objects;

/**
 * Outcome of a Syntheto type definition,
 * or clique of mutually recursive type definitions,
 * that is successfully submitted.
 */
public class TypeSuccess extends Outcome {
    // TODO:
    // this might contain information about the type(s)


    /**
     * Constructor.  For this class we only need the inherited String {@code info}.
     *
     */
    private TypeSuccess(String info) {
        super(info);
    }

    /**
     * Static make method.
     * @param info a string describing the type success outcome
     * @return the new TypeSuccess object
     */
    public static TypeSuccess make(String info) {
        return new TypeSuccess(info);
    }

    /* *************************************************************************
     * to and from SExpression
     * ************************************************************************/

    /**
     * The symbol for the ACL2 type that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-OUTCOME-TYPE-SUCCESS");

    @Override
    public SExpression toSExpression() {
        return SExpression.list(classMakerFn,
                SExpression.keyword("MESSAGE"), SExpression.string(info));
    }

    /**
     * Constructs a TypeSuccess from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-OUTCOME-TYPE-SUCCESS :MESSAGE MESSAGE)
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static TypeSuccess fromSExpression(SExpression buildFormArg) {
        if (! (buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList.");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 3)
            throw new IllegalArgumentException("List not the right length.");
        if (! (buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (! (buildForm.second().equals(SExpression.keyword("MESSAGE"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.third() instanceof SExpressionString))
            throw new IllegalArgumentException("Wrong type of :MESSAGE argument.");
        String info = ((SExpressionString) buildForm.third()).getValue();

        return make(info);
    }


    /* *************************************************************************
     * equals, hashCode, and toString
     * ************************************************************************/


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeSuccess that = (TypeSuccess) o;
        return info.equals(that.info);
    }

    @Override
    public int hashCode() {
        return Objects.hash(info);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    @Override
    public String toString(int indentLevel) {
        StringBuilder s = new StringBuilder();
        s.append(line(indentLevel, "TypeSuccess {"));
        // TODO: change to print readably, e.g. StringEscapeUtils.escapeJava(info)
        //       in the package org.apache.commons.text
        s.append(line(indentLevel + 1, "info: \"" + info + "\";"));
        s.append(line(indentLevel, "}"));
        return new String(s);
    }


}
