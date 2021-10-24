package edu.vanderbilt.isis.midas.scoping

import org.eclipse.xtext.scoping.IGlobalScopeProvider
import org.eclipse.xtext.scoping.IScope
import org.eclipse.xtext.resource.IEObjectDescription
import org.eclipse.emf.ecore.EReference
import org.eclipse.emf.ecore.resource.Resource
import com.google.common.base.Predicate;

class NoGlobalScopeSyntheto implements IGlobalScopeProvider {
	override IScope getScope(Resource context, EReference reference, Predicate<IEObjectDescription> filter) {
		return IScope.NULLSCOPE;
	}

}
