package edu.kestrel.syntheto.outcome;

import edu.kestrel.syntheto.sexpr.*;

import java.util.HashMap;

public class OutcomeBuilder {
    /**
     * Classes in edu.kestrel.syntheto.outcome that know how to make themselves.
     */
    static final String[] OutcomeClasses = new String[]{
            "FunctionSuccess",
            "ProofObligationFailure",
            "SpecificationSuccess",
            "TheoremFailure",
            "TheoremSuccess",
            "TransformationFailure",
            "TransformationSuccess",
            "TypeSuccess",
            "UnexpectedFailure"
    };

    // The interpreter uses makerToClass to dispatch to the right class
    // to handle each makerFn.
    // Each class above must define a static method called fromSExpression that builds an instance of itself.
    /**
     * Use to interpret the S-Expression that makes an AST.
     */
    static final HashMap<SExpressionSymbol, Class<?>> makerToClass;

    static {
        makerToClass = new HashMap<>();
        try {
            for (String className : OutcomeClasses) {
                Class<?> theClass = Class.forName("edu.kestrel.syntheto.outcome." + className);
                //System.out.println("className = " + className);
                java.lang.reflect.Field f = theClass.getField("classMakerFn");
                //System.out.println("field = " + f.toString());
                // get the value of the static field f (the null is ignored)
                SExpressionSymbol makerFn = (SExpressionSymbol) f.get(null);
                //System.out.println("makerFn = " + makerFn.toString());
                makerToClass.put(makerFn, theClass);
            }
        } catch (ReflectiveOperationException e) {
            System.out.println("Problem with setting up map from AST builder function to AST class.");
        }
    }

    /**
     * Builds an ASTNode from an SExpression.
     *
     * @param sExpr
     * @return
     */
    public static Outcome fromSExpression(SExpression sExpr) {
        // For now, I think all the leaves will be made by their immediate users.
        // So we only handle lists.
        if (!(sExpr instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) sExpr;
        SExpression firstItem = buildForm.first();
        if (!(firstItem instanceof SExpressionSymbol))
            throw new IllegalArgumentException("First item in the list must be a symbol.");
        Class<?> classToMake = makerToClass.get(firstItem);
        if (classToMake == null)
            throw new IllegalArgumentException("Cannot find class for: " + firstItem.toString());
        java.lang.reflect.Method method;
        try {
            method = classToMake.getMethod("fromSExpression",
                    // Find the "fromSExpression" method that has argument type SExpression
                    Class.forName("edu.kestrel.syntheto.sexpr.SExpression"));
            // invoke the static method (the first arg is ignored)
            Object builtNode = method.invoke(null, sExpr);
            if (!(builtNode instanceof Outcome))
                throw new IllegalArgumentException("fromSExpression method built a node that is not in the Outcome hierarchy");
            return (Outcome) builtNode;
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
    }
}
