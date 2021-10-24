package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.Parser;
import edu.kestrel.syntheto.sexpr.SExpression;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TopLevelTypesTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-TOPLEVEL-TYPES :GET " +
                    "(SYNTHETO::MAKE-TYPE-RECURSION :DEFINITIONS (LIST " +
                    "(SYNTHETO::MAKE-TYPE-DEFINITION :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"ordered_pair\") " +
                    ":BODY (SYNTHETO::MAKE-TYPE-DEFINER-PRODUCT :GET " +
                    "(SYNTHETO::MAKE-TYPE-PRODUCT " +
                    ":FIELDS (LIST (SYNTHETO::MAKE-FIELD :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\") :TYPE (SYNTHETO::MAKE-TYPE-INTEGER)) " +
                    "(SYNTHETO::MAKE-FIELD :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"y\") :TYPE (SYNTHETO::MAKE-TYPE-INTEGER))) " +
                    ":INVARIANT NIL))))))";

    public static final String checkExpectedType = "(syntheto::toplevelp " + expectedString + ")";

    @Test
    void toSExpression() {
        // TODO: make a real recursive definitions test.  This one should not pass static checks,
        //       since it is not really recursive.  It just tests the top level structure.

        List<TypeDefinition> defs = new ArrayList<>();

        Identifier v1 = Identifier.make("x");
        Identifier v2 = Identifier.make("y");
        Field f1 = Field.make(v1, TypeInteger.make());
        Field f2 = Field.make(v2, TypeInteger.make());
        List<Field> fields = Arrays.asList(f1, f2);
        // Test a product type with a null invariant.
        TypeProduct prod = TypeProduct.make(fields, null);
        Identifier varname1 = Identifier.make("ordered_pair");
        TypeDefinition typedef = TypeDefinition.make(varname1, TypeDefinerProduct.make(prod));

        defs.add(typedef);

        TypeRecursion recursion = TypeRecursion.make(defs);
        TopLevelTypes tlts = TopLevelTypes.make(recursion);
        SExpression sexpr = tlts.toSExpression();

        assertEquals(expectedString,
                sexpr.toString());

        Reader r = new StringReader(sexpr.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sexpr, s);


    }
}