/*
 * generated by Xtext 2.23.0
 */
package edu.vanderbilt.isis.midas.scoping

import org.eclipse.xtext.scoping.IScope
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EReference
import edu.vanderbilt.isis.midas.syntheto.ElementTagQualifier
import edu.vanderbilt.isis.midas.syntheto.Subelement
import edu.vanderbilt.isis.midas.syntheto.TypedVariable
import edu.vanderbilt.isis.midas.syntheto.PrimaryTypeElement
import java.util.List
import org.eclipse.xtext.scoping.Scopes
import edu.vanderbilt.isis.midas.syntheto.ProductTypeDefinition
import edu.vanderbilt.isis.midas.syntheto.SumTypeDefinition
import edu.vanderbilt.isis.midas.syntheto.SubTypeDefinition
import java.util.ArrayList
import org.eclipse.xtext.resource.EObjectDescription
import org.eclipse.xtext.naming.QualifiedName
import org.eclipse.xtext.scoping.impl.SimpleScope
import edu.vanderbilt.isis.midas.syntheto.ProductAssignment
import edu.vanderbilt.isis.midas.syntheto.ProductLiteral
import org.eclipse.xtext.EcoreUtil2
import edu.vanderbilt.isis.midas.syntheto.VariableAssignment
import edu.vanderbilt.isis.midas.syntheto.TransformDefinition
import edu.vanderbilt.isis.midas.syntheto.FunctionDefinition

/**
 * This class contains custom scoping description.
 * 
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#scoping
 * on how and when to use it.
 */
class SynthetoScopeProvider extends AbstractSynthetoScopeProvider {
	override IScope getScope(EObject context, EReference reference) {

		// if (context instanceof ElementTagQualifier){ System.err.println(reference)}
//		if (context instanceof VariableAssignment) {
//
//			System.out.println(context + " " + context.eContainer)
//		}

		if (context instanceof ElementTagQualifier && reference.name == "subelement") {

			// System.err.println("child")
			var base = context as ElementTagQualifier
			var left = base.left
			// System.err.println("----" + left)
			if (left !== null && left instanceof ElementTagQualifier) {
				// var left_e = left
				var child = left.child
				// System.err.println(child) 
				if (child === null) {
					child = left.subelement
				}
				// System.out.println("we are here")
				var values = child.getSubChildFirstLevel
			//	System.out.println(values)
				return values 
			}
		}
		if (context instanceof ElementTagQualifier && reference.name == "child") { 

			var parent = context.eContainer
			var condition = (parent === null) || (parent instanceof TransformDefinition)
			while (condition == false) {
				parent = parent.eContainer
				condition = (parent === null) || (parent instanceof TransformDefinition)

			}
			if (parent instanceof TransformDefinition) {
				//System.out.println(context + " " + context.eContainer + " reference " + reference.name +" parent"+parent)
				var fn = parent.transformed_fn
				if (fn instanceof FunctionDefinition){
					var params= (fn as FunctionDefinition).param
					var returnparam=(fn as FunctionDefinition).returnlist
					val result = newArrayList
					for (p : params) {
						result.add(p.tag)
					}
					for (p : returnparam) {
						result.add(p.tag)
					} 
					return Scopes.scopeFor(result)
				}
				
			}

		}

		if (context instanceof SumTypeDefinition && reference.name == "subelement") {
			// System.err.println("child")
			var base = context as ElementTagQualifier
			var left = base.left
			// System.err.println("----" + left)
			if (left !== null && left instanceof ElementTagQualifier) {
				// var left_e = left
				var child = left.child
				// System.err.println(child)
				if (child === null) {
					child = left.subelement
				}
				// System.out.println("we are here 2")
				return child.getSubChildFirstLevel
			}
		}

		if (context instanceof ProductAssignment && reference.name == "left") {
			var productassignment = context as ProductAssignment
			var ProductLiteral literal = productassignment.eContainer as ProductLiteral
			if (literal !== null) {
				var product = literal.product
				var candidates = EcoreUtil2.getAllContentsOfType(product, typeof(TypedVariable));

				// System.out.println("we are here 3")
				return Scopes.scopeFor(candidates);

			}

		}

		return super.getScope(context, reference);
	}

	def IScope getGetSubChildFirstLevel(Subelement subelement) {
		var List<Subelement> childcandidates = new ArrayList<Subelement>
		if (subelement instanceof edu.vanderbilt.isis.midas.syntheto.TypedVariable) {
			var type = (subelement as edu.vanderbilt.isis.midas.syntheto.TypedVariable).type
			if (type instanceof PrimaryTypeElement && (type as PrimaryTypeElement).typeref !== null) {
				var realtype = (type as PrimaryTypeElement).typeref
				if (realtype instanceof ProductTypeDefinition) {
					var _list = (realtype).element.toList
					for (l : _list) {
						childcandidates.add(l as Subelement)
					}
				}
				if (realtype instanceof SumTypeDefinition) {
					var _list = (realtype).alternatives.toList
					val result = newArrayList
					for (l : _list) {
						result.add(EObjectDescription.create(QualifiedName.create(l.productID), l))
					}
					return new SimpleScope(IScope.NULLSCOPE, result)

				}
				if (realtype instanceof SubTypeDefinition) {
					return IScope.NULLSCOPE

				}
				var IScope existingScope = Scopes.scopeFor(childcandidates)
				return existingScope
			} else {
				return IScope.NULLSCOPE
			}
		} else if (subelement instanceof ProductTypeDefinition) {
			var _list = subelement.element.toList
			for (l : _list) {
				childcandidates.add(l as Subelement)
			}
			var IScope existingScope = Scopes.scopeFor(childcandidates)
			return existingScope
		} else if (subelement instanceof SumTypeDefinition) {
			var _list = subelement.alternatives.toList
			val result = newArrayList
			for (l : _list) {
				result.add(EObjectDescription.create(QualifiedName.create(l.productID), l))
			// System.out.println(l)
			}
			return new SimpleScope(IScope.NULLSCOPE, result)
		}

		return IScope.NULLSCOPE
	}

}
