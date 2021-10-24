package edu.vanderbilt.isis.midas.generator

import edu.kestrel.syntheto.ast.ASTNode
import edu.kestrel.syntheto.ast.Alternative
import edu.kestrel.syntheto.ast.Branch
import edu.kestrel.syntheto.ast.Expression
import edu.kestrel.syntheto.ast.ExpressionBinary
import edu.kestrel.syntheto.ast.ExpressionBind
import edu.kestrel.syntheto.ast.ExpressionCall
import edu.kestrel.syntheto.ast.ExpressionCond
import edu.kestrel.syntheto.ast.ExpressionIf
import edu.kestrel.syntheto.ast.ExpressionProductConstruct
import edu.kestrel.syntheto.ast.ExpressionProductField
import edu.kestrel.syntheto.ast.ExpressionUnary
import edu.kestrel.syntheto.ast.ExpressionUnless
import edu.kestrel.syntheto.ast.ExpressionWhen
import edu.kestrel.syntheto.ast.Field
import edu.kestrel.syntheto.ast.FunctionDefiner
import edu.kestrel.syntheto.ast.FunctionDefinerRegular
import edu.kestrel.syntheto.ast.FunctionHeader
import edu.kestrel.syntheto.ast.FunctionSpecification
import edu.kestrel.syntheto.ast.FunctionSpecifierInputOutput
import edu.kestrel.syntheto.ast.Identifier
import edu.kestrel.syntheto.ast.Initializer
import edu.kestrel.syntheto.ast.LiteralBoolean
import edu.kestrel.syntheto.ast.LiteralInteger
import edu.kestrel.syntheto.ast.LiteralString
import edu.kestrel.syntheto.ast.TopLevel
import edu.kestrel.syntheto.ast.TopLevelTransform
import edu.kestrel.syntheto.ast.Transform
import edu.kestrel.syntheto.ast.TransformArgument
import edu.kestrel.syntheto.ast.TransformArgumentValue
import edu.kestrel.syntheto.ast.TransformArgumentValueBoolean
import edu.kestrel.syntheto.ast.TransformArgumentValueIdentifier
import edu.kestrel.syntheto.ast.TransformArgumentValueIdentifiers
import edu.kestrel.syntheto.ast.TransformArgumentValueTerm
import edu.kestrel.syntheto.ast.Type
import edu.kestrel.syntheto.ast.TypeBoolean
import edu.kestrel.syntheto.ast.TypeCharacter
import edu.kestrel.syntheto.ast.TypeDefined
import edu.kestrel.syntheto.ast.TypeDefinition
import edu.kestrel.syntheto.ast.TypeInteger
import edu.kestrel.syntheto.ast.TypeMap
import edu.kestrel.syntheto.ast.TypeOption
import edu.kestrel.syntheto.ast.TypeProduct
import edu.kestrel.syntheto.ast.TypeSequence
import edu.kestrel.syntheto.ast.TypeSet
import edu.kestrel.syntheto.ast.TypeString
import edu.kestrel.syntheto.ast.TypeSubset
import edu.kestrel.syntheto.ast.TypeSum
import edu.kestrel.syntheto.ast.Variable
import edu.vanderbilt.isis.midas.syntheto.And_expr
import edu.vanderbilt.isis.midas.syntheto.BlockExpression
import edu.vanderbilt.isis.midas.syntheto.BooleanLiteral
import edu.vanderbilt.isis.midas.syntheto.Compare_expr
import edu.vanderbilt.isis.midas.syntheto.CondBranches
import edu.vanderbilt.isis.midas.syntheto.CondExpression
import edu.vanderbilt.isis.midas.syntheto.Div
import edu.vanderbilt.isis.midas.syntheto.drop_irrelevant_param
import edu.vanderbilt.isis.midas.syntheto.ElementTagQualifier
import edu.vanderbilt.isis.midas.syntheto.Elseexpr
import edu.vanderbilt.isis.midas.syntheto.finite_difference
import edu.vanderbilt.isis.midas.syntheto.flatten_param
import edu.vanderbilt.isis.midas.syntheto.FunctionCall
import edu.vanderbilt.isis.midas.syntheto.FunctionDefinition
import edu.vanderbilt.isis.midas.syntheto.FunctionSpecfication
import edu.vanderbilt.isis.midas.syntheto.IfExpression
import edu.vanderbilt.isis.midas.syntheto.Implies_expr
import edu.vanderbilt.isis.midas.syntheto.isomorphism
import edu.vanderbilt.isis.midas.syntheto.LetExpression
import edu.vanderbilt.isis.midas.syntheto.LiteralValue
import edu.vanderbilt.isis.midas.syntheto.Map
import edu.vanderbilt.isis.midas.syntheto.Minus
import edu.vanderbilt.isis.midas.syntheto.Modulo
import edu.vanderbilt.isis.midas.syntheto.Multi
import edu.vanderbilt.isis.midas.syntheto.NumberLiteral
import edu.vanderbilt.isis.midas.syntheto.Option
import edu.vanderbilt.isis.midas.syntheto.Or_expr
import edu.vanderbilt.isis.midas.syntheto.Plus
import edu.vanderbilt.isis.midas.syntheto.PrimaryTypeElement
import edu.vanderbilt.isis.midas.syntheto.ProductLiteral
import edu.vanderbilt.isis.midas.syntheto.ProductTypeDefinition
import edu.vanderbilt.isis.midas.syntheto.Program
import edu.vanderbilt.isis.midas.syntheto.remove_cdring
import edu.vanderbilt.isis.midas.syntheto.rename_param
import edu.vanderbilt.isis.midas.syntheto.Sequence
import edu.vanderbilt.isis.midas.syntheto.Set
import edu.vanderbilt.isis.midas.syntheto.simplify
import edu.vanderbilt.isis.midas.syntheto.SingleValueBuiltins
import edu.vanderbilt.isis.midas.syntheto.StringLiteral
import edu.vanderbilt.isis.midas.syntheto.SubTypeDefinition
import edu.vanderbilt.isis.midas.syntheto.Subelement
import edu.vanderbilt.isis.midas.syntheto.SumTypeDefinition
import edu.vanderbilt.isis.midas.syntheto.tail_recursion
import edu.vanderbilt.isis.midas.syntheto.Theorem
import edu.vanderbilt.isis.midas.syntheto.TransformDefinition
import edu.vanderbilt.isis.midas.syntheto.TwoValueBuiltins
import edu.vanderbilt.isis.midas.syntheto.TypeElement
import edu.vanderbilt.isis.midas.syntheto.TypedVariable
import edu.vanderbilt.isis.midas.syntheto.Unary_expr
import edu.vanderbilt.isis.midas.syntheto.UnlessExpression
import edu.vanderbilt.isis.midas.syntheto.VariableAssignment
import edu.vanderbilt.isis.midas.syntheto.WhenExpression
import edu.vanderbilt.isis.midas.syntheto.wrap_output
import edu.vanderbilt.isis.midas.syntheto.util.SynthetoSwitch
import java.math.BigInteger
import java.util.ArrayList
import java.util.HashMap
import java.util.List
import java.util.Stack
import java.util.logging.Logger
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.xtext.nodemodel.util.NodeModelUtils
import edu.kestrel.syntheto.ast.TopLevelType
import edu.kestrel.syntheto.ast.TypeDefinerProduct
import edu.kestrel.syntheto.ast.TypeDefinerSum
import edu.kestrel.syntheto.ast.TypeDefinerSubset
import edu.kestrel.syntheto.ast.TopLevelFunction
import edu.kestrel.syntheto.ast.TopLevelSpecification
import edu.kestrel.syntheto.ast.TopLevelTheorem
import edu.kestrel.syntheto.ast.ExpressionLiteral
import edu.vanderbilt.isis.midas.syntheto.SeqLiteral
import edu.kestrel.syntheto.ast.ExpressionSumConstruct

class SynthetoVisitor extends SynthetoSwitch<ASTNode> {
	var String resourceName
	var Logger Log;
	var HashMap<Integer, ASTNode> _status
	var debugDetails = true

	new(String string, Logger _log) {
		resourceName = string
		Log = _log
		_status = new HashMap<Integer, ASTNode>
		Log.info("constructing  visitor for " + resourceName)
	}

	def Type toAcl2(TypeElement type) {

		System.out.println(type)
		Log.fine("visiting " + type.details)
		if (type instanceof PrimaryTypeElement) {
			if (type.primary !== null) {
				switch type.primary {
					case type.primary.isBoolean: return TypeBoolean.make()
					case type.primary.isChar: return TypeCharacter.make()
					case type.primary.isInt: return TypeInteger.make()
					case type.primary.isString: return TypeString.make()
				}
			} else {
				// This is a referred type
				var referredtype = type.typeref
				var referredtypename = ""
				switch (referredtype) {
					case referredtype instanceof ProductTypeDefinition:
						referredtypename = (referredtype as ProductTypeDefinition).productID
					case referredtype instanceof SubTypeDefinition:
						referredtypename = (referredtype as SubTypeDefinition).name
					case referredtype instanceof SumTypeDefinition:
						referredtypename = (referredtype as SumTypeDefinition).name
				}

				var astnode = TypeDefined.make(Identifier.make(referredtypename))
				return astnode as Type
			}

		} else // either a sequence or a set or an option
		{
			var astnode = doSwitch(type)
			return astnode as Type
		}

	}

	override caseBlockExpression(BlockExpression object) {
		Log.fine("visiting " + object.details)
		return object.expr.doSwitch;
	}

	def String getDetails(EObject definition) {
		if (debugDetails) {
			var srcNode = NodeModelUtils.getNode(definition)
			var line = srcNode.getText().replace("\n", "").replace("\r", "");
			var EObject semanticObject = NodeModelUtils.findActualSemanticObjectFor(srcNode);
			if (semanticObject !== null) {
				return " " + line + " " + (EcoreUtil.getURI(semanticObject));
			} else
				return " " + line + " "
		} else
			return " " + definition.toString + " "
	}

	override caseProgram(Program object) {
		Log.fine("visiting " + object.details)
		if(object === null) throw new RuntimeException('object cannot be null')
		if (_status.containsKey(object.hashCode)) {
			Log.info("Already visited " + object.details)
			var ASTNode program = _status.get(object.hashCode)
			return program
		}
		var ASTNode program
		var List<TopLevel> tops = new ArrayList<TopLevel>
		for (entry : object.contents) {
			var ASTNode output = doSwitch(entry)
			if (output !== null)
				try {
					tops.add(output as TopLevel)
				} catch (Exception exception) {
					Log.severe(exception.toString)
				}
		}
		program = edu.kestrel.syntheto.ast.Program.make(tops)
		_status.put(object.hashCode, program)
		return program
	}

	override caseTransformDefinition(TransformDefinition object) {
	    var ASTNode transform

        var newFnName = edu.kestrel.syntheto.ast.Identifier.make(object.name)
        var oldFnName = getOldFunctionName(object.transformed_fn)
        var transformName = getTransformName(object.transformation)
        var args = getTransformArguments(object.transformation)

	    transform = edu.kestrel.syntheto.ast.Transform.make(newFnName,
	                                                        oldFnName,
	                                                        transformName,
	                                                        args)

        var topLevelTransform = TopLevelTransform.make(transform as Transform)
	    _status.put(object.hashCode, topLevelTransform)
	    return topLevelTransform
	}

	def getTransformName(Object object) {
	    var String name = ""

	    switch object {
	        isomorphism : name = "isomorphism"
	        finite_difference : name = "finite_difference"
            flatten_param : name = "flatten_param"
            wrap_output : name = "wrap_output"
            drop_irrelevant_param : name = "drop_irrelevant_param"
            tail_recursion : name = "tail_recursion"
            rename_param : name = "rename_param"
            remove_cdring : name = "remove_cdring"
            simplify : name = "simplify"
	    }

	    return name
	}

	def getTransformArguments(Object object) {
	    var List<TransformArgument> args = new ArrayList<TransformArgument>()
	    var Identifier value
	    var Identifier key
	    var TransformArgumentValue argValue

	    switch object {
                    edu.vanderbilt.isis.midas.syntheto.isomorphism : {
                        key = edu.kestrel.syntheto.ast.Identifier.make("parameter")
                        value = edu.kestrel.syntheto.ast.Identifier.make(object.parameter)
                        argValue = TransformArgumentValueIdentifier.make(value)
                        args.add(TransformArgument.make(key, argValue))

                        key = edu.kestrel.syntheto.ast.Identifier.make("new_parameter_name")
                        value = edu.kestrel.syntheto.ast.Identifier.make(object.new_parameter_name)
                        argValue = TransformArgumentValueIdentifier.make(value)
                        args.add(TransformArgument.make(key, argValue))

                        key = edu.kestrel.syntheto.ast.Identifier.make("old_type")
                        value = edu.kestrel.syntheto.ast.Identifier.make(object.old_type)
                        argValue = TransformArgumentValueIdentifier.make(value)
                        args.add(TransformArgument.make(key, argValue))

                        key = edu.kestrel.syntheto.ast.Identifier.make("new_type")
                        value = edu.kestrel.syntheto.ast.Identifier.make(object.new_type)
                        argValue = TransformArgumentValueIdentifier.make(value)
                        args.add(TransformArgument.make(key, argValue))

                        key = edu.kestrel.syntheto.ast.Identifier.make("old_to_new")
                        value = edu.kestrel.syntheto.ast.Identifier.make(object.old_to_new)
                        argValue = TransformArgumentValueIdentifier.make(value)
                        args.add(TransformArgument.make(key, argValue))

                        key = edu.kestrel.syntheto.ast.Identifier.make("new_to_old")
                        value = edu.kestrel.syntheto.ast.Identifier.make(object.new_to_old)
                        argValue = TransformArgumentValueIdentifier.make(value)
                        args.add(TransformArgument.make(key, argValue))

                        key = edu.kestrel.syntheto.ast.Identifier.make("simplify")
                        var boolean isTrue = object.simplify.isIsTrue()
                        argValue = TransformArgumentValueBoolean.make(isTrue)
                        args.add(TransformArgument.make(key, argValue))
                    }
                    edu.vanderbilt.isis.midas.syntheto.remove_cdring : {
                        key = edu.kestrel.syntheto.ast.Identifier.make("simplify")
                        var boolean isTrue = object.simplify.isIsTrue()
                        argValue = TransformArgumentValueBoolean.make(isTrue)
                        args.add(TransformArgument.make(key, argValue))
                    }
                    edu.vanderbilt.isis.midas.syntheto.rename_param : {
                        key = edu.kestrel.syntheto.ast.Identifier.make("old")
                        value = edu.kestrel.syntheto.ast.Identifier.make(object.old)
                        argValue = TransformArgumentValueIdentifier.make(value)
                        args.add(TransformArgument.make(key, argValue))

                        key = edu.kestrel.syntheto.ast.Identifier.make("new")
                        value = edu.kestrel.syntheto.ast.Identifier.make(object.getNew())
                        argValue = TransformArgumentValueIdentifier.make(value)
                        args.add(TransformArgument.make(key, argValue))
                    }
                    edu.vanderbilt.isis.midas.syntheto.tail_recursion : {
                        key = edu.kestrel.syntheto.ast.Identifier.make("new_parameter_name")
                        value = edu.kestrel.syntheto.ast.Identifier.make(object.identifier)
                        argValue = TransformArgumentValueIdentifier.make(value)
                        args.add(TransformArgument.make(key, argValue))
                    }
                    edu.vanderbilt.isis.midas.syntheto.drop_irrelevant_param : {
                        key = edu.kestrel.syntheto.ast.Identifier.make("param")
                        value = edu.kestrel.syntheto.ast.Identifier.make(object.identifier)
                        argValue = TransformArgumentValueIdentifier.make(value)
                        args.add(TransformArgument.make(key, argValue))
                    }
                    edu.vanderbilt.isis.midas.syntheto.wrap_output : {
                        key = edu.kestrel.syntheto.ast.Identifier.make("wrap_function")
                        value = edu.kestrel.syntheto.ast.Identifier.make(object.identifier)
                        argValue = TransformArgumentValueIdentifier.make(value)
                        args.add(TransformArgument.make(key, argValue))
                    }
                    edu.vanderbilt.isis.midas.syntheto.finite_difference : {
                        key = edu.kestrel.syntheto.ast.Identifier.make("expression")
                        var synthetoexpression = object.expression
                    	var Expression acl2Expression = synthetoexpression.doSwitch as Expression
                    	argValue = TransformArgumentValueTerm.make(acl2Expression)
                        args.add(TransformArgument.make(key, argValue))

                        key = edu.kestrel.syntheto.ast.Identifier.make("new_parameter_name")
                        value = edu.kestrel.syntheto.ast.Identifier.make(object.new_parameter_name)
                        argValue = TransformArgumentValueIdentifier.make(value)
                        args.add(TransformArgument.make(key, argValue))

                        key = edu.kestrel.syntheto.ast.Identifier.make("simplify")
                        var boolean isTrue = object.simplify.isIsTrue()
                        argValue = TransformArgumentValueBoolean.make(isTrue)
                        args.add(TransformArgument.make(key, argValue))
                    }
                    edu.vanderbilt.isis.midas.syntheto.flatten_param : {
                        key = edu.kestrel.syntheto.ast.Identifier.make("old")
                        value = edu.kestrel.syntheto.ast.Identifier.make(object.old)
                        argValue = TransformArgumentValueIdentifier.make(value)
                        args.add(TransformArgument.make(key, argValue))

                        key = edu.kestrel.syntheto.ast.Identifier.make("new")
                        var List<Identifier> params
                        for (param : object.newlist) {
                            params.add(Identifier.make(param))
                        }
                        argValue = TransformArgumentValueIdentifiers.make(params)
                        args.add(TransformArgument.make(key, argValue))
                    }
                    edu.vanderbilt.isis.midas.syntheto.simplify : {
                        // nothing to do because args is empty
                    }
        }

        return args
	}

	def getOldFunctionName(Object object) {
	    var String name = ""

	    switch object {
	        edu.vanderbilt.isis.midas.syntheto.TransformDefinition : name = object.name
	        edu.vanderbilt.isis.midas.syntheto.FunctionDefinition :  name = object.name
	    }

	    return Identifier.make(name)
	}

	override caseProductTypeDefinition(ProductTypeDefinition object) {
		Log.fine("visiting " + object.details)
		if(object === null) throw new RuntimeException('object cannot be null')
		if (_status.containsKey(object.hashCode)) {
			Log.info("Already visited " + object.details)
			return _status.get(object.hashCode)
		}
		// Log.info("visiting product|" + object.productID)
		var List<Field> fields = new ArrayList<Field>
		for (_e : object.element) {
			// TODO:get type AST and add it here.
			var acl2type = _e.type.toAcl2
//			Log.info("visiting product element|" + _e.name)
//			Log.info("visiting product element|" + _e.type)
			fields.add(Field.make(Identifier.make(_e.name), acl2type));
		}
		var synthetoexpression = object.invariant
		var Expression Acl2expression = null // initialized to null
		if (synthetoexpression !== null) {
			Acl2expression = synthetoexpression.doSwitch as Expression
		}

		if (object.isStruct) {
			var definer = TypeDefinerProduct.make(TypeProduct.make(fields, Acl2expression)); // null for no invariant expression
			var struct = TopLevelType.make(TypeDefinition.make(Identifier.make(object.name), definer));
			_status.put(object.hashCode, struct)
			return struct
		} else {
			var definer = TypeProduct.make(fields, Acl2expression);
			_status.put(object.hashCode, definer)
			return definer;
		}

	}

	override caseSumTypeDefinition(SumTypeDefinition object) {
		Log.fine("visiting " + object.details)
		if(object === null) throw new RuntimeException('object cannot be null')
		if (_status.containsKey(object.hashCode)) {
			Log.info("Already visited " + object.details)
			return _status.get(object.hashCode)
		}

		var List<Alternative> alts = new ArrayList<Alternative>

		for (_alternative : object.alternatives) {
			if (_alternative.element !== null && _alternative.element.size > 0) {
				var acl2product = doSwitch(_alternative)
				var acl2Alternative = Alternative.make(Identifier.make(_alternative.name), acl2product as TypeProduct)
				alts.add(acl2Alternative)
			} else {
				// its an alternative with just a name and no product
				var List<Field> fields = new ArrayList<Field>
				var acl2product = TypeProduct.make(fields, null) // components of alternatives aren't wrapped in TypeDefinerProduct
				var acl2Alternative = Alternative.make(Identifier.make(_alternative.name),
					acl2product as TypeProduct)
				alts.add(acl2Alternative)

			}
		}

		var definer = TypeDefinerSum.make(TypeSum.make(alts));
		var variant = TopLevelType.make(TypeDefinition.make(Identifier.make(object.name), definer));
		_status.put(object.hashCode, variant)

		return variant;
	}

	def String name(ProductTypeDefinition definition) {
		return definition.productID
	}

	override caseSubTypeDefinition(SubTypeDefinition object) {
		Log.fine("visiting " + object.details)
		if(object === null) throw new RuntimeException('object cannot be null')
		if (_status.containsKey(object.hashCode)) {
			Log.info("Already visited " + object.details)
			return _status.get(object.hashCode)
		}
		var _e = object.element
		var acl2type = _e.type.toAcl2

		var synthetoexpression = object.invariant
		var Expression Acl2expression = null // initialized to null
		if (synthetoexpression !== null) {
			Acl2expression = synthetoexpression.doSwitch as Expression
		}
		var definer = TypeDefinerSubset.make(TypeSubset.make(acl2type, Identifier.make(_e.name), Acl2expression, null))
		var subtype = TopLevelType.make(TypeDefinition.make(Identifier.make(object.name), definer));
		_status.put(object.hashCode, subtype)
		return subtype;
	}

	override caseOption(Option object) {
		Log.fine("visiting " + object.details)
		if(object === null) throw new RuntimeException('object cannot be null')
		if (_status.containsKey(object.hashCode)) {
			Log.info("Already visited " + object.details)
			return _status.get(object.hashCode)
		}
		var _e = object.element
		var acl2type = _e.toAcl2

		var optiontype = TypeOption.make(acl2type)
		_status.put(object.hashCode, optiontype)
		return optiontype;
	}

	override caseSequence(Sequence object) {
		Log.fine("visiting " + object.details)
		if(object === null) throw new RuntimeException('object cannot be null')
		if (_status.containsKey(object.hashCode)) {
			Log.info("Already visited " + object.details)
			return _status.get(object.hashCode)
		}
		var _e = object.element
		var acl2type = _e.toAcl2

		var seqtype = TypeSequence.make(acl2type)
		_status.put(object.hashCode, seqtype)
		return seqtype;
	}

	override caseSet(Set object) {
		Log.fine("visiting " + object.details)
		if(object === null) throw new RuntimeException('object cannot be null')
		if (_status.containsKey(object.hashCode)) {
			Log.info("Already visited " + object.details)
			return _status.get(object.hashCode)
		}
		var _e = object.element
		var acl2type = _e.toAcl2
		var settype = TypeSet.make(acl2type)
		_status.put(object.hashCode, settype)
		return settype;
	}

	override caseMap(Map object) {
		Log.fine("visiting " + object.details)
		if(object === null) throw new RuntimeException('object cannot be null')
		if (_status.containsKey(object.hashCode)) {
			Log.info("Already visited " + object.details)
			return _status.get(object.hashCode)
		}
		var _domain = object.domain
		var _range = object.range
		var _domainacl2type = _domain.toAcl2
		var _rangeacl2type = _range.toAcl2

		var maptype = TypeMap.make(_domainacl2type, _rangeacl2type)
		_status.put(object.hashCode, maptype)
		return maptype;
	}

	override caseLetExpression(LetExpression object) {
		Log.fine("visiting " + object.details)
		if(object === null) throw new RuntimeException('object cannot be null')
		if (_status.containsKey(object.hashCode)) {
			Log.info("Already visited " + object.details)
			return _status.get(object.hashCode)
		}
		// all variables
		var List<edu.kestrel.syntheto.ast.TypedVariable> vars = new ArrayList<edu.kestrel.syntheto.ast.TypedVariable>;
		for (_v : object.vars) {
			var typedvar = edu.kestrel.syntheto.ast.TypedVariable.make(Identifier.make(_v.name), _v.type.toAcl2)
			vars.add(typedvar)
		}
		var firstexpr = doSwitch(object.first) as Expression
		if (firstexpr === null) {
			Log.severe("found null when converting first Expression " + object.details)
		}
		var secondexpr = doSwitch(object.second) as Expression
		if (secondexpr === null) {
			Log.severe("found null when converting second Expression " + object.details)
		}

		var letacl2expression = ExpressionBind.make(vars, firstexpr, secondexpr)
		// Log.info(letacl2expression.toString())
		_status.put(object.hashCode, letacl2expression)
		return letacl2expression;
	}

	override caseWhenExpression(WhenExpression object) {
		Log.fine("visiting " + object.details)
		if(object === null) throw new RuntimeException('object cannot be null')
		if (_status.containsKey(object.hashCode)) {
			Log.info("Already visited " + object.details)
			return _status.get(object.hashCode)
		}

		var testexpr = doSwitch(object.test) as Expression
		if (testexpr === null) {
			Log.severe("found null when converting test Expression " + object.details)
		}
		var thenexpr = doSwitch(object.thenexpr) as Expression
		if (thenexpr === null) {
			Log.severe("found null when converting then Expression " + object.details)
		}
		var elseexpr = doSwitch(object.elseexpr) as Expression
		if (elseexpr === null) {
			Log.severe("found null when converting else Expression " + object.details)
		}
		var whenacl2expression = ExpressionWhen.make(testexpr, thenexpr, elseexpr)

		_status.put(object.hashCode, whenacl2expression)
		return whenacl2expression;
	}

	override caseUnlessExpression(UnlessExpression object) {
		Log.fine("visiting " + object.details)
		if(object === null) throw new RuntimeException('object cannot be null')
		if (_status.containsKey(object.hashCode)) {
			Log.info("Already visited " + object.details)
			return _status.get(object.hashCode)
		}
		var testexpr = doSwitch(object.test) as Expression
		if (testexpr === null) {
			Log.severe("found null when converting test Expression " + object.details)
		}
		var thenexpr = doSwitch(object.thenexpr) as Expression
		if (thenexpr === null) {
			Log.severe("found null when converting then Expression " + object.details)
		}
		var elseexpr = doSwitch(object.elseexpr) as Expression
		if (elseexpr === null) {
			Log.severe("found null when converting else Expression " + object.details)
		}
		var unlessexpr = ExpressionUnless.make(testexpr, thenexpr, elseexpr)

		_status.put(object.hashCode, unlessexpr)
		return unlessexpr;
	}

	override caseElseexpr(Elseexpr object) {
		Log.fine("visiting " + object.details)
		var expr = object.getElseexpr
		if (expr === null) {
			Log.severe("found null when converting else Expression " + object.details)
		}
		var acl2equivalent = expr.doSwitch
		return acl2equivalent
	}

	override caseIfExpression(IfExpression object) {
		Log.fine("visiting " + object.details)
		if(object === null) throw new RuntimeException('object cannot be null')
		if (_status.containsKey(object.hashCode)) {
			Log.info("Already visited " + object.details)
			return _status.get(object.hashCode)
		}
		var testexpr = doSwitch(object.test) as Expression
		if (testexpr === null) {
			Log.severe("found null when converting test Expression " + object.details)
		}
		var thenexpr = doSwitch(object.thenexpr) as Expression
		if (thenexpr === null) {
			Log.severe("found null when converting then Expression " + object.details)
		}
		var elseexpr = doSwitch(object.elseexpr) as Expression
		if (elseexpr === null) {
			Log.severe("found null when converting else Expression " + object.details)
		}
		var ifexpr = ExpressionIf.make(testexpr, thenexpr, elseexpr)

		_status.put(object.hashCode, ifexpr)
		return ifexpr;
	}

	override caseCondExpression(CondExpression object) {
		Log.fine("visiting " + object.details)
		if(object === null) throw new RuntimeException('object cannot be null')

		if(object === null) throw new RuntimeException('object cannot be null')

		if (_status.containsKey(object.hashCode)) {
			Log.info("Already visited " + object.details)
			return _status.get(object.hashCode)
		}
		var List<Branch> branches = new ArrayList<Branch>
		for (_branch : object.branches) {
			var astnode = _branch.doSwitch
			if (astnode === null) {
				Log.severe("found null when converting branch Expression " + object.details)

			}
			branches.add(astnode as Branch)
		}
		var condexpr = ExpressionCond.make(branches)
		_status.put(object.hashCode, condexpr)
		return condexpr;
	}

	override caseCondBranches(CondBranches object) {
		Log.fine("visiting " + object.details)
		if(object === null) throw new RuntimeException('object cannot be null')
		if (_status.containsKey(object.hashCode)) {
			Log.info("Already visited " + object.details)
			return _status.get(object.hashCode)
		}

		var testexpr = doSwitch(object.test) as Expression
		if (testexpr === null) {
			Log.severe("found null when converting test Expression " + object.details)
		}
		var thenexpr = doSwitch(object.thenexpr) as Expression
		if (thenexpr === null) {
			Log.severe("found null when converting then Expression " + object.details)
		}
		var branch = Branch.make(testexpr, thenexpr)

		_status.put(object.hashCode, branch)

		return branch;
	}

	override caseOr_expr(Or_expr object) {
		Log.fine("visiting " + object.details)
		if(object === null) throw new RuntimeException('object cannot be null')
		if (_status.containsKey(object.hashCode)) {
			Log.info("Already visited " + object.details)
			return _status.get(object.hashCode)
		}

		var leftexpr = doSwitch(object.left) as Expression
		if (leftexpr === null) {
			Log.severe("found null when converting left Expression " + object.details)
		}
		var rightexpr = doSwitch(object.right) as Expression
		if (rightexpr === null) {
			Log.severe("found null when converting right Expression " + object.details)
		}

		var oracl2 = ExpressionBinary.make(ExpressionBinary.Operator.OR, leftexpr, rightexpr)

		_status.put(object.hashCode, oracl2)

		return oracl2;
	}

	override caseAnd_expr(And_expr object) {
		Log.fine("visiting " + object.details)
		if(object === null) throw new RuntimeException('object cannot be null')
		if (_status.containsKey(object.hashCode)) {
			Log.info("Already visited " + object.details)
			return _status.get(object.hashCode)
		}

		var leftexpr = doSwitch(object.left) as Expression
		if (leftexpr === null) {
			Log.severe("found null when converting left Expression " + object.details)
		}
		var rightexpr = doSwitch(object.right) as Expression
		if (rightexpr === null) {
			Log.severe("found null when converting right Expression " + object.details)
		}

		var andacl2 = ExpressionBinary.make(ExpressionBinary.Operator.AND, leftexpr, rightexpr)

		_status.put(object.hashCode, andacl2)

		return andacl2;
	}

	override caseImplies_expr(Implies_expr object) {
		Log.fine("visiting " + object.details)
		if(object === null) throw new RuntimeException('object cannot be null')
		if (_status.containsKey(object.hashCode)) {
			Log.info("Already visited " + object.details)
			return _status.get(object.hashCode)
		}

		var leftexpr = doSwitch(object.left) as Expression
		if (leftexpr === null) {
			Log.severe("found null when converting left Expression " + object.details)
		}
		var rightexpr = doSwitch(object.right) as Expression
		if (rightexpr === null) {
			Log.severe("found null when converting right Expression " + object.details)
		}

		var ExpressionBinary impliesacl2

		switch object {
			case object.implies:
				impliesacl2 = ExpressionBinary.make(ExpressionBinary.Operator.IMPLIES, leftexpr, rightexpr)
			case object.implied:
				impliesacl2 = ExpressionBinary.make(ExpressionBinary.Operator.IMPLIED, leftexpr, rightexpr)
			case object.iff:
				impliesacl2 = ExpressionBinary.make(ExpressionBinary.Operator.IFF, leftexpr, rightexpr)
		}
		_status.put(object.hashCode, impliesacl2)

		return impliesacl2;
	}

	override caseCompare_expr(Compare_expr object) {
		Log.fine("visiting " + object.details)
		if(object === null) throw new RuntimeException('object cannot be null')
		if (_status.containsKey(object.hashCode)) {
			Log.info("Already visited " + object.details)
			return _status.get(object.hashCode)
		}

		var leftexpr = doSwitch(object.left) as Expression
		if (leftexpr === null) {
			Log.severe("found null when converting left Expression " + object.details)
		}
		var rightexpr = doSwitch(object.right) as Expression
		if (rightexpr === null) {
			Log.severe("found null when converting right Expression " + object.details)
		}

		var ExpressionBinary compareacl2
		// // (geq?='>=' | leq?='<=' | eq?='==' | neq?='!=' | gt?='>' | lt?='<')
		switch object {
			case object.geq: compareacl2 = ExpressionBinary.make(ExpressionBinary.Operator.GE, leftexpr, rightexpr)
			case object.gt: compareacl2 = ExpressionBinary.make(ExpressionBinary.Operator.GT, leftexpr, rightexpr)
			case object.leq: compareacl2 = ExpressionBinary.make(ExpressionBinary.Operator.LE, leftexpr, rightexpr)
			case object.lt: compareacl2 = ExpressionBinary.make(ExpressionBinary.Operator.LT, leftexpr, rightexpr)
			case object.neq: compareacl2 = ExpressionBinary.make(ExpressionBinary.Operator.NE, leftexpr, rightexpr)
			case object.eq: compareacl2 = ExpressionBinary.make(ExpressionBinary.Operator.EQ, leftexpr, rightexpr)
		}
		_status.put(object.hashCode, compareacl2)

		return compareacl2;

	}

	override casePlus(Plus object) {
		Log.fine("visiting " + object.details)
		if(object === null) throw new RuntimeException('object cannot be null')
		if (_status.containsKey(object.hashCode)) {
			Log.info("Already visited " + object.details)
			return _status.get(object.hashCode)
		}

		var leftexpr = doSwitch(object.left) as Expression
		if (leftexpr === null) {
			Log.severe("found null when converting left Expression " + object.details)
		}
		var rightexpr = doSwitch(object.right) as Expression
		if (rightexpr === null) {
			Log.severe("found null when converting right Expression " + object.details)
		}

		var plusacl2 = ExpressionBinary.make(ExpressionBinary.Operator.ADD, leftexpr, rightexpr)

		_status.put(object.hashCode, plusacl2)

		return plusacl2;
	}

	override caseMinus(Minus object) {
		Log.fine("visiting " + object.details)
		if(object === null) throw new RuntimeException('object cannot be null')
		if (_status.containsKey(object.hashCode)) {
			Log.info("Already visited " + object.details)
			return _status.get(object.hashCode)
		}

		var leftexpr = doSwitch(object.left) as Expression
		if (leftexpr === null) {
			Log.severe("found null when converting left Expression " + object.details)
		}
		var rightexpr = doSwitch(object.right) as Expression
		if (rightexpr === null) {
			Log.severe("found null when converting right Expression " + object.details)
		}

		var minusacl2 = ExpressionBinary.make(ExpressionBinary.Operator.SUB, leftexpr, rightexpr)

		_status.put(object.hashCode, minusacl2)

		return minusacl2;
	}

	override caseMulti(Multi object) {
		Log.fine("visiting " + object.details)
		if(object === null) throw new RuntimeException('object cannot be null')
		if (_status.containsKey(object.hashCode)) {
			Log.info("Already visited " + object.details)
			return _status.get(object.hashCode)
		}

		var leftexpr = doSwitch(object.left) as Expression
		if (leftexpr === null) {
			Log.severe("found null when converting left Expression " + object.details)
		}
		var rightexpr = doSwitch(object.right) as Expression
		if (rightexpr === null) {
			Log.severe("found null when converting right Expression " + object.details)
		}

		var multiplyacl2 = ExpressionBinary.make(ExpressionBinary.Operator.MUL, leftexpr, rightexpr)

		_status.put(object.hashCode, multiplyacl2)

		return multiplyacl2;
	}

	override caseDiv(Div object) {
		Log.fine("visiting " + object.details)
		if(object === null) throw new RuntimeException('object cannot be null')
		if (_status.containsKey(object.hashCode)) {
			Log.info("Already visited " + object.details)
			return _status.get(object.hashCode)
		}

		var leftexpr = doSwitch(object.left) as Expression
		if (leftexpr === null) {
			Log.severe("found null when converting left Expression " + object.details)
		}
		var rightexpr = doSwitch(object.right) as Expression
		if (rightexpr === null) {
			Log.severe("found null when converting right Expression " + object.details)
		}

		var divacl2 = ExpressionBinary.make(ExpressionBinary.Operator.DIV, leftexpr, rightexpr)

		_status.put(object.hashCode, divacl2)

		return divacl2;
	}

	override caseModulo(Modulo object) {
		Log.fine("visiting " + object.details)
		if(object === null) throw new RuntimeException('object cannot be null')
		if (_status.containsKey(object.hashCode)) {
			Log.info("Already visited " + object.details)
			return _status.get(object.hashCode)
		}

		var leftexpr = doSwitch(object.left) as Expression
		if (leftexpr === null) {
			Log.severe("found null when converting left Expression " + object.details)
		}
		var rightexpr = doSwitch(object.right) as Expression
		if (rightexpr === null) {
			Log.severe("found null when converting right Expression " + object.details)
		}

		var moduloacl2 = ExpressionBinary.make(ExpressionBinary.Operator.REM, leftexpr, rightexpr)

		_status.put(object.hashCode, moduloacl2)

		return moduloacl2;
	}

	override caseUnary_expr(Unary_expr object) {
		Log.fine("visiting " + object.details)
		if(object === null) throw new RuntimeException('object cannot be null')
		if (_status.containsKey(object.hashCode)) {
			Log.info("Already visited " + object.details)
			return _status.get(object.hashCode)
		}
		var operand = object.operand.doSwitch as Expression
		if (operand === null) {
			Log.severe("found null when converting unary Expression " + object.details)
		}
		var ExpressionUnary unaryacl2 = null
		switch (object) {
			case object.op.not: unaryacl2 = ExpressionUnary.make(ExpressionUnary.Operator.NOT, operand)
			case object.op.negation: unaryacl2 = ExpressionUnary.make(ExpressionUnary.Operator.MINUS, operand)
		}

		_status.put(object.hashCode, unaryacl2)

		return unaryacl2;
	}

	override caseLiteralValue(LiteralValue object) {
		Log.fine("visiting " + object.details)
		if(object === null) throw new RuntimeException('object cannot be null')
		if (_status.containsKey(object.hashCode)) {
			Log.info("Already visited " + object.details)
			return _status.get(object.hashCode)
		}

		var literalvalueacle = doSwitch(object.value)

		_status.put(object.hashCode, literalvalueacle)

		return literalvalueacle;
	}

	override caseFunctionCall(FunctionCall object) {
		Log.fine("visiting " + object.details)
		if(object === null) throw new RuntimeException('object cannot be null')
		if (_status.containsKey(object.hashCode)) {
			Log.info("Already visited " + object.details)
			return _status.get(object.hashCode)
		}
		var List<Expression> arguments = new ArrayList<Expression>
		for (_a : object.args) {
			var argexpression = doSwitch(_a)
			arguments.add(argexpression as Expression)
		}
		var name = object.func.name
		var callacl2 = ExpressionCall.make(Identifier.make(name), arguments)
		_status.put(object.hashCode, callacl2)
		return callacl2;
	}

	override caseVariableAssignment(VariableAssignment object) {
		Log.fine("visiting " + object.details)
		if(object === null) throw new RuntimeException('object cannot be null')
		if (_status.containsKey(object.hashCode)) {
			Log.info("Already visited " + object.details)
			return _status.get(object.hashCode)
		}

		var variabletarget = object.variable
		var firstvariable = variabletarget.child;
		var secondvariable = variabletarget.subelement
		if (secondvariable === null && firstvariable !== null) {
			var firstvarname = (firstvariable as TypedVariable).name
			var acl2variable = Variable.make(Identifier.make(firstvarname))
			return acl2variable
		}
		if (secondvariable !== null) { 
			// there is a rightmost expression
			var _list = new Stack
			var rightmostvariable = secondvariable
			_list.add(rightmostvariable)
			if (rightmostvariable instanceof ProductTypeDefinition) {
				var _parent = rightmostvariable.eContainer
				System.out.println(_parent)
				if (_parent instanceof SumTypeDefinition) {
					var Identifier sumTypeId = Identifier.make(_parent.name);
					var Identifier altTypeId = Identifier.make(rightmostvariable.productID);
					var inits = new ArrayList;
					return ExpressionSumConstruct.make(sumTypeId, altTypeId, inits);
				}
				throw new Error(object.details + " right most cannot be a product type when parent is not a sumtype")

			}
			var leftexpression = variabletarget.left
			if (leftexpression === null) {
				throw new Error(object.details + " something is wrong ")

			}

			while (leftexpression.child === null) {
				_list.add(leftexpression.subelement)
				leftexpression = leftexpression.left

			}
			_list.add(leftexpression.child)
			if (_list.head instanceof ProductTypeDefinition || _list.tail instanceof ProductTypeDefinition) {
				throw new Error(object.details + " leftmost and rightmost cannot be a product type")
			}
			var output = _list.toAcl2ExpressionFromVariableAssignment
			return output
		}
		return null;
	}

	def toAcl2ExpressionFromVariableAssignment(Stack<Subelement> subelements) {
		// TODO:Support for Sum Field
		var Expression _toReturn = null
		var elementtop = ((subelements.peek as TypedVariable).type as PrimaryTypeElement).typeref
		if (elementtop instanceof ProductTypeDefinition) {
			var headelement = Variable.make(Identifier.make((subelements.peek as TypedVariable).name))

			subelements.pop
			var secondelement = Identifier.make((subelements.peek as TypedVariable).name)

			var expr = ExpressionProductField.make(Identifier.make(elementtop.productID), headelement, secondelement)
			subelements.pop
			_toReturn = toAcl2ExpressionFromVariableAssignmentInternal(subelements, expr)

		}

		// Log.info(_toReturn.toString())
		return _toReturn

	}

	def Expression toAcl2ExpressionFromVariableAssignmentInternal(Stack<Subelement> stack,
		ExpressionProductField field) {
		if(stack.empty()) return field
		var elementtop = ((stack.peek as TypedVariable).type as PrimaryTypeElement).typeref
		if (elementtop !== null && elementtop instanceof ProductTypeDefinition) {
			var element = Identifier.make((stack.peek as TypedVariable).name)
			var parent = (stack.peek.eContainer as ProductTypeDefinition)
			if (parent === null) {
				Log.severe("why parent is null" + stack.peek.details)
			}
			var expr = ExpressionProductField.make(Identifier.make(parent.productID), field, element)
			stack.pop
			return toAcl2ExpressionFromVariableAssignmentInternal(stack, expr)
		}
		if (elementtop !== null && elementtop instanceof SubTypeDefinition) {
			var element = Identifier.make((stack.peek as TypedVariable).name)
			var parent = (stack.peek.eContainer as ProductTypeDefinition)
			if (parent === null) {
				Log.severe("why parent is null" + stack.peek.details)
			}
			var expr = ExpressionProductField.make(Identifier.make(parent.productID), field, element)
			stack.pop
			return toAcl2ExpressionFromVariableAssignmentInternal(stack, expr)
		}
		if (elementtop === null) {
			// must be a primitive type or a sequence - does not matter. just use the name
			var element = Identifier.make((stack.peek as TypedVariable).name)
			var parent = (stack.peek.eContainer as ProductTypeDefinition)
			if (parent === null) {
				Log.severe("why parent is null" + stack.peek.details)
			}
			var expr = ExpressionProductField.make(Identifier.make(parent.productID), field, element)
			stack.pop
			return expr
		}
		return field
	}

	override caseTwoValueBuiltins(TwoValueBuiltins object) {
		Log.fine("visiting " + object.details)
		if(object === null) throw new RuntimeException('object cannot be null')
		if (_status.containsKey(object.hashCode)) {
			Log.info("Already visited " + object.details)
			return _status.get(object.hashCode)
		}
		var List<Expression> arguments = new ArrayList<Expression>
		var firstvariable = object.operand
		var firstvariableacl2 = doSwitch(firstvariable)

		// var firstvarname = (firstvariable as TypedVariable).name
		// var acl2variable = Variable.make(Identifier.make(firstvarname))
		arguments.add(firstvariableacl2 as Expression)

		var _a = object.element

		var argexpression = doSwitch(_a)

		// System.out.println(argexpression)
		arguments.add(argexpression as Expression)
		var name = object.builtin

		var childtype = object.deriverealtype
		if (childtype === null) {
			System.out.println("---" + name + "---")
			System.out.println(arguments)

		}
		var realacl2type = childtype.toAcl2
		var List<Type> types = new ArrayList
		types.add(realacl2type)

		// types.add(TypeSequence.make(TypeInteger.make()));
		var callacl2 = ExpressionCall.make(Identifier.make(name), types, arguments)
		_status.put(object.hashCode, callacl2)
		return callacl2;
	}

	/*
	 * override caseSeqLiteral(SeqLiteral object) {
	 * 	Log.fine("visiting " + object.details)
	 * 	if(object === null) throw new RuntimeException('object cannot be null')
	 * 	var List<Expression> allseqelement = new ArrayList<Expression>
	 * 	for (_e : object.elements) {
	 * 		var _expression = doSwitch(_e)
	 * 		allseqelement.add(_expression as Expression)
	 * 	}
	 * 	// todo: @daniel - we need to make a syntheto acl 2 Expression from the
	 * 	// seq types - some kind of make function will relevant here.
	 * 	return null;
	 * }
	 */
	override caseSingleValueBuiltins(SingleValueBuiltins object) {
		Log.fine("visiting " + object.details)
		if(object === null) throw new RuntimeException('object cannot be null')

		if (object.empty) {
			// this is the case when an empty has been passed as in example
			// return empty
			// we need to handle it
			var name = 'empty'
			var List<Expression> arguments = new ArrayList<Expression>
			var function = object.eContainer
			var condition = (function === null) || (function instanceof FunctionDefinition)
			while (condition == false) {
				function = function.eContainer
				condition = (function === null) || (function instanceof FunctionDefinition)

			}
			if (function === null) {
				return null

			} else {
				var child = (function as FunctionDefinition).returnlist
				var frontelement = child.get(0)
				var functype = frontelement.tag.type
				var realacl2type = functype.toAcl2
				var List<Type> types = new ArrayList
				types.add(realacl2type)

				var callacl2 = ExpressionCall.make(Identifier.make(name), types, arguments)
				_status.put(object.hashCode, callacl2)
				return callacl2;
			}

		} else {
			if (_status.containsKey(object.hashCode)) {
				Log.info("Already visited " + object.details)
				return _status.get(object.hashCode)
			}
			var List<Expression> arguments = new ArrayList<Expression>
			var _a = object.element
			var argexpression = doSwitch(_a)
			arguments.add(argexpression as Expression)

			var childtype = object.deriverealtype
			var realacl2type = childtype.toAcl2
			var List<Type> types = new ArrayList
			types.add(realacl2type)

			var name = object.builtin
			// types.add(TypeSequence.make(TypeInteger.make()));
			var callacl2 = ExpressionCall.make(Identifier.make(name), types, arguments)
			_status.put(object.hashCode, callacl2)
			return callacl2;
		}

	}

	def TypeElement deriverealtype(TwoValueBuiltins builtins) {
		Log.fine("visiting " + builtins.details)
		var element = builtins.element;
		if (element instanceof LiteralValue && (element as LiteralValue).value !== null &&
			(element as LiteralValue).value instanceof SingleValueBuiltins) {

			System.out.println(element + " is of type singlevalue")
			return deriverealtype((element as LiteralValue).value as SingleValueBuiltins)
		}
		if (element instanceof LiteralValue && (element as LiteralValue).value !== null &&
			(element as LiteralValue).value instanceof TwoValueBuiltins) {
			return deriverealtype((element as LiteralValue).value as TwoValueBuiltins)
		}
		if (element instanceof FunctionCall && (element as FunctionCall).func !== null) {
			var child = (element as FunctionCall).func.returnlist
			var frontelement = child.get(0)
			return frontelement.tag.type
		}
		if (element instanceof VariableAssignment && (element as VariableAssignment).variable !== null) {
			var child = (element as VariableAssignment).variable.realType
			return child
		}
		if (element instanceof TwoValueBuiltins && element !== null) {
			return (element as TwoValueBuiltins).deriverealtype
		}
	}

	def TypeElement deriverealtype(SingleValueBuiltins builtins) {

		if (builtins.empty) {
			Log.fine("saw empty entry")
			var function = builtins.eContainer
			var condition = (function === null) || (function instanceof FunctionDefinition)
			while (condition == false) {
				function = function.eContainer
				condition = (function === null) || (function instanceof FunctionDefinition)

			}
			if (function === null) {
				return null

			} else {
				var child = (function as FunctionDefinition).returnlist
				var frontelement = child.get(0)
				var functype = frontelement.tag.type
				return functype
			}

		}

		Log.fine("visiting " + builtins.details)
		var element = builtins.element;
		if (element instanceof LiteralValue && (element as LiteralValue).value !== null &&
			(element as LiteralValue).value instanceof SingleValueBuiltins) {
			return deriverealtype((element as LiteralValue).value as SingleValueBuiltins)
		}
		if (element instanceof LiteralValue && (element as LiteralValue).value !== null &&
			(element as LiteralValue).value instanceof TwoValueBuiltins) {
			return deriverealtype((element as LiteralValue).value as TwoValueBuiltins)
		}

		if (element instanceof FunctionCall && (element as FunctionCall).func !== null) {
			var child = (element as FunctionCall).func.returnlist
			var frontelement = child.get(0)
			return frontelement.tag.type
		}

		if (element instanceof VariableAssignment && (element as VariableAssignment).variable !== null) {
			var child = (element as VariableAssignment).variable.realType
			return child
		}
		if (element instanceof TwoValueBuiltins && element !== null) {
			return (element as TwoValueBuiltins).deriverealtype
		}
	}

	def TypeElement getRealType(ElementTagQualifier variabletarget) {
		Log.fine("visiting " + variabletarget.details)
		var firstvariable = variabletarget.child;
		var secondvariable = variabletarget.subelement
		if (secondvariable === null && firstvariable !== null) {
			return (firstvariable as TypedVariable).type
		}
		if (secondvariable !== null) {
			var rightmostvariable = (secondvariable as TypedVariable).type
			return rightmostvariable
		}
		return null
	}

	override caseBooleanLiteral(BooleanLiteral object) {
		Log.fine("visiting " + object.details)
		if(object === null) throw new RuntimeException('object cannot be null')
		if(object.isIsTrue) return ExpressionLiteral.make(LiteralBoolean.make(true)) else ExpressionLiteral.make(LiteralBoolean.make(false))
	}

	override caseNumberLiteral(NumberLiteral object) {
		Log.fine("visiting " + object.details)
		if(object === null) throw new RuntimeException('object cannot be null')
		var value = object.value
		return ExpressionLiteral.make(LiteralInteger.make(BigInteger.valueOf(value)));
	}

	override caseStringLiteral(StringLiteral object) {
		Log.fine("visiting " + object.details)
		if(object === null) throw new RuntimeException('object cannot be null')
		var value = object.value
		return ExpressionLiteral.make(LiteralString.make(value));
	}

//TODO:SetLiterals
//	override caseSetLiteral(SetLiteral object) {
//		if(object === null) throw new RuntimeException('object cannot be null')
//		if (_status.containsKey(object.hashCode)) {
//			Log.info("Already visited " + object.details)
//			return _status.get(object.hashCode)
//		}
//		return null;
//	}
	override caseProductLiteral(ProductLiteral object) {
		Log.fine("visiting " + object.details)
		if(object === null) throw new RuntimeException('object cannot be null')
		var typereal = object.product
		var List<Initializer> inits = new ArrayList
		for (assign : object.assignment) {
			var _init = Initializer.make(Identifier.make(assign.left.name), assign.right.doSwitch as Expression)
			inits.add(_init)
		}
		var expr = ExpressionProductConstruct.make(Identifier.make(typereal.productID), inits)
		return expr
	}

	override caseTheorem(Theorem object) {
		Log.fine("visiting " + object.details)
		if(object === null) throw new RuntimeException('object cannot be null')
		if (_status.containsKey(object.hashCode)) {
			Log.info("Already visited " + object.details)
			return _status.get(object.hashCode)
		}

		var List<edu.kestrel.syntheto.ast.TypedVariable> vars = new ArrayList
		for (_var : object.foralltag) {
			vars.add(edu.kestrel.syntheto.ast.TypedVariable.make(Identifier.make(_var.name), _var.type.toAcl2));
		}

		var formula = object.expression.doSwitch as Expression

		var thm = TopLevelTheorem.make(edu.kestrel.syntheto.ast.Theorem.make(Identifier.make(object.name), vars, formula));

		_status.put(object.hashCode, thm)
		return thm;

	}

	override caseFunctionSpecfication(FunctionSpecfication object) {
		Log.fine("visiting " + object.details)
		if(object === null) throw new RuntimeException('object cannot be null')
		if (_status.containsKey(object.hashCode)) {
			Log.info("Already visited " + object.details)
			return _status.get(object.hashCode)
		}
		var List<edu.kestrel.syntheto.ast.TypedVariable> inputs = new ArrayList
		for (_a : object.param) {
			inputs.add(edu.kestrel.syntheto.ast.TypedVariable.make(Identifier.make(_a.tag.name), _a.tag.type.toAcl2));
		}
		var List<edu.kestrel.syntheto.ast.TypedVariable> outputs = new ArrayList
		for (_a : object.returnlist) {
			outputs.add(edu.kestrel.syntheto.ast.TypedVariable.make(Identifier.make(_a.tag.name), _a.tag.type.toAcl2));
		}
		var header = FunctionHeader.make(Identifier.make(object.funcName), inputs, outputs);

		var List<FunctionHeader> functions = new ArrayList
		functions.add(header)

		var Expression relation = object.expr.doSwitch as Expression // build expression ordered(out) && permutation(out,input)
		var specifier = FunctionSpecifierInputOutput.make(relation);
		var acl2specification = TopLevelSpecification.make(FunctionSpecification.make(Identifier.make(object.name), functions, specifier));
		_status.put(object.hashCode, acl2specification)
		return acl2specification;
	}

	override caseFunctionDefinition(FunctionDefinition object) {
		Log.fine("visiting " + object.details)
		if(object === null) throw new RuntimeException('object cannot be null')
		if (_status.containsKey(object.hashCode)) {
			Log.info("Already visited " + object.details)
			return _status.get(object.hashCode)
		}

		var List<edu.kestrel.syntheto.ast.TypedVariable> inputs = new ArrayList
		if (object.param !== null) {
			for (_a : object.param) {
				inputs.add(
					edu.kestrel.syntheto.ast.TypedVariable.make(Identifier.make(_a.tag.name), _a.tag.type.toAcl2));
			}

		}
		var List<edu.kestrel.syntheto.ast.TypedVariable> outputs = new ArrayList
		if (object.returnlist !== null) {
			for (_a : object.returnlist) {
				outputs.add(
					edu.kestrel.syntheto.ast.TypedVariable.make(Identifier.make(_a.tag.name), _a.tag.type.toAcl2));
			}

		}
		var header = FunctionHeader.make(Identifier.make(object.name), inputs, outputs);
		var Expression precondition = null
		var Expression postcondition = null
		if (object.assumes !== null) {
			precondition = object.assumes.doSwitch as Expression
		}
		if (object.ensures !== null) {
			postcondition = object.ensures.doSwitch as Expression
		}
		var Expression body = object.expr.doSwitch as Expression

		var measure = object.measure
		var Expression measureAcl2 = null
		if (measure !== null) {
			measureAcl2 = measure.doSwitch as Expression
		}

		var FunctionDefiner definer = FunctionDefinerRegular.make(body, measureAcl2);

		var fundef = TopLevelFunction.make(edu.kestrel.syntheto.ast.FunctionDefinition.make(header, precondition, postcondition, definer));
		_status.put(object.hashCode, fundef)
		return fundef;
	}

}
