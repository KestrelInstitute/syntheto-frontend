/*
 * generated by Xtext 2.23.0
 */
package edu.vanderbilt.isis.midas.ui.labeling

import com.google.inject.Inject
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider
import org.eclipse.xtext.ui.label.DefaultEObjectLabelProvider
import edu.vanderbilt.isis.midas.syntheto.Expression
import edu.vanderbilt.isis.midas.syntheto.Compare_expr

/**
 * Provides labels for EObjects.
 * 
 * See https://www.eclipse.org/Xtext/documentation/310_eclipse_support.html#label-provider
 */
class SynthetoLabelProvider extends DefaultEObjectLabelProvider {

	@Inject
	new(AdapterFactoryLabelProvider delegate) {
		super(delegate);
	}

	// Labels and icons can be computed like this:
	def text(Compare_expr object) {
		switch object {
			case object.geq: return 'Compare:>='
			case object.gt: return 'Compare:>'
			case object.leq: return 'Compare:<='
			case object.lt: return 'Compare:<'
			case object.neq: return 'Compare:!='
			case object.eq: return 'Compare:=='
		}
	}
//
//	def image(Greeting ele) {
//		'Greeting.gif'
//	}
}
