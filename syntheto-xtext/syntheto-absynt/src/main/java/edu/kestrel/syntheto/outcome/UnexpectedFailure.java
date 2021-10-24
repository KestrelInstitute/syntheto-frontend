package edu.kestrel.syntheto.outcome;

import edu.kestrel.syntheto.sexpr.SExpression;
import edu.kestrel.syntheto.sexpr.SExpressionList;
import edu.kestrel.syntheto.sexpr.SExpressionString;
import edu.kestrel.syntheto.sexpr.SExpressionSymbol;

import java.util.Objects;

/**
 * Failure that is not supposed to happen.
 * While certain failures are expected (e.g. a proof obligation fails to prove),
 * others are not because the IDE is supposed to ensure that
 * certain requirements are satisfied
 * (e.g. a function has no duplicate parameter names),
 * and the Java-to-ACL2 translation is supposed to ensure that
 * those requirements are satisfied.
 * However, implementation errors may violate this.
 * When a function definition is submitted to ACL2,
 * and it happens to have duplicate parameter names,
 * ACL2 will return a failure.
 * If and when that happens, that ACL2 failure
 * will manifest as an instance of this Java class.
 */
public class UnexpectedFailure extends Outcome {

    /**
     * Constructor.  For this class we only need the inherited String {@code info}.
     *
     */
    private UnexpectedFailure(String info) {
        super(info);
    }

    /**
     * Static make method.
     * @param info a string describing the unexpected failure outcome
     * @return the new UnexpectedFailure object
     */
    public static UnexpectedFailure make(String info) {
        return new UnexpectedFailure(info);
    }


    /* *************************************************************************
     * to and from SExpression
     * ************************************************************************/

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-OUTCOME-UNEXPECTED-FAILURE");

    @Override
    public SExpression toSExpression() {
        return SExpression.list(classMakerFn,
                SExpression.keyword("MESSAGE"), SExpression.string(info));
    }

    /**
     * Constructs a FunctionSuccess from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-OUTCOME-UNEXPECTED-FAILURE :MESSAGE MESSAGE)
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static UnexpectedFailure fromSExpression(SExpression buildFormArg) {
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
        UnexpectedFailure that = (UnexpectedFailure) o;
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
        s.append(line(indentLevel, "UnexpectedFailure {"));
        // TODO: change to print readably, e.g. StringEscapeUtils.escapeJava(info)
        //       in the package org.apache.commons.text
        s.append(line(indentLevel + 1, "info: \"" + info + "\";"));
        s.append(line(indentLevel, "}"));
        return new String(s);
    }


}
