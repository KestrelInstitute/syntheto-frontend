package edu.kestrel.syntheto.bridge;

import edu.kestrel.syntheto.ast.*;
import edu.kestrel.syntheto.sexpr.SExpression;
import edu.kestrel.syntheto.sexpr.SExpressionList;

import java.util.List;

public class Util {

    public static String wrapTopLevel(TopLevel def) {
        return wrapCommand("(syntheto::translate-to-acl2 " + def.toSExpression().toString() + ")");
    }

    public static String wrapTopLevel(List<TopLevel> defs) {
        StringBuilder sb = new StringBuilder();
        sb.append("(syntheto::translate-to-acl2 (LIST ");
        for (TopLevel def: defs) {
            sb.append(def.toSExpression().toString());
            sb.append(" ");
        }
        sb.append("))");
        return wrapCommand(sb.toString());
    }

    public static String wrapCommand(String form) {
        if (form == null || form.equals("") || !form.startsWith("(")) {
            throw new IllegalArgumentException("form sent to the bridge must start with an open parenthesis");
        }
        return "(bridge::try-in-main-thread (nld '" + form + "))";
    }
    
    public static String wrapTopLevelCommand(String form) {
    	return "(SYNTHETO::PROCESS-SYNTHETO-TOPLEVEL " + form + ")";
    }
    
    public static SExpression extractMakeForm2(SExpression returnedSExpr) {
        if (!(returnedSExpr instanceof SExpressionList)) return null;
        SExpressionList fullForm = (SExpressionList) returnedSExpr;
        if (fullForm.length() != 3) return null;
        if (fullForm.first() != SExpression.NIL()) return null;
        if (fullForm.third() != SExpression.NIL()) return null;
        SExpression innerThing = fullForm.second();
        if (!(innerThing instanceof SExpressionList)) return null;
        SExpressionList innerForm = (SExpressionList) innerThing;
        if (innerForm.length() != 5) return null;
        if (innerForm.first() != SExpression.NIL()) return null;

        SExpression inThing1 = innerForm.second();
        if (!(inThing1 instanceof SExpressionList)) return null;
        SExpressionList inList1 = (SExpressionList) inThing1;
        if (inList1.length() != 3) return null;
        if (inList1.first() != SExpression.NIL()) return null;
        if (inList1.second() != SExpression.NIL()) return null;
        if (inList1.third() != SExpression.NIL()) return null;  // was STATE

        if (innerForm.third() != SExpression.NIL()) return null;
        if (innerForm.fifth() != SExpression.NIL()) return null; // was REPLACED-STATE

        SExpression makeForm = innerForm.fourth();
        // additional checks could go here
        return makeForm;
    }
}
