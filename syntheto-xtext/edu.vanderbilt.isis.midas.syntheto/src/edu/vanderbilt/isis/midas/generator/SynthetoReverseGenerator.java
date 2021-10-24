package edu.vanderbilt.isis.midas.generator;

import java.util.ArrayList;
import java.util.List;

import edu.kestrel.syntheto.ast.Alternative;
import edu.kestrel.syntheto.ast.Expression;
import edu.kestrel.syntheto.ast.ExpressionBinary;
import edu.kestrel.syntheto.ast.ExpressionBinary.Operator;
import edu.kestrel.syntheto.ast.ExpressionBind;
import edu.kestrel.syntheto.ast.ExpressionCall;
import edu.kestrel.syntheto.ast.ExpressionComponent;
import edu.kestrel.syntheto.ast.ExpressionCond;
import edu.kestrel.syntheto.ast.ExpressionIf;
import edu.kestrel.syntheto.ast.ExpressionLiteral;
import edu.kestrel.syntheto.ast.ExpressionMulti;
import edu.kestrel.syntheto.ast.ExpressionProductConstruct;
import edu.kestrel.syntheto.ast.ExpressionProductField;
import edu.kestrel.syntheto.ast.ExpressionProductUpdate;
import edu.kestrel.syntheto.ast.ExpressionSumConstruct;
import edu.kestrel.syntheto.ast.ExpressionSumField;
import edu.kestrel.syntheto.ast.ExpressionSumTest;
import edu.kestrel.syntheto.ast.ExpressionSumUpdate;
import edu.kestrel.syntheto.ast.ExpressionUnary;
import edu.kestrel.syntheto.ast.ExpressionUnless;
import edu.kestrel.syntheto.ast.ExpressionWhen;
import edu.kestrel.syntheto.ast.Field;
import edu.kestrel.syntheto.ast.FunctionDefiner;
import edu.kestrel.syntheto.ast.FunctionDefinerQuantified;
import edu.kestrel.syntheto.ast.FunctionDefinerRegular;
import edu.kestrel.syntheto.ast.FunctionDefinition;
import edu.kestrel.syntheto.ast.FunctionHeader;
import edu.kestrel.syntheto.ast.FunctionRecursion;
import edu.kestrel.syntheto.ast.FunctionSpecification;
import edu.kestrel.syntheto.ast.FunctionSpecifier;
import edu.kestrel.syntheto.ast.FunctionSpecifierInputOutput;
import edu.kestrel.syntheto.ast.FunctionSpecifierQuantified;
import edu.kestrel.syntheto.ast.FunctionSpecifierRegular;
import edu.kestrel.syntheto.ast.Identifier;
import edu.kestrel.syntheto.ast.Literal;
import edu.kestrel.syntheto.ast.LiteralBoolean;
import edu.kestrel.syntheto.ast.LiteralCharacter;
import edu.kestrel.syntheto.ast.LiteralInteger;
import edu.kestrel.syntheto.ast.LiteralString;
import edu.kestrel.syntheto.ast.Quantifier;
import edu.kestrel.syntheto.ast.QuantifierExists;
import edu.kestrel.syntheto.ast.QuantifierForall;
import edu.kestrel.syntheto.ast.Theorem;
import edu.kestrel.syntheto.ast.TopLevel;
import edu.kestrel.syntheto.ast.TopLevelFunction;
import edu.kestrel.syntheto.ast.TopLevelFunctions;
import edu.kestrel.syntheto.ast.TopLevelSpecification;
import edu.kestrel.syntheto.ast.TopLevelTheorem;
import edu.kestrel.syntheto.ast.TopLevelType;
import edu.kestrel.syntheto.ast.TopLevelTypes;
import edu.kestrel.syntheto.ast.Type;
import edu.kestrel.syntheto.ast.TypeBoolean;
import edu.kestrel.syntheto.ast.TypeCharacter;
import edu.kestrel.syntheto.ast.TypeCollection;
import edu.kestrel.syntheto.ast.TypeDefined;
import edu.kestrel.syntheto.ast.TypeDefiner;
import edu.kestrel.syntheto.ast.TypeDefinerProduct;
import edu.kestrel.syntheto.ast.TypeDefinerSubset;
import edu.kestrel.syntheto.ast.TypeDefinerSum;
import edu.kestrel.syntheto.ast.TypeDefinition;
import edu.kestrel.syntheto.ast.TypeInteger;
import edu.kestrel.syntheto.ast.TypeMap;
import edu.kestrel.syntheto.ast.TypeOption;
import edu.kestrel.syntheto.ast.TypePrimitive;
import edu.kestrel.syntheto.ast.TypeProduct;
import edu.kestrel.syntheto.ast.TypeRecursion;
import edu.kestrel.syntheto.ast.TypeSequence;
import edu.kestrel.syntheto.ast.TypeSet;
import edu.kestrel.syntheto.ast.TypeString;
import edu.kestrel.syntheto.ast.TypeSubset;
import edu.kestrel.syntheto.ast.TypeSum;
import edu.kestrel.syntheto.ast.TypedVariable;
import edu.kestrel.syntheto.ast.Variable;

public class SynthetoReverseGenerator {

	protected List<String> topLevelStrings = new ArrayList<>();
	
	private boolean inSemicolonFunction = false;
	
	public List<String> getTopLevelStrings() {
		return this.topLevelStrings;
	}
	
	public void doGenerateSyntheto(edu.kestrel.syntheto.ast.Program astNode) {		
		for (TopLevel top : astNode.getTops()) {
			topLevelStrings.add(doTopLevel(top));
		}
		
		System.out.println("Reversed Syntheto:");
		for (String s : topLevelStrings) {
			System.out.println(s);
		}
	}
	
	public String doTopLevel(TopLevel astNode) {
		if (astNode instanceof TopLevelFunction) {
			return doTopLevelFunction((TopLevelFunction)astNode);
		} else if (astNode instanceof TopLevelFunctions) {
			return doTopLevelFunctions((TopLevelFunctions)astNode);
		} else if (astNode instanceof TopLevelSpecification) {
			return doTopLevelSpecification((TopLevelSpecification)astNode);
		} else if (astNode instanceof TopLevelTheorem) {
			return doTopLevelTheorem((TopLevelTheorem)astNode);
		} if (astNode instanceof TopLevelType) {
			return doTopLevelType((TopLevelType)astNode);
		} else if (astNode instanceof TopLevelTypes) {
			return doTopLevelTypes((TopLevelTypes)astNode);
		}
		
		return "error at doTopLevel";
	}
	
	protected String doTopLevelTypes(TopLevelTypes astNode) {
		TypeRecursion recursion = astNode.getTypeRecursion();
		return doTypeRecursion(recursion);
	}

	private String doTypeRecursion(TypeRecursion astNode) {
		for (TypeDefinition typeDefinition : astNode.getDefinitions()) {
			return doTypeDefinition(typeDefinition);
		}
		
		return "errorDoTypeRecursion";
	}

	protected String doTopLevelTheorem(TopLevelTheorem astNode) {
		Theorem theorem = astNode.getTheorem();
		return doTheorem(theorem);
	}

	protected String doTheorem(Theorem theorem) {
		StringBuilder sb = new  StringBuilder();
		
		sb.append("theorem ");
		sb.append(theorem.getName().getName());
		sb.append(" forall ");
		sb.append("(");
		
		int size = theorem.getVariables().size();
		for (int i = 0; i < size; i++) {
			sb.append(doTypedVariable(theorem.getVariables().get(i)));
			if ((size > 1) && (i < (size - 1))) {
				sb.append(", ");
			}
		}
		
		sb.append(") ");
		
		Expression expression = theorem.getFormula();
		sb.append(doExpression(expression));
		
		return sb.toString();
	}

	private int expressionDepth = 0;
	
	public String doExpression(Expression astNode) {
		StringBuilder sb = new StringBuilder();
		
		if (astNode instanceof ExpressionBinary ||
			astNode instanceof ExpressionCall ||
			astNode instanceof ExpressionLiteral ||
			astNode instanceof ExpressionUnary ||
			astNode instanceof Variable) {
			expressionDepth++;
			
			if (astNode instanceof ExpressionBinary) {
				sb.append(doExpressionBinary((ExpressionBinary)astNode));
			} else if (astNode instanceof ExpressionCall) {
				sb.append(doExpressionCall((ExpressionCall)astNode));
			} else if (astNode instanceof ExpressionLiteral) {
				sb.append(doExpressionLiteral((ExpressionLiteral)astNode));
			} else if (astNode instanceof ExpressionUnary) {
				sb.append(doExpressionUnary((ExpressionUnary)astNode));
			} else if (astNode instanceof Variable) {
				sb.append(doVariable((Variable)astNode));
			}			
			
			expressionDepth--;
			if ((expressionDepth == 0) && inSemicolonFunction) {
				sb.append(";");
			}			
		} else {
			if (astNode instanceof ExpressionBind) {
				sb.append(doExpressionBind((ExpressionBind)astNode));
			} else if (astNode instanceof ExpressionComponent) {
				sb.append(doExpressionComponent((ExpressionComponent)astNode));
			} else if (astNode instanceof ExpressionCond) {
				sb.append(doExpressionCond((ExpressionCond)astNode));
			} else if (astNode instanceof ExpressionIf) {
				sb.append(doExpressionIf((ExpressionIf)astNode));
			} else if (astNode instanceof ExpressionMulti) {
				sb.append(doExpressionMulti((ExpressionMulti)astNode));
			} else if (astNode instanceof ExpressionProductConstruct) {
				sb.append(doExpressionProductConstruct((ExpressionProductConstruct)astNode));
			} else if (astNode instanceof ExpressionProductField) {
				sb.append(doExpressionProductField((ExpressionProductField)astNode));
			} else if (astNode instanceof ExpressionProductUpdate) {
				sb.append(doExpressionProductUpdate((ExpressionProductUpdate)astNode));
			} else if (astNode instanceof ExpressionSumConstruct) {
				sb.append(doExpressionSumConstruct((ExpressionSumConstruct)astNode));
			} else if (astNode instanceof ExpressionSumField) {
				sb.append(doExpressionSumField((ExpressionSumField)astNode));
			} else if (astNode instanceof ExpressionSumTest) {
				sb.append(doExpressionSumTest((ExpressionSumTest)astNode));
			} else if (astNode instanceof ExpressionSumUpdate) {
				sb.append(doExpressionSumUpdate((ExpressionSumUpdate)astNode));
			} else if (astNode instanceof ExpressionUnless) {
				sb.append(doExpressionUnless((ExpressionUnless)astNode));
			} else if (astNode instanceof ExpressionWhen) {
				sb.append(doExpressionWhen((ExpressionWhen)astNode));
			}
		}
		
		return sb.toString();
	}

	private String doVariable(Variable astNode) {
		return astNode.getName().getName();
	}

	private String doExpressionProductUpdate(ExpressionProductUpdate astNode) {
		// TODO Auto-generated method stub
		return null;
	}

	private String doExpressionWhen(ExpressionWhen astNode) {
		// TODO Auto-generated method stub
		return null;
	}

	private String doExpressionUnless(ExpressionUnless astNode) {
		// TODO Auto-generated method stub
		return null;
	}

	private String doExpressionUnary(ExpressionUnary astNode) {
		StringBuilder sb = new StringBuilder();
		
		switch (astNode.getOperator()) {
		case MINUS:
			sb.append("-");
			break;
		case NOT:
			sb.append("!"); // TODO: check this is the right char
			break;
		default:
			sb.append("errorExpressionUnary");
		}
		
		sb.append(doExpression(astNode.getOperand()));
		return sb.toString();
	}

	private String doExpressionSumUpdate(ExpressionSumUpdate astNode) {
		// TODO Auto-generated method stub
		return null;
	}

	private String doExpressionSumTest(ExpressionSumTest astNode) {
		// TODO Auto-generated method stub
		return null;
	}

	private String doExpressionSumField(ExpressionSumField astNode) {
		// TODO Auto-generated method stub
		return null;
	}

	private String doExpressionSumConstruct(ExpressionSumConstruct astNode) {
		// TODO Auto-generated method stub
		return null;
	}

	private String doExpressionProductField(ExpressionProductField astNode) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(doExpression(astNode.getTarget()));
		sb.append(".");
		sb.append(astNode.getField().getName());
		sb.append(" ");
		
		return sb.toString();
	}

	private String doExpressionProductConstruct(ExpressionProductConstruct astNode) {
		// TODO Auto-generated method stub
		return null;
	}

	private String doExpressionMulti(ExpressionMulti astNode) {
		// TODO Auto-generated method stub
		return null;
	}

	private String doExpressionLiteral(ExpressionLiteral astNode) {
		return doLiteral(astNode.getLiteral());
	}

	private String doLiteral(Literal astNode) {
		if (astNode instanceof LiteralBoolean) {
			return doLiteralBoolean((LiteralBoolean)astNode);
		} else if (astNode instanceof LiteralCharacter) {
			return doLiteralCharacter((LiteralCharacter)astNode);
		} else if (astNode instanceof LiteralInteger) {
			return doLiteralInteger((LiteralInteger)astNode);
		} else if (astNode instanceof LiteralString) {
			return doLiteralString((LiteralString)astNode);
		}
		
		return "error";
	}

	private String doLiteralString(LiteralString astNode) {
		return astNode.getValue().toString();
	}

	private String doLiteralInteger(LiteralInteger astNode) {
		return astNode.getValue().toString();
	}

	private String doLiteralCharacter(LiteralCharacter astNode) {
		return "" + astNode.getValue();
	}

	private String doLiteralBoolean(LiteralBoolean astNode) {
		return astNode.getValue() ? "true" : "false";
	}

	private String doExpressionCond(ExpressionCond astNode) {
		// TODO Auto-generated method stub
		return null;
	}

	private String doExpressionComponent(ExpressionComponent astNode) {
		// TODO Auto-generated method stub
		return null;
	}

	private String doExpressionCall(ExpressionCall astNode) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(astNode.getFunction().getName());
		
		sb.append("(");
		
		int size = astNode.getArguments().size();
		for (int i = 0; i < size; i++) {
			sb.append(doExpression(astNode.getArguments().get(i)));
			if (size > 1 && i < size - 1) {
				sb.append(",");
			}
		}
		
		sb.append(")");
		
		return sb.toString();
	}

	private String doExpressionBind(ExpressionBind astNode) {
		// TODO Auto-generated method stub
		return null;
	}

	private String doExpressionIf(ExpressionIf astNode) {
		StringBuilder sb = new StringBuilder();
		
		Expression testExpr = astNode.getTest();
		Expression thenExpr = astNode.getThen();
		Expression elseExpr = astNode.getElse_();
				
		sb.append("if");
		sb.append("(");
		boolean prevSemicolonNeeded = inSemicolonFunction;
		inSemicolonFunction = false;
		sb.append(doExpression(testExpr));
		inSemicolonFunction = prevSemicolonNeeded;
		sb.append(") ");
		sb.append("{ \n");
		sb.append(doExpression(thenExpr));
		sb.append("\n} ");
		
		if (elseExpr != null) {
			sb.append("else {\n");
			sb.append(doExpression(elseExpr));
			sb.append("\n} ");
		}
		
		return sb.toString();
	}

	private String doExpressionBinary(ExpressionBinary astNode) {
		StringBuilder sb = new StringBuilder();
		
		String opString = ""; 		
		switch (astNode.getOperator()) {
			case ADD:
				opString = "+";
				break;
			case AND:
				opString = "&&";
				break;
			case DIV:
				opString = "/";
				break;
			case EQ:
				opString = "==";
				break;
			case GE:
				opString = ">=";
				break;
			case GT:
				opString = ">";
				break;
			case IFF:
				opString = "<=>";
				break;
			case IMPLIED:
				opString = "<=";
				break;
			case IMPLIES:
				opString = "=>";
				break;
			case LE:
				opString = "<=";
				break;
			case LT:
				opString = "<";
				break;
			case MUL:
				opString = "*";
				break;
			case NE:
				opString = "!=";
				break;
			case OR:
				opString = "||";
				break;
			case REM:
				opString = "%";
				break;
			case SUB:
				opString = "-";
				break;
			default:
				opString = "error"; // TODO: return error
				break;
		}
		
		Expression left = astNode.getLeftOperand();
		Expression right = astNode.getRightOperand();
		
		sb.append("(");
		sb.append(doExpression(left));
		sb.append(opString);
		sb.append(doExpression(right));
		sb.append(")");
		
		return sb.toString();
	}

	private String doTypedVariable(TypedVariable astNode) {
		StringBuilder sb = new StringBuilder();
		
		String name = astNode.getName().getName();
		sb.append(name);		
		sb.append(":");
		
		Type type = astNode.getType();
		sb.append(doType(type));
		
		return sb.toString();
	}

	protected String doTopLevelSpecification(TopLevelSpecification astNode) {
		FunctionSpecification functionSpecification = astNode.getFunctionSpecification();
		return doFunctionSpecification(functionSpecification);
	}

	private String doFunctionSpecification(FunctionSpecification astNode) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("specification ");
	    sb.append(astNode.getName().getName());
	    sb.append("\n");
	    
	    sb.append("(");
	    sb.append("function ");
	    
	    // TODO: verify that there is exactly 1 FunctionHeader
	    for (FunctionHeader functionHeader : astNode.getFunctions()) {
	    	sb.append(functionHeader.getName().getName());
	    	sb.append(" ");
	    	
	    	sb.append(doFunctionHeaderInputs(functionHeader));
	    	sb.append("returns ");
	    	sb.append(doFunctionHeaderOutputs(functionHeader));
	    }
	    
	    sb.append(") ");
	    sb.append("{");
	    sb.append("\n");
	    
	    boolean prevSemicolonNeeded = inSemicolonFunction;
		inSemicolonFunction = true;
	    sb.append(doFunctionSpecifier(astNode.getSpecifier()));
	    inSemicolonFunction = prevSemicolonNeeded;
	    
	    sb.append("\n");
	    sb.append("}");
	    
	    return sb.toString();
	}

	private String doFunctionSpecifier(FunctionSpecifier astNode) {
		
		
		if (astNode instanceof FunctionSpecifierInputOutput) {
			return doFunctionSpecifierInputOutput((FunctionSpecifierInputOutput)astNode);
		} else if (astNode instanceof FunctionSpecifierQuantified) {
			return doFunctionSpecifierQuantified((FunctionSpecifierQuantified)astNode);
		} else if (astNode instanceof FunctionSpecifierRegular) {
			return doFunctionSpecifierRegular((FunctionSpecifierRegular)astNode);
		}
		
		return "errorDoFunctionSpecifier";
	}

	private String doFunctionSpecifierRegular(FunctionSpecifierRegular astNode) {
		Expression expression = astNode.getBody();
		return doExpression(expression);
	}

	private String doFunctionSpecifierQuantified(FunctionSpecifierQuantified astNode) {
		StringBuilder sb = new StringBuilder();
		
		Quantifier quantifier = astNode.getQuantifier();
		sb.append(doQuantifier(quantifier));
		
		Expression expression = astNode.getMatrix();
		sb.append(doExpression(expression));
		
		for (TypedVariable typedVariable : astNode.getVariables()) {
			sb.append(doTypedVariable(typedVariable));
		}
		
		return sb.toString();
	}

	private String doQuantifier(Quantifier astNode) {
		if (astNode instanceof QuantifierExists) {
			return doQuantifierExists((QuantifierExists)astNode);
		} else if (astNode instanceof QuantifierForall) {
			return doQuantifierForall((QuantifierForall)astNode);
		}
		
		return "errorDoQuantifier";
	}

	private String doQuantifierForall(QuantifierForall astNode) {
		return "";
	}

	private String doQuantifierExists(QuantifierExists astNode) {
		return "";
	}

	private String doFunctionSpecifierInputOutput(FunctionSpecifierInputOutput astNode) {
		Expression expression = astNode.getRelation();
		return doExpression(expression);
	}
	
	private String doFunctionHeaderName(FunctionHeader astNode) {
		return astNode.getName().getName();
	}

	private String doFunctionHeaderInputs(FunctionHeader astNode) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("(");
		int inputSize = astNode.getInputs().size();
		for (int i = 0; i < inputSize; i++) {			
			sb.append(doTypedVariable(astNode.getInputs().get(i)));
			if ((inputSize > 1) && (i < inputSize - 1)) {
				sb.append(",");
			}
		}
		sb.append(") ");	
		
		return sb.toString();
	}
	
	private String doFunctionHeaderOutputs(FunctionHeader astNode) {
		StringBuilder sb = new StringBuilder();
				
		sb.append("(");
		int outputSize = astNode.getOutputs().size();
		for (int i = 0; i < outputSize; i++) {			
			sb.append(doTypedVariable(astNode.getOutputs().get(i)));
			if ((outputSize > 1) && (i < outputSize - 1)) {
				sb.append(", ");
			}
		}
		sb.append(") ");
		
		return sb.toString();
	}

	protected String doTopLevelFunctions(TopLevelFunctions astNode) {
		FunctionRecursion functionRecursion = astNode.getFunctionRecursion();
		return doFunctionRecursion(functionRecursion);
	}

	private String doFunctionRecursion(FunctionRecursion astNode) {
		return "";
	}

	protected String doTopLevelFunction(TopLevelFunction astNode) {
		FunctionDefinition functionDefinition = astNode.getFunctionDefinition();
		return doFunctionDefinition(functionDefinition);
	}

	private String doFunctionDefinition(FunctionDefinition functionDefinition) {
		StringBuilder sb = new StringBuilder();
		sb.append("function ");
		
		FunctionHeader functionHeader = functionDefinition.getHeader();
		
		sb.append(doFunctionHeaderName(functionHeader));
		sb.append(doFunctionHeaderInputs(functionHeader));
		
		// Do precondition if non-null
		Expression precondition = functionDefinition.getPrecondition();
		if (precondition != null) {
			sb.append("assumes ");
			sb.append(doExpression(precondition));
		}
		
		sb.append("returns ");
		
		sb.append(doFunctionHeaderOutputs(functionHeader));
		
		// Do postcondition if non-null
		Expression postcondition = functionDefinition.getPostcondition();
		if (postcondition!= null) {
			sb.append("ensures ");
			sb.append(doExpression(postcondition));
		}
		
		inSemicolonFunction = true;
		FunctionDefiner functionDefiner = functionDefinition.getDefiner();
		if (functionDefiner instanceof FunctionDefinerRegular) {
			FunctionDefinerRegular definer = (FunctionDefinerRegular)functionDefiner;
			Expression measure = definer.getMeasure();
			Expression expression = definer.getBody();
			
			if (measure != null) {
				sb.append("measure ");
				sb.append(doExpression(measure));
			}
		
			sb.append("{ \n");
			
			sb.append(doExpression(expression));
			
			sb.append("\n} ");
			
		} else {
			// TODO: cover the additional cases
		}
		
		inSemicolonFunction = false;
		return sb.toString();
	}

	protected String doTopLevelType(TopLevelType astNode) {
		TypeDefinition typeDefinition = astNode.getTypeDefinition();
		return doTypeDefinition(typeDefinition);
	}
	
	protected String doTypeDefinition(TypeDefinition astNode) {
		StringBuilder sb = new StringBuilder();		
		String name = astNode.getName().getName();
		
		TypeDefiner definer = astNode.getBody();
		if (definer instanceof TypeDefinerProduct) {
			sb.append("struct ");
		} else if (definer instanceof TypeDefinerSubset) {
			sb.append("subtype ");
		} else if (definer instanceof TypeDefinerSum) {
			
		}
		
		sb.append(name);
		sb.append(" { ");		
		sb.append(doTypeDefiner(definer));		
		sb.append("} ");
		
		return sb.toString();
	}
	
	protected String doTypeDefiner(TypeDefiner astNode) {
		if (astNode instanceof TypeDefinerProduct) {
			return doTypeDefinerProduct((TypeDefinerProduct)astNode);
		} else if (astNode instanceof TypeDefinerSubset) {
			return doTypeDefinerSubset((TypeDefinerSubset)astNode);
		} else if (astNode instanceof TypeDefinerSum) {
			return doTypeDefinerSum((TypeDefinerSum)astNode);
		}
		
		return "errorDoTypeDefiner";
	}
	
	private String doTypeDefinerSubset(TypeDefinerSubset astNode) {
		TypeSubset typeSubset = astNode.getSubset();
		return doTypeSubset(typeSubset);
	}

	private String  doTypeSubset(TypeSubset astNode) {
		StringBuilder sb = new StringBuilder();
		
		Type superType = astNode.getSupertype();		
		Identifier identifier = astNode.getVariable();
		Expression expression = astNode.getRestriction();
		Expression witness = astNode.getWitness();
		
		sb.append(identifier.getName());
		sb.append(":");
		sb.append(doType(superType));
		sb.append(" | ");
		sb.append(doExpression(expression));
		
		// TODO: add the witness
		return sb.toString();
	}

	private String doTypeDefinerProduct(TypeDefinerProduct astNode) {
		TypeProduct typeProduct = astNode.getProduct();
		return doTypeProduct(typeProduct);
	}

	protected String doTypeDefinerSum(TypeDefinerSum astNode) {
		StringBuilder sb = new StringBuilder();
		
		TypeSum typeSum = astNode.getSum();
		
		for (int i = 0; i < typeSum.getAlternatives().size(); i++) {
			sb.append(doAlternative(typeSum.getAlternatives().get(i)));
			
			
		}
			
		return sb.toString();
	}
	
	protected void doTypeSum(TypeSum astNode) {
		for (Alternative alt : astNode.getAlternatives()) {
			doAlternative(alt);			
		}
	}
	
	private String doAlternative(Alternative astNode) {
		StringBuilder sb = new StringBuilder();
		
		// TODO: fix this
		String name = astNode.getName().getName();
		sb.append(name);
		
		TypeProduct typeProduct = astNode.getProduct();
		sb.append(doTypeProduct(typeProduct));
		
		return sb.toString();
	}

	protected String doTypeProduct(TypeProduct astNode) {
		StringBuilder sb = new StringBuilder();
		
		int size = astNode.getFields().size();
		for (int i = 0; i < size; i++) {
			sb.append(doField(astNode.getFields().get(i)));
			if ((size > 1) && (i < (size - 1))) {
				sb.append(", ");
			}
		}
		
		sb.append(" | ");
		sb.append(doExpression(astNode.getInvariant()));
		
		return sb.toString();
	}
	
	protected String doField(Field astNode) {	
		StringBuilder sb = new StringBuilder();
		
		sb.append(astNode.getName().getName());
		sb.append(" : ");
		
		Type type = astNode.getType();
		sb.append(doType(type));
		
		return sb.toString();
	}
	
	protected String doType(Type astNode) {
		if (astNode instanceof TypeCollection) {
			return doTypeCollection((TypeCollection)astNode);
		} else if (astNode instanceof TypeDefined) {
			return doTypeDefined((TypeDefined)astNode);
		} else if (astNode instanceof TypeOption) {
			return doTypeOption((TypeOption)astNode);
		} else if (astNode instanceof TypePrimitive) {
			return doTypePrimitive((TypePrimitive)astNode);
		} 
		
		return "error";
	}
	
	protected String doTypeCollection(TypeCollection astNode) {
		if (astNode instanceof TypeMap) {
			return doTypeMap((TypeMap)astNode);
		} else if (astNode instanceof TypeSequence) {
			return doTypeSequence((TypeSequence)astNode);
		} else if (astNode instanceof TypeSet) {
			return doTypeSet((TypeSet)astNode);
		}
		
		return "error";
	}
	
	protected String doTypeDefined(TypeDefined astNode) {
		// TODO: double-check this is correct
		return astNode.getName().getName();
	}
	
	protected String doTypeOption(TypeOption astNode) {
		Type type = astNode.getBase();
		return(doType(type));
	}
	
	protected String doTypePrimitive(TypePrimitive astNode) {
		if (astNode instanceof TypeBoolean) {
			return doTypeBoolean((TypeBoolean)astNode);
		} else if (astNode instanceof TypeCharacter) {
			return doTypeCharacter((TypeCharacter)astNode);
		} else if (astNode instanceof TypeInteger) {
			return doTypeInteger((TypeInteger)astNode);
		} else if (astNode instanceof TypeString) {
			return doTypeString((TypeString)astNode);
		}
		
		// TODO: report type error
		return "error";
	}
	
	protected String doTypeMap(TypeMap astNode) {
		StringBuilder sb = new StringBuilder();
		
		Type domain = astNode.getDomain();
		Type range = astNode.getRange();
		
		doType(domain);
		doType(range);
		
		return sb.toString();
	}
	
	protected String doTypeSequence(TypeSequence astNode) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("seq<");
		sb.append(doType(astNode.getElement()));
		sb.append(">");
		
		return sb.toString();
	}
	
	protected String doTypeSet(TypeSet astNode) {
		StringBuilder sb = new StringBuilder();
		
		// TODO: check what to generate here
		Type element = astNode.getElement();
		String typeString = doType(element);
		
		sb.append(typeString);
		return sb.toString();
	}
	
	protected String doTypeBoolean(TypeBoolean astNode) {
		// TODO: use constants
		return "bool";
	}
	
	protected String doTypeCharacter(TypeCharacter astNode) {
		// TODO: use constants
		return "char";
	}

	protected String doTypeInteger(TypeInteger astNode) {
		// TODO: use constants
		return "int";
	}

	protected String doTypeString(TypeString astNode) {
		// TODO: use constants
		return "string";
	}
}
