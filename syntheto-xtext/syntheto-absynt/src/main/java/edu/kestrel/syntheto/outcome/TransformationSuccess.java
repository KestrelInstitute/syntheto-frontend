package edu.kestrel.syntheto.outcome;

import edu.kestrel.syntheto.ast.TopLevel;
import edu.kestrel.syntheto.ast.ASTBuilder;
import edu.kestrel.syntheto.sexpr.SExpression;
import edu.kestrel.syntheto.sexpr.SExpressionList;
import edu.kestrel.syntheto.sexpr.SExpressionString;
import edu.kestrel.syntheto.sexpr.SExpressionSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Outcome of a successful Syntheto transformation.
 * (Note that we still need to add transformations to Syntheto.)
 */
public class TransformationSuccess extends Outcome {

    // The main result of a successful transformation is a sequence of
    // top-level constructions -- new function(s) and relating theorem(s).
    private final List<TopLevel> topLevels;

    public List<TopLevel> getTopLevels() {
        return topLevels;
    }

    /**
     * Constructor.
     */
    private TransformationSuccess(String info, List<TopLevel> topLevels) {
        super(info);
        this.topLevels = topLevels;
    }

    /**
     * Static make method.
     *
     * @param info a string describing the proof obligation failure outcome
     * @param topLevels the new top level definitions
     * @return the new TransformationSuccess object
     */
    public static TransformationSuccess make(String info, List<TopLevel> topLevels) {
        return new TransformationSuccess(info, topLevels);
    }



    /* *************************************************************************
     * to and from SExpression
     * ************************************************************************/

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-OUTCOME-TRANSFORMATION-SUCCESS");

    @Override
    public SExpression toSExpression() {
        List<SExpression> topLevelSExprs = new ArrayList<>();
        for (TopLevel topLevel : topLevels) {
            topLevelSExprs.add(topLevel.toSExpression());
        }
        return SExpression.list(classMakerFn,
                SExpression.keyword("MESSAGE"), SExpression.string(info),
                SExpression.keyword("TOPLEVELS"), SExpression.listMaker(topLevelSExprs));
    }

    /**
     * Constructs a TransformationSuccess from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-OUTCOME-TRANSFORMATION-SUCCESS :MESSAGE MESSAGE :TOPLEVELS (LIST toplevel..))
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static TransformationSuccess fromSExpression(SExpression buildFormArg) {
        if (!(buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList.");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 5)
            throw new IllegalArgumentException("List not the right length.");
        if (!(buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (!(buildForm.second().equals(SExpression.keyword("MESSAGE"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (!(buildForm.third() instanceof SExpressionString))
            throw new IllegalArgumentException("Wrong type of :MESSAGE argument.");
        String info = ((SExpressionString) buildForm.third()).getValue();

        if (! (buildForm.fourth().equals(SExpression.keyword("TOPLEVELS"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.fifth() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :TOPLEVELS argument.");
        // Look up the SExpressionList specifically.
        SExpressionList opSExpr = (SExpressionList) buildForm.fifth();
        if (opSExpr.isEmpty())
            throw new IllegalArgumentException("List of top level S-Expressions must start with the special symbol LIST.");
        SExpression listSExpr = opSExpr.first();
        if (! (listSExpr.equals(SExpression.LIST())))
            throw new IllegalArgumentException("List of top level S-Expressions must start with the special symbol LIST.");
        List<TopLevel> tops = new ArrayList<>();
        for (SExpression sexpr: opSExpr.rest().getElements()) {
            Object newTop = ASTBuilder.fromSExpression(sexpr);
            if (!(newTop instanceof TopLevel))
                throw new IllegalArgumentException("Built class should be TopLevel.");
            tops.add((TopLevel) newTop);
        }
        return make(info, tops);
    }


    /* *************************************************************************
     * equals, hashCode, and toString
     * ************************************************************************/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransformationSuccess that = (TransformationSuccess) o;
        return info.equals(that.info) && topLevels.equals(that.topLevels);
    }

    @Override
    public int hashCode() {
        return Objects.hash(info, topLevels);
    }


    @Override
    public String toString() {
        return this.toString(0);
    }

    @Override
    public String toString(int indentLevel) {
        StringBuilder s = new StringBuilder();
        s.append(line(indentLevel, "TransformationSuccess {"));
        // TODO: change to print readably, e.g. StringEscapeUtils.escapeJava(info)
        //       in the package org.apache.commons.text
        s.append(line(indentLevel + 1, "info: \"" + info + "\";"));
        s.append(line(indentLevel + 1, "toplevels:"));
        for (TopLevel top: this.topLevels)
            s.append(top.toString(indentLevel + 1));
        s.append(line(indentLevel, "}"));
        return new String(s);
    }



}
