package edu.vanderbilt.isis.midas

import org.eclipse.xtext.naming.DefaultDeclarativeQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName
import edu.vanderbilt.isis.midas.syntheto.ProductTypeDefinition
import edu.vanderbilt.isis.midas.syntheto.Program
import edu.vanderbilt.isis.midas.syntheto.SumTypeDefinition

class SynthetoQualifiedName extends DefaultDeclarativeQualifiedNameProvider {

	def QualifiedName qualifiedName(ProductTypeDefinition e) {
		var container = e.eContainer();
		if (container instanceof Program)
			return QualifiedName.create(e.productID)
		if (container instanceof SumTypeDefinition)
			return QualifiedName.create(container.name, e.productID)
	}

}
